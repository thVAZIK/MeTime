package com.example.metime.Models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Salon {
    private int salon_id;
    private String name;
    private String address;
    private String phone;
    private String image_link;
    @SerializedName("Salons_Ratings_Summary")
    private List<SalonsRatingsSummary> salons_Ratings_Summary;

    @SerializedName("Coupons")
    private List<Coupon> coupons;

    public List<Coupon> getCoupons() {
        return coupons;
    }

    public void setCoupons(List<Coupon> coupons) {
        this.coupons = coupons;
    }

    public int getSalon_id() {
        return salon_id;
    }

    public void setSalon_id(int salon_id) {
        this.salon_id = salon_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getImage_link() {
        return image_link;
    }

    public void setImage_link(String image_link) {
        this.image_link = image_link;
    }

    public List<SalonsRatingsSummary> getSalons_Ratings_Summary() {
        return salons_Ratings_Summary;
    }

    public void setSalons_Ratings_Summary(List<SalonsRatingsSummary> salons_Ratings_Summary) {
        this.salons_Ratings_Summary = salons_Ratings_Summary;
    }
}
