/*
package com.monsoonblessing.kevinfaust.loc8personal.Activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.monsoonblessing.kevinfaust.loc8personal.CurrentFirebaseUserUtils;
import com.monsoonblessing.kevinfaust.loc8personal.User;

*
 * Created by Kevin Faust on 12/19/2016.



public class FirebaseDatabaseReferences extends AppCompatActivity {

    public static final String FIREBASE_USER_DATA_KEY = "UserData";
    public static final String FIREBASE_FRIEND_REQUESTS_KEY = "FriendRequests";
    public static final String FIREBASE_EMAIL_KEY = "email";
    public static final String FIREBASE_FRIENDS_KEY = "friends";
    public static final String FIREBASE_ID_KEY = "id";
    public static final String FIREBASE_LATITUDE_KEY = "latitude";
    public static final String FIREBASE_LONGITUDE_KEY = "longitude";
    public static final String FIREBASE_NAME_KEY = "name";
    public static final String FIREBASE_ONLINE_KEY = "online";
    public static final String FIREBASE_PICTURE_URL_KEY = "pictureUrl";
    public static final String FIREBASE_STATUS_MSG_KEY = "statusMsg";

    protected FirebaseAuth mFirebaseAuth;

    protected DatabaseReference mFirebaseRootDatabaseRef; //reference to root database
    protected DatabaseReference mFirebaseAllUsersDatabaseRef; //reference to all the user's in the database

    // if user is logged in:
    protected DatabaseReference mFirebaseCurrentUserDatabaseRef; //reference to user's database node
    protected CurrentFirebaseUserUtils mCurrentFirebaseUserUtils; // tools to manage current user's database settings

    protected DatabaseReference mFirebaseFriendRequestsDatabaseRef; // reference to all user's friend requests
    protected DatabaseReference mFirebaseCurrentUserFriendRequestsDatabaseRef;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseRootDatabaseRef = FirebaseDatabase.getInstance().getReference();
        mFirebaseAllUsersDatabaseRef = mFirebaseRootDatabaseRef.child(FIREBASE_USER_DATA_KEY);
        mFirebaseFriendRequestsDatabaseRef = mFirebaseRootDatabaseRef.child(FIREBASE_FRIEND_REQUESTS_KEY);

        // if user is logged in, get reference to their database
        FirebaseUser currentUser = mFirebaseAuth.getCurrentUser();
        if (currentUser != null) {
            String currentUserID = currentUser.getUid();
            mFirebaseCurrentUserDatabaseRef = mFirebaseAllUsersDatabaseRef.child(currentUserID);
            mCurrentFirebaseUserUtils = new CurrentFirebaseUserUtils(currentUserID);
            mFirebaseCurrentUserFriendRequestsDatabaseRef = mFirebaseFriendRequestsDatabaseRef.child(currentUserID);
        } else {
            // we are not inside main app so ignore
        }
    }




    public void makeOffline() {
        mFirebaseCurrentUserDatabaseRef.child(FirebaseDatabaseReferences.FIREBASE_ONLINE_KEY).setValue(false);
    }


    public void makeOnline() {
        mFirebaseCurrentUserDatabaseRef.child(FirebaseDatabaseReferences.FIREBASE_ONLINE_KEY).setValue(true);
    }


    public void updateLocation(String longitude, String latitude) {
        mFirebaseCurrentUserDatabaseRef.child(FirebaseDatabaseReferences.FIREBASE_LONGITUDE_KEY).setValue(longitude);
        mFirebaseCurrentUserDatabaseRef.child(FirebaseDatabaseReferences.FIREBASE_LATITUDE_KEY).setValue(latitude);
    }


    public void updateStatus(String message) {
        mFirebaseCurrentUserDatabaseRef.child(FirebaseDatabaseReferences.FIREBASE_STATUS_MSG_KEY).setValue(message);
    }


    public void updatePicture(String url) {
        mFirebaseCurrentUserDatabaseRef.child(FirebaseDatabaseReferences.FIREBASE_PICTURE_URL_KEY).setValue(url);
    }

    public void addFriend(String friend_id, String friend_email) {
        mFirebaseCurrentUserDatabaseRef.child(FirebaseDatabaseReferences.FIREBASE_FRIENDS_KEY).child(friend_id).setValue(friend_email);
    }

    public void removeFriend(String friend_id) {
        mFirebaseCurrentUserDatabaseRef.child(FirebaseDatabaseReferences.FIREBASE_FRIENDS_KEY).child(friend_id).removeValue();
    }

    public void sendFriendRequest(String friend_id, User currUser) {
        DatabaseReference sendeesFriendRequests = mFirebaseFriendRequestsDatabaseRef.child(friend_id);
        sendeesFriendRequests = sendeesFriendRequests.child(currUser.getId());
        sendeesFriendRequests.child(FIREBASE_NAME_KEY).setValue(currUser.getName());
        sendeesFriendRequests.child(FIREBASE_EMAIL_KEY).setValue(currUser.getEmail());
        sendeesFriendRequests.child(FIREBASE_ID_KEY).setValue(currUser.getId());
        sendeesFriendRequests.child(FIREBASE_PICTURE_URL_KEY).setValue(currUser.getPictureUrl());
    }

    public void deleteFriendRequest(String friend_id) {
        mFirebaseCurrentUserFriendRequestsDatabaseRef.child(friend_id).removeValue();
    }

}
*/
