package com.mso.pigeonui.model;

/**
 * This class represents the response from the server
 * when checking if a username is available.
 */
public class UsernameCheckResponse {
    // true if the username is available; false if already taken
    private boolean available;

    // Getter method to access the availability status
    public boolean isAvailable() {
        return available;
    }

    // Setter method to update the availability status
    public void setAvailable(boolean available) {
        this.available = available;
    }
}
