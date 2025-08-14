package com.mso.pigeonui;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class WelcomeFragment extends Fragment {
    // Default constructor
    public WelcomeFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_welcome, container, false);
        // Get references to the buttons
        Button btnGoToLogin = view.findViewById(R.id.btnGoToLogin);
        Button btnGoToRegister = view.findViewById(R.id.btnGoToRegister);
        Button btnToggleTheme = view.findViewById(R.id.btnToggleTheme); // ✔️ תיקון כאן

        // Set onClick listener to navigate to the login screen
        btnGoToLogin.setOnClickListener(v ->
                Navigation.findNavController(view).navigate(R.id.action_welcomeFragment_to_loginFragment));

        // Set onClick listener to navigate to the register screen
        btnGoToRegister.setOnClickListener(v ->
                Navigation.findNavController(view).navigate(R.id.action_welcomeFragment_to_registerFragment));

        // Set the initial icon for the theme toggle button based on the current theme
        int currentMode = AppCompatDelegate.getDefaultNightMode();
        if (currentMode == AppCompatDelegate.MODE_NIGHT_YES) {
            btnToggleTheme.setText("☀️");
        } else {
            btnToggleTheme.setText("🌙");
        }

        // Handle the toggle theme button click
        btnToggleTheme.setOnClickListener(v -> {
            int mode = AppCompatDelegate.getDefaultNightMode();
            if (mode == AppCompatDelegate.MODE_NIGHT_YES) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                btnToggleTheme.setText("🌙");
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                btnToggleTheme.setText("☀️");
            }
        });

        // Return the full set up view
        return view;
    }
}
