package com.monsoonblessing.kevinfaust.loc8personal.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.monsoonblessing.kevinfaust.loc8personal.InternetConnectivityUtils;
import com.monsoonblessing.kevinfaust.loc8personal.PermissionManager;
import com.monsoonblessing.kevinfaust.loc8personal.Popups.InternetRequiredPopup;
import com.monsoonblessing.kevinfaust.loc8personal.Popups.PasswordRequestPopup;
import com.monsoonblessing.kevinfaust.loc8personal.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/*
A login control activity
--> Users without internet will have the app close
--> Users already logged in will go directly to the main app
 */
public class LoginActivity extends AppCompatActivity {


    @BindView(R.id.activity_login)
    LinearLayout rootView;
    @BindView(R.id.email_field)
    EditText emailField;
    @BindView(R.id.password_field)
    EditText passwordField;

    private FirebaseAuth mAuth;

    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        mProgressDialog = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            // user is logged in so send them to main app
            launchMainApp();
        }

    }

    @OnClick(R.id.register_btn)
    void onRegister() {
        startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
    }

    @OnClick(R.id.login_btn)
    void onLogin() {

        // check if we are connected to the internet
        if (InternetConnectivityUtils.isConnectedToInternet(this)) {
            // check permission
            // if we have permission, try to log user in
            if (PermissionManager.hasLocationPermission(this)) {
                startLogin();
            } else {
                // request location permission from user before logging in
                PermissionManager.requestPermission(this, PermissionManager.LOCATION_PERMISSION, PermissionManager.PERMISSION_LOCATION_CODE);
            }
        } else {
            // tell user they need internet to login and continue to app
            new InternetRequiredPopup().newInstance().show(getSupportFragmentManager(), "Internet Required Popup");
        }

    }

    @OnClick(R.id.forgot_pass_btn)
    void onForgot() {
        PasswordRequestPopup d = new PasswordRequestPopup();
        d.show(getSupportFragmentManager(), "PasswordRequestPopup");
    }

    public void startLogin() {
        /*
        Logs user in and goes to main app
         */

        String email = emailField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();

        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {

            mProgressDialog.setMessage("Logging in");
            mProgressDialog.show();

            // log user in
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful()) {

                        // user logged in
                        launchMainApp();

                    } else {
                        Snackbar.make(rootView, "Error logging in", Snackbar.LENGTH_SHORT).show();
                    }

                    mProgressDialog.dismiss();

                }
            });

        } else {
            Snackbar.make(rootView, "Please fill out all fields", Snackbar.LENGTH_SHORT).show();
        }
    }

    private void launchMainApp() {
        /*
        Goes from login screen to main activity
         */
        Intent loginIntent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(loginIntent);
        finish();
    }


    /*
    Callback from requesting location permission
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PermissionManager.PERMISSION_LOCATION_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay!
                    // can now proceed to log user in and go to main app
                    startLogin();


                }
                break;
            }

        }
    }
}
