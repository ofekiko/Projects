package com.mso.pigeonui.repository;

// Import Libraries
import java.util.List;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import androidx.lifecycle.LiveData;

import org.json.JSONObject;

// Import Classes
import com.mso.pigeonui.data.local.dao.LabelDao;
import com.mso.pigeonui.model.LabelEntity;
import com.mso.pigeonui.model.LabelRequest;
import com.mso.pigeonui.model.LabelResponse;
import com.mso.pigeonui.network.ApiService;

// The class that connect the viewModel of labels to the room and api database
public class LabelRepository {
    // Fields of the class: a LabelDao object and an ApiService object
    private final LabelDao labelDao;
    private final ApiService apiService;

    // The constructor
    public LabelRepository(ApiService apiService, LabelDao labelDao) {
        this.labelDao = labelDao;
        this.apiService = apiService;
    }

    // Sends a request to the API service to create a new label
    public void createLabel(LabelRequest request, Runnable onSuccess, Consumer<String> onError) {
        // Using the apiService in order to create a new label in the api service
        apiService.createLabel(request).enqueue(new Callback<LabelResponse>() {

            @Override
            public void onResponse(Call<LabelResponse> call, Response<LabelResponse> response) {
                // If the request was successful and response body is not null,
                // extract the label and trigger the success callback
                if (response.isSuccessful() && response.body() != null) {
                    LabelResponse label = response.body();
                    LabelEntity entity = new LabelEntity(label.getId(), label.getName());
                    saveToLocal(entity);
                    onSuccess.run();
                }
                // If the request failed, try to extract the error message from the response body
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
            public void onFailure(Call<LabelResponse> call, Throwable t) {
                onError.accept("Network error: " + t.getMessage());
            }
        });
    }

    // Sends a request to the API service to get all labels
    public void getLabels(Consumer<List<LabelResponse>> onSuccess, Consumer<String> onError) {
        // Call the API service to fetch all labels asynchronously
        apiService.getAllLabels().enqueue(new Callback<List<LabelResponse>>() {
            @Override
            public void onResponse(Call<List<LabelResponse>> call, Response<List<LabelResponse>> response) {
                // If the response is successful and the body is not null,
                // pass the list of labels to the success callback
                if (response.isSuccessful() && response.body() != null) {
                    onSuccess.accept(response.body());
                }
                // Handle API-level error
                else {
                    onError.accept("Failed to load labels: " + response.code());
                }
            }

            // If we don't get no answer at all send the error by onError
            @Override
            public void onFailure(Call<List<LabelResponse>> call, Throwable t) {
                onError.accept("Network error: " + t.getMessage());
            }
        });
    }

    // Sends a request to the API service to delete a label
    public void deleteLabel(String id, Runnable onSuccess, Consumer<String> onError) {
        // Using the apiService in order to delete a label from the api service
        apiService.deleteLabel(id).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                // If the request was successful trigger the success callback
                if (response.isSuccessful()) {
                    deleteFromLocal(new LabelEntity(id, ""));
                    onSuccess.run();
                }
                // Handle API-level error
                else {
                    onError.accept("Failed to delete label: " + response.code());
                }
            }

            // If we don't get no answer at all send the error by onError
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                onError.accept("Network error: " + t.getMessage());
            }
        });
    }

    // Sends a request to the API service to edit a label
    public void editLabel(String id,LabelRequest request, Runnable onSuccess, Consumer<String> onError) {
        apiService.editLabel(id, request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                // If the request was successful trigger the success callback
                if (response.isSuccessful()) {
                    updateInLocal(new LabelEntity(id, request.getName()));
                    onSuccess.run();
                }
                // If the request failed, try to extract the error message from the response body
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
            public void onFailure(Call<Void> call, Throwable t) {
                onError.accept("Network error: " + t.getMessage());
            }
        });
    }

    // Sync labels from the server and update the local Room database accordingly
    public void syncLabelsFromServer(Consumer<String> onError) {
        // Fetch all labels from the API server
        getLabels(labelsFromServer -> {
            // Run database operations in a background thread
            Executors.newSingleThreadExecutor().execute(() -> {
                // Clear the local Room database to avoid duplicates or outdated data
                labelDao.clearAll();

                // Insert all labels fetched from the server into the local database
                for (LabelResponse label : labelsFromServer) {
                    LabelEntity entity = new LabelEntity(label.getId(), label.getName());
                    labelDao.insertLabel(entity);
                }
            });
        }, onError);
    }

    // Saves the label entity to the local Room database
    private void saveToLocal(LabelEntity label) {
        Executors.newSingleThreadExecutor().execute(() -> labelDao.insertLabel(label));
    }

    // Delete the label entity from the local Room database
    private void deleteFromLocal(LabelEntity label) {
        Executors.newSingleThreadExecutor().execute(() -> labelDao.deleteByMongoId(label.getId()));
    }

    // Update the label entity in the local room database
    private void updateInLocal(LabelEntity label) {
        Executors.newSingleThreadExecutor().execute(() -> labelDao.updateLabel(label));
    }

    // Get all labels from the room
    public LiveData<List<LabelEntity>> getAllLabelsFromRoom() {
        return labelDao.getLabels();
    }
}
