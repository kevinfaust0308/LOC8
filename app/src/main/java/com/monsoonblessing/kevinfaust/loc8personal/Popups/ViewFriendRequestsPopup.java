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
import android.widget.ImageButton;
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

    // need this when adding a new friend. friends are stored in database as -> id : email
    private String mCurrentUserEmail;

    public interface FriendRequests {
        void onAddNewFriend(String friend_id);
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.view_friend_requests_popup, null);
        ButterKnife.bind(this, v);
        mCurrentUserEmail = getArguments().getString("UserEmail");

        mFirebaseDatabaseLoggedInReferences = new FirebaseDatabaseLoggedInReferences();

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, true));


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(v);
        builder.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
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

                // get friend requester's email and display it
                viewHolder.setEmail(model.getEmail());

                // on accept, add the friend request
                viewHolder.acceptRequest.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mFirebaseDatabaseLoggedInReferences.addFriend(model.getId(), model.getEmail(), mCurrentUserEmail);

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
        ImageButton acceptRequest;
        @BindView(R.id.deleteRequest)
        ImageButton deleteRequest;


        public FriendRequestViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            email = (TextView) itemView.findViewById(R.id.email);
        }


        public void setEmail(String email) {
            this.email.setText(email);
        }

    }


}
