package com.ronaldbarrera.themoviesapp.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ReviewsModel {
    @SerializedName("results")
    List<Review>  reviews;

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }
}
