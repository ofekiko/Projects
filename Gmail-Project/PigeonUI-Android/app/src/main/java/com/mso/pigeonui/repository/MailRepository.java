package com.mso.pigeonui.repository;
import android.os.Handler;
import android.os.Looper;
// Import Libraries
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.json.JSONObject;

// Import Classes
import com.mso.pigeonui.data.local.dao.LabelDao;
import com.mso.pigeonui.data.local.dao.MailDao;
import com.mso.pigeonui.model.LabelEntity;
import com.mso.pigeonui.model.MailEntity;
import com.mso.pigeonui.model.MailRequest;
import com.mso.pigeonui.model.MailApiResponse;
import com.mso.pigeonui.model.MailUpdateRequest;
import com.mso.pigeonui.network.ApiService;
import com.mso.pigeonui.model.AddBlacklistUrlRequest;
import org.json.JSONException;

import java.util.concurrent.ExecutorService;

// The class that connect the viewModel of mails to the room and api database
public class MailRepository {
    private static final String TAG = "MailRepository";
    private final MailDao mailDao;
    private final ApiService apiService;
    private final ExecutorService databaseExecutor;
    private final Handler mainThreadHandler;
    private final LabelDao labelDao;
    private final Executor executor = Executors.newSingleThreadExecutor();

    // The constructor
    public MailRepository(ApiService apiService, MailDao mailDao, LabelDao labelDao) {
        this.apiService = apiService;
        this.mailDao = mailDao;
        this.labelDao = labelDao;
        this.databaseExecutor = Executors.newSingleThreadExecutor();
        this.mainThreadHandler = new Handler(Looper.getMainLooper());
    }
    // Interface for handling URL operations
    public interface UrlOperationCallback {
        void onSuccess(String url);
        void onError(String url, String errorMessage);
    }


    // A function the create a MailEntity by MailApiResponse
    private MailEntity toEntity(MailApiResponse response) {
        MailEntity entity = new MailEntity();
        entity.setServerMailId(String.valueOf(response.getId()));
        entity.setTitle(response.getTitle());
        entity.setContent(response.getContent());
        entity.setSentAt(response.getSentAt());
        entity.setUserId(response.getUserId());
        entity.setAuthor(response.getAuthor());
        entity.setAuthorFirstName(response.getAuthorFirstName());
        entity.setAuthorLastName(response.getAuthorLastName());
        entity.setRecipientsEmails(String.join(" ", response.getRecipientsEmails()));
        entity.setToSend(response.isToSend());
        entity.setRead(response.isRead());
        entity.setBox(response.getBox());
        entity.setBlacklisted(response.isBlacklisted());
        entity.setSenderCopy(response.isSenderCopy());
        return entity;
    }

