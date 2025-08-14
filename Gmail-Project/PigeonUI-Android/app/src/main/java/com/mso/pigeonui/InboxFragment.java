package com.mso.pigeonui;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Collections;
import java.io.ByteArrayInputStream;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

// Import Classes
import com.mso.pigeonui.adapter.MailAdapter;
import com.mso.pigeonui.data.local.database.MailDatabase;
import com.mso.pigeonui.model.LabelEntity;
import com.mso.pigeonui.model.MailEntity;
import com.mso.pigeonui.network.ApiClient;
import com.mso.pigeonui.repository.LabelRepository;
import com.mso.pigeonui.repository.UserRepository;
import com.mso.pigeonui.viewmodel.InboxViewModel;
import com.mso.pigeonui.viewmodel.LabelViewModel;
import com.mso.pigeonui.viewmodel.LabelViewModelFactory;
import com.mso.pigeonui.viewmodel.UserViewModel;
import com.mso.pigeonui.viewmodel.UserViewModelFactory;




public class InboxFragment extends Fragment {
    // Fields of class
    private static final String TAG = "InboxFragment";
    private DrawerLayout drawerLayout;
    private String jwtToken;
    private LabelViewModel labelViewModel;
    private InboxViewModel inboxViewModel;
    private UserViewModel userViewModel;
    private RecyclerView recyclerViewInbox;
    private ProgressBar progressBarInbox;
    private TextView textViewEmptyList;
    private MailAdapter mailAdapter;
    private TextView selectedLabelView = null;
    private EditText etSearchBox;
    private String lastSelectedBox = null;



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

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("lastSelectedBox", lastSelectedBox);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inboxViewModel = new ViewModelProvider(this).get(InboxViewModel.class);
        LabelRepository labelRepository = new LabelRepository(
                ApiClient.getApiService(requireContext()),
                MailDatabase.getInstance(requireContext().getApplicationContext()).labelDao()
        );
        labelViewModel = new ViewModelProvider(this, new LabelViewModelFactory(labelRepository))
                .get(LabelViewModel.class);
        userViewModel = new ViewModelProvider(
                this,
                new UserViewModelFactory(new UserRepository(
                        ApiClient.getApiService(requireContext())
                ))
        ).get(UserViewModel.class);
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Getting the XML layout file of the fragment
        View view = inflater.inflate(R.layout.fragment_inbox, container, false);

        if (savedInstanceState != null) {
            lastSelectedBox = savedInstanceState.getString("lastSelectedBox");
        }

        // Getting the jwt token
        SharedPreferences prefs = requireContext().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        jwtToken = prefs.getString("auth_token", null);

        // Finding the views in the fragment
        recyclerViewInbox = view.findViewById(R.id.recyclerViewInbox);
        progressBarInbox = view.findViewById(R.id.progressBar);
        textViewEmptyList = view.findViewById(R.id.textViewEmptyList);
        drawerLayout = view.findViewById(R.id.drawerLayout);
        etSearchBox = view.findViewById(R.id.etSearchBox);

        // Finding the buttons and views in the fragment
        ImageView btnMenu = view.findViewById(R.id.btnMenu);
        ExtendedFloatingActionButton btnCompose = view.findViewById(R.id.btnCompose);
        LinearLayout labelContainer = view.findViewById(R.id.labelContainer);
        TextView addLabelTextView = view.findViewById(R.id.itemAddLabel);
        ImageView ivProfilePicture = view.findViewById(R.id.ivProfilePicture);
        FloatingActionButton btnToggleTheme = view.findViewById(R.id.btnToggleTheme);

        // Find all left bar items
        TextView itemInbox = view.findViewById(R.id.itemInbox);
        TextView itemSent = view.findViewById(R.id.itemSent);
        TextView itemDrafts = view.findViewById(R.id.itemDrafts);
        TextView itemSpam = view.findViewById(R.id.itemSpam);
        TextView itemTrash = view.findViewById(R.id.itemTrash);
        TextView[] navItems = {itemInbox, itemSent, itemDrafts, itemSpam, itemTrash};

