package com.polamokh.homeinternetreview.ui.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.polamokh.homeinternetreview.R;
import com.polamokh.homeinternetreview.ui.activities.EditProfileNameActivity;
import com.polamokh.homeinternetreview.utils.BitmapUtils;
import com.polamokh.homeinternetreview.utils.FirebaseAuthUtils;
import com.polamokh.homeinternetreview.viewmodel.ProfileViewModel;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class ProfileFragment extends Fragment {
    private static final String TAG = ProfileFragment.class.getSimpleName();

    private static final int RC_EDIT_PROFILE = 101;
    private static final int RC_PICK_IMAGE = 102;

    private ProfileViewModel profileViewModel;

    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    private ImageView profilePic;
    private ProgressBar progressBar;
    private TextView name;
    private TextView email;
    private Button removeAccount;
    private Button signOut;
    private Button signIn;
    private ImageButton editProfilePic;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (firebaseAuth.getCurrentUser() != null)
            return inflater.inflate(R.layout.fragment_profile, container, false);
        else
            return inflater.inflate(R.layout.fragment_profile_sign_in, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if ((firebaseAuth.getCurrentUser() != null)) {
            initializeUserProfile(view);
        } else {
            initializeSignIn(view);
        }
    }

    private void initializeUserProfile(View view) {
        profileViewModel = new ViewModelProvider(ViewModelStore::new)
                .get(ProfileViewModel.class);

        progressBar = view.findViewById(R.id.profile_progress);
        profilePic = view.findViewById(R.id.profile_pic);
        name = view.findViewById(R.id.profile_name_text);
        email = view.findViewById(R.id.profile_email_text);
        removeAccount = view.findViewById(R.id.profile_remove_account);
        editProfilePic = view.findViewById(R.id.profile_pic_edit);
        signOut = view.findViewById(R.id.profile_sign_out);

        profileViewModel.getUser().observe(getViewLifecycleOwner(), user -> {
            Glide.with(this)
                    .load(user.getPhotoUrl())
                    .placeholder(R.drawable.ic_profile_24dp)
                    .circleCrop()
                    .into(profilePic);

            editProfilePic.setOnClickListener(v -> startPickImageActivity());

            name.setText(user.getDisplayName());
            name.setOnClickListener(v -> startEditProfileNameActivity());

            email.setText(user.getEmail());

            signOut.setOnClickListener(v -> {
                firebaseAuth.signOut();
                reloadFragment();
            });

            removeAccount.setOnClickListener(v -> {
                profileViewModel.deleteUserProfilePicture();
                profileViewModel.deleteUserReviews();
                profileViewModel.deleteUserAccount()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful())
                                reloadFragment();
                        });
            });
        });
    }

    private void initializeSignIn(View view) {
        signIn = view.findViewById(R.id.profile_sign_in);
        signIn.setOnClickListener(v -> FirebaseAuthUtils.startAuthUserActivity(this));
    }

    public void reloadFragment() {
        if (isVisible())
            getParentFragmentManager().beginTransaction()
                    .detach(this).attach(this).commit();
    }

    private void startEditProfileNameActivity() {
        startActivityForResult(new Intent(requireContext(), EditProfileNameActivity.class)
                , RC_EDIT_PROFILE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case FirebaseAuthUtils.RC_SIGN_IN:
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    IdpResponse response = IdpResponse.fromResultIntent(data);

                    if (response.isNewUser() && user.getPhotoUrl() != null) {
                        Glide.with(this)
                                .asBitmap()
                                .load(user.getPhotoUrl())
                                .into(new CustomTarget<Bitmap>() {
                                    @Override
                                    public void onResourceReady(@NonNull Bitmap resource,
                                                                Transition<? super Bitmap> transition) {
                                        profileViewModel.uploadProfilePicture(
                                                BitmapUtils.convertBitmapToInputStream(resource));
                                    }

                                    @Override
                                    public void onLoadCleared(@Nullable Drawable placeholder) {
                                    }
                                });
                    }
                    reloadFragment();
                    return;

                case RC_EDIT_PROFILE:
                    //reloadFragment();
                    return;

                case RC_PICK_IMAGE:
                    startCropImageActivity(data.getData());
                    return;

                case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                    handleCropImageResult(CropImage.getActivityResult(data).getUri());
                    return;

                default:
            }
        }
    }

    private void handleCropImageResult(Uri imageUri) {
        InputStream inputStream = null;
        try {
            inputStream =
                    requireContext().getContentResolver().openInputStream(imageUri);

            Bitmap bitmap =
                    BitmapUtils.convertInputStreamToBitmap(inputStream, imageUri.toString());

            profileViewModel.uploadProfilePicture(BitmapUtils.convertBitmapToInputStream(bitmap));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void startPickImageActivity() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        if (intent.resolveActivity(requireContext().getPackageManager()) != null)
            startActivityForResult(intent, RC_PICK_IMAGE);
    }

    private void startCropImageActivity(Uri data) {
        CropImage.activity(data)
                .setAspectRatio(1, 1)
                .setFixAspectRatio(true)
                .start(requireContext(), this);
    }
}
