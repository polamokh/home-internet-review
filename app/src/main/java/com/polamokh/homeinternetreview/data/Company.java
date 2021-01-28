package com.polamokh.homeinternetreview.data;

public class Company implements Comparable<Company> {

    private String name;
    private double rating;
    private int numOfRatings;

    public Company() {
    }

    public Company(String name, double rating, int numOfRatings) {
        this.name = name;
        this.rating = rating;
        this.numOfRatings = numOfRatings;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public int getNumOfRatings() {
        return numOfRatings;
    }

    public void setNumOfRatings(int numOfRatings) {
        this.numOfRatings = numOfRatings;
    }

    @Override
    public int compareTo(Company o) {
        return Double.compare(this.rating, o.rating);
    }
}
