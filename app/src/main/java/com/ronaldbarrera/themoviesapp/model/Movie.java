package com.ronaldbarrera.themoviesapp.model;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.recyclerview.widget.DiffUtil;

import com.google.gson.annotations.SerializedName;
import com.ronaldbarrera.themoviesapp.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Movie {

    @SerializedName("poster_path")
    private String poster_path;

    @SerializedName("id")
    private int id;

    @SerializedName("backdrop_path")
    private String backdrop_path;

    @SerializedName("original_title")
    private String original_title;

    @SerializedName("title")
    private String title;

    @SerializedName("vote_average")
    private float vote_average;

    @SerializedName("overview")
    private String overview;

    @SerializedName("release_date")
    private String release_date;

    public Movie(String poster_path, int id, String backdrop_path, String original_title, String title, float vote_average, String overview, String release_date ) {
        this.poster_path = poster_path;
        this.id = id;
        this.backdrop_path = backdrop_path;
        this.original_title = original_title;
        this.title = title;
        this.vote_average = vote_average;
        this.overview = overview;
        this.release_date = release_date;
    }

    public Movie() {
    }


    public String getPoster_path() {
        return poster_path;
    }

    public void setPoster_path(String poster_path) {
        this.poster_path = poster_path;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBackdrop_path() {
        return backdrop_path;
    }

    public void setBackdrop_path(String backdrop_path) {
        this.backdrop_path = backdrop_path;
    }

    public String getOriginal_title() {
        return original_title;
    }

    public void setOriginal_title(String original_title) {
        this.original_title = original_title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    //public String getVote_average() { return String.valueOf((int) (vote_average * 10)) ; }
    public float getVote_average() {
        return vote_average;
    }

    public void setVote_average(float vote_average) {
        this.vote_average = vote_average;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }



}
