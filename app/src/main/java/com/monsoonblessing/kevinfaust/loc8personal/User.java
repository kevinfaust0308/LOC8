package com.monsoonblessing.kevinfaust.loc8personal;

import com.google.android.gms.maps.model.Marker;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Kevin on 2016-12-11.
 */


public class User extends FirebaseUserModel {

    private Map<String, Marker> onlineFriendsMarkers = new HashMap<>(); // unique friend id : friend object
    private Marker currentUserMarker = null;

    public User(String id, String name, String email, String pictureUrl, String latitude, String longitude, String statusMsg, boolean online, Map<String, String> friends) {
        super(id, name, email, pictureUrl, latitude, longitude, statusMsg, online, friends);
    }


    public Marker getCurrentUserMarker() {
        return currentUserMarker;
    }


    public void setCurrentUserMarker(Marker currentUserMarker) {
        this.currentUserMarker = currentUserMarker;
    }


    public Marker getOnlineFriendMarker(String id) {
        return onlineFriendsMarkers.get(id);
    }


    public void addOnlineFriendMarker(String id, Marker marker) {
        onlineFriendsMarkers.put(id, marker);
    }


    public void removeOnlineFriendMarker(String id) {
        onlineFriendsMarkers.remove(id);
    }
}


