package com.polamokh.homeinternetreview.ui.fragments;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.polamokh.homeinternetreview.R;
import com.polamokh.homeinternetreview.data.Review;
import com.polamokh.homeinternetreview.viewmodel.ReviewViewModel;

abstract class AbstractCreateReviewFragment extends Fragment {
    static final String EXTRA_COMPANY_NAME = "companyName";
    static final String EXTRA_GOVERNORATE_NAME = "governorateName";

    ReviewViewModel reviewViewModel;

    EditText descriptionText;

    abstract void initializeUi(View view);

    abstract Review getReviewDetails();

    private void createReview() {
        reviewViewModel.create(getReviewDetails());
        requireActivity().finish();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.create_review_menu, menu);

        Button postButton = menu.findItem(R.id.action_post_review).getActionView()
                .findViewById(R.id.action_post_review_button);
        postButton.setOnClickListener(v -> createReview());
    }
}
