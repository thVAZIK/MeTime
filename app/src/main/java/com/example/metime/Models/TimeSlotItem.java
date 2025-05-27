package com.example.metime.Models;

public class TimeSlotItem {
    private String time;
    private String masterName;
    private String masterId;
    private int calendarId;
    private boolean isSelected;

    public TimeSlotItem(String time, String masterName, String masterId, int calendarId) {
        this.time = time;
        this.masterName = masterName;
        this.masterId = masterId;
        this.calendarId = calendarId;
        this.isSelected = false;
    }

    public String getTime() {
        return time;
    }

    public String getMasterName() {
        return masterName;
    }

    public String getMasterId() {
        return masterId;
    }

    public int getCalendarId() {
        return calendarId;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}