package com.monsoonblessing.kevinfaust.loc8personal;

import java.util.Map;

/**
 * Created by Kevin Faust on 11/12/2016.
 */

public class FirebaseUser {

    // keys in a firebase user object
    public static final String FIREBASE_USER_DATA_KEY = "UserData";
    public static final String FIREBASE_EMAIL_KEY = "email";
    public static final String FIREBASE_FRIENDS_KEY = "friends";
    public static final String FIREBASE_ID_KEY = "id";
    public static final String FIREBASE_LATITUDE_KEY = "latitude";
    public static final String FIREBASE_LONGITUDE_KEY = "longitude";
    public static final String FIREBASE_NAME_KEY = "name";
    public static final String FIREBASE_ONLINE_KEY = "online";
    public static final String FIREBASE_PICTURE_URL_KEY = "pictureUrl";
    public static final String FIREBASE_STATUS_MSG_KEY = "statusMsg";

    private String id, name, email, pictureUrl, latitude, longitude, statusMsg;
    private boolean online;
    private Map<String, String> friends;

    public FirebaseUser() {
    }

    public FirebaseUser(String id, String name, String email, String pictureUrl, String latitude, String longitude, String statusMsg, boolean online, Map<String, String> friends) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.pictureUrl = pictureUrl;
        this.latitude = latitude;
        this.longitude = longitude;
        this.statusMsg = statusMsg;
        this.online = online;
        this.friends = friends;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public Map<String, String> getFriends() {
        return friends;
    }

    public void setFriends(Map<String, String> friends) {
        this.friends = friends;
    }

    public String getStatusMsg() {
        return statusMsg;
    }

    public void setStatusMsg(String statusMsg) {
        this.statusMsg = statusMsg;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public double getDoubleLatitude() {
        return Double.parseDouble(this.latitude);
    }

    public double getDoubleLongitude() {
        return Double.parseDouble(this.longitude);
    }
}
