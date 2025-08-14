package com.mso.pigeonui.model;

// The class that represents the body of label we sent to the server
public class LabelRequest {

    // The fields of label we want to sent
    private final String name;

    // The constructor
    public LabelRequest(String name) {
        this.name = name;
    }

    // Getters:
    public String getName() {
        return name;
    }
}
