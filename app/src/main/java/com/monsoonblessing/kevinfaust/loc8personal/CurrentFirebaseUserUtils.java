package com.monsoonblessing.kevinfaust.loc8personal;

import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Kevin on 2016-12-11.
 */

public class CurrentFirebaseUserUtils {
    /*
    Updates different properties of the current user in the database
     */

    private DatabaseReference mFirebaseCurrentUserDatabaseRef; //reference to user's database node


    public CurrentFirebaseUserUtils(String currentUserID) {
        mFirebaseCurrentUserDatabaseRef = FirebaseDatabase.getInstance().getReference().child(FirebaseUser.FIREBASE_USER_DATA_KEY).child(currentUserID);
        ;
    }


    public void makeOffline() {
        mFirebaseCurrentUserDatabaseRef.child(FirebaseUser.FIREBASE_ONLINE_KEY).setValue(false);
    }


    public void makeOnline() {
        mFirebaseCurrentUserDatabaseRef.child(FirebaseUser.FIREBASE_ONLINE_KEY).setValue(true);
    }


    public void updateLocation(String longitude, String latitude) {
        mFirebaseCurrentUserDatabaseRef.child(FirebaseUser.FIREBASE_LONGITUDE_KEY).setValue(longitude);
        mFirebaseCurrentUserDatabaseRef.child(FirebaseUser.FIREBASE_LATITUDE_KEY).setValue(latitude);
    }


    public void updateStatus(String message) {
        mFirebaseCurrentUserDatabaseRef.child(FirebaseUser.FIREBASE_STATUS_MSG_KEY).setValue(message);
    }


    public void updatePicture(String url) {
        mFirebaseCurrentUserDatabaseRef.child(FirebaseUser.FIREBASE_PICTURE_URL_KEY).setValue(url);
    }

}
