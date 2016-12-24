package com.monsoonblessing.kevinfaust.loc8personal;

import android.app.ProgressDialog;
import android.util.Log;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Kevin Faust on 12/19/2016.
 */

public class FirebaseDatabaseLoggedInReferences extends FirebaseDatabaseReferences {

    private static final String TAG = "FirebaseDatabaseLoggedI";

    private DatabaseReference mFirebaseCurrentUserDatabaseRef; //reference to user's database node
    private DatabaseReference mFirebaseCurrentUserFriendRequestsDatabaseRef; // reference to current user's friend requests

    private com.google.firebase.auth.FirebaseUser mCurrentUser;


    public FirebaseDatabaseLoggedInReferences() {

        // user should be logged in if we are here
        mCurrentUser = mFirebaseAuth.getCurrentUser();
        mFirebaseCurrentUserDatabaseRef = mFirebaseAllUsersDatabaseRef.child(mCurrentUser.getUid());
        mFirebaseCurrentUserFriendRequestsDatabaseRef = mFirebaseAllFriendRequestsDatabaseRef.child(mCurrentUser.getUid());

    }


    public FirebaseUser getCurrentUser() {
        return mCurrentUser;
    }


    public DatabaseReference getFirebaseCurrentUserDatabaseRef() {
        return mFirebaseCurrentUserDatabaseRef;
    }


    public DatabaseReference getFirebaseCurrentUserFriendRequestsDatabaseRef() {
        return mFirebaseCurrentUserFriendRequestsDatabaseRef;
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


    private DatabaseReference friendNodeWithinUserFriendsDatabaseRef(String friend_id) {
        // in current user's database, go to the friends list
        return mFirebaseCurrentUserDatabaseRef.child(FirebaseDatabaseReferences.FIREBASE_FRIENDS_KEY)
                // add the new friend to the user's friend list with the id and email of the friend
                .child(friend_id);
    }


    private DatabaseReference userNodeWithinFriendFriendsDatabaseRef(String friend_id) {
        // go to the friend's database and go to the friends list
        return mFirebaseAllUsersDatabaseRef.child(friend_id).child(FIREBASE_FRIENDS_KEY)
                // add user to the friend's friend list with id and email of the user
                .child(mCurrentUser.getUid());
    }


    public void addFriend(String friend_id, String friend_email, String current_user_email) {
        /*
        Adding is a two way thing. Both user and friend must appear in each other's friend's list and
        both shouldn't have each other under friend request's anymore
         */

        friendNodeWithinUserFriendsDatabaseRef(friend_id).setValue(friend_email);
        userNodeWithinFriendFriendsDatabaseRef(friend_id).setValue(current_user_email);

        // user has been added so we can remove the friend request
        deleteFriendRequest(friend_id);

        // in case other person sent a friend request to us as well
        // this ensures both users are not in each others friend request list
        if (mFirebaseAllFriendRequestsDatabaseRef.child(friend_id).child(mCurrentUser.getUid()) != null)
            mFirebaseAllFriendRequestsDatabaseRef.child(friend_id).child(mCurrentUser.getUid()).removeValue();
    }


    public void removeFriend(String friend_id) {
        friendNodeWithinUserFriendsDatabaseRef(friend_id).removeValue();
        userNodeWithinFriendFriendsDatabaseRef(friend_id).removeValue();
    }


    public void sendFriendRequest(String friend_id) {
        /*
        User's id and email gets added under friend's friendlist
         */

        // access the friend you want to add -> get their friend requests
        final DatabaseReference friendToAddFriendListDatabaseRef = mFirebaseAllFriendRequestsDatabaseRef.child(friend_id);

        // check if the friend already has the current user's id in the friend request list
        friendToAddFriendListDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // we get all the user id's of friends in the friend list
                // if current user's id already exists there, we already sent a request
                boolean requestAlreadySent = dataSnapshot.hasChild(mCurrentUser.getUid());

                if (!requestAlreadySent) {
                    // add a new friend request with your id and email
                    DatabaseReference newFriendRequest = friendToAddFriendListDatabaseRef.child(mCurrentUser.getUid());

                    // add two fields: name and id
                    // todo: option for name and picture fields

                    newFriendRequest.child(FIREBASE_ID_KEY).setValue(mCurrentUser.getUid());
                    newFriendRequest.child(FIREBASE_EMAIL_KEY).setValue(mCurrentUser.getEmail());
                } else {
                    Log.d(TAG, "Friend request is pending. Did not resend request");
                }
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }


    public void deleteFriendRequest(String friend_id) {
        mFirebaseCurrentUserFriendRequestsDatabaseRef.child(friend_id).removeValue();
    }

}
