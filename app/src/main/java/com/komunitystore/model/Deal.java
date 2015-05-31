package com.komunitystore.model;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Deal extends BaseResponse implements Serializable, Comparable<Deal> {

    @SerializedName("id")
    private int id;

    @SerializedName("title")
    private String title;

    @SerializedName("content")
    private String content;

    @SerializedName("type")
    private String type;

    @SerializedName("nb_comments")
    private int nb_comments;

    @SerializedName("nb_users_likes")
    private int nb_users_likes;

    @SerializedName("lat")
    private double lat;

    @SerializedName("lng")
    private double lng;

    @SerializedName("created")
    private Date created;

    @SerializedName("updated")
    private Date updated;

    @SerializedName("user")
    private User user;

    @SerializedName("categories")
    private List<String> categories;

    @SerializedName("type_view")
    private TypeView type_view;

    private String reduction;

    @SerializedName("medias")
    private List<Media> medias;

    @SerializedName("price")
    private Float price;

    @SerializedName("users_like")
    private List<String> users_like;

    @SerializedName("currency")
    private String currency;

    @SerializedName("already_share")
    private boolean shared;

    @SerializedName("already_like")
    private boolean liked;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getNb_comments() {
        return nb_comments;
    }

    public void setNb_comments(int nb_comments) {
        this.nb_comments = nb_comments;
    }

    public int getNb_users_likes() {
        return nb_users_likes;
    }

    public void setNb_users_likes(int nb_users_likes) {
        this.nb_users_likes = nb_users_likes;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    public TypeView getType_view() {
        return type_view;
    }

    public void setType_view(TypeView type_view) {
        this.type_view = type_view;
    }

    public List<Media> getMedias() {
        return medias;
    }

    public void setMedias(List<Media> medias) {
        this.medias = medias;
    }

    public Float getPrice() {
        return price;
    }

    public String getStringPrice() {
        try {
            if (price == 0.0f) {
                return "FREE";
            } else {
                String stringPrice = String.valueOf(price);
                if (stringPrice.contains(".")) {
                    String[] split = stringPrice.split("\\.");
                    if (split[1].equals("0")) {
                        stringPrice = split[0];
                    }
                }
                stringPrice = stringPrice + " " + getCurrency();
                return stringPrice;
            }
        } catch (Exception e) {
            return "No price";
        }

    }

    public boolean hasUserLike(String username) {
        return users_like.contains(username);
    }

    public int getUserLikesCount() {
        return users_like.size();
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public String getCurrency() {
        String curr = "";
        if (currency != null) {
            switch (currency) {
                case "euro":
                    curr = "€";
                    break;
                case "dollar":
                    curr = "$";
                    break;
                default:
                    curr = currency;
                    break;
            }
        }
        return curr;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public boolean isGeoloc() {
        return (getLat() != 0.0 || getLng() != 0.0);
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getDate() {

        Calendar startDateTime = Calendar.getInstance();
        if (updated != null) {
            startDateTime.setTime(updated);
        } else {
            startDateTime.setTime(created);
        }

        Calendar endDateTime = Calendar.getInstance();

        long milliseconds1 = startDateTime.getTimeInMillis();
        long milliseconds2 = endDateTime.getTimeInMillis();
        long diff = milliseconds2 - milliseconds1;

        long hours = diff / (60 * 60 * 1000);
        long days = hours / 24;
        long minutes = diff / (60 * 1000);
        minutes = minutes - 60 * hours;
        long seconds = diff / (1000);

        if (days >= 1) {
            return new SimpleDateFormat("dd MMM").format(startDateTime.getTime());
        } else if (hours >= 1) {
            return hours + " h";
        } else if (minutes >= 1) {
            return minutes + " min";
        } else {
            return seconds + " sec";
        }
    }

    public boolean isShared() {
        return shared;
    }

    public boolean isLiked() {
        return liked;
    }

    @Override
    public String toString() {

        if (super.getError() != null) {
            return "Deal{error='" + super.getError() + "'}";
        }

        return this.getTitle();
    }

    @Override
    public int compareTo(Deal another) {
        return getCreated().after(another.getCreated()) ? -1 : 0;
    }
}