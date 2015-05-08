package com.komunitystore.model;


import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ThumbnailUrl implements Serializable {

    @SerializedName("image_deal")
    protected String image_deal;

    @SerializedName("user_profile_tile")
    protected String user_profile_tile;

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
}
