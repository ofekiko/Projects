package com.mso.pigeonui.model;

/**
 * This class represents the data sent to the server during user registration.
 * It includes personal information, credentials, and the user's profile image.
 */
public class RegisterRequest {
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private String confirmPassword;
    private String birthdate;
    private String gender;

    private String imageUri;

    // Constructor to initialize all fields
    public RegisterRequest(String firstName, String lastName, String username, String password, String confirmPassword, String birthdate, String gender, String imageUri){
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.confirmPassword = confirmPassword;
        this.birthdate = birthdate;
        this.gender = gender;
        this.imageUri = imageUri;
    }

    // Getters
    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public String getBirthdate() {
        return birthdate;
    }
    public String getGender() {
        return gender;
    }


    public String getImageUri() {
        return imageUri;
    }
}
