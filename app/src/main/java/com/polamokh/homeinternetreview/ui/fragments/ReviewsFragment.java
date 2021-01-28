package com.polamokh.homeinternetreview.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.polamokh.homeinternetreview.R;
import com.polamokh.homeinternetreview.ui.activities.CreateReviewActivity;
import com.polamokh.homeinternetreview.ui.adapters.ReviewsAdapter;
import com.polamokh.homeinternetreview.viewmodel.ReviewsViewModel;

public class ReviewsFragment extends Fragment {
    private ReviewsViewModel reviewsViewModel;

    private FloatingActionButton floatingActionButton;
    private RecyclerView reviewsRecyclerView;
    private ReviewsAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reviews, container, false);

        initializeUi(view);

        return view;
    }

    private void initializeUi(View view) {
        reviewsViewModel = new ViewModelProvider(this)
                .get(ReviewsViewModel.class);

        reviewsRecyclerView = view.findViewById(R.id.reviews_recycler_view);

        adapter = new ReviewsAdapter(Glide.with(this));

        reviewsViewModel.getAll().observe(getViewLifecycleOwner(), reviews -> {
            adapter.setReviews(reviews);
        });

        reviewsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        reviewsRecyclerView.setAdapter(adapter);

        floatingActionButton = view.findViewById(R.id.add_review_fab);
        floatingActionButton.setOnClickListener(v -> {
            showReviewActivity();
        });
    }

    private void showReviewActivity() {
        Intent intent = new Intent(requireContext(), CreateReviewActivity.class);
        startActivity(intent);
    }
}
