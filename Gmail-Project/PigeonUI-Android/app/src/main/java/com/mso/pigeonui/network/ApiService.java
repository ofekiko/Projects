package com.mso.pigeonui.network;

// Import Libraries
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

import java.util.List;

// Import Classes
import com.mso.pigeonui.model.AddBlacklistUrlRequest;
import com.mso.pigeonui.model.LabelRequest;
import com.mso.pigeonui.model.LabelResponse;
import com.mso.pigeonui.model.MailRequest;
import com.mso.pigeonui.model.MailApiResponse;
import com.mso.pigeonui.model.MailUpdateRequest;
import com.mso.pigeonui.model.RegisterRequest;
import com.mso.pigeonui.model.RegisterResponse;
import com.mso.pigeonui.model.LoginRequest;
import com.mso.pigeonui.model.LoginResponse;
import com.mso.pigeonui.model.UserResponse;
import com.mso.pigeonui.model.UsernameCheckResponse;

// The interface of the requests we sent to the api service
public interface ApiService {

    // Get users
    @POST("api/users")
    Call<RegisterResponse> registerUser(@Body RegisterRequest request);

    // Create token
    @POST("/api/tokens")
    Call<LoginResponse> loginUser(@Body LoginRequest request);

    // Get user details by username
    @GET("/api/users/check-username")
    Call<UsernameCheckResponse> checkUsername(@Query("username") String username);

    // Get user details by id
    @GET("/api/users/{id}")
    Call<UserResponse> getUserDetails(@Path("id") String id);

    // Get my user details
    @GET("/api/users/me")
    Call<UserResponse> getMyDetails();

    // If it *did* send back the created mail, you'd use Call<MailApiResponse>.
    @POST("/api/mails")
    Call<MailApiResponse> createMail(@Body MailRequest request);

    // MODIFIED: Get the latest mails (e.g., for an overview or "All Mail")
    // Changed MailResponse to MailApiResponse
    @GET("/api/mails")
    Call<List<MailApiResponse>> getLatestMails();

    @GET("/api/mails/box/{boxName}")
    Call<List<MailApiResponse>> getMailsInBox(@Path("boxName") String boxName);

    // MODIFIED: Get a specific mail by its ID (server ID)
    // Changed MailResponse to MailApiResponse and Path("id") int id to String id
    @GET("/api/mails/{id}")
    Call<MailApiResponse> getMailById(@Path("id") String mailId);

    // NEW: Delete a mail (moves to Trash on server or permanently deletes)
    // mailId is the server's String ID
    @DELETE("/api/mails/{id}")
    Call<Void> deleteMail(@Path("id") String mailId);

    // Edit a specific draft
    @PATCH("/api/mails/{id}")
    Call<MailApiResponse> editDraft(@Path("id") String mailId, @Body MailRequest updateRequest);

    // MODIFIED: Update a mail using the specific MailUpdateRequest POJO
    @PATCH("/api/mails/{id}")
    Call<MailApiResponse> updateMail(@Path("id") String mailId, @Body MailUpdateRequest updateRequest);

     @GET("/api/mails/search") // Or a more RESTful path like "/api/search/mails"
     Call<List<MailApiResponse>> searchMail(@Query("query") String query, @Query("box") String box);
     @GET("/api/mails/search/{query}")
     Call<List<MailApiResponse>> searchMailOverall(@Path("query") String query);

     @GET("/api/mails/search/{query}/{boxName}")
     Call<List<MailApiResponse>> searchMailInBox(@Path("query") String query, @Path("boxName") String boxName);

    // Get labels
    @GET("/api/labels")
    Call<List<LabelResponse>> getAllLabels();

    // Create labels
    @POST("/api/labels")
    Call<LabelResponse> createLabel(@Body LabelRequest request);

    // Get label by id
    @GET("/api/labels/{id}")
    Call<LabelResponse> getLabelById(@Path("id") String id);

    // Edit a specific label
    @PATCH("/api/labels/{id}")
    Call<Void> editLabel(@Path("id") String id, @Body LabelRequest request);

    // Delete a specific label
    @DELETE("/api/labels/{id}")
    Call<Void> deleteLabel(@Path("id") String id);

    @POST("api/blacklist") // Path relative to your Retrofit base URL
    Call<Void> addUrlToBlacklist(@Body AddBlacklistUrlRequest request);
    @DELETE("api/blacklist/{url}") // Path relative to your Retrofit base URL
    Call<Void> deleteUrlFromBlacklist(@Path("url") String url);
}
