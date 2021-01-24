package com.polamokh.homeinternetreview.utils;

import android.net.Uri;

import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;

import java.io.InputStream;

public final class FirebaseStorageUtils {
    private static final String USERS_PROFILE_PIC_LOCATION = "users";
    private static final FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();

    private FirebaseStorageUtils() {
    }

    public static Task<Uri> uploadProfilePictureAsStream(String fileName, InputStream stream) {
        return firebaseStorage.getReference(USERS_PROFILE_PIC_LOCATION)
                .child(fileName)
                .putStream(stream)
                .continueWithTask(task -> {
                    if (!task.isSuccessful())
                        throw task.getException();

                    return getProfilePictureUri(fileName);
                });
    }

    public static Task<Void> deleteProfilePicture(String fileName) {
        return firebaseStorage.getReference(USERS_PROFILE_PIC_LOCATION)
                .child(fileName)
                .delete();
    }

    public static Task<Uri> getProfilePictureUri(String fileName) {
        return firebaseStorage.getReference(USERS_PROFILE_PIC_LOCATION)
                .child(fileName)
                .getDownloadUrl();
    }
}
