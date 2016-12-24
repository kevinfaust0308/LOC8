package com.monsoonblessing.kevinfaust.loc8personal.Popups;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.monsoonblessing.kevinfaust.loc8personal.FirebaseDatabaseLoggedInReferences;
import com.monsoonblessing.kevinfaust.loc8personal.FirebaseDatabaseReferences;
import com.monsoonblessing.kevinfaust.loc8personal.FirebaseUserModel;
import com.monsoonblessing.kevinfaust.loc8personal.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Kevin Faust on 12/8/2016.
 */

public class SearchFriendPopup extends DialogFragment {
    /*
    A popup allowing you to search other users and request to connect (friend requests)
     */

    private static final String TAG = "SearchFriendPopup";

    @BindView(R.id.search_field)
    EditText searchField;
    @BindView(R.id.search_profile_pic_result)
    ImageView profilePicResult;
    @BindView(R.id.search_user_name_result)
    TextView nameResult;
    @BindView(R.id.add_user_btn)
    Button addUserBtn;
    @BindView(R.id.delete_user_btn)
    Button delUserBtn;
    @BindView(R.id.search_popup)
    LinearLayout rootView;

    private FirebaseDatabaseLoggedInReferences mFirebaseDatabaseLoggedInReferences;

    private ValueEventListener mFirebaseFriendEmailsUpdateListener;

    private ArrayList<String> mCurrentFriendEmails;

    private String mSearchResultFriendId;
    private String mSearchResultFriendEmail;


    public interface FriendSearch {
        void onDeleteFriend(String friend_id);
    }


    public SearchFriendPopup newInstance(ArrayList<String> friendEmails) {
        Bundle b = new Bundle();
        ;
        b.putStringArrayList("friendEmails", friendEmails);
        SearchFriendPopup sfp = new SearchFriendPopup();
        sfp.setArguments(b);
        return sfp;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.search_friend_popup, null);
        ButterKnife.bind(this, v);

        //get user's friend list so if friend already exists, disable the add friend button
        mCurrentFriendEmails = getArguments().getStringArrayList("friendEmails");

        mFirebaseDatabaseLoggedInReferences = new FirebaseDatabaseLoggedInReferences();

        // listens to any new friend updates
        // if we send a friend request and user accepts, our friend list emails would not contain the updated user email
        // which would allow us to send a friend request when we are already added
        mFirebaseFriendEmailsUpdateListener = mFirebaseDatabaseLoggedInReferences.getFirebaseCurrentUserDatabaseRef().child(FirebaseDatabaseReferences.FIREBASE_FRIENDS_KEY).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String email = (String) ds.getValue();
                    if (!mCurrentFriendEmails.contains(email)) {
                        mCurrentFriendEmails.add(email);
                    }
                }
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Log.d(TAG, "FirebaseDatabaseReferences friend emails: " + mCurrentFriendEmails);
        Log.d(TAG, "Current user email: " + mFirebaseDatabaseLoggedInReferences.getCurrentUser().getEmail());

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(v);
        builder.setMessage("Friend settings")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // FirebaseDatabaseReferences cancelled the dialog
                        dismiss();
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();

    }


    @Override
    public void onStop() {
        super.onStop();
        mFirebaseDatabaseLoggedInReferences.getFirebaseCurrentUserDatabaseRef().child(FirebaseDatabaseReferences.FIREBASE_FRIENDS_KEY).removeEventListener(mFirebaseFriendEmailsUpdateListener);
    }


    @OnClick(R.id.search_btn)
    void onSearch() {

        // get search email text
        final String searchText = searchField.getText().toString().trim();


        //only do search if search is not empty and search doesn't equal current user's email
        if (!TextUtils.isEmpty(searchText)) {

            // check if their exists an email with that search text
            Query queryRef = mFirebaseDatabaseLoggedInReferences.getFirebaseAllUsersDatabaseRef().orderByChild("email").equalTo(searchText);

            queryRef.addChildEventListener(new ChildEventListener() {

                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    FirebaseUserModel searchedUser = dataSnapshot.getValue(FirebaseUserModel.class);

                    Log.d(TAG, "Profile pic url: " + searchedUser.getPictureUrl());

                    //update screen to show the found user's name and profile pic
                    nameResult.setText(searchedUser.getName());
                    Picasso.with(getActivity())
                            .load(searchedUser.getPictureUrl())
                            .placeholder(R.drawable.placeholder)
                            .into(profilePicResult);

                    addUserBtn.setVisibility(View.VISIBLE);
                    delUserBtn.setVisibility(View.VISIBLE);

                    //if user is already in user's friend list, allow delete option
                    if (mCurrentFriendEmails.contains(searchedUser.getEmail())) {
                        showDeleteButton();
                    }
                    //if you search yourself, disable add
                    else if (mFirebaseDatabaseLoggedInReferences.getCurrentUser().getEmail().equals(searchText)) {
                        showAddButton();
                        addUserBtn.setText("You");
                        addUserBtn.setEnabled(false);
                    }
                    // brand new user. let user add this person
                    else {
                        showAddButton();
                        addUserBtn.setText("Add");
                        addUserBtn.setEnabled(true);
                    }

                    // keep hold on user's id+username for adding later to user's friends list
                    mSearchResultFriendId = searchedUser.getId();
                    // keep hold on user's email so that if we delete the user, we can remove from our email list
                    mSearchResultFriendEmail = searchedUser.getEmail();

                    Log.d(TAG, "Searched friend's ID: " + mSearchResultFriendId);

                    // todo: storing more things into friend request = can more easily distinguish ppl when accepting friend requests
                }


                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }


                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }


                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }


                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        } else {
            Snackbar.make(rootView, "Search field empty", Snackbar.LENGTH_SHORT).show();
        }
    }


    @OnClick(R.id.add_user_btn)
    void onAdd() {

        Log.d(TAG, "Sending request to following friend id: " + mSearchResultFriendId);
        mFirebaseDatabaseLoggedInReferences.sendFriendRequest(mSearchResultFriendId);

        // the friend has been sent a request so disable the add button now
        addUserBtn.setEnabled(false);

    }

    @OnClick(R.id.delete_user_btn) void onDelete() {
        Log.d(TAG, "Deleting friend with following friend id: " + mSearchResultFriendId);
        mFirebaseDatabaseLoggedInReferences.removeFriend(mSearchResultFriendId);

        // friend is deleted so change delete button to add button
        showAddButton();

        // remove friend from our friend list emails
        mCurrentFriendEmails.remove(mSearchResultFriendEmail);

        // remove friend from map and listview
        // tell main activity to hook remove event listener/marker configuration for this deleted friend
        ((FriendSearch) getActivity()).onDeleteFriend(mSearchResultFriendId);
    }

    public void showAddButton() {
        addUserBtn.setVisibility(View.VISIBLE);
        delUserBtn.setVisibility(View.GONE);
    }

    public void showDeleteButton() {
        addUserBtn.setVisibility(View.GONE);
        delUserBtn.setVisibility(View.VISIBLE);
    }



}
