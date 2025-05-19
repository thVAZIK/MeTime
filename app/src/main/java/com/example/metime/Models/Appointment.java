package com.example.metime.Models;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class Appointment {
    private int appointment_id;
    private String user_id;
    private String master_id;
    private int service_id;
    private int salon_id;
    private Date appointment_time;
    private int status;
    private Date created_at;
    private Object updated_at;
    private Object coupon_id;
    @SerializedName("Masters")
    private Master masters;
    @SerializedName("Services")
    private Service services;

    @SerializedName("Salons")
    private Salon salons;

    public Salon getSalons() {
        return salons;
    }

    public void setSalons(Salon salons) {
        this.salons = salons;
    }

    public Master getMasters() {
        return masters;
    }

    public void setMasters(Master masters) {
        this.masters = masters;
    }

    public Service getServices() {
        return services;
    }

    public void setServices(Service services) {
        this.services = services;
    }

    public int getAppointment_id() {
        return appointment_id;
    }

    public void setAppointment_id(int appointment_id) {
        this.appointment_id = appointment_id;
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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }

    public Object getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(Object updated_at) {
        this.updated_at = updated_at;
    }

    public Object getCoupon_id() {
        return coupon_id;
    }

    public void setCoupon_id(Object coupon_id) {
        this.coupon_id = coupon_id;
    }
}

