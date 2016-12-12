package com.monsoonblessing.kevinfaust.loc8personal;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

/**
 * Created by Kevin Faust on 11/20/2016.
 */

public class GoogleMapHelper {

    private static final String TAG = "GoogleMapHelper";

    private Context context;
    private ImageLoader imageLoader;

    public GoogleMapHelper(Context context) {
        this.context = context;
       /* imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(context));*/
    }

    /***********************************************************************************************
     * MARKER METHODS
     **********************************************************************************************/

    /*
    * Creates a basic marker
    */
    public MarkerOptions setUpMarker(FirebaseUser firebaseUser, String name) {

        Log.d(TAG, "Creating new marker");

        final LatLng coor = new LatLng(firebaseUser.getDoubleLatitude(), firebaseUser.getDoubleLongitude());

        return new MarkerOptions()
                .position(coor)
                .title(name)
                .snippet("Status: " + firebaseUser.getStatusMsg());
    }

    // make your own marker say "Me" as marker name
    public MarkerOptions setUpUserMarker(FirebaseUser firebaseUser) {
        return setUpMarker(firebaseUser, "Me");
    }

    public MarkerOptions setUpFriendMarker(FirebaseUser firebaseUser) {
        return setUpMarker(firebaseUser, firebaseUser.getName());
    }



    public void removeFriendMarker(FirebaseUser friend, User currentUser) {

        // get this friend's marker
        Marker friendMarker = currentUser.getOnlineFriendMarker(friend.getId());

        // remove friend from map if marker already exists
        removeMarkerIfExist(friendMarker);

        // remove friend from our list of friend markers which we keep track of
        currentUser.removeOnlineFriendMarker(friend.getId());

    }


/*    *//*
Creates and places user marker on map
 *//*
    public void addUserMarker(final GoogleMap mappy, User currentUser) {

        Marker userMarker = currentUser.getCurrentUserMarker();
        Log.d(TAG, "Current user marker: " + userMarker);

        // remove user from map if marker already exists
        removeMarkerIfExist(userMarker);

        // create new marker options
        final MarkerOptions o = setUpUserMarker(currentUser);

        // load friend's profile pic as marker on map
        imageLoader.loadImage(currentUser.getPictureUrl(), new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

                Bitmap resizedBitmap = Bitmap.createScaledBitmap(loadedImage, 150, 150, false);

                o.icon(BitmapDescriptorFactory.fromBitmap(resizedBitmap));

                Marker m = mappy.addMarker(o);

                Log.d(TAG, "Setting current user marker: " + m);
                Log.d(TAG, "New user marker has following status msg: " + m.getSnippet());
                // save current user's marker for later deletion/updation
                currentUser.setCurrentUserMarker(m);

            }
        });

    }

    *//*
    Creates and places a friend marker on map
     *//*
    public void addFriendMarker(final GoogleMap mappy, final FirebaseUser friend, final User currentUser) {

        Marker friendMarker = currentUser.getOnlineFriendMarker(friend.getId());

        // remove friend from map if marker already exists
        removeMarkerIfExist(friendMarker);

        final MarkerOptions o = setUpFriendMarker(friend);

        // load friend's profile pic as marker on map
        imageLoader.loadImage(friend.getPictureUrl(), new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

                Bitmap resizedBitmap = Bitmap.createScaledBitmap(loadedImage, 150, 150, false);

                o.icon(BitmapDescriptorFactory.fromBitmap(resizedBitmap));

                Marker m = mappy.addMarker(o);
                currentUser.addOnlineFriendMarker(friend.getId(), m);

            }
        });

    }*/

    public void removeMarkerIfExist(Marker m) {
        // remove a marker from the map if marker already exists
        if (m != null) {
            Log.d(TAG, "Removing marker");
            m.remove();
        }
    }

}
