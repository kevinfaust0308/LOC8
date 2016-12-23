package com.monsoonblessing.kevinfaust.loc8personal.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.monsoonblessing.kevinfaust.loc8personal.FirebaseDatabaseLoggedInReferences;
import com.monsoonblessing.kevinfaust.loc8personal.FirebaseDatabaseReferences;
import com.monsoonblessing.kevinfaust.loc8personal.R;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegisterActivity extends AppCompatActivity {


    private static final String TAG = "RegisterActivity";
    private static final int GALLERY_REQUEST = 100;

    @BindView(R.id.activity_register)
    LinearLayout rootView;
    @BindView(R.id.name_field)
    EditText nameField;
    @BindView(R.id.email_field)
    EditText emailField;
    @BindView(R.id.password_field)
    EditText passwordField;
    @BindView(R.id.confirm_password_field)
    EditText confirmPasswordField;
    @BindView(R.id.upload_pic_btn)
    ImageButton uploadPic;

    private FirebaseDatabaseReferences mFirebaseDatabaseReferences;

    private StorageReference mStorageRef;

    private ProgressDialog mProgressDialog;
    private Uri mImageUri = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        mFirebaseDatabaseReferences = new FirebaseDatabaseReferences();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mProgressDialog = new ProgressDialog(this);
    }


    @OnClick(R.id.register_btn)
    void OnRegister() {
        startRegister();
    }


    @OnClick(R.id.upload_pic_btn)
    void OnImageSelect() {
        launchGallery();
    }


    private void launchGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GALLERY_REQUEST);
    }


    private void startRegister() {

        final String name = nameField.getText().toString().trim();
        final String email = emailField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();
        String confirm_password = confirmPasswordField.getText().toString().trim();

        if (hasValidFrields(name, email, password, confirm_password)) {

            mProgressDialog.setMessage("Setting your account up");
            mProgressDialog.show();

            // log user in
            mFirebaseDatabaseReferences.getFirebaseAuth().createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    Log.d(TAG, "account creation completed");

                    if (task.isSuccessful()) {
                        Log.d(TAG, "account creation completed successfully");

                        // user registered

                        // create new entry in database with user's data: name, email, location, profile picture, list of your friends

                        StorageReference filepath = mStorageRef.child("ProfilePictures").child(mImageUri.getLastPathSegment());
                        filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Log.d(TAG, "file uploaded successfully");

                                FirebaseDatabaseLoggedInReferences firebaseDatabaseLoggedInReferences = new FirebaseDatabaseLoggedInReferences();
                                DatabaseReference currentUserDb = firebaseDatabaseLoggedInReferences.getFirebaseCurrentUserDatabaseRef();

                                // url of the picture stored on google
                                Uri downloadUrl = taskSnapshot.getDownloadUrl();

                                // add the boiler plate data to the user's database
                                currentUserDb.child(FirebaseDatabaseReferences.FIREBASE_ID_KEY).setValue(firebaseDatabaseLoggedInReferences.getCurrentUser().getUid());
                                currentUserDb.child(FirebaseDatabaseReferences.FIREBASE_NAME_KEY).setValue(name);
                                currentUserDb.child(FirebaseDatabaseReferences.FIREBASE_EMAIL_KEY).setValue(email);
                                currentUserDb.child(FirebaseDatabaseReferences.FIREBASE_PICTURE_URL_KEY).setValue(downloadUrl.toString());
                                currentUserDb.child(FirebaseDatabaseReferences.FIREBASE_ONLINE_KEY).setValue(true);
                                currentUserDb.child(FirebaseDatabaseReferences.FIREBASE_STATUS_MSG_KEY).setValue("Hi I'm " + name + "!");

                                // map of all our friends. all users have LOC8 as a friend
                                currentUserDb.child(FirebaseDatabaseReferences.FIREBASE_FRIENDS_KEY).child(FirebaseDatabaseReferences.LOC8_DEFAULT_FRIEND_ID).setValue("LOC8");

                                // hardcoded location values to new york
                                currentUserDb.child(FirebaseDatabaseReferences.FIREBASE_LATITUDE_KEY).setValue("40.730610");
                                currentUserDb.child(FirebaseDatabaseReferences.FIREBASE_LONGITUDE_KEY).setValue("-73.935242");

                                mProgressDialog.dismiss();
                                // user logged in
                                Intent loginIntent = new Intent(RegisterActivity.this, MainActivity.class);
                                startActivity(loginIntent);
                                finish();
                            }


                        });
                    } else {
                        showShortSnackbar(rootView, "User with this email already exists");
                        mProgressDialog.dismiss();
                    }
                }
            });


        }
    }


    public boolean hasValidFrields(String name, String email, String password, String confirm_password) {

        boolean hasValidFields = false;

        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(name) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(confirm_password) && mImageUri != null) {

            // make sure password is at least 5 characters
            if (password.length() > 5) {

                if (password.equals(confirm_password)) {

                    // we passed all the validation checks
                    hasValidFields = true;

                } else {
                    showShortSnackbar(rootView, "Passwords not matching");
                }
            } else {
                showShortSnackbar(rootView, "Password must be at least 5 characters");
            }
        } else {
            showShortSnackbar(rootView, "Please fill out all fields");
        }

        return hasValidFields;
    }


    public void showShortSnackbar(View root, String message) {
        Snackbar.make(root, message, Snackbar.LENGTH_SHORT).show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {

            // get the uri of the chosen picture
            Uri selectPicUri = data.getData();
            Log.d(TAG, "Selected pic uri: " + selectPicUri.toString());

            // crop the selected picture
            CropImage.activity(selectPicUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .setMaxCropResultSize(2000, 2000)
                    .start(this);

        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {
                Uri croppedImgUri = result.getUri();
                Log.d(TAG, "Cropped pic uri: " + croppedImgUri.toString());

                // update profile pic and uri variable to the cropped image
                mImageUri = croppedImgUri;
                uploadPic.setImageURI(croppedImgUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }


}
