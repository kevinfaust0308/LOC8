package com.monsoonblessing.kevinfaust.loc8personal;

import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Kevin Faust on 11/20/2016.
 */

public class GoogleMapHelper {

    private static final String TAG = "GoogleMapHelper";

    private Map<String, Marker> mOnlineFriendsMarkersStorage = new HashMap<>(); // unique friend id : friend marker
    private Marker mCurrentUserMarker = null;


    public void setCurrentUserMarker(Marker currentUserMarker) {
        mCurrentUserMarker = currentUserMarker;
    }


    public void addOnlineFriendMarkerToStorage(String friendID, Marker friendMarker) {
        mOnlineFriendsMarkersStorage.put(friendID, friendMarker);
    }


    /*
    CREATING MARKER OPTIONS
     */


    private MarkerOptions createNewMarkerOptions(FirebaseUserModel firebaseUser, String name) {

        Log.d(TAG, "Creating new marker");

        final LatLng coor = new LatLng(firebaseUser.getDoubleLatitude(), firebaseUser.getDoubleLongitude());

        return new MarkerOptions()
                .position(coor)
                .title(name)
                .snippet("Status: " + firebaseUser.getStatusMsg());
    }


    // make your own marker say "Me" as marker name
    public MarkerOptions createNewUserMarkerOptions(FirebaseUserModel firebaseUser) {
        return createNewMarkerOptions(firebaseUser, "Me");
    }


    public MarkerOptions createNewFriendMarkerOptions(FirebaseUserModel firebaseFriendData) {
        return createNewMarkerOptions(firebaseFriendData, firebaseFriendData.getName());
    }

    /*
    REMOVING MARKERS
     */


    public void removeFriendMarkerFromMapIfExists(String friendID) {
        // get this friend's marker by ID
        Marker friendMarker = mOnlineFriendsMarkersStorage.get(friendID);
        // remove marker from our online friends marker storage
        mOnlineFriendsMarkersStorage.remove(friendID);

        // remove friend from map if marker already exists
        removeMarkerFromMapIfExist(friendMarker);
    }


    public void removeUserMarkerFromMapIfExist() {
        removeMarkerFromMapIfExist(mCurrentUserMarker);
    }


    private void removeMarkerFromMapIfExist(Marker m) {
        // remove a marker from the map if marker already exists
        if (m != null) {
            Log.d(TAG, "Removing marker");
            m.remove();
        }
    }

    /*
    MAP CAMERA
     */


    public void focusMapCamera(GoogleMap mappy, double latitude, double longitude, int zoom) {
        // focus camera on user location
        mappy.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(latitude, longitude)));
        mappy.animateCamera(CameraUpdateFactory.zoomTo(zoom));
    }

}
