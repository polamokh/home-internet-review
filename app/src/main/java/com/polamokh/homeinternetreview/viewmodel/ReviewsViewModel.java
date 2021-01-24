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
    private MutableLiveData<List<Review>> mutableLiveData;

    public LiveData<List<Review>> getAll() {
        if (mutableLiveData == null)
            mutableLiveData = new MutableLiveData<>();

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

                        mutableLiveData.postValue(reviews);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        return mutableLiveData;
    }
}
