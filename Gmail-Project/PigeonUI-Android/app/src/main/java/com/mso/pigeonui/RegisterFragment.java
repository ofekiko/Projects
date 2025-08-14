package com.mso.pigeonui;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;

import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.exifinterface.media.ExifInterface;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.AutoCompleteTextView;
import android.widget.ArrayAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.lifecycle.ViewModelProvider;
import com.mso.pigeonui.model.RegisterRequest;
import com.mso.pigeonui.model.RegisterResponse;
import com.mso.pigeonui.viewmodel.RegisterViewModel;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.appcompat.widget.TooltipCompat;


import android.util.Log;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.navigation.Navigation;

import java.util.Calendar;

public class RegisterFragment extends Fragment {

    // Declare all input fields and buttons
    private EditText etFirstName, etLastName, etUsername, etPassword, etConfirmPassword, etBirthdate;
    private ImageView ivProfileImage;
    private Button btnPickImage, btnRegister;
    private AutoCompleteTextView etGender;

    private Uri imageUri;
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    private RegisterViewModel viewModel;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable usernameCheckRunnable;
    private Uri cameraImageUri;

    // Required empty constructor
    public RegisterFragment() {}

    // Called to create the view for the fragment
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        // Set up ViewModel
        viewModel = new ViewModelProvider(this).get(RegisterViewModel.class);

        // Initialize UI elements
        etFirstName = view.findViewById(R.id.etFirstName);
        etLastName = view.findViewById(R.id.etLastName);
        etUsername = view.findViewById(R.id.etUsername);
        etPassword = view.findViewById(R.id.etPassword);

