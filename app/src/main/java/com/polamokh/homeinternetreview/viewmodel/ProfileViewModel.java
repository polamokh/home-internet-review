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
import com.polamokh.homeinternetreview.data.dao.CompanyDao;
import com.polamokh.homeinternetreview.data.dao.ReviewDao;
import com.polamokh.homeinternetreview.utils.FirebaseAuthUtils;
import com.polamokh.homeinternetreview.utils.FirebaseStorageUtils;

import java.io.InputStream;

public class ProfileViewModel extends ViewModel {
    private static final String TAG = ProfileViewModel.class.getSimpleName();

    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    private MutableLiveData<FirebaseUser> userLiveData;
    private MutableLiveData<Boolean> isLoadingLiveData;

    public void setProfileName(String name) {
        isLoadingLiveData.setValue(true);
        FirebaseAuthUtils.updateProfileName(name)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful())
                        userLiveData.postValue(user);
                    isLoadingLiveData.postValue(false);
                });
    }

    public void setProfilePicture(Uri uri) {
        isLoadingLiveData.setValue(true);
        FirebaseAuthUtils.updateProfilePicture(uri)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful())
                        userLiveData.postValue(user);
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
        getProfileReviews()
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Review review = dataSnapshot.getValue(Review.class);
                            ReviewDao.getInstance().delete(dataSnapshot.getKey());
                            if (review != null) {
                                CompanyDao.getInstance().updateCompaniesStandings(review,
                                        CompanyDao.updateState.REMOVED);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    public Task<Void> deleteProfilePicture() {
        return FirebaseStorageUtils.deleteProfilePicture(getUser().getValue().getUid());
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

    public LiveData<Boolean> isLoading() {
        if (isLoadingLiveData == null)
            isLoadingLiveData = new MutableLiveData<>();

        isLoadingLiveData.setValue(false);

        return isLoadingLiveData;
    }
}
