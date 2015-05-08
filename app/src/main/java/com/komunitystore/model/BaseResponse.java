package com.komunitystore.model;

import com.google.gson.annotations.SerializedName;

public abstract class BaseResponse {

    @SerializedName("error")
    private String error;

    @SerializedName("error_description")
    private String error_description;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getError_description() {
        return error_description;
    }

    public void setError_description(String error_description) {
        this.error_description = error_description;
    }
}