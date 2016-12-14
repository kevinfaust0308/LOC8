package com.monsoonblessing.kevinfaust.loc8personal.Activities;

import android.*;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.monsoonblessing.kevinfaust.loc8personal.Activities.LoginActivity;
import com.monsoonblessing.kevinfaust.loc8personal.ConnectivityChangeReceiver;
import com.monsoonblessing.kevinfaust.loc8personal.CurrentFirebaseUserUtils;
import com.monsoonblessing.kevinfaust.loc8personal.FirebaseUser;
import com.monsoonblessing.kevinfaust.loc8personal.GoogleMapHelper;
import com.monsoonblessing.kevinfaust.loc8personal.InternetConnectivityUtils;
import com.monsoonblessing.kevinfaust.loc8personal.MetricUtils;
import com.monsoonblessing.kevinfaust.loc8personal.Popups.InternetRequiredPopup;
import com.monsoonblessing.kevinfaust.loc8personal.Popups.SearchFriendPopup;
import com.monsoonblessing.kevinfaust.loc8personal.User;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.monsoonblessing.kevinfaust.loc8personal.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Kevin on 2016-12-11.
 */


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, SearchFriendPopup.Search {

    private static final String TAG = "MainActivity";

    // VIEWS
    @BindView(R.id.statusMessage)
    EditText statusMessage;
    @BindView(R.id.statusAvailability)
    ImageButton statusAvailability;
    @BindView(R.id.slide_menu)
    LinearLayout slideMenu;
    @BindView(R.id.slide_menu_toggle_btn)
    ImageView slideMenuToggleBtn;
    @BindView(R.id.changeStatusAvailability)
    TextView menuItem1;
    @BindView(R.id.search_btn)
    TextView menuItem2;
    @BindView(R.id.logout)
    TextView menuItem3;
    private MapFragment mMapFragment;

    // BROADCAST RECEIVERS
    private ConnectivityChangeReceiver mConnectivityChangeReceiver;

    // NON-VIEWS
    private GoogleMap mMap;
    private GoogleMapHelper mGoogleMapHelper;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private FirebaseAuth mAuth;

    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private DatabaseReference mFirebaseRootDatabaseRef; //reference to root database
    private DatabaseReference mFirebaseAllUsersDatabaseRef; //reference to all the user's in the database

    private DatabaseReference mFirebaseCurrentUserDatabaseRef; //reference to user's database node
    private User mCurrentUser; //contains current user object
    private CurrentFirebaseUserUtils mCurrentFirebaseUserUtils; // tools to manage current user's database settings

    private boolean mFirstLoad = true; //so that we only assign the listeners to the friends once
    private ImageLoader imageLoader;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mMapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mGoogleMapHelper = new GoogleMapHelper(this);
        buildGoogleApiClient();

        //database setup
        mAuth = FirebaseAuth.getInstance();
        String user_id = mAuth.getCurrentUser().getUid();
        mFirebaseRootDatabaseRef = FirebaseDatabase.getInstance().getReference();
        mFirebaseAllUsersDatabaseRef = mFirebaseRootDatabaseRef.child("UserData");
        mFirebaseCurrentUserDatabaseRef = mFirebaseAllUsersDatabaseRef.child(user_id);
        mCurrentFirebaseUserUtils = new CurrentFirebaseUserUtils(user_id);

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                Log.d(TAG, "mAuthStateListener code being run");

                //if user logs out, redirect them to login page
                if (firebaseAuth.getCurrentUser() == null) {
                    Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(loginIntent);
                    finish();
                }

            }
        };

        slideMenuToggleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ObjectAnimator menuAnimX;
                ObjectAnimator menuItem1Anim;
                ObjectAnimator menuItem2Anim;
                ObjectAnimator menuItem3Anim;

                // if it is hidden position, show menu
                if (slideMenu.getTranslationX() != 0f) {
                    menuAnimX = ObjectAnimator.ofFloat(slideMenu, "translationX", 0f);
                    menuItem1Anim = ObjectAnimator.ofFloat(menuItem1, "translationX", 0f);
                    menuItem2Anim = ObjectAnimator.ofFloat(menuItem2, "translationX", 0f);
                    menuItem3Anim = ObjectAnimator.ofFloat(menuItem3, "translationX", 0f);
                    menuAnimX.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            slideMenuToggleBtn.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.right_arrow));
                        }
                    });
                } else {
                    menuAnimX = ObjectAnimator.ofFloat(slideMenu, "translationX", MetricUtils.dpToPx(250));
                    menuItem1Anim = ObjectAnimator.ofFloat(menuItem1, "translationX", MetricUtils.dpToPx(210));
                    menuItem2Anim = ObjectAnimator.ofFloat(menuItem2, "translationX", MetricUtils.dpToPx(210));
                    menuItem3Anim = ObjectAnimator.ofFloat(menuItem3, "translationX", MetricUtils.dpToPx(210));
                    menuAnimX.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            slideMenuToggleBtn.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.left_arrow));
                        }
                    });
                }

                menuItem1Anim.setDuration(400);
                menuItem2Anim.setDuration(500);
                menuItem3Anim.setDuration(600);
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playTogether(menuAnimX, menuItem1Anim, menuItem2Anim, menuItem3Anim);
                animatorSet.start();
            }
        });

        imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(this));


        // check if we have internet when we first open app
        if (!InternetConnectivityUtils.isConnectedToInternet(this)) {
            // tell user they need internet to login and continue to app
            new InternetRequiredPopup().newInstance().show(getSupportFragmentManager(), "Internet Required Popup");
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "OnStart");
        mMapFragment.getMapAsync(this);

        mAuth.addAuthStateListener(mAuthStateListener);

        // location stuff
        Log.d(TAG, "Connecting to Google Api Client");
        mGoogleApiClient.connect();

        // animation in broadcast receiver needs activity to be already created (views all set) so i put
        // the initialization here for now
        mConnectivityChangeReceiver = new ConnectivityChangeReceiver();
        // listen to changes in internet
        registerReceiver(
                mConnectivityChangeReceiver,
                new IntentFilter(
                        ConnectivityManager.CONNECTIVITY_ACTION));

        showUserOnMap();

    }


    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "Calling onStop");
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
        mAuth.removeAuthStateListener(mAuthStateListener);
        unregisterReceiver(mConnectivityChangeReceiver);
        mCurrentFirebaseUserUtils.makeOffline();
    }


    @OnClick(R.id.changeStatusAvailability)
    void onStatusChange() {
        // user has to be online to change visibility
        if (!InternetConnectivityUtils.isConnectedToInternet(this)) {
            new InternetRequiredPopup().newInstance().show(getSupportFragmentManager(), "Internet Required Popup");
        } else {
            toggleUserVisibilityOnMap();
        }
    }


    @OnClick(R.id.saveStatusBtn)
    void onStatusSave() {
        // user has to be online to change status
        if (!InternetConnectivityUtils.isConnectedToInternet(this)) {
            new InternetRequiredPopup().newInstance().show(getSupportFragmentManager(), "Internet Required Popup");
        } else {
            String status = statusMessage.getText().toString();
            mCurrentFirebaseUserUtils.updateStatus(status);
        }
    }


    @OnClick(R.id.search_btn)
    void onSearch() {

        // user has to be online to do searches
        if (!InternetConnectivityUtils.isConnectedToInternet(this)) {
            new InternetRequiredPopup().newInstance().show(getSupportFragmentManager(), "Internet Required Popup");
        } else {

            ArrayList<String> friendEmails = new ArrayList<>(mCurrentUser.getFriends().values());

            //pass emails to search popup so we know which emails are already friend's
            SearchFriendPopup p = new SearchFriendPopup().newInstance(mCurrentUser.getId(), mCurrentUser.getEmail(), friendEmails);
            p.show(getSupportFragmentManager(), "SearchFriendPopup");
        }
    }


    /*
    Remove user from mMap as they signout
     */
    @OnClick(R.id.logout)
    void onLogout() {
        // user has to be online to logout
        if (!InternetConnectivityUtils.isConnectedToInternet(this)) {
            new InternetRequiredPopup().newInstance().show(getSupportFragmentManager(), "Internet Required Popup");
        } else {
            mCurrentFirebaseUserUtils.makeOffline();
            mAuth.signOut();
        }
    }


    /***********************************************************************************************
     * USER ON MAP VISIBILITY STATUS
     ***********************************************************************************************/

    /*
    When user signs out or closes app, hide them
     */
    public void hideUserFromMap() {
        Log.d(TAG, "Make me offline");
        mCurrentFirebaseUserUtils.makeOffline();
        statusAvailability.setBackground(ContextCompat.getDrawable(this, R.drawable.red_circle));
    }


    /*
    When user opens app, show them
    */
    public void showUserOnMap() {
        Log.d(TAG, "Make me online");
        mCurrentFirebaseUserUtils.makeOnline();
        statusAvailability.setBackground(ContextCompat.getDrawable(this, R.drawable.green_circle));
    }


    /*
    FirebaseUser visible -> then hide
    FirebaseUser hidden -> then show
    */
    public void toggleUserVisibilityOnMap() {
        // can only toggle from quick menu which will have User object configured
        if (mCurrentUser.isOnline()) {
            hideUserFromMap();
        } else {
            showUserOnMap();
        }
    }


