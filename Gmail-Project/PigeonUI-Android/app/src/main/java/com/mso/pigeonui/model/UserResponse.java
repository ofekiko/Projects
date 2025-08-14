package com.mso.pigeonui.model;

// Import Libraries
import com.google.gson.annotations.SerializedName;

// The class that represent the answer we get from the api server about user
public class UserResponse {
    // Fields
    private String id;
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private String gender;
    private String birthdate;
    @SerializedName("imageUri")
    private String image;

    // Getters
    public String getId() {
        return id;
    }
    public String getFirstName() {
        return firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public String getUsername() {
        return username;
    }
    public String getEmail() {
        return email;
    }
    public String getGender() {
        return gender;
    }
    public String getBirthdate() {
        return birthdate;
    }
    public String getImage() {
        return image;
    }
}
