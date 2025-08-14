package com.mso.pigeonui.model;

/**
 * This class represents the response returned by the server after a registration request.
 * It contains a success flag and a message string.
 */
public class RegisterResponse {
    // Indicates if registration was successful (true/false)
    private boolean success;
    // Message from the server (e.g., "Registration successful" or an error message)
    private String message;

    // Getter for the success field
    public boolean isSuccess(){
        return success;
    }

    // Getter for the message field
    public String getMessage(){
        return message;
    }
}