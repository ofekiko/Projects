package com.mso.pigeonui;

// Import Libraries
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.mso.pigeonui.viewmodel.InboxViewModel;

import java.io.ByteArrayInputStream;

// The class responsible for the fragment that show the profile of the user and enable logout
public class ProfileFragment extends Fragment {
    // Fields of class
    private ImageButton btnCloseProfile;
    private TextView mailValue, hiValue;
    private ImageView profileImage;
    private ExtendedFloatingActionButton btnLogOut;

    private InboxViewModel inboxViewModel;

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


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Getting the XML layout file of the fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Finding the buttons in the fragment
        btnCloseProfile = view.findViewById(R.id.btnCloseProfile);
        mailValue = view.findViewById(R.id.tvMailUser);
        hiValue = view.findViewById(R.id.tvHiMessage);
        profileImage = view.findViewById(R.id.ivUserPicture);
        btnLogOut = view.findViewById(R.id.btnLogout);

        inboxViewModel = new ViewModelProvider(requireActivity()).get(InboxViewModel.class);

        // Getting the bundle arguments to know the user details
        Bundle args = getArguments();
        if (args != null) {
            String email = args.getString("userMail");
            String firstName = args.getString("userFirstName");
            String lastName = args.getString("userLastName");
            String base64Image = args.getString("userPicture");

            // Assign into the button values the rellevant values
            mailValue.setText(email);
            String capitalizedFirstName = firstName.substring(0, 1).toUpperCase() + firstName.substring(1).toLowerCase();
            String capitalizedLastName = lastName.substring(0, 1).toUpperCase() + lastName.substring(1).toLowerCase();
            hiValue.setText("Hi, " + capitalizedFirstName + " " + capitalizedLastName + "!");

            if (base64Image != null && !base64Image.isEmpty()) {
                Bitmap bitmap = decodeBase64ToBitmap(base64Image);
                if (bitmap != null) {
                    profileImage.setImageBitmap(getCircularBitmap(bitmap));
                }
            } else {
                profileImage.setImageResource(R.drawable.ic_avatar);
            }
        }

        // When clicking on the close button
        btnCloseProfile.setOnClickListener(v -> {
            // Moving back to inbox fragment
            NavHostFragment.findNavController(this).popBackStack();
        });

        // When clicking on the log out button
        btnLogOut.setOnClickListener(v -> {

            inboxViewModel.clearAllMailsFromDatabase();
            inboxViewModel.clearMails();
            // Removing the token
            requireContext().getSharedPreferences("app_prefs", 0)
                    .edit()
                    .remove("auth_token")
                    .apply();

            // Moving back to welcome fragment
            NavController navController = NavHostFragment.findNavController(ProfileFragment.this);
            NavOptions navOptions = new NavOptions.Builder()
                    .setPopUpTo(R.id.nav_graph, true)
                    .build();
            navController.navigate(R.id.welcomeFragment, null, navOptions);
        });

        return view;
    }
}

