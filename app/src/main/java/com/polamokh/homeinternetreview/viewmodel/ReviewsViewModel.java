package com.polamokh.homeinternetreview.viewmodel;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.polamokh.homeinternetreview.data.Review;
import com.polamokh.homeinternetreview.data.dao.ReviewDao;

import java.util.LinkedList;
import java.util.List;

public class ReviewsViewModel extends ViewModel {
    private static final String TAG = ReviewsViewModel.class.getSimpleName();

    private final ReviewDao reviewDao = ReviewDao.getInstance();

    private List<Review> reviews;
    private MutableLiveData<List<Review>> reviewsLiveData;
    private MutableLiveData<Boolean> hasAvailableReviewsLiveData;
    private MutableLiveData<Boolean> isLoadingLiveData;

    public LiveData<List<Review>> getAll() {
        if (reviewsLiveData == null) {
            reviewsLiveData = new MutableLiveData<>();

            reviewDao.getAll()
                    .limitToLast(20)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            reviews = new LinkedList<>();
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                Log.d(TAG, "onDataChange: " + dataSnapshot.getKey());
                                reviews.add(0, dataSnapshot.getValue(Review.class));
                            }

                            hasAvailableReviewsLiveData.postValue(reviews.size() != 0);
                            reviewsLiveData.postValue(reviews);
                            isLoadingLiveData.postValue(false);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            isLoadingLiveData.postValue(false);
                        }
                    });
        }

        return reviewsLiveData;
    }

    public LiveData<Boolean> isLoading() {
        if (isLoadingLiveData == null) {
            isLoadingLiveData = new MutableLiveData<>();
            isLoadingLiveData.setValue(true);
        }

        return isLoadingLiveData;
    }

    public LiveData<Boolean> hasAvailableReviews() {
        if (hasAvailableReviewsLiveData == null) {
            hasAvailableReviewsLiveData = new MutableLiveData<>();
            hasAvailableReviewsLiveData.setValue(true);
        }

        return hasAvailableReviewsLiveData;
    }
}
