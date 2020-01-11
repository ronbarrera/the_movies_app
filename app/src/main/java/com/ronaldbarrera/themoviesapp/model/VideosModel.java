package com.ronaldbarrera.themoviesapp.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class VideosModel {
    @SerializedName("results")
    List<Video> videos;

    public List<Video> getVideos() {
        return videos;
    }

    public void setVideos(List<Video> videos) {
        this.videos = videos;
    }
}
