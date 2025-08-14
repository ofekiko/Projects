package com.mso.pigeonui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import com.mso.pigeonui.model.LoginRequest;
import com.mso.pigeonui.model.LoginResponse;
import com.mso.pigeonui.model.Result;
import com.mso.pigeonui.viewmodel.LoginViewModel;

public class LoginFragment extends Fragment {
    // Declare UI components
    private EditText etUsername, etPassword;
    private Button btnLogin;
    private LoginViewModel viewModel;

    // Required empty constructor
    public LoginFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        // Bind UI elements
        etUsername = view.findViewById(R.id.etUsername);
        etPassword = view.findViewById(R.id.etPassword);
        btnLogin = view.findViewById(R.id.btnLogin);

        // ViewModel
        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        // Button click
        btnLogin.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString();

            if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
                Toast.makeText(getContext(), "Both fields are required", Toast.LENGTH_SHORT).show();
                return;
            }

            LoginRequest request = new LoginRequest(username, password);
            viewModel.login(request);
        });
        Button btnToggleTheme = view.findViewById(R.id.btnToggleTheme);
        TextView tvGoToRegister = view.findViewById(R.id.tvGoToRegister);

        tvGoToRegister.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(requireView());
            navController.navigate(R.id.action_loginFragment_to_registerFragment);
        });

        // Theme toggle button (🌙 / ☀️)
        int currentMode = AppCompatDelegate.getDefaultNightMode();
        if (currentMode == AppCompatDelegate.MODE_NIGHT_YES) {
            btnToggleTheme.setText("☀️");
        } else {
            btnToggleTheme.setText("🌙");
        }
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


        // Observe login result from ViewModel
        observeViewModel();

        return view;
    }

    // This method listens for changes in the login result LiveData
    private void observeViewModel() {
        viewModel.getLoginResult().observe(getViewLifecycleOwner(), result -> {
            if (result.isSuccess()) {
                LoginResponse response = result.getData();
                String token = response.getToken();

                SharedPreferences prefs = requireContext().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
                prefs.edit()
                        .putString("jwt_token", token)
                        .apply();

                NavController navController = Navigation.findNavController(requireView());
                NavOptions navOptions = new NavOptions.Builder()
                        .setPopUpTo(R.id.loginFragment, true)
                        .build();
                navController.navigate(R.id.action_loginFragment_to_inboxFragment, null, navOptions);

            } else {
                String error = result.getError() != null ? result.getError() : "Unknown error";
                Toast.makeText(getContext(), "❌ Login failed: " + error, Toast.LENGTH_LONG).show();
            }
        });
    }

}
