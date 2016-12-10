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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.monsoonblessing.kevinfaust.loc8personal.FirebaseUser;
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

    private static final String TAG = "SearchFriendPopup";

    @BindView(R.id.search_field)
    EditText searchField;
    @BindView(R.id.search_profile_pic_result)
    ImageView profilePicResult;
    @BindView(R.id.search_user_name_result)
    TextView nameResult;
    @BindView(R.id.add_user_btn)
    Button addUserBtn;
    @BindView(R.id.search_popup)
    LinearLayout rootView;

    private ArrayList<String> mCurrentFriendEmails;
    private String mCurrentUserEmail;
    private String mCurrentUserId;
    private String mSearchResultFriendEmail;
    private String mSearchResultFriendId;

    private DatabaseReference mFirebaseAllUsersDatabaseRef;
    private DatabaseReference mFirebaseCurrentUserDatabaseRef; //reference to user's database node

    private ProgressDialog progressDialog;

    public interface Search {
        void onAddNewFriend(String friend_id);
    }

    public enum AddDisableReason {
        ALREADY_ADDED("Already Added"),
        ADDING_YOURSELF("You");

        private String mMsg;

        AddDisableReason(String msg) {
            mMsg = msg;
        }

        public String getMsg() {
            return mMsg;
        }
    }

    public SearchFriendPopup newInstance(String currentUserId, String currentUserEmail, ArrayList<String> friendEmails) {
        Bundle b = new Bundle();
        b.putString("currentUserId", currentUserId);
        b.putString("currentUserEmail", currentUserEmail);
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
        mCurrentUserEmail = getArguments().getString("currentUserEmail");
        mCurrentUserId = getArguments().getString("currentUserId");

        mFirebaseAllUsersDatabaseRef = FirebaseDatabase.getInstance().getReference().child("UserData");
        mFirebaseCurrentUserDatabaseRef = mFirebaseAllUsersDatabaseRef.child(mCurrentUserId);
        progressDialog = new ProgressDialog(getActivity());


        Log.d(TAG, "FirebaseUser friend emails: " + mCurrentFriendEmails);
        Log.d(TAG, "Current user email: " + mCurrentUserEmail);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(v);
        builder.setMessage("Add new friend")
                /*.setPositiveButton("Send", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {


                    }
                })*/
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // FirebaseUser cancelled the dialog
                        dismiss();
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();

    }

    @OnClick(R.id.search_btn)
    void onSearch() {

        // get search email text
        final String searchText = searchField.getText().toString().trim();


        //only do search if search is not empty and search doesn't equal current user's email
        if (!TextUtils.isEmpty(searchText)) {


            //TODO: we find user, user details get populated and add button appears but if we search again and no results come back, ui wont update. good enough for demo

            // check if their exists an email with that search text
            Query queryRef = mFirebaseAllUsersDatabaseRef.orderByChild("email").equalTo(searchText);

            queryRef.addChildEventListener(new ChildEventListener() {

                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    FirebaseUser searchedUser = dataSnapshot.getValue(FirebaseUser.class);

                    Log.d(TAG, "Profile pic url: " + searchedUser.getPictureUrl());

                    //update screen to show the found user's name and profile pic
                    nameResult.setText(searchedUser.getName());
                    Picasso.with(getActivity())
                            .load(searchedUser.getPictureUrl())
                            .placeholder(R.drawable.placeholder)
                            .into(profilePicResult);

                    addUserBtn.setVisibility(View.VISIBLE);

                    //if user is already in user's friend list, disable the button
                    if (mCurrentFriendEmails.contains(searchedUser.getEmail())) {
                        disableAddUserButton(AddDisableReason.ALREADY_ADDED);
                    }
                    //if you search yourself, disable add
                    else if (mCurrentUserEmail.equals(searchText)) {
                        disableAddUserButton(AddDisableReason.ADDING_YOURSELF);
                    }
                    // brand new user. let user add this person
                    else {
                        addUserBtn.setText("Add");
                        addUserBtn.setEnabled(true);
                    }

                    //keep hold on user's id+username for adding later to user's friends list
                    mSearchResultFriendId = searchedUser.getId();
                    mSearchResultFriendEmail = searchedUser.getEmail();

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

        //start progress dialog
        progressDialog.setMessage("Adding friend");
        progressDialog.show();


        //add friend to current user's friend list
        mFirebaseCurrentUserDatabaseRef.child("friends")
                // have new friend's ID as key
                .child(mSearchResultFriendId)
                // map that ID to the new friend's email
                .setValue(mSearchResultFriendEmail);

        // add current user to the added friend's friend list
        mFirebaseAllUsersDatabaseRef.child(mSearchResultFriendId).child("friends")
                // add current user's ID to new friend's friend list as key
                .child(mCurrentUserId)
                // make the value the current user's email
                .setValue(mCurrentUserEmail);

        // this friend is added so disable the add button now
        disableAddUserButton();

        // add this newly added friend's email to our list of friend's so that if
        // user searches for this user again right now (no reopening popup), we want the add
        // button to be disabled
        mCurrentFriendEmails.add(mSearchResultFriendEmail);

        // tell main activity to hook on event listener/marker configuration for this newly added friend
        ((Search) getActivity()).onAddNewFriend(mSearchResultFriendId);

        //dismiss progress dialog
        progressDialog.dismiss();

    }


    // disables add button and sets the message of the button to the reason why we disabled it
    public void disableAddUserButton(AddDisableReason reason) {
        addUserBtn.setText(reason.getMsg());
        disableAddUserButton();
    }

    public void disableAddUserButton() {
        addUserBtn.setEnabled(false);
    }

}
