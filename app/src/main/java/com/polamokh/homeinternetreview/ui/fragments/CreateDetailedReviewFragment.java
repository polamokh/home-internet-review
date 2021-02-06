package com.polamokh.homeinternetreview.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;

import androidx.annotation.NonNull;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.polamokh.homeinternetreview.R;
import com.polamokh.homeinternetreview.data.DetailedReview;
import com.polamokh.homeinternetreview.data.Review;

public class CreateDetailedReviewFragment extends AbstractCreateReviewFragment {

    private RatingBar speedRatingBar;
    private RatingBar priceRatingBar;
    private RatingBar reliabilityRatingBar;
    private RatingBar customerServiceRatingBar;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        View view = inflater.inflate(R.layout.fragment_create_detailed_review, container, false);

        initializeUi(view);

        return view;
    }

    @Override
    void initializeUi(View view) {
        speedRatingBar = view.findViewById(R.id.detailed_review_speed_rating);
        priceRatingBar = view.findViewById(R.id.detailed_review_price_rating);
        reliabilityRatingBar = view.findViewById(R.id.detailed_review_reliability_rating);
        customerServiceRatingBar = view.findViewById(R.id.detailed_review_customer_service_rating);

        TextInputLayout descriptionLayout = view.findViewById(R.id.review_description_text);
        descriptionText = descriptionLayout.getEditText();
    }

    @Override
    Review getReviewDetails() {
        return new DetailedReview(speedRatingBar.getRating(),
                priceRatingBar.getRating(),
                reliabilityRatingBar.getRating(),
                customerServiceRatingBar.getRating(),
                getArguments().getString(EXTRA_COMPANY_NAME),
                getArguments().getString(EXTRA_GOVERNORATE_NAME),
                descriptionText.getText().toString(),
                System.currentTimeMillis(),
                FirebaseAuth.getInstance().getCurrentUser().getUid());
    }
}
