package com.example.metime.Models;

import java.util.Date;

public class AppointmentInsert {
    private String user_id;
    private String master_id;
    private int service_id;
    private int salon_id;
    private Date appointment_time;
    private String coupon_id;

    public AppointmentInsert(String user_id, String master_id, int service_id, int salon_id, Date appointment_time, String coupon_id) {
        this.user_id = user_id;
        this.master_id = master_id;
        this.service_id = service_id;
        this.salon_id = salon_id;
        this.appointment_time = appointment_time;
        this.coupon_id = coupon_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getMaster_id() {
        return master_id;
    }

    public void setMaster_id(String master_id) {
        this.master_id = master_id;
    }

    public int getService_id() {
        return service_id;
    }

    public void setService_id(int service_id) {
        this.service_id = service_id;
    }

    public int getSalon_id() {
        return salon_id;
    }

    public void setSalon_id(int salon_id) {
        this.salon_id = salon_id;
    }

    public Date getAppointment_time() {
        return appointment_time;
    }

    public void setAppointment_time(Date appointment_time) {
        this.appointment_time = appointment_time;
    }

    public String getCoupon_id() {
        return coupon_id;
    }

    public void setCoupon_id(String coupon_id) {
        this.coupon_id = coupon_id;
    }
}
