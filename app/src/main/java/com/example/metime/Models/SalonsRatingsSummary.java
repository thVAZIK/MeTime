package com.example.metime.Models;

public class SalonsRatingsSummary {
    private int salon_id;
    private double average_rating;
    private int rating_1_count;
    private int rating_2_count;
    private int rating_3_count;
    private int rating_4_count;
    private int rating_5_count;

    public int getSalon_id() {
        return salon_id;
    }

    public void setSalon_id(int salon_id) {
        this.salon_id = salon_id;
    }

    public double getAverage_rating() {
        return average_rating;
    }

    public void setAverage_rating(double average_rating) {
        this.average_rating = average_rating;
    }

    public int getRating_1_count() {
        return rating_1_count;
    }

    public void setRating_1_count(int rating_1_count) {
        this.rating_1_count = rating_1_count;
    }

    public int getRating_2_count() {
        return rating_2_count;
    }

    public void setRating_2_count(int rating_2_count) {
        this.rating_2_count = rating_2_count;
    }

    public int getRating_3_count() {
        return rating_3_count;
    }

    public void setRating_3_count(int rating_3_count) {
        this.rating_3_count = rating_3_count;
    }

    public int getRating_4_count() {
        return rating_4_count;
    }

    public void setRating_4_count(int rating_4_count) {
        this.rating_4_count = rating_4_count;
    }

    public int getRating_5_count() {
        return rating_5_count;
    }

    public void setRating_5_count(int rating_5_count) {
        this.rating_5_count = rating_5_count;
    }
}
