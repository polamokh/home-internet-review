package com.polamokh.homeinternetreview.data;

public class DetailedReview extends Review {
    private static final float PROPERTIES_REVIEW_NUM = 4.0f;

    private double speed;
    private double price;
    private double reliability;
    private double customerService;

    public DetailedReview() {
    }

    public DetailedReview(double speed, double price, double reliability, double customerService,
                          String company, String governorate, String description, long time, String userId) {
        super(company, 0.0, governorate, description, time, userId);
        this.speed = speed;
        this.price = price;
        this.reliability = reliability;
        this.customerService = customerService;
        updateOverallRating();
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
        updateOverallRating();
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
        updateOverallRating();
    }

    public double getReliability() {
        return reliability;
    }

    public void setReliability(double reliability) {
        this.reliability = reliability;
        updateOverallRating();
    }

    public double getCustomerService() {
        return customerService;
    }

    public void setCustomerService(double customerService) {
        this.customerService = customerService;
        updateOverallRating();
    }

    private void updateOverallRating() {
        this.setRating((this.customerService +
                this.price +
                this.reliability +
                this.speed) / PROPERTIES_REVIEW_NUM
        );
    }
}
