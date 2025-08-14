package com.mso.pigeonui.repository;

// Import Classes
import com.mso.pigeonui.model.UserResponse;
import com.mso.pigeonui.network.ApiService;

// Import Libraries
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import org.json.JSONObject;

import java.util.function.Consumer;

// The class that connect the viewModel of users to the api database
public class UserRepository {
    // Field of the class: an ApiService object
    private final ApiService apiService;

    // The constructor
    public UserRepository(ApiService apiService) {
        this.apiService = apiService;
    }

    // Sends a request to the API service to get the details of my user
    public void getMyUser(Consumer<UserResponse> onSuccess, Consumer<String> onError) {
        // Call the API service to fetch the user details asynchronously
        apiService.getMyDetails().enqueue(new Callback<UserResponse>() {

            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                // If the response is successful and the body is not null,
                // pass the details of the user to the success callback
                if (response.isSuccessful() && response.body() != null) {
                    onSuccess.accept(response.body());
                }
                // Handle API-level error
                else {
                    try {
                        if (response.errorBody() != null) {
                            JSONObject json = new JSONObject(response.errorBody().string());
                            String errorMsg = json.optString("error", "Unknown error");
                            onError.accept(errorMsg);
                        } else {
                            onError.accept("Unknown server error");
                        }
                    } catch (Exception e) {
                        onError.accept("Failed to read error: " + e.getMessage());
                    }
                }
            }

            // If we don't get no answer at all send the error by onError
            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                onError.accept("Network error: " + t.getMessage());
            }
        });
    }

    public void getUserDetailsById(String userId, Consumer<UserResponse> onSuccess, Consumer<String> onError) {
        apiService.getUserDetails(userId).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    onSuccess.accept(response.body());
                } else {
                    try {
                        if (response.errorBody() != null) {
                            JSONObject json = new JSONObject(response.errorBody().string());
                            String errorMsg = json.optString("error", "Unknown error");
                            onError.accept(errorMsg);
                        } else {
                            onError.accept("Unknown server error");
                        }
                    } catch (Exception e) {
                        onError.accept("Failed to read error: " + e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                onError.accept("Network error: " + t.getMessage());
            }
        });
    }

}
