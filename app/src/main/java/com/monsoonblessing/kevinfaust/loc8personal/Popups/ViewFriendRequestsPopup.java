package com.monsoonblessing.kevinfaust.loc8personal.Popups;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.monsoonblessing.kevinfaust.loc8personal.FirebaseDatabaseLoggedInReferences;
import com.monsoonblessing.kevinfaust.loc8personal.FirebaseDatabaseReferences;
import com.monsoonblessing.kevinfaust.loc8personal.FirebaseFriendRequestModel;
import com.monsoonblessing.kevinfaust.loc8personal.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Kevin Faust on 12/14/2016.
 */

public class ViewFriendRequestsPopup extends DialogFragment {

    private static final String TAG = "ViewFriendRequestsPopup";

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    private FirebaseRecyclerAdapter<FirebaseFriendRequestModel, FriendRequestViewHolder> mFirebaseRecyclerAdapter;
    private FirebaseDatabaseLoggedInReferences mFirebaseDatabaseLoggedInReferences;

    private ProgressDialog progressDialog;

    public interface FriendRequests {
        void onAddNewFriend(String friend_id);
    }



        /* todo: this is all copy pasted code so clean this up
       todo: we get passed in a friend request map, need some way to get the requesting person's name
       todo: display all friend requests reverse order and have option to accept or delete (recyclerview/listview?)
       todo: CurrentFireUserUtils has friend request deletion and friend adding methods
       todo: if we accept, add the friend and delete the friend request
       todo: if we just delete, remove the friend request
    */

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.view_friend_requests_popup, null);
        ButterKnife.bind(this, v);
        progressDialog = new ProgressDialog(getActivity());

        mFirebaseDatabaseLoggedInReferences = new FirebaseDatabaseLoggedInReferences();

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, true));


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(v);
        builder.setMessage("Add new friend")
                /*.setPositiveButton("Send", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {


                    }
                })*/
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
    public void onStart() {
        super.onStart();

        mFirebaseRecyclerAdapter = new FirebaseRecyclerAdapter<FirebaseFriendRequestModel, FriendRequestViewHolder>(
                FirebaseFriendRequestModel.class,
                R.layout.friend_request_row,
                FriendRequestViewHolder.class,
                mFirebaseDatabaseLoggedInReferences.getFirebaseCurrentUserFriendRequestsDatabaseRef()
        ) {
            @Override
            protected void populateViewHolder(FriendRequestViewHolder viewHolder, final FirebaseFriendRequestModel model, int position) {
                Log.d(TAG, model.toString());

                // omit the default LOC8 friend request by hiding the row
                if (model.getId().equals(FirebaseDatabaseReferences.LOC8_DEFAULT_FRIEND_ID)) {
                    viewHolder.itemView.setVisibility(View.GONE);
                } else {
                    viewHolder.itemView.setVisibility(View.VISIBLE);
                    // get friend requester's email and display it
                    viewHolder.setEmail(model.getEmail());

                    // on accept, add the friend request
                    viewHolder.acceptRequest.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mFirebaseDatabaseLoggedInReferences.addFriend(model.getId(), model.getEmail());

                            // tell main activity to hook on event listener/marker configuration for this newly added friend
                            ((FriendRequests) getActivity()).onAddNewFriend(model.getId());
                        }
                    });

                    // on delete, remove the friend request
                    viewHolder.deleteRequest.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mFirebaseDatabaseLoggedInReferences.deleteFriendRequest(model.getId());
                        }
                    });
                }
            }

        };

        recyclerView.setAdapter(mFirebaseRecyclerAdapter);
    }

    @Override
    public void onStop() {
        super.onStop();
        mFirebaseRecyclerAdapter.cleanup();
    }

    public static class FriendRequestViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.email)
        TextView email;
        @BindView(R.id.acceptRequest)
        Button acceptRequest;
        @BindView(R.id.deleteRequest)
        Button deleteRequest;

        View itemView;

        public FriendRequestViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            email = (TextView) itemView.findViewById(R.id.email);
            this.itemView = itemView;
        }

        public void setEmail(String email) {
            this.email.setText(email);
        }

    }


}
