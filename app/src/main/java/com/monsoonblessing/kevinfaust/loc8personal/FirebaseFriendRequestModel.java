package com.monsoonblessing.kevinfaust.loc8personal;

/**
 * Created by Kevin Faust on 12/19/2016.
 */

public class FirebaseFriendRequestModel {
    /*
    Model class of the data stored within a friend request in firebase
     */

    private String id;
    private String email;

    public FirebaseFriendRequestModel() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "FirebaseFriendRequestModel{" +
                "id='" + id + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
