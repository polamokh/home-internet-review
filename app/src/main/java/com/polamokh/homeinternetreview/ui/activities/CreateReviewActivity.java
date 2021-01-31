package com.polamokh.homeinternetreview.ui.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.polamokh.homeinternetreview.R;
import com.polamokh.homeinternetreview.data.User;
import com.polamokh.homeinternetreview.data.dao.UserDao;
import com.polamokh.homeinternetreview.utils.FirebaseAuthUtils;

public class CreateReviewActivity extends AppCompatActivity {

    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private NavController navController;

    private FirebaseAuth.AuthStateListener authStateListener;
    private NavController.OnDestinationChangedListener onDestinationChangedListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        navController =
                Navigation.findNavController(this, R.id.create_review_nav_host_fragment);

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph())
                .build();

        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        onDestinationChangedListener = (controller, destination, arguments) -> {
            if (destination.getId() == navController.getGraph().getStartDestination()) {
                ActionBar actionBar = getSupportActionBar();
                if (actionBar != null) {
                    actionBar.setDisplayHomeAsUpEnabled(true);
                    actionBar.setHomeAsUpIndicator(R.drawable.ic_close_24dp);
                }
            }
        };

        authStateListener = firebaseAuth -> {
            if (firebaseAuth.getCurrentUser() == null)
                FirebaseAuthUtils.startAuthUserActivity(CreateReviewActivity.this,
                        FirebaseAuthUtils.RC_SIGN_IN);
        };
    }

    @Override
    protected void onResume() {
        super.onResume();

        firebaseAuth.addAuthStateListener(authStateListener);
        navController.addOnDestinationChangedListener(onDestinationChangedListener);
    }

    @Override
    protected void onPause() {
        firebaseAuth.removeAuthStateListener(authStateListener);
        navController.removeOnDestinationChangedListener(onDestinationChangedListener);

        super.onPause();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == FirebaseAuthUtils.RC_SIGN_IN) {
                IdpResponse response = IdpResponse.fromResultIntent(data);
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (response.isNewUser())
                    UserDao.getInstance().create(new User(user));
            }
        } else
            finish();
    }
}
