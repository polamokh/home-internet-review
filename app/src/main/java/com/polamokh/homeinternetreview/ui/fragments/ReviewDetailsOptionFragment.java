package com.polamokh.homeinternetreview.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.polamokh.homeinternetreview.R;

public class ReviewDetailsOptionFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_review_details_option, container, false);

        view.findViewById(R.id.review_details_option_quick)
                .setOnClickListener(v -> {
                    Navigation.findNavController(view)
                            .navigate(R.id.action_reviewDetailsOptionFragment_to_createReviewFragment,
                                    getArguments());
                });

        view.findViewById(R.id.review_details_option_detailed)
                .setOnClickListener(v -> {
                    Navigation.findNavController(view)
                            .navigate(R.id.action_reviewDetailsOptionFragment_to_createDetailedReviewFragment,
                                    getArguments());
                });

        return view;
    }
}
