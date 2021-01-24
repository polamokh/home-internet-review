package com.polamokh.homeinternetreview.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.polamokh.homeinternetreview.data.Review;
import com.polamokh.homeinternetreview.data.dao.ReviewDao;

public class ReviewViewModel extends ViewModel {
    private final ReviewDao reviewDao = ReviewDao.getInstance();

    private MutableLiveData<Review> reviewMutableLiveData;


    public Task<Void> create(Review review) {
        return reviewDao.create(review);
    }

    public Task<Void> edit(String id, Review newReview) {
        return reviewDao.edit(id, newReview);
    }

    public Task<Void> delete(String id) {
        return reviewDao.delete(id);
    }

    public LiveData<Review> getById(String id) {
        if (reviewMutableLiveData == null)
            reviewMutableLiveData = new MutableLiveData<>();

        reviewDao.getById(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Review review = snapshot.getValue(Review.class);
                reviewMutableLiveData.postValue(review);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return reviewMutableLiveData;
    }


}
