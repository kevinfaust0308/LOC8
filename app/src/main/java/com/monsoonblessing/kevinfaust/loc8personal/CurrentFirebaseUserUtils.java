/*
package com.monsoonblessing.kevinfaust.loc8personal;

import com.monsoonblessing.kevinfaust.loc8personal.Activities.FirebaseDatabaseReferences;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

*/
/**
 * Created by Kevin on 2016-12-11.
 *//*


public class CurrentFirebaseUserUtils {
    */
/*
    Updates different properties of the current user in the database
     *//*


    private String mCurrentUserID;
    //private String mCurrentUserEmail;

    private DatabaseReference mFirebaseAllFriendRequestsDatabaseRef; // reference to all friend requests
    private DatabaseReference mFirebaseCurrentUserFriendRequestsDatabaseRef; // reference to current user's friend requests
    private DatabaseReference mFirebaseAllUsersDatabaseRef; //reference to all the user's in the database
    private DatabaseReference mFirebaseCurrentUserDatabaseRef; //reference to user's database node


    public CurrentFirebaseUserUtils(String currentUserID) {
        mCurrentUserID = currentUserID;
        //mCurrentUserEmail = currentUserEmail;
        DatabaseReference root = FirebaseDatabase.getInstance().getReference();

        mFirebaseAllFriendRequestsDatabaseRef = root.child(FirebaseDatabaseReferences.FIREBASE_FRIEND_REQUESTS_KEY);
        mFirebaseCurrentUserFriendRequestsDatabaseRef = mFirebaseAllFriendRequestsDatabaseRef.child(currentUserID);
        mFirebaseAllUsersDatabaseRef = root.child(FirebaseDatabaseReferences.FIREBASE_USER_DATA_KEY);
        mFirebaseCurrentUserDatabaseRef = mFirebaseAllUsersDatabaseRef.child(currentUserID);

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
        mFirebaseCurrentUserDatabaseRef.child(com.monsoonblessing.kevinfaust.loc8personal.Activities.FirebaseDatabaseReferences.FIREBASE_FRIENDS_KEY).child(friend_id).setValue(friend_email);
    }

    public void removeFriend(String friend_id) {
        mFirebaseCurrentUserDatabaseRef.child(FirebaseDatabaseReferences.FIREBASE_FRIENDS_KEY).child(friend_id).removeValue();
    }

    public void sendFriendRequest(String friend_id, User currUser) {
        DatabaseReference sendeesFriendRequests = mFirebaseAllFriendRequestsDatabaseRef.child(friend_id);
        sendeesFriendRequests = sendeesFriendRequests.child(mCurrentUserID);
        sendeesFriendRequests.child("name").setValue(currUser.getName());
        sendeesFriendRequests.child("email").setValue(currUser.getEmail());
        sendeesFriendRequests.child("id").setValue(currUser.getId());
        sendeesFriendRequests.child("pictureUrl").setValue(currUser.getPictureUrl());
    }

    public void deleteFriendRequest(String friend_id) {
        mFirebaseCurrentUserFriendRequestsDatabaseRef.child(friend_id).removeValue();
    }

}
*/
