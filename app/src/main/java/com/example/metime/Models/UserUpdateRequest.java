package com.example.metime.Models;

import com.google.gson.JsonElement;

public class UserUpdateRequest {
    private String email;
    private String password;

    public UserUpdateRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