        // Tooltip on password field
        etPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_info_warning, 0);
        TooltipCompat.setTooltipText(etPassword, "Password must be at least 8 characters long and contain both letters and numbers");
        etConfirmPassword = view.findViewById(R.id.etConfirmPassword);
        etBirthdate = view.findViewById(R.id.etBirthdate);
        etGender = view.findViewById(R.id.etGender);

        // Clear errors when editing fields
        etUsername.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                etUsername.setError(null);
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        etPassword.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                etPassword.setError(null);
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        etConfirmPassword.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                String password = etPassword.getText().toString();
                String confirm = s.toString();

                if (confirm.isEmpty()) {
                    etConfirmPassword.setError(null);
                    etConfirmPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                } else if (confirm.equals(password)) {
                    etConfirmPassword.setError(null);
                    etConfirmPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_check_circle_green, 0);
                } else {
                    // no icon and no error while typing
                    etConfirmPassword.setError(null);
                    etConfirmPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                }
            }
            @Override public void afterTextChanged(Editable s) {}
        });
        etGender = view.findViewById(R.id.etGender);

        // Set up gender dropdown (male/female)
        String[] genders = new String[]{"male", "female"};
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                genders
        );
        etGender.setAdapter(genderAdapter);

        etGender.setOnClickListener(v -> etGender.showDropDown());

        etGender.setKeyListener(null);

        // Clear errors for birthdate and gender while typing
        etBirthdate.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                etBirthdate.setError(null);
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        etGender.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                etGender.setError(null);
            }
            @Override public void afterTextChanged(Editable s) {}
        });


        // Initialize image view and buttons
        ivProfileImage = view.findViewById(R.id.ivProfileImage);
        btnPickImage = view.findViewById(R.id.btnPickImage);
        btnRegister = view.findViewById(R.id.btnRegister);

        // Handle username checking with 300ms debounce
        handler = new Handler(Looper.getMainLooper());

        etUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (usernameCheckRunnable != null) {
                    handler.removeCallbacks(usernameCheckRunnable);
                }

                final String username = s.toString().trim();

                if (username.isEmpty()) {
                    etUsername.setError(null);
                    etUsername.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                    return;
                }

                usernameCheckRunnable = () -> viewModel.checkUsernameAvailability(username);

                handler.postDelayed(usernameCheckRunnable, 300);
            }

            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
        });

        // Observe username availability result and show checkmark or error
        viewModel.getIsUsernameAvailable().observe(getViewLifecycleOwner(), isAvailable -> {
            String username = etUsername.getText().toString().trim();

            if (username.isEmpty()) {
                etUsername.setError(null);
                etUsername.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                return;
            }

            if (!isAvailable) {
                etUsername.setError("Username is already taken");
            } else {
                etUsername.setError(null);
                etUsername.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_check_circle_green, 0);
            }
        });
        TextView tvGoToLogin = view.findViewById(R.id.tvGoToLogin);

        // Link to go back to Login screen
        tvGoToLogin.setOnClickListener(v -> {
            NavController navController = NavHostFragment.findNavController(this);
            navController.navigate(R.id.action_registerFragment_to_loginFragment);
        });

        // Dark mode toggle logic
        Button btnToggleTheme = view.findViewById(R.id.btnToggleTheme);

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


        // Set up birthdate picker, image picker, and form validation
        setupBirthdatePicker();
        setupImagePicker();
        setupValidation();
        observeViewModel();

        return view;
    }


    // This shows a DatePickerDialog when the user taps the birthdate field
    private void setupBirthdatePicker() {
        etBirthdate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            // Create and show the date picker
            DatePickerDialog dialog = new DatePickerDialog(requireContext(),
                    (view, y, m, d) -> etBirthdate.setText(d + "/" + (m + 1) + "/" + y),
                    year, month, day);
            dialog.show();
        });
    }

    // This opens the gallery to pick a profile image
    private void setupImagePicker() {
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        if (result.getData() != null && result.getData().getData() != null) {
                            imageUri = result.getData().getData();
                        } else {
                            imageUri = cameraImageUri; // המצלמה מחזירה את זה דרך EXTRA_OUTPUT
                        }
                        ivProfileImage.setImageURI(imageUri);
                    }
                });

        btnPickImage.setOnClickListener(v -> {
            CharSequence[] options = {"Camera", "Gallery"};
            new android.app.AlertDialog.Builder(requireContext())
                    .setTitle("Select Profile Image")
                    .setItems(options, (dialog, which) -> {
                        if (which == 0) {
                            openCamera();
                        } else {
                            openGallery();
                        }
                    })
                    .show();
        });
    }

    // Open gallery intent
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    // Check for camera permission and open camera if allowed
    private void openCamera() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, 101);
        } else {
            launchCamera();
        }
    }

    // Launch the camera app to take a picture
    private void launchCamera() {
        File photoFile = new File(requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "profile.jpg");
        cameraImageUri = FileProvider.getUriForFile(requireContext(), requireContext().getPackageName() + ".provider", photoFile);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraImageUri);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        imageUri = cameraImageUri;

        imagePickerLauncher.launch(intent);
    }

    // Handle camera permission result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                launchCamera();
            } else {
                Toast.makeText(getContext(), "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // This handles input validation when the Register button is clicked
    private void setupValidation() {
        btnRegister.setOnClickListener(v -> {
            String firstName = etFirstName.getText().toString().trim();
            String lastName = etLastName.getText().toString().trim();
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString();
            String confirmPassword = etConfirmPassword.getText().toString();
            String birthdate = etBirthdate.getText().toString();
            String gender = etGender.getText().toString().trim();

            if (firstName.isEmpty() || lastName.isEmpty() || username.isEmpty()
                    || password.isEmpty() || confirmPassword.isEmpty()
                    || birthdate.isEmpty() || gender.isEmpty()) {
                Toast.makeText(getContext(), "All fields are required", Toast.LENGTH_SHORT).show();
                return;
            }

            if (imageUri == null) {
                Toast.makeText(getContext(), "Profile image is required", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirmPassword)) {
                Toast.makeText(getContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }
            String imageBase64 = imageUri != null ? encodeImageToBase64(imageUri) : "";
            RegisterRequest request = new RegisterRequest(
                    firstName,
                    lastName,
                    username,
                    password,
                    confirmPassword,
                    birthdate,
                    gender,
                    imageBase64
            );


            viewModel.register(request);
        });
    }
    // Handle result of registration (success or failure)
    private void observeViewModel() {
        viewModel.getRegisterResult().observe(getViewLifecycleOwner(), result -> {
            if (result.isSuccess()) {
                NavController navController = NavHostFragment.findNavController(this);
                navController.navigate(R.id.action_registerFragment_to_loginFragment);
            } else {
                String errorMessage = result.getError() != null ? result.getError() : "Unknown error";

                if (errorMessage.contains("Username already exists")) {
                    etUsername.setError("Username is already taken");
                    etUsername.requestFocus();
                } else if (errorMessage.contains("Passwords do not match")) {
                    etConfirmPassword.setError("Passwords do not match");
                    etConfirmPassword.requestFocus();
                } else if (errorMessage.contains("Password must be at least")) {
                    etPassword.setError("Password must contain at least 8 chars with letters and numbers");
                    etPassword.requestFocus();
                } else if (errorMessage.contains("Invalid birthdate format")) {
                    etBirthdate.setError("Invalid birthdate format (e.g. dd/MM/yyyy)");
                    etBirthdate.requestFocus();
                } else if (errorMessage.contains("Gender must be")) {
                    etGender.setError("Gender must be either 'male' or 'female'");
                    etGender.requestFocus();
                } else if (errorMessage.contains("This field is required")) {
                    Toast.makeText(getContext(), "Please fill all required fields", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getContext(), "❌ " + errorMessage, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    // Convert selected image to Base64 string to send to backend
    private String encodeImageToBase64(Uri imageUri) {
        try {
            // Load original image from URI
            Bitmap original = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), imageUri);
            original = correctImageOrientation(imageUri, original);


            // Resize: Limit to 800x800 max
            Bitmap resized = resizeBitmap(original, 800, 800);

            // Compress to JPEG with 50% quality
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            resized.compress(Bitmap.CompressFormat.JPEG, 50, stream);

            byte[] byteArray = stream.toByteArray();
            stream.close();

            return Base64.encodeToString(byteArray, Base64.NO_WRAP); // No line breaks
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    // Read EXIF orientation data and rotate image if needed
    private Bitmap correctImageOrientation(Uri imageUri, Bitmap bitmap) {
        try {
            InputStream input = requireContext().getContentResolver().openInputStream(imageUri);
            ExifInterface exif = new ExifInterface(input);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            input.close();

            Matrix matrix = new Matrix();
            switch (orientation) {
                case 6: // ORIENTATION_ROTATE_90
                    matrix.postRotate(90);
                    break;
                case 3: // ORIENTATION_ROTATE_180
                    matrix.postRotate(180);
                    break;
                case 8: // ORIENTATION_ROTATE_270
                    matrix.postRotate(270);
                    break;
                default:
                    return bitmap; // no rotation needed
            }


            return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        } catch (Exception e) {
            e.printStackTrace();
            return bitmap;
        }
    }

    // Resize image to max 800x800
    private Bitmap resizeBitmap(Bitmap original, int maxWidth, int maxHeight) {
        int width = original.getWidth();
        int height = original.getHeight();

        float scale = Math.min((float) maxWidth / width, (float) maxHeight / height);
        int scaledWidth = Math.round(scale * width);
        int scaledHeight = Math.round(scale * height);

        return Bitmap.createScaledBitmap(original, scaledWidth, scaledHeight, true);
    }




}
