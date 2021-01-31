package com.polamokh.homeinternetreview.viewmodel;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.polamokh.homeinternetreview.data.Review;
import com.polamokh.homeinternetreview.data.User;
import com.polamokh.homeinternetreview.data.dao.CompanyDao;
import com.polamokh.homeinternetreview.data.dao.ReviewDao;
import com.polamokh.homeinternetreview.data.dao.UserDao;
import com.polamokh.homeinternetreview.utils.FirebaseAuthUtils;
import com.polamokh.homeinternetreview.utils.FirebaseStorageUtils;

import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

public class ProfileViewModel extends ViewModel {
    private static final String TAG = ProfileViewModel.class.getSimpleName();

    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private List<Review> reviews;

    private MutableLiveData<FirebaseUser> userLiveData;
    private MutableLiveData<List<Review>> reviewsLiveData;
    private MutableLiveData<Boolean> isLoadingLiveData;

    public void setProfileName(String name) {
        isLoadingLiveData.setValue(true);
        FirebaseAuthUtils.updateProfileName(name)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        UserDao.getInstance().edit(user.getUid(), new User(user));
                        userLiveData.postValue(user);
                    }
                    isLoadingLiveData.postValue(false);
                });
    }

    public void setProfilePicture(Uri uri) {
        isLoadingLiveData.setValue(true);
        FirebaseAuthUtils.updateProfilePicture(uri)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        UserDao.getInstance().edit(user.getUid(), new User(user));
                        userLiveData.postValue(user);
                    }
                    isLoadingLiveData.postValue(false);
                });
    }

    public void uploadProfilePicture(InputStream inputStream) {
        isLoadingLiveData.setValue(true);
        FirebaseStorageUtils.uploadProfilePictureAsStream(getUser().getValue().getUid(), inputStream)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful())
                        setProfilePicture(task.getResult());
                    isLoadingLiveData.postValue(false);
                });
    }

    public Query getProfileReviews() {
        return ReviewDao.getInstance()
                .getAll()
                .orderByChild("userId")
                .equalTo(getUser().getValue().getUid());
    }

    public void deleteReviews() {
        for (Review review : reviews) {
            ReviewDao.getInstance().delete(review.getId())
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            CompanyDao.getInstance().updateCompaniesStandings(review,
                                    CompanyDao.updateState.REMOVED);
                        }
                    });
        }
    }

    public Task<Void> deleteProfilePicture() {
        return FirebaseStorageUtils.deleteProfilePicture(getUser().getValue().getUid());
    }

    public Task<Void> deleteProfileInfo() {
        return UserDao.getInstance().delete(user.getUid());
    }

    public Task<Void> deleteProfile() {
        return getUser().getValue().delete();
    }

    public Task<Void> reauthorizeProfile(AuthCredential credential) {
        return getUser().getValue().reauthenticate(credential);
    }

    public void setUser(FirebaseUser user) {
        if (userLiveData == null)
            userLiveData = new MutableLiveData<>();

        userLiveData.setValue(user);
    }

    public LiveData<FirebaseUser> getUser() {
        return userLiveData;
    }

    public LiveData<List<Review>> getReviews() {
        if (reviewsLiveData == null)
            reviewsLiveData = new MutableLiveData<>();

        getProfileReviews().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                reviews = new LinkedList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Review review = dataSnapshot.getValue(Review.class);
                    review.setId(dataSnapshot.getKey());
                    reviews.add(0, review);
                }
                reviewsLiveData.setValue(reviews);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        return reviewsLiveData;
    }

    public LiveData<Boolean> isLoading() {
        if (isLoadingLiveData == null)
            isLoadingLiveData = new MutableLiveData<>();

        isLoadingLiveData.setValue(false);

        return isLoadingLiveData;
    }
}
