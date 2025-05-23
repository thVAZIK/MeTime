package com.example.metime.Models;

import com.google.gson.JsonElement;

public class VerifyRequest {
    private String email;
    private String token;
    private String type;

    public VerifyRequest(String email, String token, String type) {
        this.email = email;
        this.token = token;
        this.type = type;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
