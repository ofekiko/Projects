package com.mso.pigeonui.model;

import com.google.gson.annotations.SerializedName;

public class AddBlacklistUrlRequest {

    @SerializedName("url")
    private String url;

    public AddBlacklistUrlRequest(String url) {
        this.url = url;
    }
}