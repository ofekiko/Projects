package com.mso.pigeonui.repository;

import androidx.lifecycle.MutableLiveData;

import com.mso.pigeonui.model.LoginRequest;
import com.mso.pigeonui.model.LoginResponse;
import com.mso.pigeonui.model.Result;

import com.mso.pigeonui.model.RegisterRequest;
import com.mso.pigeonui.model.RegisterResponse;
import com.mso.pigeonui.network.ApiClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import org.json.JSONObject;


public class AuthRepository {

    // Handles user registration request
    public void registerUser(RegisterRequest request, MutableLiveData<Result<RegisterResponse>> liveData) {
        ApiClient.getApiService().registerUser(request).enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.postValue(Result.success(response.body()));
                } else {
                    String errorMessage = "Registration failed";
                    try {
                        if (response.errorBody() != null) {
                            String errorJson = response.errorBody().string();
                            JSONObject jsonObject = new JSONObject(errorJson);
                            errorMessage = jsonObject.optString("error", errorMessage);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    liveData.postValue(Result.failure(new Exception(errorMessage)));
                }
            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                liveData.postValue(Result.failure(t));
            }
        });
    }

    // Handles user login request
    public void loginUser(LoginRequest request, MutableLiveData<Result<LoginResponse>> liveData) {
        ApiClient.getApiService().loginUser(request).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.postValue(Result.success(response.body()));
                } else {
                    String errorMessage = "Login failed";
                    try {
                        if (response.errorBody() != null) {
                            String errorJson = response.errorBody().string();
                            JSONObject jsonObject = new JSONObject(errorJson);
                            errorMessage = jsonObject.optString("error", errorMessage); // or "message", depends on API
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    liveData.postValue(Result.failure(new Exception(errorMessage)));
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                liveData.postValue(Result.failure(t));
            }
        });
    }

    // Checks if a username is available by calling backend API
    public void checkUsernameAvailability(String username, UsernameCheckCallback callback) {
        ApiClient.getApiService().checkUsername(username).enqueue(new Callback<com.mso.pigeonui.model.UsernameCheckResponse>() {
            @Override
            public void onResponse(Call<com.mso.pigeonui.model.UsernameCheckResponse> call, Response<com.mso.pigeonui.model.UsernameCheckResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onResult(response.body().isAvailable());
                } else {
                    callback.onResult(false); // אם יש שגיאה או שאין גוף תגובה
                }
            }

            @Override
            public void onFailure(Call<com.mso.pigeonui.model.UsernameCheckResponse> call, Throwable t) {
                callback.onResult(false);
            }
        });
    }

    // Interface for username check result
    public interface UsernameCheckCallback {
        void onResult(boolean available);
    }


}
