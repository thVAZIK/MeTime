package com.example.metime.Models;

public class ChatCreateRequest {
    private String user_id;

    public ChatCreateRequest(String userId) {
        this.user_id = userId;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}
