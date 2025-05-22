package com.example.metime.Models;

public class ProfileUpdate {
    private String username;
    private String avatar_url;
    private String email;

    public ProfileUpdate(String username, String avatar_url, String email) {
        this.username = username;
        this.avatar_url = avatar_url;
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAvatar_url() {
        return avatar_url;
    }

    public void setAvatar_url(String avatar_url) {
        this.avatar_url = avatar_url;
    }
}
