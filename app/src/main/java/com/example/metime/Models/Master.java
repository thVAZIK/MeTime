package com.example.metime.Models;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

public class Master {
    private int salon_id;
    private String last_name;
    private String master_id;
    private Date created_at;
    private String first_name;
    private String image_link;
    private Date updated_at;
    private int specialization_id;
    @SerializedName("Specializations")
    private Specializations specialization;

    @SerializedName("Master_Ratings_Summary")
    private List<MasterRatingsSummary> masterRatingsSummaries;

    public List<MasterRatingsSummary> getMasterRatingsSummaries() {
        return masterRatingsSummaries;
    }

    public void setMasterRatingsSummaries(List<MasterRatingsSummary> masterRatingsSummaries) {
        this.masterRatingsSummaries = masterRatingsSummaries;
    }

    public Specializations getSpecialization() {
        return specialization;
    }

    public void setSpecialization(Specializations specialization) {
        this.specialization = specialization;
    }

    public int getSalon_id() {
        return salon_id;
    }

    public void setSalon_id(int salon_id) {
        this.salon_id = salon_id;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getMaster_id() {
        return master_id;
    }

    public void setMaster_id(String master_id) {
        this.master_id = master_id;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getImage_link() {
        return image_link;
    }

    public void setImage_link(String image_link) {
        this.image_link = image_link;
    }

    public Date getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(Date updated_at) {
        this.updated_at = updated_at;
    }

    public int getSpecialization_id() {
        return specialization_id;
    }

    public void setSpecialization_id(int specialization_id) {
        this.specialization_id = specialization_id;
    }
}
