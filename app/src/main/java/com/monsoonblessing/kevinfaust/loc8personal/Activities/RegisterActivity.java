package com.monsoonblessing.kevinfaust.loc8personal.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.monsoonblessing.kevinfaust.loc8personal.R;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegisterActivity extends AppCompatActivity {


    private static final String TAG = "RegisterActivity";
    private static final int GALLERY_REQUEST = 100;
    private static final String LOC8_DEFAULT_FRIEND_ID = "meQJqhVlJGcqbXEvIi1aJVg4Z5U2";

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

    private FirebaseAuth mAuth;
    private DatabaseReference mFirebaseAllUsersDatabaseRef; //reference to all the user's in the database
    private StorageReference mStorageRef;

    private ProgressDialog mProgressDialog;
    private Uri mImageUri = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        mAuth = FirebaseAuth.getInstance();
        mFirebaseAllUsersDatabaseRef = FirebaseDatabase.getInstance().getReference().child("UserData");
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
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {

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

                                // url of the picture stored on google
                                Uri downloadUrl = taskSnapshot.getDownloadUrl();

                                String user_id = mAuth.getCurrentUser().getUid(); //unique id of registered user

                                //new child node with user's unique id as key
                                DatabaseReference currentUserDb = mFirebaseAllUsersDatabaseRef.child(user_id);
                                currentUserDb.child("name").setValue(name);
                                currentUserDb.child("email").setValue(email);
                                currentUserDb.child("pictureUrl").setValue(downloadUrl.toString());
                                currentUserDb.child("online").setValue(true);
                                currentUserDb.child("statusMsg").setValue("Hi I'm " + name + "!");

                                // map of all our friends. all users have LOC8 as a friend
                                currentUserDb.child("friends").child(LOC8_DEFAULT_FRIEND_ID).setValue("LOC8");

                                // hardcoded location values to new york
                                currentUserDb.child("latitude").setValue("40.730610");
                                currentUserDb.child("longitude").setValue("-73.935242");

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




























/*
package com.monsoonblessing.kevinfaust.loc8personal;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegisterActivity extends AppCompatActivity {


    private static final String TAG = "RegisterActivity";
    private static final int GALLERY_REQUEST = 100;
    private static final int GALLERY_PERMISSION_CODE = 200;

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

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseRef;
    private StorageReference mStorageRef;

    private ProgressDialog mProgressDialog;
    private Uri mImageUri = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        mAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mProgressDialog = new ProgressDialog(this);
    }

    @OnClick(R.id.register_btn)
    void OnRegister() {
        startRegister();
    }


    @OnClick(R.id.upload_pic_btn)
    void OnImageSelect() {
        int hasGalleryPermission = ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE);


            launchGallery();

    }

    private void launchGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image*/
/*");
        startActivityForResult(galleryIntent, GALLERY_REQUEST);
    }

    private void startRegister() {
        final String name = nameField.getText().toString().trim();
        final String email = emailField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();
        String confirm_password = confirmPasswordField.getText().toString().trim();

        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(name) && password.length() > 5 && password.equals(confirm_password) && mImageUri != null) {

            mProgressDialog.setMessage("Setting your account up");
            mProgressDialog.show();

            // log user in
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {

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

                                Uri downloadUrl = taskSnapshot.getDownloadUrl();

                                String user_id = mAuth.getCurrentUser().getUid(); //unique id of registered user

                                //new child node with user's unique id as key
                                DatabaseReference currentUserDb = mDatabaseRef.child("UserData").child(user_id);
                                currentUserDb.child("name").setValue(name);
                                currentUserDb.child("email").setValue(email);
                                currentUserDb.child("pictureUrl").setValue(downloadUrl.toString());
                                currentUserDb.child("online").setValue(true);
                                currentUserDb.child("statusMsg").setValue("Hi I'm " + name + "!");

                                // list of all our friends. master account is default friend
                                */
/*Map<String, String> friends = new HashMap<>();
                                friends.add("master@master.com");*//*

                                currentUserDb.child("friends").push().setValue("master@master.com");

                                // hardcoded location values to yale
                                currentUserDb.child("latitude").setValue("43.130026");
                                currentUserDb.child("longitude").setValue("-82.798263");

                                mProgressDialog.dismiss();
                                // user logged in
                                Intent loginIntent = new Intent(RegisterActivity.this, MainActivity.class);
                                startActivity(loginIntent);
                                finish();
                            }


                        });
                    } else {
                        Toast.makeText(RegisterActivity.this, "Error signing up", Toast.LENGTH_LONG).show();
                        mProgressDialog.dismiss();
                    }
                    ;

                }
            });


        } else {
            Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_LONG).show();
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {

            // get the uri of the chosen picture
            Uri selectPicUri = data.getData();

            // crop the selected picture
            CropImage.activity(selectPicUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(this);
        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {
                Uri croppedImgUri = result.getUri();

                // update profile pic and uri variable to the cropped image
                mImageUri = croppedImgUri;
                uploadPic.setImageURI(croppedImgUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case GALLERY_PERMISSION_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    launchGallery();

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "Please enable gallery permission", Toast.LENGTH_LONG).show();
                }
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private void requestGalleryPermissions() {

        // Should we show an explanation?
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE)) {

            // Show an explanation to the user *asynchronously* -- don't block
            // this thread waiting for the user's response! After the user
            // sees the explanation, try again to request the permission.

            new AlertDialog.Builder(this)
                    .setMessage("Gallery requires READ_EXTERNAL_STORAGE permission")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(RegisterActivity.this,
                                    new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                                    GALLERY_PERMISSION_CODE);
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .create()
                    .show();

        } else {

            // No explanation needed, we can request the permission.

            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                    GALLERY_PERMISSION_CODE);

            // GALLERY_PERMISSION_CODE is an
            // app-defined int constant. The callback method gets the
            // result of the request.
        }

    }


}*/
