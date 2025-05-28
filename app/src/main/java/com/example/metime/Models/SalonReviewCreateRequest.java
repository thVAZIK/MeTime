package com.example.metime.Models;

public class SalonReviewCreateRequest {
    private int salon_id;
    private String user_id;
    private int rating;
    private String comment;

    public SalonReviewCreateRequest(int salonId, String userId, int rating, String comment) {
        this.salon_id = salonId;
        this.user_id = userId;
        this.rating = rating;
        this.comment = comment;
    }

    public int getSalon_id() {
        return salon_id;
    }

    public void setSalon_id(int salon_id) {
        this.salon_id = salon_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
