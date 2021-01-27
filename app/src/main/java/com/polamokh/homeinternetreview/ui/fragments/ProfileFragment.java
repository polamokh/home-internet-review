package com.polamokh.homeinternetreview.ui.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.polamokh.homeinternetreview.R;
import com.polamokh.homeinternetreview.utils.BitmapUtils;
import com.polamokh.homeinternetreview.utils.FirebaseAuthUtils;
import com.polamokh.homeinternetreview.utils.FirebaseStorageUtils;
import com.polamokh.homeinternetreview.utils.GoogleSignInUtils;
import com.polamokh.homeinternetreview.viewmodel.ProfileViewModel;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class ProfileFragment extends Fragment {
    private static final String TAG = ProfileFragment.class.getSimpleName();

    private static final int RC_PICK_IMAGE = 102;
    private static final int RC_REAUTHORIZE = 103;

    private ProfileViewModel profileViewModel;

    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    private LinearLayout profileLayout;
    private ConstraintLayout loadingLayout;
    private ImageView profilePic;
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

    private void initializeSignIn(View view) {
        signIn = view.findViewById(R.id.profile_sign_in);
        signIn.setOnClickListener(v -> FirebaseAuthUtils.startAuthUserActivity(this,
                FirebaseAuthUtils.RC_SIGN_IN));
    }

    private void initializeUserProfile(View view) {
        profileViewModel = new ViewModelProvider(ViewModelStore::new)
                .get(ProfileViewModel.class);

        profileLayout = view.findViewById(R.id.profile_layout);
        loadingLayout = view.findViewById(R.id.loading_layout);
        profilePic = view.findViewById(R.id.profile_pic);
        name = view.findViewById(R.id.profile_name_text);
        email = view.findViewById(R.id.profile_email_text);
        removeAccount = view.findViewById(R.id.profile_remove_account);
        editProfilePic = view.findViewById(R.id.profile_pic_edit);
        signOut = view.findViewById(R.id.profile_sign_out);

        profileViewModel.setUser(firebaseAuth.getCurrentUser());
        profileViewModel.isLoading().observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean)
                showLoading();
            else
                hideLoading();
        });

        profileViewModel.getUser().observe(getViewLifecycleOwner(), user -> {
            Toast.makeText(requireContext(),
                    user.getUid(),
                    Toast.LENGTH_SHORT)
                    .show();

            Glide.with(this)
                    .load(user.getPhotoUrl())
                    .placeholder(R.drawable.ic_profile_24dp)
                    .circleCrop()
                    .into(profilePic);

            name.setText(user.getDisplayName());
            email.setText(user.getEmail());
        });

        editProfilePic.setOnClickListener(v -> startPickImageActivity());
        name.setOnClickListener(v -> showEditProfileNameDialog());

        signOut.setOnClickListener(v -> {
            firebaseAuth.signOut();
            reloadFragment();
        });

        removeAccount.setOnClickListener(v -> {
            FirebaseUser user = profileViewModel.getUser().getValue();
            reauthorizeAndDeleteUser(user);
        });
    }

    private void reauthorizeAndDeleteUser(FirebaseUser user) {
        switch (user.getProviderData().get(1).getProviderId()) {
            case EmailAuthProvider.PROVIDER_ID:
                showAuthorizeProfileDialog();
                return;

            case GoogleAuthProvider.PROVIDER_ID:
                GoogleSignInUtils.startSignInActivity(this, GoogleSignInUtils.RC_SIGN_IN);
                return;

            default:
        }
    }

    private void hideLoading() {
        loadingLayout.setVisibility(View.GONE);
        profileLayout.setVisibility(View.VISIBLE);
    }

    private void showLoading() {
        profileLayout.setVisibility(View.GONE);
        loadingLayout.setVisibility(View.VISIBLE);
    }

    public void reloadFragment() {
        if (isVisible())
            getParentFragmentManager().beginTransaction()
                    .detach(this).attach(this).commit();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case FirebaseAuthUtils.RC_SIGN_IN:
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    IdpResponse response = IdpResponse.fromResultIntent(data);
                    Log.d(TAG, "onActivityResult: " + user.getPhotoUrl());
                    if (response.isNewUser() && user.getPhotoUrl() != null)
                        handleNewUserWithProfilePicture(user);
                    reloadFragment();
                    return;

                case GoogleSignInUtils.RC_SIGN_IN:
                    GoogleSignIn.getSignedInAccountFromIntent(data)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    AuthCredential credential =
                                            getGoogleCredential(task.getResult().getIdToken());
                                    reauthorizeProfile(credential);
                                }
                            });
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

    private void handleNewUserWithProfilePicture(FirebaseUser user) {
        Glide.with(this)
                .asBitmap()
                .load(user.getPhotoUrl())
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource,
                                                Transition<? super Bitmap> transition) {
                        FirebaseStorageUtils.uploadProfilePictureAsStream(user.getUid(),
                                BitmapUtils.convertBitmapToInputStream(resource))
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful())
                                        FirebaseAuthUtils
                                                .updateProfilePicture(task.getResult());
                                });
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                    }
                });
    }

    private void handleCropImageResult(Uri imageUri) {
        InputStream inputStream;
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

    private void showEditProfileNameDialog() {
        EditProfileNameDialogFragment dialogFragment =
                new EditProfileNameDialogFragment(profileViewModel.getUser().getValue().getDisplayName(),
                        extra -> profileViewModel.setProfileName(extra.toString()));
        dialogFragment.show(getParentFragmentManager(), null);
    }

    private void showDeleteProfileDialog() {
        DeleteProfileDialogFragment dialogFragment =
                new DeleteProfileDialogFragment(extra -> {
                    profileViewModel.deleteProfilePicture();
                    profileViewModel.deleteReviews();
                    profileViewModel.deleteProfile()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful())
                                    reloadFragment();
                                else
                                    Log.d(TAG, "onActivityResult: DELETE PROFILE FAILED");
                            });
                });
        dialogFragment.show(getParentFragmentManager(), null);
    }

    private void showAuthorizeProfileDialog() {
        AuthorizeProfileDialogFragment dialogFragment =
                new AuthorizeProfileDialogFragment(extra -> {
                    AuthCredential credential =
                            getEmailCredential(profileViewModel.getUser().getValue().getEmail(),
                                    extra.toString());
                    reauthorizeProfile(credential);
                });
        dialogFragment.show(getParentFragmentManager(), null);
    }

    private void reauthorizeProfile(AuthCredential credential) {
        profileViewModel.reauthorizeProfile(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful())
                        showDeleteProfileDialog();
                    else
                        Toast.makeText(requireContext(),
                                task.getException().getMessage(),
                                Toast.LENGTH_SHORT)
                                .show();
                });
    }

    private AuthCredential getEmailCredential(String email, String password) {
        return EmailAuthProvider.getCredential(email, password);
    }

    private AuthCredential getGoogleCredential(String idToken) {
        return GoogleAuthProvider.getCredential(idToken, null);
    }
}
