package com.komunitystore.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class TypeView implements Serializable {

    @SerializedName("sub_type")
    private String sub_type;

    @SerializedName("infos_view")
    private String infos_view;

    public String getSub_type() {
        return sub_type;
    }

    public void setSub_type(String sub_type) {
        this.sub_type = sub_type;
    }

    public String getInfos_view() {
        return infos_view;
    }

    public void setInfos_view(String infos_view) {
        this.infos_view = infos_view;
    }
}
