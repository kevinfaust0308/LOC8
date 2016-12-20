package com.monsoonblessing.kevinfaust.loc8personal;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Kevin Faust on 12/19/2016.
 */

public class FirebaseDatabaseReferences {

    private static final String TAG = "FirebaseDatabaseReferen";

    public static final String LOC8_DEFAULT_FRIEND_ID = "meQJqhVlJGcqbXEvIi1aJVg4Z5U2";

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
    protected DatabaseReference mFirebaseAllFriendRequestsDatabaseRef; // reference to all user's friend requests

    public FirebaseDatabaseReferences() {

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseRootDatabaseRef = FirebaseDatabase.getInstance().getReference();
        mFirebaseAllUsersDatabaseRef = mFirebaseRootDatabaseRef.child(FIREBASE_USER_DATA_KEY);
        mFirebaseAllFriendRequestsDatabaseRef = mFirebaseRootDatabaseRef.child(FIREBASE_FRIEND_REQUESTS_KEY);

    }

    public FirebaseAuth getFirebaseAuth() {
        return mFirebaseAuth;
    }

    public DatabaseReference getFirebaseRootDatabaseRef() {
        return mFirebaseRootDatabaseRef;
    }

    public DatabaseReference getFirebaseAllUsersDatabaseRef() {
        return mFirebaseAllUsersDatabaseRef;
    }

    public DatabaseReference getFirebaseAllFriendRequestsDatabaseRef() {
        return mFirebaseAllFriendRequestsDatabaseRef;
    }
}
