package com.polamokh.homeinternetreview.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.fragment.app.Fragment;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public final class ImagePickerUtils {

    public static final int RC_PICK_IMAGE = 102;

    private ImagePickerUtils() {

    }

    public static Intent createPickImageIntent(Fragment fragment) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        return intent;
    }

    public static void startPickImageActivity(Fragment fragment) {
        Intent intent = createPickImageIntent(fragment);
        if (intent.resolveActivity(fragment.requireContext().getPackageManager()) != null)
            fragment.startActivityForResult(intent, RC_PICK_IMAGE);
    }

    public static void startCropImageActivity(Uri data, Fragment fragment) {
        CropImage.activity(data)
                .setAspectRatio(1, 1)
                .setFixAspectRatio(true)
                .setCropShape(CropImageView.CropShape.OVAL)
                .start(fragment.requireContext(), fragment);
    }
}
