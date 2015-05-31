package com.komunitystore.model;


import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class User extends BaseResponse implements Serializable {

    @SerializedName("id")
    private int id;

    @SerializedName("username")
    private String username;

    @SerializedName("email")
    private String email;

    @SerializedName("enabled")
    private Boolean enabled;

    @SerializedName("locked")
    private Boolean locked;

    @SerializedName("nb_comments")
    private Integer nb_comments;

    @SerializedName("created")
    private Date created;

    @SerializedName("updated")
    private Date updated;

    @SerializedName("deals")
    private List<Deal> deals;

    @SerializedName("nb_deals")
    private int nb_deals;

    @SerializedName("nb_subscribes")
    private int nb_subscribes;

    @SerializedName("nb_followers")
    private int nb_followers;

    @SerializedName("already_follow")
    private boolean followed;

    @SerializedName("media_profile")
    private Media media_profile;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Boolean getLocked() {
        return locked;
    }

    public void setLocked(Boolean locked) {
        this.locked = locked;
    }

    public Integer getNb_comments() {
        return nb_comments;
    }

    public void setNb_comments(Integer nb_comments) {
        this.nb_comments = nb_comments;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public List<Deal> getDeals() {
        return deals;
    }

    public void setDeals(List<Deal> deals) {
        this.deals = deals;
    }

    public int getNb_deals() {
        return nb_deals;
    }

    public int getNb_subscribes() {
        return nb_subscribes;
    }

    public int getNb_followers() {
        return nb_followers;
    }

    public boolean isFollowed() {
        return followed;
    }

    public Media getMedia_profile() {
        return media_profile;
    }
}
