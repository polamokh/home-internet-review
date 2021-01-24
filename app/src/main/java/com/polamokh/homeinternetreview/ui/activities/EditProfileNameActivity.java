package com.polamokh.homeinternetreview.ui.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.polamokh.homeinternetreview.R;
import com.polamokh.homeinternetreview.ui.fragments.ProfileFragment;
import com.polamokh.homeinternetreview.utils.FirebaseAuthUtils;

public class EditProfileNameActivity extends AppCompatActivity {
    private static final String TAG = EditProfileNameActivity.class.getSimpleName();

    private final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    private EditText editText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile_name);

        TextInputLayout inputLayout = findViewById(R.id.edit_profile_name_text);
        editText = inputLayout.getEditText();
        editText.setText(user.getDisplayName());

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_24dp);
            toolbar.setNavigationOnClickListener(v -> finish());
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_profile_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_save_changes_profile) {
            FirebaseAuthUtils.updateProfileName(editText.getText().toString().trim())
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "onOptionsItemSelected: ", e);
                        setResult(RESULT_CANCELED);
                    })
                    .addOnSuccessListener(aVoid -> {
                        setResult(RESULT_OK);
                        finish();
                    });
            return true;
        }
        return false;
    }
}
