package com.ronaldbarrera.themoviesapp.model;

import com.google.gson.annotations.SerializedName;
import com.ronaldbarrera.themoviesapp.R;

public class Video {

    private final String YOUTUBE_PREFIX = "https://www.youtube.com/watch?v=";

    @SerializedName("id")
    private String id;

    @SerializedName("key")
    private String key;

    @SerializedName("name")
    private String name;

    @SerializedName("site")
    private String site;

    @SerializedName("type")
    private String type;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getYouTubeUrl() {
        if(key != null && !key.isEmpty())
            return YOUTUBE_PREFIX + key;
        else
            return null;
    }
}
