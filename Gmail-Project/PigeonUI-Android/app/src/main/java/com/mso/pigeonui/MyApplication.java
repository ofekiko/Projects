package com.mso.pigeonui;

// Import Libraries
import android.app.Application;
import android.content.Context;

// A custom Application class that stores a global application context.
public class MyApplication extends Application {
    // Field of class
    private static Context appContext;

    // Store the application context when the app starts
    @Override
    public void onCreate() {
        super.onCreate();
        appContext = getApplicationContext();
    }

    // Returns the global application context
    public static Context getAppContext() {
        return appContext;
    }
}
