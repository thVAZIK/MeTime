package com.example.metime.Models;

public class MessageCreateRequest {
    private int chat_id;
    private String sender_id;
    private String content;

    public MessageCreateRequest(int chatId, String senderId, String content) {
        this.chat_id = chatId;
        this.sender_id = senderId;
        this.content = content;
    }

    public int getChat_id() {
        return chat_id;
    }

    public void setChat_id(int chat_id) {
        this.chat_id = chat_id;
    }

    public String getSender_id() {
        return sender_id;
    }

    public void setSender_id(String sender_id) {
        this.sender_id = sender_id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
