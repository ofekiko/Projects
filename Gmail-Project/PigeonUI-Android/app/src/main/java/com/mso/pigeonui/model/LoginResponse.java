package com.mso.pigeonui.model;

/**
 * This class represents the response from the server after a successful login.
 * It includes the user's ID and a JWT token used for authentication.
 */
public class LoginResponse {
    // User's unique ID (from the backend)
    private String id;
    // JWT authentication token (used for authorized API requests)
    private String token;
    // Getter for the user ID
    public String getId() { return id; }
    // Getter for the authentication token
    public String getToken() { return token; }
}
