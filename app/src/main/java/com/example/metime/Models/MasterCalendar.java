package com.example.metime.Models;

import com.google.gson.annotations.SerializedName;

public class MasterCalendar {
    public int calendar_id;
    public String master_id;
    public String start_time;
    public String end_time;
    public boolean is_available;
    @SerializedName("Masters")
    public Master masters;

    public int getCalendar_id() {
        return calendar_id;
    }

    public void setCalendar_id(int calendar_id) {
        this.calendar_id = calendar_id;
    }

    public String getMaster_id() {
        return master_id;
    }

    public void setMaster_id(String master_id) {
        this.master_id = master_id;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public boolean isIs_available() {
        return is_available;
    }

    public void setIs_available(boolean is_available) {
        this.is_available = is_available;
    }

    public Master getMasters() {
        return masters;
    }

    public void setMasters(Master masters) {
        this.masters = masters;
    }
}
