package com.polamokh.homeinternetreview.data;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.Exclude;

public class User {
    @Exclude
    private String id;

    private String name;
    private String email;
    private String profilePictureUrl;

    public User() {
    }

    public User(FirebaseUser user) {
        this.id = user.getUid();
        this.name = user.getDisplayName();
        this.email = user.getEmail();
        this.profilePictureUrl = null;

        if (user.getPhotoUrl() != null)
            this.profilePictureUrl = user.getPhotoUrl().toString();
    }

    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public User(String name, String email, String profilePictureUrl) {
        this.name = name;
        this.email = email;
        this.profilePictureUrl = profilePictureUrl;
    }

    @Exclude
    public String getId() {
        return id;
    }

    @Exclude
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

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }
}