/*    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_logout:
                mAuth.signOut();
                break;
        }

        return super.onOptionsItemSelected(item);
    }*/


    /***********************************************************************************************
     * MAP STUFF
     **********************************************************************************************/

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        Log.d(TAG, "onMapReady called");
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
        }

        // get details from logged in user's database (run whenever values change in this specific node)
        mFirebaseCurrentUserDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                FirebaseUser currentUserFirebase = dataSnapshot.getValue(FirebaseUser.class);

                // if we are loading map for first time, we dont have a user object
                if (mFirstLoad) {
                    mCurrentUser = new User(
                            currentUserFirebase.getId(),
                            currentUserFirebase.getName(),
                            currentUserFirebase.getEmail(),
                            currentUserFirebase.getPictureUrl(),
                            currentUserFirebase.getLatitude(),
                            currentUserFirebase.getLongitude(),
                            currentUserFirebase.getStatusMsg(),
                            currentUserFirebase.isOnline(),
                            currentUserFirebase.getFriends()
                    );
                } else {
                    // update user object with updated data
                    // we do this so we dont reset the important stuff we are keeping track on in the User class
                    mCurrentUser.setPictureUrl(currentUserFirebase.getPictureUrl());
                    mCurrentUser.setLatitude(currentUserFirebase.getLatitude());
                    mCurrentUser.setLongitude(currentUserFirebase.getLongitude());
                    mCurrentUser.setStatusMsg(currentUserFirebase.getStatusMsg());
                    mCurrentUser.setOnline(currentUserFirebase.isOnline());
                    mCurrentUser.setFriends(currentUserFirebase.getFriends());
                }

                // update user's status message
                statusMessage.setText(mCurrentUser.getStatusMsg());

                Log.d(TAG, "Online? " + mCurrentUser.isOnline());

                // create new user marker/update user marker on mMap
                addUserMarker();

                // user object fully initialized by this point so we can show the hidden menu whose
                // functionality relies on the user object
                slideMenuToggleBtn.setVisibility(View.VISIBLE);

                // only set up listeners on friends on first load
                if (mFirstLoad) {
                    mFirstLoad = false;
                    Log.d(TAG, "Setting up firebase friends");
                    Log.d(TAG, "User's friends: " + currentUserFirebase.getFriends().values());

                    // loop through the unique user id's of of each friend in user's friend list
                    for (String friend_id : currentUserFirebase.getFriends().keySet()) {
                        addValueEventListenerAndMarkerForFriend(friend_id);
                    }
                    // center camera on user only on first load
                    focusCamera(googleMap, mCurrentUser.getDoubleLatitude(), mCurrentUser.getDoubleLongitude(), 13);
                }
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    /*
    Adds or removes a specified friend's marker from the mMap
    Adding: making marker visible on mMap with any new changes to the user object (such as location, status msg, etc)
    Removing: removes marker from mMap
     */
    private void refreshFriendMarker(FirebaseUser randomFriend) {

        // check if friend is online or not
        if (randomFriend.isOnline()) {

            Log.d(TAG, randomFriend.getName() + " is an online friend");
            addFriendMarker(randomFriend);

        } else {

            Log.d(TAG, randomFriend.getName() + " is an offline friend");
            mGoogleMapHelper.removeFriendMarker(randomFriend, mCurrentUser);
            mCurrentUser.removeOnlineFriendMarker(randomFriend.getId());
        }
    }


    private void focusCamera(GoogleMap mappy, double latitude, double longitude, int zoom) {
        // focus camera on user location
        mappy.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(latitude, longitude)));
        mappy.animateCamera(CameraUpdateFactory.zoomTo(zoom));
    }


    /*
    Creates and places user marker on map
    */
    public void addUserMarker() {

        Marker userMarker = mCurrentUser.getCurrentUserMarker();
        Log.d(TAG, "Current user marker: " + userMarker);

        // remove user from map if marker already exists
        mGoogleMapHelper.removeMarkerIfExist(userMarker);

        // create new marker options
        final MarkerOptions o = mGoogleMapHelper.setUpUserMarker(mCurrentUser);

        // load friend's profile pic as marker on map
        imageLoader.loadImage(mCurrentUser.getPictureUrl(), new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

                Bitmap resizedBitmap = Bitmap.createScaledBitmap(loadedImage, 150, 150, false);

                o.icon(BitmapDescriptorFactory.fromBitmap(resizedBitmap));

                Marker m = mMap.addMarker(o);

                Log.d(TAG, "Setting current user marker: " + m);
                Log.d(TAG, "New user marker has following status msg: " + m.getSnippet());
                // save current user's marker for later deletion/updation
                mCurrentUser.setCurrentUserMarker(m);

                Log.d(TAG, "CALLING GET CURRENT USER MARKER: " + mCurrentUser.getCurrentUserMarker());

            }
        });

    }


    /*
    Creates and places a friend marker on map
     */
    public void addFriendMarker(final FirebaseUser friend) {

        Marker friendMarker = mCurrentUser.getOnlineFriendMarker(friend.getId());

        // remove friend from map if marker already exists
        mGoogleMapHelper.removeMarkerIfExist(friendMarker);

        final MarkerOptions o = mGoogleMapHelper.setUpFriendMarker(friend);

        // load friend's profile pic as marker on map
        imageLoader.loadImage(friend.getPictureUrl(), new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

                Bitmap resizedBitmap = Bitmap.createScaledBitmap(loadedImage, 150, 150, false);

                o.icon(BitmapDescriptorFactory.fromBitmap(resizedBitmap));

                Marker m = mMap.addMarker(o);
                mCurrentUser.addOnlineFriendMarker(friend.getId(), m);

            }
        });

    }


    private void buildGoogleApiClient() {
        Log.d(TAG, "Building google api client");
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "Connected to google api client");
        mLocationRequest = new LocationRequest();
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        /* google api client is built only when we have the permissions so this check is redundant but
        android doesn't know so i will leave this here */
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }


    @Override
    public void onConnectionSuspended(int i) {
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }


    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "Location updated. Callback run");
        mCurrentFirebaseUserUtils.updateLocation(String.valueOf(location.getLongitude()), String.valueOf(location.getLatitude()));
    }


    /*
    REQUIRES MAP TO BE LOADED PRIOR TO CALLING THIS
     */
    public void addValueEventListenerAndMarkerForFriend(String friend_id) {

        // create new database reference with this current friend's id and
        // add a value event lister to friend to listen for changes
        mFirebaseAllUsersDatabaseRef.child(friend_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                FirebaseUser randomFriend = dataSnapshot.getValue(FirebaseUser.class);
                refreshFriendMarker(randomFriend);
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    /*
    When new friend is added, add value event listener to the new friend so that
    new friend can show/hide from mMap
     */
    @Override
    public void onAddNewFriend(String friend_id) {
        addValueEventListenerAndMarkerForFriend(friend_id); //REQUIRES MAP TO BE LOADED PRIOR TO CALLING THIS
    }
}