        if (lastSelectedBox != null) {
            boolean found = false;

            for (TextView item : navItems) {
                if (item.getText().toString().equalsIgnoreCase(lastSelectedBox)) {
                    item.setSelected(true);
                    handleNavigationSelection(item, labelContainer);
                    found = true;
                    break;
                }
            }

            if (!found && labelContainer != null) {
                for (int i = 0; i < labelContainer.getChildCount(); i++) {
                    View child = labelContainer.getChildAt(i);
                    if (child instanceof TextView) {
                        TextView labelView = (TextView) child;
                        String labelName = labelView.getText().toString();
                        if (labelName.equals(lastSelectedBox)) {
                            labelView.setSelected(true);
                        } else {
                            labelView.setSelected(false);
                        }
                    }
                }
            }
        } else {
            itemInbox.setSelected(true);
            handleNavigationSelection(itemInbox, labelContainer);
        }



        // Initialize the current UI mode (light or dark) outside the OnClickListener
        int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;

        // Set the appropriate icon on the toggle button based on current theme
        if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
            // If in dark mode, show sun icon
            btnToggleTheme.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_sun));
        } else {
            // If in light mode, show moon icon
            btnToggleTheme.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_moon));
        }

        // Load user details and sync labels from API/local DB
        labelViewModel.syncLabels();
        userViewModel.loadUser();

        userViewModel.getUser().observe(getViewLifecycleOwner(), user -> {
            if (user.getImage() != null && !user.getImage().isEmpty()) {
                Bitmap bitmap = decodeBase64ToBitmap(user.getImage());
                if (bitmap != null) {
                    Bitmap circularBitmap = getCircularBitmap(bitmap);
                    ivProfilePicture.setImageBitmap(circularBitmap);
                } else {
                    ivProfilePicture.setImageResource(R.drawable.ic_avatar);
                }
            } else {
                ivProfilePicture.setImageResource(R.drawable.ic_avatar);
            }
        });

        // When clicking on the darkMode button
        btnToggleTheme.setOnClickListener(v -> {
            // Toggle the theme (switch from dark to light or vice versa)
            if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO); // switch to light mode
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES); // switch to dark mode
            }

            // Recreate the activity to apply the theme change
            requireActivity().recreate();
        });


        // When clicking on the menu button
        btnMenu.setOnClickListener(v -> {
            if (drawerLayout != null) drawerLayout.openDrawer(GravityCompat.START);
        });

        // When clicking on the compose button
        btnCompose.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putBoolean("isEdit", false);
            NavController navController = NavHostFragment.findNavController(InboxFragment.this);
            navController.navigate(R.id.action_inboxFragment_to_composeFragment, bundle);
        });

        // When clicking on the profile picture button
        ivProfilePicture.setOnClickListener(v -> {
            var user = userViewModel.getUser().getValue();
            if (user != null) {
                Bundle bundle = new Bundle();
                bundle.putString("userMail", user.getEmail());
                bundle.putString("userFirstName", user.getFirstName());
                bundle.putString("userLastName", user.getLastName());
                bundle.putString("userPicture", user.getImage());

                Navigation.findNavController(v).navigate(R.id.action_inboxFragment_to_profileFragment, bundle);
            } else {
                Toast.makeText(requireContext(), "User not loaded yet", Toast.LENGTH_SHORT).show();
            }
        });

        // When clicking on the add label button
        addLabelTextView.setOnClickListener(v -> {
            // Close drawer first
            drawerLayout.closeDrawer(GravityCompat.START);
            // Navigate after slight delay to ensure drawer is closed
            v.postDelayed(() -> {
                Bundle bundle = new Bundle();
                bundle.putBoolean("isEdit", false);
                Navigation.findNavController(v).navigate(R.id.action_inboxFragment_to_labelFragment, bundle);
            }, 250);
        });


        // Set default selected (Inbox) and behavior for nav items
        //itemInbox.setSelected(true);
        for (TextView item : navItems) {
            item.setOnClickListener(v -> {
                for (TextView navItem : navItems) navItem.setSelected(false);
                for (int i = 0; i < labelContainer.getChildCount(); i++) {
                    labelContainer.getChildAt(i).setSelected(false);
                }
                selectedLabelView = null;
                v.setSelected(true);
                drawerLayout.closeDrawer(GravityCompat.START);
                handleNavigationSelection((TextView) v, labelContainer);
            });
        }

        // Observe labels
        labelViewModel.getAllLabels().observe(getViewLifecycleOwner(), labels -> {
            // Clear previous labels
            labelContainer.removeAllViews();

            // Get a layout for each of the labels
            for (LabelEntity label : labels) {
                TextView labelView = (TextView) inflater.inflate(R.layout.item_label, labelContainer, false);
                labelView.setText(label.getName());
                // Check if this is the selected label and mark it
                if (label.getName().equals(lastSelectedBox)) {
                    labelView.setSelected(true);
                    selectedLabelView = labelView;
                } else {
                    labelView.setSelected(false);
                }
                Drawable icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_label);
                labelView.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null);

                labelView.setOnClickListener(v -> {
                    for (TextView navItem : navItems) navItem.setSelected(false);
                    for (int i = 0; i < labelContainer.getChildCount(); i++) {
                        labelContainer.getChildAt(i).setSelected(false);
                    }
                    v.setSelected(true);
                    drawerLayout.closeDrawer(GravityCompat.START);
                    String selectedLabelName = label.getName();
                    lastSelectedBox = selectedLabelName;

                    if (etSearchBox != null && etSearchBox.getText().length() > 0) {
                        etSearchBox.setText("");
                    } else {
                        if (inboxViewModel != null && inboxViewModel.getCurrentSearchQuery().getValue() != null) {
                            inboxViewModel.setSearchQuery(null); // Clear active search
                        }
                    }

                    if (recyclerViewInbox != null) recyclerViewInbox.setVisibility(View.VISIBLE);

                    if (inboxViewModel != null) {
                        inboxViewModel.loadMailsForBox(selectedLabelName, true);
                    } else {
                        Toast.makeText(getContext(), "Error: ViewModel not available", Toast.LENGTH_SHORT).show();
                    }
                });

                labelView.setOnLongClickListener(v -> {
                    // Open a popup with the option to edit and delete a label
                    PopupMenu popup = new PopupMenu(requireContext(), v);
                    popup.getMenu().add("Edit");
                    popup.getMenu().add("Delete");
                    popup.setOnMenuItemClickListener(item -> {
                        if (item.getTitle().equals("Edit")) {
                            drawerLayout.closeDrawer(GravityCompat.START);
                            v.postDelayed(() -> {
                                // Pass arguments to the label fragment to indicate edit mode vs create mode
                                Bundle bundle = new Bundle();
                                bundle.putBoolean("isEdit", true);
                                bundle.putString("nameLabel", label.getName());
                                bundle.putString("idLabel", label.getId());
                                Navigation.findNavController(v).navigate(R.id.action_inboxFragment_to_labelFragment, bundle);
                            }, 250);
                        } else if (item.getTitle().equals("Delete")) {
                            labelViewModel.deleteLabel(label.getId());
                        }
                        return true;
                    });
                    popup.show();
                    return true;
                });
                labelContainer.addView(labelView);
            }
        });
        if (etSearchBox != null) {
            etSearchBox.setOnEditorActionListener((v, actionId, event) -> {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String query = etSearchBox.getText().toString().trim();
                    inboxViewModel.setSearchQuery(query); // ViewModel handles if query is empty
                    hideKeyboard();
                    etSearchBox.clearFocus(); // Remove focus from EditText
                    return true;
                }
                return false;
            });
        }
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Setup RecyclerView for displaying emails
        setupEmailRecyclerView();
        // Setup observers for LiveData from ViewModels
        setupEmailObservers();

        // Initial data load or refresh based on current state (search query or selected box).
        if (inboxViewModel != null) {
            String initialQuery = (etSearchBox != null) ? etSearchBox.getText().toString() : null;
            String lastBox = inboxViewModel.getCurrentBoxName();
            if (initialQuery != null && !initialQuery.isEmpty()) {
                // If there's an active search query , apply it.
                inboxViewModel.setSearchQuery(initialQuery);
                inboxViewModel.refreshCurrentDataFromServer();
            } else {
                // If no search query, load mails for the last known box
                inboxViewModel.loadMailsForBox(lastBox, false);
            }
        }
    }

    // Handles actions to be taken when a navigation item
    private void handleNavigationSelection(TextView selectedView, LinearLayout labelContainer) {
        CharSequence selectedText = selectedView.getText();
        // Get localized strings for standard mailboxes
        String inboxString = getResources().getString(R.string.inbox);
        String sentString = getResources().getString(R.string.sent);
        String draftsString = getResources().getString(R.string.drafts);
        String spamString = getResources().getString(R.string.spam);
        String trashString = getResources().getString(R.string.trash);
        lastSelectedBox = selectedView.getText().toString();

        // Clear search query in EditText and ViewModel when a new box is selected.
        if (etSearchBox != null) {
            etSearchBox.setText(""); // Clear visual search text
        } else {
            if (inboxViewModel != null) {
                inboxViewModel.setSearchQuery(null); // Clear active search in ViewModel
            }
        }
        // Determine which box to load based on the selected item's text.
        if (inboxString.contentEquals(selectedText)) {
            if (recyclerViewInbox != null) recyclerViewInbox.setVisibility(View.VISIBLE);
            if (inboxViewModel != null) inboxViewModel.loadMailsForBox("Inbox",true);
        } else if (sentString.contentEquals(selectedText)) {
            if (recyclerViewInbox != null) recyclerViewInbox.setVisibility(View.VISIBLE);
            if (inboxViewModel != null) inboxViewModel.loadMailsForBox("Sent",true);
        } else if (draftsString.contentEquals(selectedText)) {
            if (recyclerViewInbox != null) recyclerViewInbox.setVisibility(View.VISIBLE);
            if (inboxViewModel != null) inboxViewModel.loadMailsForBox("Drafts",true);
        } else if (spamString.contentEquals(selectedText)) {
            if (recyclerViewInbox != null) recyclerViewInbox.setVisibility(View.VISIBLE);
            if (inboxViewModel != null) inboxViewModel.loadMailsForBox("Spam",true);
        } else if (trashString.contentEquals(selectedText)) {
            if (recyclerViewInbox != null) recyclerViewInbox.setVisibility(View.VISIBLE);
            if (inboxViewModel != null) inboxViewModel.loadMailsForBox("Trash",true);
        }
        else {
            if (recyclerViewInbox != null) {
                recyclerViewInbox.setVisibility(View.VISIBLE);
            }
            if (inboxViewModel != null) {
                inboxViewModel.loadMailsForBox(lastSelectedBox, true);
            }
        }
    }

    // Updates the UI elements
    private void updateUI() {
        // Ensure all necessary UI components and ViewModel are initialized.
        if (inboxViewModel == null || mailAdapter == null || recyclerViewInbox == null || progressBarInbox == null || textViewEmptyList == null) {
            Log.e(TAG, "One of the required variables is null. Cannot update UI.");
            return;
        }

        // Get current loading state and mail list from ViewModel.
        Boolean loading = inboxViewModel.getIsLoading().getValue();
        List<MailEntity> mails = inboxViewModel.getDisplayedMails().getValue();

        boolean isLoading = loading != null && loading; // Treat null loading state as not loading.

        // Show/hide ProgressBar based on loading state
        progressBarInbox.setVisibility(isLoading ? View.VISIBLE : View.GONE);

        if (isLoading) {
            // If loading, hide RecyclerView and empty list message
            recyclerViewInbox.setVisibility(View.GONE);
            textViewEmptyList.setVisibility(View.GONE);
        } else {
            // If not loading, check if there are mails to display.
            if (mails != null && !mails.isEmpty()) {
                recyclerViewInbox.setVisibility(View.VISIBLE);
                textViewEmptyList.setVisibility(View.GONE);
                mailAdapter.submitList(mails);
            } else {
                recyclerViewInbox.setVisibility(View.GONE);
                textViewEmptyList.setVisibility(View.VISIBLE);
                mailAdapter.submitList(Collections.emptyList());
            }
        }
    }

    // Sets up the RecyclerView for displaying emails
    private void setupEmailRecyclerView() {
        if (recyclerViewInbox == null) {
            return;
        }
        mailAdapter = new MailAdapter();
        recyclerViewInbox.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewInbox.setAdapter(mailAdapter);

        // Set an item click listener on the adapter.
        mailAdapter.setOnItemClickListener(mail -> {
            // If the mail is a draft, navigate to ComposeFragment in edit mode.
            if ("Drafts".equalsIgnoreCase(mail.getBox())) {
                Bundle bundle = new Bundle();
                bundle.putBoolean("isEdit", true);
                bundle.putString("recipientsDraft", mail.getRecipientsEmails().replace(",", " "));
                bundle.putString("titleDraft", mail.getTitle());
                bundle.putString("contentDraft", mail.getContent());
                bundle.putString("idDraft", mail.getServerMailId());

                NavController navController = Navigation.findNavController(requireView());
                navController.navigate(R.id.action_inboxFragment_to_composeFragment, bundle);
                return;
            }

            // If the mail is unread and has a server ID, mark it as read via ViewModel.
            if (!mail.isRead() && mail.getServerMailId() != null && inboxViewModel != null) {
                inboxViewModel.markEmailAsRead(mail.getServerMailId(), true);
            }
            String currentMailId = mail.getServerMailId();
            // If a valid server mail ID exists, navigate to MailDetailFragment.
            if (currentMailId != null && !currentMailId.isEmpty()) {
                // Create the action, passing the mailId (String)
                InboxFragmentDirections.ActionInboxFragmentToMailDetailFragment action =
                        InboxFragmentDirections.actionInboxFragmentToMailDetailFragment(currentMailId);

                NavController navController = Navigation.findNavController(requireView());
                navController.navigate(action);
            } else {
                Toast.makeText(getContext(), "Error: Mail details unavailable.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Sets up observers for LiveData objects from the ViewModel
    private void setupEmailObservers() {
        if (inboxViewModel == null) {
            return;
        }
        // Observe changes in the list of displayed mails
        inboxViewModel.getDisplayedMails().observe(getViewLifecycleOwner(), mails -> {
            updateUI();
        });
        // Observe changes in the loading state.
        inboxViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            updateUI();
        });

        // Observe error messages from the ViewModel.
        inboxViewModel.getErrorMessage().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
                inboxViewModel.clearErrorMessage();
            }
        });
        // Observe changes in the current search query to update the search box text.
        if (etSearchBox != null) {
            inboxViewModel.getCurrentSearchQuery().observe(getViewLifecycleOwner(), query -> {
                if (query == null && etSearchBox.getText().length() > 0) {
                    etSearchBox.setText(""); // This might re-trigger TextWatcher, but VM should handle it.
                } else if (query != null && !etSearchBox.getText().toString().equals(query)) {
                    etSearchBox.setText(query); // Use with caution if TextWatcher is active
                }
            });
        }
    }
    // Hides the keyboard
    private void hideKeyboard() {
        View view = requireActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }
}