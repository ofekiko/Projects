package com.mso.pigeonui;

// Import Libraries
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

// Import Class
import com.mso.pigeonui.data.local.database.MailDatabase;
import com.mso.pigeonui.model.LabelEntity;
import com.mso.pigeonui.model.LabelRequest;
import com.mso.pigeonui.network.ApiClient;
import com.mso.pigeonui.repository.LabelRepository;
import com.mso.pigeonui.viewmodel.LabelViewModel;
import com.mso.pigeonui.viewmodel.LabelViewModelFactory;

// The class responsible for the fragment that creates and edits labels
public class NewLabelFragment extends Fragment {
    // Fields of class
    private LabelViewModel labelViewModel;
    private TextView btnCancel, btnSave;
    private EditText nameEdit;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Getting the XML layout file of the fragment
        View view = inflater.inflate(R.layout.fragment_new_label, container, false);

        // Finding the buttons in the fragment
        btnCancel = view.findViewById(R.id.btnCancel);
        btnSave = view.findViewById(R.id.btnSave);
        nameEdit = view.findViewById(R.id.etLabelName);

        // Getting the bundle arguments to determine if we are editing or creating a label
        Bundle args = getArguments();
        boolean isEdit = args != null && args.getBoolean("isEdit", false);
        String nameLabel = args != null ? args.getString("nameLabel") : null;
        String idLabel = args != null ? args.getString("idLabel") : null;

        // Creating an instance of LabelViewModel by LabelViewModelFactory,
        // by using the room data base and api service
        labelViewModel = new ViewModelProvider(
                this,
                new LabelViewModelFactory(
                        new LabelRepository(
                                ApiClient.getApiService(requireContext()),
                                MailDatabase.getInstance(requireContext()).labelDao()
                        )
                )
        ).get(LabelViewModel.class);

        // If we are in editing mode
        if (isEdit && nameLabel != null) {
            nameEdit.setText(nameLabel);
        }

        // When clicking on the save button
        btnSave.setOnClickListener(v -> {
            // Assign into title the name the user wrote
            String title = nameEdit.getText().toString().trim();

            // Gives an error if the label name is empty
            if (title.isEmpty()) {
                nameEdit.setError("Label name cannot be empty");
                return;
            }

            // Creating a LabelRequest with title
            LabelRequest request = new LabelRequest(title);

            // In editing mode
            if(isEdit) {
                labelViewModel.editLabel(idLabel, request);
            }
            // In creating mode
            else {
                labelViewModel.createLabel(request);
            }
        });

        // When clicking on the cancel button
        btnCancel.setOnClickListener(v -> {
            // Moving back to inbox fragment
            NavHostFragment.findNavController(this).popBackStack();
        });

        // Observe isLabelCreated, if it success then close the fragment and return to inbox fragment
        labelViewModel.getIsLabelCreated().observe(getViewLifecycleOwner(), success -> {
            if (Boolean.TRUE.equals(success)) {
                NavHostFragment.findNavController(this).popBackStack();
            }
        });

        // Observe isLabelEdited, if it success then close the fragment and return to inbox fragment
        labelViewModel.getIsLabelEdited().observe(getViewLifecycleOwner(), success -> {
            if (Boolean.TRUE.equals(success)) {
                NavHostFragment.findNavController(this).popBackStack();
            }
        });

        // If there was an error with the label operation, show the error message
        labelViewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
            }
        });

        return view;
    }
}