    // Sends a request to the API service to create a new mail
    public void sendMail(MailRequest request, Runnable onSuccess, Consumer<String> onError) {
        // Using the apiService in order to create a new mail in the api service
        apiService.createMail(request).enqueue(new Callback<MailApiResponse>() {
            @Override
            public void onResponse(@NonNull Call<MailApiResponse> call, @NonNull Response<MailApiResponse> response) {
                // If the request was successful and response body is not null,
                // extract the mail and trigger the success callback
                if (response.isSuccessful() && response.body() != null) {
                    MailApiResponse apiResponse = response.body();
                    MailEntity entity = toEntity(apiResponse);
                    saveToLocal(entity);
                    if (onSuccess != null) onSuccess.run();
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
            public void onFailure(@NonNull Call<MailApiResponse> call, @NonNull Throwable t) {
                onError.accept("Network error: " + t.getMessage());
            }
        });
    }

    // Sends a request to the API service to create a new mail
    public void editDraft(String id, MailRequest request, Runnable onSuccess, Consumer<String> onError) {
        // Using the apiService in order to create edit a draft in the api service
        apiService.editDraft(id, request).enqueue(new Callback<MailApiResponse>() {
            @Override
            public void onResponse(@NonNull Call<MailApiResponse> call, @NonNull Response<MailApiResponse> response) {
                // If the request was successful and response body is not null,
                // extract the mail and trigger the success callback
                if (response.isSuccessful() && response.body() != null) {
                    MailApiResponse apiResponse = response.body();
                    MailEntity entity = toEntity(apiResponse);
                    updateInLocal(entity);
                    if (onSuccess != null) onSuccess.run();
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
            public void onFailure(@NonNull Call<MailApiResponse> call, @NonNull Throwable t) {
                onError.accept("Network error: " + t.getMessage());
            }
        });
    }

    // Delete all mails from local room database
    public void clearLocalMails() {
        executor.execute(() -> {
            mailDao.deleteAllMails();
        });
    }

    // Save the mail entity in the local room database
    public void saveToLocal(MailEntity mail) {
        databaseExecutor.execute(() -> {
            if (mail != null) {
                mailDao.insertMail(mail);
            }
        });
    }

    // Update the mail entity in the local room database
    private void updateInLocal(MailEntity mail) {
        databaseExecutor.execute(() -> {
            if (mail != null) {
                mailDao.updateMail(mail);
            }
        });
    }

    // Helper to map API response to Room Entity
    private MailEntity mapApiResponseToEntityBackground(MailApiResponse apiMail) {
        if (apiMail == null) return null;

        MailEntity existingEntity = mailDao.findByServerIdSync(apiMail.getId());
        MailEntity entity = (existingEntity != null) ? existingEntity : new MailEntity();

        entity.setServerMailId(apiMail.getId());
        entity.setTitle(apiMail.getTitle());
        entity.setContent(apiMail.getContent());
        entity.setSentAt(apiMail.getSentAt());
        entity.setCreatedDate(apiMail.getCreatedAt());
        try {
            if (apiMail.getUserId() != null && !apiMail.getUserId().isEmpty()) {
                entity.setUserId(apiMail.getUserId());
            }
        } catch (NumberFormatException e) {
            Log.e(TAG, "Could not parse API userId: " + apiMail.getUserId(), e);
        }
        entity.setAuthorId(apiMail.getAuthorId());
        entity.setAuthor(apiMail.getAuthor());
        entity.setAuthorFirstName(apiMail.getAuthorFirstName());
        entity.setAuthorLastName(apiMail.getAuthorLastName());
        if (apiMail.getRecipientsEmails() != null) {
            entity.setRecipientsEmails(String.join(",", apiMail.getRecipientsEmails()));
        } else {
            entity.setRecipientsEmails(null);
        }
        entity.setToSend(apiMail.isToSend());
        entity.setRead(apiMail.isRead());
        entity.setBox(apiMail.getBox());
        entity.setBlacklisted(apiMail.isBlacklisted());
        entity.setSenderCopy(apiMail.isSenderCopy());
        return entity;
    }



    // Refreshes the mails for a specific box from the network API
    public void refreshMailsForBox(String boxName, Runnable onSuccess, Consumer<String> onError) {
        // Make an API call to get mails for the specified box
        apiService.getMailsInBox(boxName).enqueue(new DefaultCallback<>(
            apiResponseList -> {
                // If the API response list is null
                if (apiResponseList == null) {
                    if (onSuccess != null) mainThreadHandler.post(onSuccess);
                    return;
                }

                // Execute database operations on a background thread
                databaseExecutor.execute(() -> {
                    List<MailEntity> mailEntities = new ArrayList<>();
                    for (MailApiResponse apiResponse : apiResponseList) {
                        MailEntity entity = mapApiResponseToEntityBackground(apiResponse);
                        if (entity != null) {
                            mailEntities.add(entity);
                        }
                    }
                    // Clear existing mails from this box in the local database
                    mailDao.clearBox(boxName);
                    // Insert all newly fetched and mapped mail entities into the database
                    mailDao.insertAllMails(mailEntities);
                    if (onSuccess != null) {
                        mainThreadHandler.post(onSuccess);
                    }
                });
            },
                errorMsg -> {
                    if (onError != null) mainThreadHandler.post(() -> onError.accept(errorMsg));
                },
                "Error fetching mails for box " + boxName
        ));
    }
    // Refreshes the details of a specific mail from the network API
    public void refreshMailDetails(String serverMailId, Consumer<MailEntity> onSuccess, Consumer<String> onError) {
        // Make an API call to get a specific mail by its ID.
        apiService.getMailById(serverMailId).enqueue(new DefaultCallback<MailApiResponse>(
                apiResponse -> {
                    if (apiResponse == null) {
                        String errorMsg = "API response for mail " + serverMailId + " is null.";
                        if (onError != null) mainThreadHandler.post(() -> onError.accept(errorMsg));
                        return;
                    }
                    // Execute database operations on a background thread.
                    databaseExecutor.execute(() -> {
                        MailEntity entity = mapApiResponseToEntityBackground(apiResponse);
                        if (entity != null) {
                            mailDao.insertMail(entity);
                            MailEntity finalEntity = mailDao.findByServerIdSync(entity.getServerMailId());
                            if (onSuccess != null) mainThreadHandler.post(() -> onSuccess.accept(finalEntity));
                        } else {
                            String errorMsg = "Failed to map API response for mail " + serverMailId;
                            if (onError != null) mainThreadHandler.post(() -> onError.accept(errorMsg));
                        }
                    });
                },
                errorMsg -> {
                    if (onError != null) mainThreadHandler.post(() -> onError.accept(errorMsg));
                },
                "Error fetching details for mail " + serverMailId
        ));
    }

    // Updates the read status of a mail
    public void updateMailReadStatus(String serverMailId, boolean isRead, Consumer<MailEntity> onSuccess, Consumer<String> onError) {
        MailUpdateRequest request = new MailUpdateRequest().setRead(isRead);

        apiService.updateMail(serverMailId, request).enqueue(new DefaultCallback<MailApiResponse>(
                apiResponse -> {
                    if (apiResponse == null) {
                        String errorMsg = "API response for updating mail " + serverMailId + " is null.";
                        if (onError != null) mainThreadHandler.post(() -> onError.accept(errorMsg));
                        return;
                    }
                    databaseExecutor.execute(() -> {
                        MailEntity updatedEntity = mapApiResponseToEntityBackground(apiResponse);
                        if (updatedEntity != null) {
                            mailDao.insertMail(updatedEntity);
                            MailEntity finalEntity = mailDao.findByServerIdSync(updatedEntity.getServerMailId());
                            if (onSuccess != null) {
                                mainThreadHandler.post(() -> onSuccess.accept(finalEntity));
                            }
                        } else {
                            String errorMsg = "Failed to map updated mail response for " + serverMailId;
                            if (onError != null) {
                                mainThreadHandler.post(() -> onError.accept(errorMsg)); // Post error back to main thread
                            }
                        }
                    });
                },
                errorMsg -> {
                    if (onError != null) {
                        mainThreadHandler.post(() -> onError.accept(errorMsg));
                    }
                },
                "Error updating read status for mail " + serverMailId
        ));
    }

    // Moves a mail to a designated box
    public void moveMailToBox(String serverMailId, String newBoxName, Consumer<MailEntity> onSuccess, Consumer<String> onError) {
        MailUpdateRequest request = new MailUpdateRequest().setBox(newBoxName);

        apiService.updateMail(serverMailId, request).enqueue(new DefaultCallback<MailApiResponse>(
                apiResponse -> {
                    if (apiResponse == null) {
                        String errorMsg = "API response for moving mail " + serverMailId + " is null.";
                        if (onError != null) mainThreadHandler.post(() -> onError.accept(errorMsg));
                        return;
                    }
                    databaseExecutor.execute(() -> {
                        MailEntity updatedEntity = mapApiResponseToEntityBackground(apiResponse);
                        if (updatedEntity != null) {
                            mailDao.insertMail(updatedEntity);
                            MailEntity finalEntity = mailDao.findByServerIdSync(updatedEntity.getServerMailId());
                            if (onSuccess != null) mainThreadHandler.post(() -> onSuccess.accept(finalEntity));
                        } else {
                            String errorMsg = "Failed to map moved mail response for " + serverMailId;
                            if (onError != null) mainThreadHandler.post(() -> onError.accept(errorMsg));
                        }
                    });
                },
                errorMsg -> {
                    if (onError != null) mainThreadHandler.post(() -> onError.accept(errorMsg));
                },
                "Error moving mail " + serverMailId
        ));
    }
    // Constructor for DefaultCallback
    private class DefaultCallback<T> implements Callback<T> {
        private final Consumer<T> onSuccessWithData;
        private final Consumer<String> onError;
        private final String errorPrefix;

        public DefaultCallback(Consumer<T> onSuccessWithData, Consumer<String> onError, String errorPrefix) {
            this.onSuccessWithData = onSuccessWithData;
            this.onError = onError;
            this.errorPrefix = errorPrefix != null ? errorPrefix : "API Error";
        }

        @Override
        public void onResponse(@NonNull Call<T> call, Response<T> response) {
            if (response.isSuccessful()) {
                if (onSuccessWithData != null) {
                    onSuccessWithData.accept(response.body());
                }
            } else {
                handleApiError(response, errorPrefix, onError);
            }
        }

        @Override
        public void onFailure(@NonNull Call<T> call, Throwable t) {
            String errorMsg = errorPrefix + ": Network failure - " + t.getMessage();
            if (onError != null) {
                onError.accept(errorMsg);
            }
        }
    }

    //Handles API error responses by attempting to parse error details
    private <T> void handleApiError(Response<T> response, String prefix, Consumer<String> onErrorCallback) {
        String errorDetailMessage;
        int httpCode = response.code();

        try {
            if (response.errorBody() != null) {
                String errorBodyString = response.errorBody().string();
                try {
                    JSONObject json = new JSONObject(errorBodyString);
                    errorDetailMessage = json.optString("error", json.optString("message", "Could not parse error details from JSON."));
                } catch (JSONException jsonException) {
                    if (errorBodyString.toLowerCase().contains("<!doctype html")) {
                        errorDetailMessage = "Server returned an HTML page (HTTP " + httpCode + ").";
                    } else if (errorBodyString.length() > 150) {
                        errorDetailMessage = "Server returned a non-JSON error (HTTP " + httpCode + "): " + errorBodyString.substring(0, 150) + "...";
                    } else {
                        errorDetailMessage = "Server returned a non-JSON error (HTTP " + httpCode + "): " + errorBodyString;
                    }
                }
            } else {
                errorDetailMessage = "Unknown server error (empty error body), HTTP Code: " + httpCode;
            }
        } catch (IOException e) { // Catch IOException from .string()
            errorDetailMessage = "Failed to process error response from server (HTTP " + httpCode + "): " + e.getMessage();
        }

        String fullErrorMsg = prefix + ". " + errorDetailMessage;
        if (onErrorCallback != null) {
            onErrorCallback.accept(fullErrorMsg);
        }
    }

    // Retrieves an observable list of the latest 50 mails for a specific box
    public LiveData<List<MailEntity>> getObservableMailsForBox(String boxName) {
        return mailDao.get50Mails(boxName);
    }

    //Retrieves observable details for a specific mail by its server ID
    public LiveData<MailEntity> getObservableMailDetails(String serverMailId) {
        return mailDao.getMailEntityByServerId(serverMailId);
    }

    // Adds a URL to the server-side blacklist
    public void addUrlToBlacklist(String url, @NonNull UrlOperationCallback callback) {
        if (url == null || url.isEmpty()) {
            mainThreadHandler.post(() -> callback.onError(url, "URL cannot be empty."));
            return;
        }

        // Create the request object for adding the URL.
        AddBlacklistUrlRequest request = new AddBlacklistUrlRequest(url);
        apiService.addUrlToBlacklist(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    mainThreadHandler.post(() -> callback.onSuccess(url));
                } else {
                    handleApiError(response, "Error adding URL " + url + " to blacklist via HTTP",
                            errorMsg -> mainThreadHandler.post(() -> callback.onError(url, errorMsg)));
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                String errorMsg = "Network failure adding URL " + url + " to blacklist via HTTP: " + t.getMessage();
                mainThreadHandler.post(() -> callback.onError(url, errorMsg));
            }
        });
    }
    // Deletes a URL from the server-side blacklist
    public void deleteUrlFromBlacklist(String url, @NonNull UrlOperationCallback callback) {
        if (url == null || url.isEmpty()) {
            mainThreadHandler.post(() -> callback.onError(url, "URL cannot be empty."));
            return;
        }

        apiService.deleteUrlFromBlacklist(url).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    mainThreadHandler.post(() -> callback.onSuccess(url));
                } else {
                    handleApiError(response, "Error deleting URL " + url + " from blacklist via HTTP",
                            errorMsg -> mainThreadHandler.post(() -> callback.onError(url, errorMsg)));
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                String errorMsg = "Network failure deleting URL " + url + " from blacklist via HTTP: " + t.getMessage();
                mainThreadHandler.post(() -> callback.onError(url, errorMsg));
            }
        });
    }
    public LiveData<List<MailEntity>> searchObservableMailsInBox(String boxName, String query) {
        String formattedQuery = "%" + query + "%";
        return mailDao.searchMailsInBox(boxName, formattedQuery);
    }
    public void refreshSearchFromServer(String query, @Nullable String boxName, Runnable onSuccess, Consumer<String> onError) {
        Call<List<MailApiResponse>> apiCall;
        String errorPrefix;

        if (boxName != null && !boxName.isEmpty()) {
            // Your ApiService has searchMailInBox(query, boxName)
            apiCall = apiService.searchMailInBox(query, boxName);
            errorPrefix = "Error searching mails in box '" + boxName + "' from server for query '" + query + "'";
        } else {
            // Your ApiService has searchMailOverall(query)
            apiCall = apiService.searchMailOverall(query); // Or searchMail(query, null) if your API supports it. Choose one.
            errorPrefix = "Error searching all mails from server for query '" + query + "'";
        }

        apiCall.enqueue(new DefaultCallback<>(
                apiResponseList -> {
                    databaseExecutor.execute(() -> {
                        List<MailEntity> mailEntities = new ArrayList<>();
                        if (apiResponseList != null) {
                            for (MailApiResponse apiResponse : apiResponseList) {
                                MailEntity entity = mapApiResponseToEntityBackground(apiResponse);
                                if (entity != null) {
                                    mailEntities.add(entity);
                                }
                            }
                        }
                        mailDao.insertAllMails(mailEntities);
                        if (onSuccess != null) {
                            mainThreadHandler.post(onSuccess);
                        }
                    });
                },
                errorMsg -> {
                    if (onError != null) mainThreadHandler.post(() -> onError.accept(errorMsg));
                },
                errorPrefix
        ));
    }
    public LiveData<List<LabelEntity>> getObservableAllDefinedLabels() {
        return labelDao.getLabels();
    }
}