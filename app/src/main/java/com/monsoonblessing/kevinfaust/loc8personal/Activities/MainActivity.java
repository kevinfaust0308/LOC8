package com.monsoonblessing.kevinfaust.loc8personal.Activities;

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
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.fitness.data.Value;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
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
import com.google.firebase.database.ValueEventListener;
import com.monsoonblessing.kevinfaust.loc8personal.ConnectivityChangeReceiver;
import com.monsoonblessing.kevinfaust.loc8personal.FirebaseDatabaseLoggedInReferences;
import com.monsoonblessing.kevinfaust.loc8personal.FirebaseUserModel;
import com.monsoonblessing.kevinfaust.loc8personal.GoogleMapHelper;
import com.monsoonblessing.kevinfaust.loc8personal.InternetConnectivityUtils;
import com.monsoonblessing.kevinfaust.loc8personal.MetricUtils;
import com.monsoonblessing.kevinfaust.loc8personal.OnlineFriendsListViewAdapter;
import com.monsoonblessing.kevinfaust.loc8personal.Popups.HelpPopup;
import com.monsoonblessing.kevinfaust.loc8personal.Popups.InternetRequiredPopup;
import com.monsoonblessing.kevinfaust.loc8personal.Popups.SearchFriendPopup;
import com.monsoonblessing.kevinfaust.loc8personal.Popups.ViewFriendRequestsPopup;
import com.monsoonblessing.kevinfaust.loc8personal.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Kevin on 2016-12-11.
 */


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, ViewFriendRequestsPopup.FriendRequests, SearchFriendPopup.FriendSearch {

    private static final String TAG = "MainActivity";
    private static final int DEFAULT_MAP_ZOOM = 13;

    // VIEWS
    @BindView(R.id.statusMessage)
    EditText statusMessage;
    @BindView(R.id.statusAvailability)
    ImageButton statusAvailability;
    @BindView(R.id.slide_menu)
    LinearLayout slideMenu;
    @BindView(R.id.slide_menu_toggle_btn)
    ImageView slideMenuToggleBtn;
    @BindView(R.id.friendsListView)
    ListView friendListView;
    @BindView(R.id.changeStatusAvailability)
    TextView menuItem1;
    @BindView(R.id.search_btn)
    TextView menuItem2;
    @BindView(R.id.friend_req_btn)
    TextView menuItem3;
    @BindView(R.id.logout)
    TextView menuItem4;
    @BindView(R.id.tool_bar)
    Toolbar toolbar;
    //////////////////////////////For the Navigation Drawer//////////////////////
    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.nav_view)
    NavigationView mNavView;
    private ActionBarDrawerToggle mDrawerToggle;
    private MapFragment mMapFragment;

    // BROADCAST RECEIVERS
    private ConnectivityChangeReceiver mConnectivityChangeReceiver;

    // MAP
    private GoogleMap mMap;
    private GoogleMapHelper mGoogleMapHelper;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    // have handle on all the firebase event listeners
    // friend_id : listener
    // when we delete a friend, we want to remove the listener
    Map<String, ValueEventListener> mFriendsValueEventListeners = new HashMap<>();

    // Database reference to all+user's data
    private FirebaseDatabaseLoggedInReferences mLoggedInFirebaseDatabaseRef;

    // LISTEN TO LOGOUT
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    FirebaseUserModel mCurrentUserFirebase;
    private boolean mFirstLoad = true; //so that we only assign the listeners to the friends once

    // will allow us to use images on the google map markers
    private ImageLoader imageLoader;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar); // toolbar set up
        setupNavView(); // nav drawer setup
        mMapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mGoogleMapHelper = new GoogleMapHelper(this);
        buildGoogleApiClient();

        // listview
        friendListView.setAdapter(mGoogleMapHelper.getOnlineFriendsListViewAdapter());
        friendListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Marker m = (Marker) friendListView.getItemAtPosition(position);
                LatLng pos = m.getPosition();

                // goto the friend's location in the map
                mGoogleMapHelper.focusMapCamera(mMap, pos.latitude, pos.longitude, DEFAULT_MAP_ZOOM);

                closeNavigationViewDrawer();
            }
        });


        mLoggedInFirebaseDatabaseRef = new FirebaseDatabaseLoggedInReferences();

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                Log.d(TAG, "mAuthStateListener code being run");

                //if user logs out, redirect them to login page
                if (mLoggedInFirebaseDatabaseRef.getFirebaseAuth().getCurrentUser() == null) {
                    Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(loginIntent);
                    finish();
                }

            }
        };

        slideMenuToggleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleQuickMenuWithAnimation();
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

        mLoggedInFirebaseDatabaseRef.getFirebaseAuth().addAuthStateListener(mAuthStateListener);

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
        mLoggedInFirebaseDatabaseRef.getFirebaseAuth().removeAuthStateListener(mAuthStateListener);
        unregisterReceiver(mConnectivityChangeReceiver);
        mLoggedInFirebaseDatabaseRef.makeOffline();
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
            mLoggedInFirebaseDatabaseRef.updateStatus(status);
        }
    }


    @OnClick(R.id.search_btn)
    void onSearch() {

        // user has to be online to do searches
        if (!InternetConnectivityUtils.isConnectedToInternet(this)) {
            new InternetRequiredPopup().newInstance().show(getSupportFragmentManager(), "Internet Required Popup");
        } else {

            // getFriends() -> friend-id : email
            ArrayList<String> friendEmails = new ArrayList<>(mCurrentUserFirebase.getFriends().values());

            //pass emails to search popup so we know which emails are already friend's
            SearchFriendPopup p = new SearchFriendPopup().newInstance(friendEmails);
            p.show(getSupportFragmentManager(), "SearchFriendPopup");
        }
    }

    @OnClick(R.id.friend_req_btn) void viewFriendReqs() {
        if (!InternetConnectivityUtils.isConnectedToInternet(this)) {
            new InternetRequiredPopup().newInstance().show(getSupportFragmentManager(), "Internet Required Popup");
        } else {
            //pass emails to search popup so we know which emails are already friend's
            ViewFriendRequestsPopup p = new ViewFriendRequestsPopup();
            Bundle b = new Bundle();
            b.putString("UserEmail", mCurrentUserFirebase.getEmail());
            p.setArguments(b);
            p.show(getSupportFragmentManager(), "ViewFriendRequestsPopup");
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
            mLoggedInFirebaseDatabaseRef.makeOffline();
            mLoggedInFirebaseDatabaseRef.getFirebaseAuth().signOut();
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
        mLoggedInFirebaseDatabaseRef.makeOffline();
        statusAvailability.setBackground(ContextCompat.getDrawable(this, R.drawable.red_circle));
    }


    /*
    When user opens app, show them
    */
    public void showUserOnMap() {
        Log.d(TAG, "Make me online");
        mLoggedInFirebaseDatabaseRef.makeOnline();
        statusAvailability.setBackground(ContextCompat.getDrawable(this, R.drawable.green_circle));
    }


    /*
    FirebaseDatabaseReferences visible -> then hide
    FirebaseDatabaseReferences hidden -> then show
    */
    public void toggleUserVisibilityOnMap() {
        // can only toggle from quick menu which will have User object configured
        if (mCurrentUserFirebase.isOnline()) {
            hideUserFromMap();
        } else {
            showUserOnMap();
        }
    }


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
        mLoggedInFirebaseDatabaseRef.getFirebaseCurrentUserDatabaseRef().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mCurrentUserFirebase = dataSnapshot.getValue(FirebaseUserModel.class);

                // update user's status message
                statusMessage.setText(mCurrentUserFirebase.getStatusMsg());

                Log.d(TAG, "Online? " + mCurrentUserFirebase.isOnline());

                // create new user marker/update user marker on mMap
                addUserMarkerToMap(mCurrentUserFirebase);

                // user object fully initialized by this point so we can show the hidden menu whose
                // functionality relies on the user object
                slideMenuToggleBtn.setVisibility(View.VISIBLE);

                // only set up listeners on friends on first load
                if (mFirstLoad) {
                    mFirstLoad = false;
                    Log.d(TAG, "Setting up firebase friends");
                    Log.d(TAG, "User's friends emails: " + mCurrentUserFirebase.getFriends().values());

                    // loop through the unique user id's of of each friend in user's friend list
                    for (String friend_id : mCurrentUserFirebase.getFriends().keySet()) {
                        // add a data change listener to each friend if online
                        addValueEventListenerAndMarkerForFriend(friend_id);
                    }
                    // center camera on user only on first load
                    mGoogleMapHelper.focusMapCamera(
                            googleMap,
                            mCurrentUserFirebase.getDoubleLatitude(),
                            mCurrentUserFirebase.getDoubleLongitude(),
                            DEFAULT_MAP_ZOOM
                    );
                }
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    /*
    Creates and places user marker on map
    */
    public void addUserMarkerToMap(FirebaseUserModel currentUserFirebase) {

        // remove user from map if marker already exists
        mGoogleMapHelper.removeUserMarkerFromMapIfExist();

        // create new marker options
        final MarkerOptions newUserMarkerOptions = mGoogleMapHelper.createNewUserMarkerOptions(currentUserFirebase);

        // load friend's profile pic as marker on map
        imageLoader.loadImage(currentUserFirebase.getPictureUrl(), new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

                Bitmap resizedBitmap = Bitmap.createScaledBitmap(loadedImage, 150, 150, false);

                newUserMarkerOptions.icon(BitmapDescriptorFactory.fromBitmap(resizedBitmap));

                Marker m = mMap.addMarker(newUserMarkerOptions);

                Log.d(TAG, "Setting current user marker: " + m);
                Log.d(TAG, "New user marker has following status msg: " + m.getSnippet());
                // save current user's marker for later deletion/updation
                mGoogleMapHelper.setCurrentUserMarker(m);
            }
        });

    }


    /*
    Creates and places a friend marker on map
     */
    public void addFriendMarkerToMap(final FirebaseUserModel friend) {

        final MarkerOptions newFriendMarkerOptions = mGoogleMapHelper.createNewFriendMarkerOptions(friend);

        // load friend's profile pic as marker on map
        imageLoader.loadImage(friend.getPictureUrl(), new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

                Bitmap resizedBitmap = Bitmap.createScaledBitmap(loadedImage, 150, 150, false);

                newFriendMarkerOptions.icon(BitmapDescriptorFactory.fromBitmap(resizedBitmap));

                Marker m = mMap.addMarker(newFriendMarkerOptions);

                // keep track of this online user marker
                mGoogleMapHelper.addOnlineFriendMarkerToStorage(friend.getId(), m);

                // make this online user appear in listview
                mGoogleMapHelper.addFriendToListView(m);
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
        mLocationRequest.setFastestInterval(30000);
        mLocationRequest.setInterval(30000);
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
        mLoggedInFirebaseDatabaseRef.updateLocation(String.valueOf(location.getLongitude()), String.valueOf(location.getLatitude()));
    }


    /*
    REQUIRES MAP TO BE LOADED PRIOR TO CALLING THIS
     */
    public void addValueEventListenerAndMarkerForFriend(String friend_id) {

        // create new database reference with this current friend's id and
        // add a value event lister to friend to listen for changes
        ValueEventListener listener = mLoggedInFirebaseDatabaseRef.getFirebaseAllUsersDatabaseRef().child(friend_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                FirebaseUserModel updatedFriendData = dataSnapshot.getValue(FirebaseUserModel.class);
                refreshFriendMarker(updatedFriendData);
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mFriendsValueEventListeners.put(friend_id, listener);
    }


    /*
    Called upon a specific friend when their data changes

    Adds or removes a specified friend's marker from the mMap
    Adding: making marker visible on mMap with any new changes to the user object (such as location, status msg, etc)
    Removing: removes marker from mMap
    */
    private void refreshFriendMarker(FirebaseUserModel randomFriend) {

        mGoogleMapHelper.removeFriendMarkerFromMapIfExists(randomFriend.getId());

        // check if friend is online or not
        if (randomFriend.isOnline()) {

            Log.d(TAG, randomFriend.getName() + " is an online friend");
            addFriendMarkerToMap(randomFriend);

        } else {

            Log.d(TAG, randomFriend.getName() + " is an offline friend");

        }
    }


    /*
    When new friend is added, add value event listener to the new friend so that
    new friend can show/hide from mMap
     */
    @Override
    public void onAddNewFriend(String friend_id) {
        Log.d(TAG, "New friend was added. Adding marker");
        addValueEventListenerAndMarkerForFriend(friend_id); //REQUIRES MAP TO BE LOADED PRIOR TO CALLING THIS
    }

    /*
    When friend is deleted, remove value event listener
    */
    @Override
    public void onDeleteFriend(String friend_id) {
        Log.d(TAG, "Friend deleted");

        // get listener of this friend
        ValueEventListener listener = mFriendsValueEventListeners.get(friend_id);
        Log.d(TAG, "Removing following friend listener " + listener.toString());
        // remove value event listener for this friend
        mFriendsValueEventListeners.remove(friend_id);
        // remove listener for firebase
        mLoggedInFirebaseDatabaseRef.getFirebaseAllUsersDatabaseRef().child(friend_id).removeEventListener(listener);

        // // remove friend from map and listview
        mGoogleMapHelper.removeFriendMarkerFromMapIfExists(friend_id);

    }


    /***********************************************************************************************
     * ANIMATIONS
     **********************************************************************************************/

    public void toggleQuickMenuWithAnimation() {
        ObjectAnimator menuAnimX;
        ObjectAnimator menuItem1Anim;
        ObjectAnimator menuItem2Anim;
        ObjectAnimator menuItem3Anim;
        ObjectAnimator menuItem4Anim;

        // if it is hidden position, show menu
        if (slideMenu.getTranslationX() != 0f) {
            menuAnimX = ObjectAnimator.ofFloat(slideMenu, "translationX", 0f);
            menuItem1Anim = ObjectAnimator.ofFloat(menuItem1, "translationX", 0f);
            menuItem2Anim = ObjectAnimator.ofFloat(menuItem2, "translationX", 0f);
            menuItem3Anim = ObjectAnimator.ofFloat(menuItem3, "translationX", 0f);
            menuItem4Anim = ObjectAnimator.ofFloat(menuItem4, "translationX", 0f);
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
            menuItem4Anim = ObjectAnimator.ofFloat(menuItem4, "translationX", MetricUtils.dpToPx(210));
            menuAnimX.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    slideMenuToggleBtn.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.left_arrow));
                }
            });
        }

        menuItem1Anim.setDuration(300);
        menuItem2Anim.setDuration(375);
        menuItem3Anim.setDuration(450);
        menuItem4Anim.setDuration(525);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(menuAnimX, menuItem1Anim, menuItem2Anim, menuItem3Anim, menuItem4Anim);
        animatorSet.start();
    }

    /***********************************************************************************************
     * MENU
     **********************************************************************************************/


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_help:
                HelpPopup h = new HelpPopup();
                h.show(getFragmentManager(), "HelpPopup");
                break;
        }

        return super.onOptionsItemSelected(item);

    }


    /**
     * Navigation View
     * --> name of user is displayed
     * --> Home, Statistics, and Settings options
     */
    private void setupNavView() {
        mDrawerToggle = new ActionBarDrawerToggle(this,
                mDrawerLayout,
                toolbar,
                R.string.drawer_open,
                R.string.drawer_close);
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        ///HEADER VIEW

        // todo: do something dynamic with header view?

        // View v = mNavView.getHeaderView(0); ////////////////////////////////////////// <<<----this thing is duh best
        // TextView headerUsernameText = (TextView) v.findViewById(R.id.nameOfCurrentUser);
        //headerUsernameText.setText();
    }


    public void closeNavigationViewDrawer() {
        mDrawerLayout.closeDrawer(GravityCompat.START); //closes the drawer by setting the gravity to "start" (all the way to the left hidden)
    }
}






