package com.polamokh.homeinternetreview.data;

public class Review {
    private String company;
    private double rating;
    private String governorate;
    private String description;
    private long time;
    private String userId;

    public Review() {
    }

    public Review(String company, double rating, String governorate, String description, long time, String userId) {
        this.company = company;
        this.rating = rating;
        this.governorate = governorate;
        this.description = description;
        this.time = time;
        this.userId = userId;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getGovernorate() {
        return governorate;
    }

    public void setGovernorate(String governorate) {
        this.governorate = governorate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
