package com.mso.pigeonui.model;

/**
 * This class represents the data sent to the server when a user tries to log in.
 * It contains the username and password entered by the user.
 */
public class LoginRequest {
    // The username entered by the user
    private String username;
    // The password entered by the user
    private String password;

    // Constructor to initialize both fields
    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // Getters
    public String getUsername() { return username; }
    public String getPassword() { return password; }
}
