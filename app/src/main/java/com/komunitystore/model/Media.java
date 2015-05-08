package com.komunitystore.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Media implements Serializable {

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'+0200'");

    @SerializedName("id")
    private int id;

    @SerializedName("user")
    private User user;

    @SerializedName("deal")
    private int deal;

    @SerializedName("user_profile")
    private boolean user_profile;

    @SerializedName("url")
    private String url;

    @SerializedName("created")
    private String created;

    @SerializedName("updated")
    private String updated;

    @SerializedName("thumbnails_url")
    private ThumbnailUrl thumbnails_url;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getDeal() {
        return deal;
    }

    public void setDeal(int deal) {
        this.deal = deal;
    }

    public boolean isUser_profile() {
        return user_profile;
    }

    public void setUser_profile(boolean user_profile) {
        this.user_profile = user_profile;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Date getCreated() throws ParseException {
        return sdf.parse(created);
    }

    public void setCreated(Date created) {
        this.created = sdf.format(created);
    }

    public Date getUpdated() throws ParseException {

        return sdf.parse(updated);
    }

    public void setUpdated(Date updated) {
        this.updated = sdf.format(updated);
    }

    public ThumbnailUrl getThumbnails_url() {
        return thumbnails_url;
    }

    public void setThumbnails_url(ThumbnailUrl thumbnails_url) {
        this.thumbnails_url = thumbnails_url;
    }
}

