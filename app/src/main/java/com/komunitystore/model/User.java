package com.komunitystore.model;


import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class User extends BaseResponse implements Serializable {

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'+0200'");

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
    private String created;

    @SerializedName("updated")
    private String updated;

    @SerializedName("deals")
    private List<Deal> deals;

    @SerializedName("nb_deals")
    private int nb_deals;

    @SerializedName("nb_subscribes")
    private int nb_subscribes;

    @SerializedName("nb_followers")
    private int nb_followers;

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
}
