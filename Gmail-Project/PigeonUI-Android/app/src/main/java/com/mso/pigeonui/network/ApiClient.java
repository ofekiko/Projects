package com.mso.pigeonui.network;

// Import Libraries
import android.content.Context;
import android.content.SharedPreferences;

import com.mso.pigeonui.MyApplication;
import com.mso.pigeonui.R;

import org.json.JSONObject;

import java.io.InputStream;

import okhttp3.OkHttpClient;
import okhttp3.Request;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

// This class is responsible for creating JSON requests to the API service
public class ApiClient {

    // Fields of class
    private static String baseUrl;
    private static ApiService apiServiceWithToken;
    private static ApiService apiServiceWithoutToken;

    // Initializes the baseUrl field by reading it from the server_config.json file
    private static void initBaseUrl() {
        // If already initialized, do nothing
        if (baseUrl != null) return;

        try {
            // Open the raw resource file server_config.json
            InputStream is = MyApplication.getAppContext().getResources().openRawResource(R.raw.server_config);

            // Read the entire file content into a byte array
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            is.close();

            // Parse the JSON and extract the value of "base_url"
            String json = new String(buffer, "UTF-8");
            JSONObject obj = new JSONObject(json);
            baseUrl = obj.getString("base_url");

        }
        // If anything goes wrong, print the error and set a fallback default URL (Emulator)
        catch (Exception e) {
            e.printStackTrace();
            baseUrl = "http://172.18.68.116:8080/";
        }
    }


    // Returns a singleton instance of the API service when jwt token is needed
    public static ApiService getApiService(Context context) {
        if (apiServiceWithToken == null) {

            // Get the Base URL
            initBaseUrl();

            OkHttpClient client = new OkHttpClient.Builder()
                    // Add an interceptor to include the JWT token in every request
                    .addInterceptor(chain -> {
                        // Get the original request
                        Request original = chain.request();

                        // Retrieve the JWT token from shared preferences
                        SharedPreferences prefs = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
                        String token = prefs.getString("jwt_token", "");

                        // Build the new request with the jwt and the original request (body and method)
                        Request.Builder requestBuilder = original.newBuilder()
                                .header("Authorization", "Bearer " + token)
                                .method(original.method(), original.body());

                        // Return the request with the token
                        return chain.proceed(requestBuilder.build());
                    })
                    .build();

            // Build the Retrofit instance with base URL, client and JSON converter
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            // Create the API service from the Retrofit instance
            apiServiceWithToken = retrofit.create(ApiService.class);
        }
        return apiServiceWithToken;
    }

    // Returns a singleton instance of the API service when jwt token is not needed
    public static ApiService getApiService() {
        if (apiServiceWithoutToken == null) {

            // Get the Base URL
            initBaseUrl();

            // Build the Retrofit instance with base URL and JSON converter
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            // Create the API service from the Retrofit instance
            apiServiceWithoutToken = retrofit.create(ApiService.class);
        }

        return apiServiceWithoutToken;
    }

}
