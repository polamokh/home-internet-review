package com.polamokh.homeinternetreview.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.polamokh.homeinternetreview.R;
import com.polamokh.homeinternetreview.data.Review;

public class CreateReviewFragment extends AbstractCreateReviewFragment {

    private RatingBar ratingBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        View view = inflater.inflate(R.layout.fragment_create_review, container, false);

        initializeUi(view);

        return view;
    }

    @Override
    void initializeUi(View view) {
        ratingBar = view.findViewById(R.id.review_rating_bar);

        TextInputLayout descriptionLayout = view.findViewById(R.id.review_description_text);
        descriptionText = descriptionLayout.getEditText();
    }

    @Override
    Review getReviewDetails() {
        return new Review(getArguments().getString(EXTRA_COMPANY_NAME),
                ratingBar.getRating(),
                getArguments().getString(EXTRA_GOVERNORATE_NAME),
                descriptionText.getText().toString(),
                System.currentTimeMillis(),
                FirebaseAuth.getInstance().getCurrentUser().getUid());
    }
}
