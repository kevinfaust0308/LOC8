/*
package com.monsoonblessing.kevinfaust.loc8personal.Activities;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.monsoonblessing.kevinfaust.loc8personal.*;
import com.squareup.picasso.Picasso;

public class ViewFriendRequestsActivity extends AppCompatActivity {

    private DatabaseReference mFirebaseAllUsersDatabaseRef;
    private DatabaseReference mFirebaseCurrentUserDatabaseRef;

    private String mUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_friend_requests);

        mUserId = getIntent().getStringExtra(FirebaseUser.FIREBASE_ID_KEY);

        mFirebaseAllUsersDatabaseRef = FirebaseDatabase.getInstance().getReference().child("UserData");
        mFirebaseCurrentUserDatabaseRef = mFirebaseAllUsersDatabaseRef.child(mUserId);
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Blog, BlogViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Blog, BlogViewHolder>(
                Blog.class,
                R.layout.blog_row,
                BlogViewHolder.class,
                mDatabaseReference
        ) {
            @Override
            protected void populateViewHolder(BlogViewHolder viewHolder, Blog model, int position) {
                viewHolder.setTitle(model.getTitle());
                viewHolder.setDesc(model.getDescription());
                viewHolder.setImage(MainActivity.this, model.getImage());
            }
        };

        mBlogList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class BlogViewHolder extends RecyclerView.ViewHolder {

        ImageView post_img;
        TextView post_title;
        TextView post_desc;

        public BlogViewHolder(View itemView) {
            super(itemView);
            post_img = (ImageView) itemView.findViewById(R.id.post_image);
            post_title = (TextView) itemView.findViewById(R.id.post_title);
            post_desc = (TextView) itemView.findViewById(R.id.post_desc);
        }

        public void setTitle(String title) {
            post_title.setText(title);
        }

        public void setDesc(String desc) {
            post_desc.setText(desc);
        }

        public void setImage(Context ctx, String url) {
            Picasso.with(ctx)
                    .load(url)
                    .into(post_img);
        }
    }

}
*/
