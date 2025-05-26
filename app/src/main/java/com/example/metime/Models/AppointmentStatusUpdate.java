package com.example.metime.Models;

import com.google.gson.JsonElement;

public class AppointmentStatusUpdate {
    private int status;

    public AppointmentStatusUpdate(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
