package com.polamokh.homeinternetreview.viewmodel;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.polamokh.homeinternetreview.data.dao.ReviewDao;
import com.polamokh.homeinternetreview.utils.FirebaseAuthUtils;
import com.polamokh.homeinternetreview.utils.FirebaseStorageUtils;

import java.io.InputStream;

public class ProfileViewModel extends ViewModel {
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    private MutableLiveData<FirebaseUser> userMutableLiveData;

    public void setUserProfileName(String name) {
        FirebaseAuthUtils.updateProfileName(name)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful())
                        userMutableLiveData.postValue(user);
                });
    }

    public void setUserProfilePicture(Uri uri) {
        FirebaseAuthUtils.updateProfilePicture(uri)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful())
                        userMutableLiveData.postValue(user);
                });
    }

    public void uploadProfilePicture(InputStream inputStream) {
        FirebaseStorageUtils.uploadProfilePictureAsStream(user.getUid(), inputStream)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        setUserProfilePicture(task.getResult());
                    }
                });
    }

    public void deleteUserReviews() {
        ReviewDao.getInstance().getAll().orderByChild("userId").equalTo(user.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren())
                            ReviewDao.getInstance().delete(dataSnapshot.getKey());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    public Task<Void> deleteUserProfilePicture() {
        return FirebaseStorageUtils.deleteProfilePicture(user.getUid());
    }

    public Task<Void> deleteUserAccount() {
        return user.delete();
    }

    public LiveData<FirebaseUser> getUser() {
        if (userMutableLiveData == null)
            userMutableLiveData = new MutableLiveData<>();

        userMutableLiveData.postValue(user);

        return userMutableLiveData;
    }
}
