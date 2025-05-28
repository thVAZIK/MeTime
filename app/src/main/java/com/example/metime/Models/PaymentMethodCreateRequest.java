package com.example.metime.Models;

public class PaymentMethodCreateRequest {
    private String user_id;
    private int type;
    private PaymentMethodDetails details;
    private boolean is_default;

    public PaymentMethodCreateRequest(String userId, int type, PaymentMethodDetails details, boolean isDefault) {
        this.user_id = userId;
        this.type = type;
        this.details = details;
        this.is_default = isDefault;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public PaymentMethodDetails getDetails() {
        return details;
    }

    public void setDetails(PaymentMethodDetails details) {
        this.details = details;
    }

    public boolean isIs_default() {
        return is_default;
    }

    public void setIs_default(boolean is_default) {
        this.is_default = is_default;
    }
}

