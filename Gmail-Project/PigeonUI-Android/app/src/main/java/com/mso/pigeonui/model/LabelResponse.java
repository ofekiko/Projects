package com.mso.pigeonui.model;

// Import Libraries
import com.google.gson.annotations.SerializedName;

// The class that represent the answer we get from the api server about label
public class LabelResponse {

    // Fields
    @SerializedName("_id")
    private String id;
    private String name;

    // Getters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
