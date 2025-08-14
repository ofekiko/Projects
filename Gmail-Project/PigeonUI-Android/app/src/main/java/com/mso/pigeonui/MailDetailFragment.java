package com.mso.pigeonui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.mso.pigeonui.network.ApiClient;
import com.mso.pigeonui.repository.UserRepository;
import com.mso.pigeonui.viewmodel.UserViewModel;
import com.mso.pigeonui.viewmodel.UserViewModelFactory;
import com.mso.pigeonui.model.UserResponse;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.Base64;

import java.io.ByteArrayInputStream;

import com.mso.pigeonui.model.MailEntity;
import com.mso.pigeonui.viewmodel.MailDetailViewModel;
import com.mso.pigeonui.viewmodel.MailDetailViewModelFactory;

import java.util.ArrayList;
import java.util.List;
public class MailDetailFragment extends Fragment {

    private static final String TAG = "MailDetailFragment"; // For logging
    // The unique ID of the mail to be displayed, passed as an argument to the fragment.
    private String serverMailIdArgument;
    // ViewModel responsible for providing and managing mail detail data.
    private MailDetailViewModel viewModel;
    private UserViewModel userViewModel;
    private MailEntity currentMailEntity;

    // UI elements for displaying mail details.
    private TextView tvSubject;
    private ImageView ivSenderIcon;
    private TextView tvSender;
    private TextView tvTimestamp;
    private TextView tvToLabel; // Label for "To:" field
    private TextView tvToValue; // Value of "To:" field (recipients)
    private TextView tvBody;

    // UI elements for mail actions.
    private ImageButton btnMailDetailBack; // Button to navigate back to the previous screen.
    private Button buttonDelete;
    private Button buttonMoveTo;
    private Button buttonToggleRead;

    // Flag to ensure that the initial attempt to mark an email as read happens only once.
    private boolean initialMarkAsReadAttempted = false;
    // Flag to prevent multiple "Move To" dialogs from being shown simultaneously.
    private boolean isMoveToDialogShowing = false;

    // Decodes a Base64 string to a Bitmap, and applies rotation based on EXIF orientation metadata
    private Bitmap decodeBase64ToBitmap(String base64Str) {
        try {
            // Decode the Base64 string to byte array
            byte[] decodedBytes = Base64.decode(base64Str, Base64.DEFAULT);

            // Create a Bitmap from the decoded bytes
            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);

            // Read EXIF metadata to determine the orientation of the image
            ExifInterface exif = new ExifInterface(new ByteArrayInputStream(decodedBytes));
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            int rotationDegrees = 0;

            // Map orientation to rotation degrees
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotationDegrees = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotationDegrees = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotationDegrees = 270;
                    break;
            }

            // Apply rotation if needed
            if (rotationDegrees != 0) {
                Matrix matrix = new Matrix();
                matrix.postRotate(rotationDegrees);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            }

            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Crops the given bitmap into a circular shape
    private Bitmap getCircularBitmap(Bitmap bitmap) {
        // Determine the size to make the image square
        int size = Math.min(bitmap.getWidth(), bitmap.getHeight());

        // Create a new square output bitmap
        Bitmap output = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, size, size);
        float radius = size / 2f;

        // Draw a circle and clip the bitmap inside it
        paint.setAntiAlias(true);
        canvas.drawCircle(radius, radius, radius, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, null, rect, paint);

