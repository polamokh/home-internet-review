package com.polamokh.homeinternetreview.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.Fragment;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.polamokh.homeinternetreview.R;

public final class GoogleSignInUtils {

    public static final int RC_SIGN_IN = 103;

    private GoogleSignInUtils() {

    }

    public static Intent createSignInIntent(Context context) {
        GoogleSignInOptions gso =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(context.getString(R.string.server_client_id))
                        .requestEmail()
                        .build();

        GoogleSignInClient client = GoogleSignIn.getClient(context, gso);
        return client.getSignInIntent();
    }

    public static void startSignInActivity(Activity activity, int requestCode) {
        activity.startActivityForResult(createSignInIntent(activity),
                requestCode);
    }

    public static void startSignInActivity(Fragment fragment, int requestCode) {
        fragment.startActivityForResult(createSignInIntent(fragment.requireContext()),
                requestCode);
    }
}
