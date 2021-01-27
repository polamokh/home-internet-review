package com.polamokh.homeinternetreview.utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import androidx.fragment.app.Fragment;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.Arrays;
import java.util.List;

public final class FirebaseAuthUtils {
    public static final int RC_SIGN_IN = 100;

    private static final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    private FirebaseAuthUtils() {

    }

    private static List<AuthUI.IdpConfig> getAvailableProviders() {
        return Arrays.asList(new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());
    }

    private static Intent createAuthUiIntent(List<AuthUI.IdpConfig> providers) {
        return AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setIsSmartLockEnabled(false)
                .setAvailableProviders(providers)
                .build();
    }

    public static void startAuthUserActivity(Activity activity, int requestCode) {
        activity.startActivityForResult(createAuthUiIntent(getAvailableProviders()),
                requestCode);
    }

    public static void startAuthUserActivity(Fragment fragment, int requestCode) {
        fragment.startActivityForResult(createAuthUiIntent(getAvailableProviders()),
                requestCode);
    }

    public static Task<Void> updateProfileName(String newName) {
        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                .setDisplayName(newName)
                .build();

        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null)
            throw new IllegalStateException();

        return user.updateProfile(request);
    }

    public static Task<Void> updateProfilePicture(Uri uri) {
        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                .setPhotoUri(uri)
                .build();

        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null)
            throw new IllegalStateException();

        return user.updateProfile(request);
    }
}