        return output;
    }

    // Default constructor
    public MailDetailFragment() {
    }

    // Called when the fragment is created
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retrieve the mail ID argument passed from the previous fragment.
        if (getArguments() != null) {
            serverMailIdArgument = MailDetailFragmentArgs.fromBundle(getArguments()).getMailId();
        }
        // Initialize the ViewModel using a Factory to pass the Application context.
        MailDetailViewModelFactory factory = new MailDetailViewModelFactory(requireActivity().getApplication());
        viewModel = new ViewModelProvider(this, factory).get(MailDetailViewModel.class);

        UserViewModelFactory userFactory = new UserViewModelFactory(
                new UserRepository(ApiClient.getApiService(requireContext()))
        );
        userViewModel = new ViewModelProvider(this, userFactory).get(UserViewModel.class);
    }

    // Called to have the fragment instantiate its user interface view
    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mail_detail, container, false);

        // Initialize UI Elements
        tvSubject = view.findViewById(R.id.tvMailDetailSubject);
        ivSenderIcon = view.findViewById(R.id.ivMailDetailSenderIcon);
        tvSender = view.findViewById(R.id.tvMailDetailSender);
        tvTimestamp = view.findViewById(R.id.tvMailDetailTimestamp);
        tvToLabel = view.findViewById(R.id.tvMailDetailToLabel);
        tvToValue = view.findViewById(R.id.tvMailDetailToValue);
        tvBody = view.findViewById(R.id.tvMailDetailBody);

        // Initialize new UI elements
        btnMailDetailBack = view.findViewById(R.id.btnMailDetailBack);
        buttonDelete = view.findViewById(R.id.buttonDelete);
        buttonMoveTo = view.findViewById(R.id.buttonMoveTo);
        buttonToggleRead = view.findViewById(R.id.buttonToggleRead);

        return view;
    }

    // Called immediately after onCreateView has returned, and is used to do final initialization
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        setupObservers(); // Set up LiveData observers from the ViewModel

        // Load mail details if a valid mail ID was provided.
        if (serverMailIdArgument != null && !serverMailIdArgument.isEmpty()) {
            viewModel.loadMailById(serverMailIdArgument);
        } else {
            tvSubject.setText("Error: Mail ID not provided.");
            Toast.makeText(getContext(), "Error: Mail ID not available.", Toast.LENGTH_LONG).show();
        }
        setupButtonClickListeners();
        setupCustomBackButtonListener();
    }
    // Sets up the onClickListener for the custom back button
    private void setupCustomBackButtonListener() {
        if (btnMailDetailBack != null) {
            btnMailDetailBack.setOnClickListener(v -> {
                NavController navController = Navigation.findNavController(requireView());
                navController.navigateUp();
            });
        }
    }

    // Sets up observers for LiveData in the ViewModel
    private void setupObservers() {
        // Observe changes to the current mail being displayed.
        viewModel.currentMail.observe(getViewLifecycleOwner(), mailEntity -> {
            this.currentMailEntity = mailEntity;
            if (mailEntity != null) {
                 updateUi(mailEntity); // Update the UI with the new mail details
                if (mailEntity.getAuthorId() != null && !mailEntity.getAuthorId().isEmpty()) {
                    userViewModel.loadUserByID(mailEntity.getAuthorId());
                }
                // Automatically mark the mail as read if it's unread and hasn't been attempted yet
                if (!mailEntity.isRead() && !initialMarkAsReadAttempted) {
                    viewModel.toggleReadStatusCurrentMail();
                    initialMarkAsReadAttempted = true; // Set flag to prevent repeated attempts.
                }
            }
        });

        userViewModel.getOtherUser().observe(getViewLifecycleOwner(), user -> {
            if (user != null && user.getImage() != null && !user.getImage().isEmpty()) {
                Bitmap bitmap = decodeBase64ToBitmap(user.getImage());
                if (bitmap != null) {
                    ivSenderIcon.setImageBitmap(getCircularBitmap(bitmap));
                } else {
                    ivSenderIcon.setImageResource(R.drawable.ic_avatar);
                }
            } else {
                ivSenderIcon.setImageResource(R.drawable.ic_avatar);
            }
        });

        // Observe loading state changes.
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            MailEntity currentMail = viewModel.currentMail.getValue();
            // Display a "Loading..." message if data is loading and no mail is currently displayed
            if (isLoading && (currentMailEntity == null || currentMailEntity.getTitle() == null)) {
                tvSubject.setText("Loading...");
            }
            else {
                // If not loading, or if some mail data is present, ensure the title is set correctly
                if (currentMail != null && currentMail.getTitle() != null) {
                    tvSubject.setText(currentMail.getTitle());
                } else if (currentMail != null) {
                    tvSubject.setText("No Title");
                }
            }
            // Enable/disable action buttons based on the loading state
            if (buttonDelete != null) buttonDelete.setEnabled(!isLoading);
            if (buttonMoveTo != null) buttonMoveTo.setEnabled(!isLoading);
            if (buttonToggleRead != null) buttonToggleRead.setEnabled(!isLoading);
        });
        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
                // If mailDetails is also null, then it's a critical error for display
                if (viewModel.currentMail.getValue() == null) {
                    tvSubject.setText("Error loading mail");
                    tvSender.setText("");
                    tvTimestamp.setText("");
                    tvToValue.setText("");
                    tvBody.setText("");
                }
                viewModel.clearErrorMessage(); // Clear error after displaying
            }
        });
    }
    // Updates the UI with the provided MailEntity
    private void updateUi(MailEntity mail) {
        if (mail == null) {
            tvSubject.setText("Mail not found");
            tvSender.setText("");
            tvTimestamp.setText("");
            tvToValue.setText("");
            tvBody.setText("");
            return;
        }
        tvSubject.setText(mail.getTitle() != null ? mail.getTitle() : "No Title");

        String senderDisplay;
        if (mail.getAuthorFirstName() != null && !mail.getAuthorFirstName().isEmpty()) {
            senderDisplay = mail.getAuthorFirstName() + (mail.getAuthorLastName() != null ? " " + mail.getAuthorLastName() : "");
        } else {
            senderDisplay = mail.getAuthor() != null ? mail.getAuthor() : "Unknown Sender";
        }
        tvSender.setText(senderDisplay);
        tvTimestamp.setText(viewModel.getFormattedDate(mail.getSentAt()));
        if (mail.getRecipientsEmails() != null && !mail.getRecipientsEmails().isEmpty()) {
            tvToValue.setText(mail.getRecipientsEmails().replace(",", ", "));
            if (tvToLabel != null) tvToLabel.setVisibility(View.VISIBLE);
            tvToValue.setVisibility(View.VISIBLE);
        } else {
            if (tvToLabel != null) tvToLabel.setVisibility(View.GONE);
            tvToValue.setVisibility(View.GONE);
            tvToValue.setText("");
        }
        tvBody.setText(mail.getContent() != null ? mail.getContent() : "No Body");

        if (buttonToggleRead != null) {
            buttonToggleRead.setEnabled(true);
        }

        if (buttonDelete != null && buttonMoveTo != null) {
            boolean isMailInTrash = MailDetailViewModel.BOX_TRASH.equals(mail.getBox());
            if (isMailInTrash) {
                buttonDelete.setEnabled(false);
            } else {
                buttonDelete.setEnabled(true);
                buttonMoveTo.setEnabled(true);
            }
        }
    }
    // Sets up click listeners for buttons
    private void setupButtonClickListeners() {
        if (buttonDelete != null) {
            buttonDelete.setOnClickListener(v -> {
                if (currentMailEntity == null) {
                    Toast.makeText(getContext(), getString(R.string.mail_details_not_loaded), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (MailDetailViewModel.BOX_TRASH.equals(currentMailEntity.getBox())) {
                    Toast.makeText(getContext(), getString(R.string.mail_already_in_trash_or_permanent_delete_not_implemented), Toast.LENGTH_LONG).show();
                    return;
                }

                new AlertDialog.Builder(requireContext())
                        .setTitle(getString(R.string.confirm_delete_title))
                        .setMessage(getString(R.string.confirm_delete_message))
                        .setPositiveButton(getString(R.string.delete_action_text), (dialog, which) -> {
                            viewModel.moveCurrentMailToBox(MailDetailViewModel.BOX_TRASH);
                            NavHostFragment.findNavController(this).popBackStack();
                        })
                        .setNegativeButton(android.R.string.cancel, null)
                        .show();
            });
        }
        if (buttonMoveTo != null) {
            buttonMoveTo.setOnClickListener(v -> {
                if (currentMailEntity == null) {
                    return;
                }
                showMoveToDialog();
            });
        }

        if (buttonToggleRead != null) {
            buttonToggleRead.setOnClickListener(v -> {
                viewModel.toggleReadStatusCurrentMail();
                NavHostFragment.findNavController(this).popBackStack();
            });
        }
    }
    // Displays a dialog for selecting a folder to move the mail to
    private void showMoveToDialog() {
        // Prevent showing multiple dialogs if one is already in progress
        if (isMoveToDialogShowing) {
            return;
        }
        isMoveToDialogShowing = true;

        // Observe the list of available mailboxes from the ViewModel.
        // This is a one-time observation to get the current list.
        viewModel.combinedAvailableBoxes.observe(getViewLifecycleOwner(), new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> boxes) {
                viewModel.combinedAvailableBoxes.removeObserver(this);
                try {
                    // Ensure current mail details are loaded before proceeding.
                    if (currentMailEntity == null || currentMailEntity.getBox() == null) {
                        Toast.makeText(getContext(), getString(R.string.mail_details_not_loaded), Toast.LENGTH_SHORT).show();
                        isMoveToDialogShowing = false;
                        return;
                    }
                    // Create a mutable list of potential destination boxes.
                    List<String> filteredBoxes = new ArrayList<>(boxes);
                    // Remove the Trash box (deletion is a separate action)
                    filteredBoxes.remove(MailDetailViewModel.BOX_TRASH);
                    // If the current mail is a senders copy prevent it from moving to inbox
                    if (currentMailEntity.isSenderCopy()){
                        filteredBoxes.remove(MailDetailViewModel.BOX_INBOX);
                        filteredBoxes.add(MailDetailViewModel.BOX_SENT);
                    }
                    // Remove the current box (cannot move to the same box).
                    filteredBoxes.remove(currentMailEntity.getBox());

                    if (filteredBoxes.isEmpty()) {
                        Toast.makeText(getContext(), getString(R.string.no_other_folders_to_move), Toast.LENGTH_SHORT).show();
                        isMoveToDialogShowing = false; // Reset flag
                        return;
                    }
                    CharSequence[] items = filteredBoxes.toArray(new CharSequence[0]);

                    // Build and show the AlertDialog.
                    AlertDialog dialog = new AlertDialog.Builder(requireContext())
                            .setTitle(getString(R.string.move_to_folder_title))
                            .setItems(items, (d, which) -> {
                                String selectedBox = items[which].toString();
                                viewModel.moveCurrentMailToBox(selectedBox);
                                NavHostFragment.findNavController(MailDetailFragment.this).popBackStack();
                            })
                            .setNegativeButton(android.R.string.cancel, null)
                            .setOnDismissListener(d -> {
                                Log.d(TAG, "MoveToDialog dismissed.");
                                isMoveToDialogShowing = false;
                            })
                            .create();

                    dialog.show();

                } catch (Exception e) {
                    Toast.makeText(getContext(), "Error showing move dialog: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    isMoveToDialogShowing = false; // Reset flag on error
                }
            }
        });
    }
}
