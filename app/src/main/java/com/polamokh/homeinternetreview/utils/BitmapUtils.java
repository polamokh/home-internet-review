package com.polamokh.homeinternetreview.utils;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public final class BitmapUtils {

    private static final int PROFILE_IMAGE_SIZE = 512; //512*512 Pixels

    private BitmapUtils() {

    }

    public static InputStream convertBitmapToInputStream(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        reduceBitmapSize(bitmap).compress(Bitmap.CompressFormat.JPEG, 90, stream);
        return new ByteArrayInputStream(stream.toByteArray());
    }

    public static Bitmap convertInputStreamToBitmap(InputStream inputStream, String srcName) {
        return ((BitmapDrawable) Drawable.createFromStream(inputStream, srcName)).getBitmap();
    }

    public static Bitmap reduceBitmapSize(Bitmap bitmap) {
        return Bitmap.createScaledBitmap(bitmap, PROFILE_IMAGE_SIZE, PROFILE_IMAGE_SIZE, false);
    }
}
