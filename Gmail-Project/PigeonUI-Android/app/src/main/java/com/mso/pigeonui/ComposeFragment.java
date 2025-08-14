package com.mso.pigeonui;

// Import Libraries
import android.app.Application;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import java.util.Arrays;

// Import Classes
import com.mso.pigeonui.data.local.database.MailDatabase;
import com.mso.pigeonui.model.MailEntity;
import com.mso.pigeonui.model.MailRequest;
import com.mso.pigeonui.network.ApiClient;
import com.mso.pigeonui.repository.MailRepository;
import com.mso.pigeonui.repository.UserRepository;
import com.mso.pigeonui.viewmodel.SendViewModel;
import com.mso.pigeonui.viewmodel.UserViewModel;
import com.mso.pigeonui.viewmodel.SendViewModelFactory;
import com.mso.pigeonui.viewmodel.UserViewModelFactory;


// The class that represents the mail compose screen where a user writes and sends an email
public class ComposeFragment extends Fragment {

    // Fields of class
    private SendViewModel sendViewModel;
    private UserViewModel userViewModel;
    private EditText titleEdit, contentEdit, recipientsEdit;
    private TextView fromValue;
    private ImageButton btnSend, btnBack;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Getting the XML layout file of the fragment
        View view = inflater.inflate(R.layout.fragment_compose, container, false);

        // Finding the views from the layout by their IDs
        titleEdit = view.findViewById(R.id.etSubject);
        contentEdit = view.findViewById(R.id.etBody);
        recipientsEdit = view.findViewById(R.id.etTo);
        fromValue = view.findViewById(R.id.tvFromValue);
        btnSend = view.findViewById(R.id.btnSend);
        btnBack = view.findViewById(R.id.btnBack);

        // Getting the bundle arguments to determine if we are editing or creating a label
        Bundle args = getArguments();
        boolean isEdit = args != null && args.getBoolean("isEdit", false);
        String recipientsDraft = args != null ? args.getString("recipientsDraft") : null;
        String titleDraft = args != null ? args.getString("titleDraft") : null;
        String contentDraft = args != null ? args.getString("contentDraft") : null;
        String idDraft = args != null ? args.getString("idDraft") : null;

        // Creating an instance of SenderViewModel by SenderViewModelFactory,
        // by using the room data base and api service
        sendViewModel = new ViewModelProvider(
                this,
                new SendViewModelFactory(
                        new MailRepository(
                                ApiClient.getApiService(requireContext()),
                                MailDatabase.getInstance(requireContext()).mailDao(),
                                MailDatabase.getInstance(requireContext()).labelDao()
                        )
                )
        ).get(SendViewModel.class);

        // Creating an instance of UserViewModel by UserViewModelFactory,
        // by using the room data base and api service
        userViewModel = new ViewModelProvider(
                this,
                new UserViewModelFactory(
                        new UserRepository(
                                ApiClient.getApiService(requireContext())
                        )
                )
        ).get(UserViewModel.class);

        // If we are in editing mode
        if (isEdit) {
            titleEdit.setText(titleDraft);
            contentEdit.setText(contentDraft);
            recipientsEdit.setText(recipientsDraft);

            Log.d("ComposeFragment", "Editing draft loaded: title=" + titleDraft +
                    ", content=" + contentDraft + ", recipients=" + recipientsDraft);
        }



        // Getting the user details
        userViewModel.loadUser();

        // Show the mail of the user in from
        userViewModel.getUser().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                fromValue.setText(user.getEmail());
            }
        });

        // When the user clicks the back button:
        btnBack.setOnClickListener(v -> {
            // Assign the values from the edit text buttons
            String title = titleEdit.getText().toString().trim();
            String content = contentEdit.getText().toString().trim();
            String recipientsString = recipientsEdit.getText().toString().trim();

            if (title.isEmpty() && content.isEmpty() && recipientsString.isEmpty()) {
                NavHostFragment.findNavController(this).popBackStack();
                return;
            }

            // Convert the recipientEmails into an array
            String[] recipientEmails = Arrays.stream(recipientsString.split("\\s+"))
                    .filter(s -> !s.isEmpty())
                    .toArray(String[]::new);

            // Create the draft request
            MailRequest request = new MailRequest(
                    title,
                    content,
                    recipientEmails,
                    false
            );

            // In editing mode
            if(isEdit) {
                sendViewModel.editDraft(idDraft, request);
            }
            // In creating mode
            else {
                sendViewModel.sendMail(request);
            }

        });

        // When the user clicks the send button:
        btnSend.setOnClickListener(v -> {
            // Assign the values from the edit text buttons
            String title = titleEdit.getText().toString().trim();
            String content = contentEdit.getText().toString().trim();
            String recipientsString = recipientsEdit.getText().toString().trim();

            // Convert the recipientEmails into an array
            String[] recipientEmails = Arrays.stream(recipientsString.split("\\s+"))
                    .filter(s -> !s.isEmpty())
                    .toArray(String[]::new);

            // Create the mail request
            MailRequest request = new MailRequest(
                    title,
                    content,
                    recipientEmails,
                    true
            );

            // In editing mode
            if(isEdit) {
                sendViewModel.editDraft(idDraft, request);
            }
            // In creating mode
            else {
                sendViewModel.sendMail(request);
            }

        });

        // If the mail was sent successfully, go back to the inbox
        sendViewModel.getIsMailSent().observe(getViewLifecycleOwner(), isSent -> {
            if (Boolean.TRUE.equals(isSent)) {
                NavHostFragment.findNavController(this).popBackStack();
            }
        });

        // If the mail was edited successfully, go back to the inbox
        sendViewModel.getIsDraftSaved().observe(getViewLifecycleOwner(), isSaved -> {
            if (Boolean.TRUE.equals(isSaved)) {
                NavHostFragment.findNavController(this).popBackStack();
            }
        });

        // If there was an error sending the mail, show the error message
        sendViewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
            }
        });

        return view;
    }
}
