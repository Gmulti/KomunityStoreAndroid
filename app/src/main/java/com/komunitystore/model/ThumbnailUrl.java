package com.komunitystore.model;


import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ThumbnailUrl implements Serializable {

    @SerializedName("image_deal")
    protected String image_deal;

    @SerializedName("image_deal_large")
    protected String image_deal_large;

    @SerializedName("user_profile_tile")
    protected String user_profile_tile;

    @SerializedName("user_profile_tile_large")
    protected String user_profile_tile_large;

    public String getImage_deal() {
        return image_deal;
    }

    public void setImage_deal(String image_deal) {
        this.image_deal = image_deal;
    }

    public String getUser_profile_tile() {
        return user_profile_tile;
    }

    public void setUser_profile_tile(String user_profile_tile) {
        this.user_profile_tile = user_profile_tile;
    }

    public String getImage_deal_large() {
        return image_deal_large;
    }

    public void setImage_deal_large(String image_deal_large) {
        this.image_deal_large = image_deal_large;
    }

    public String getUser_profile_tile_large() {
        return user_profile_tile_large;
    }

    public void setUser_profile_tile_large(String user_profile_tile_large) {
        this.user_profile_tile_large = user_profile_tile_large;
    }
}
