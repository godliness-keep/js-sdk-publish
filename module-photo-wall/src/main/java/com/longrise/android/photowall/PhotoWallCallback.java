package com.longrise.android.photowall;

import android.content.Context;

import com.longrise.android.photowall.activity.PhotoActivity;

/**
 * Created by godliness on 2020-04-19.
 *
 * @author godliness
 */
public final class PhotoWallCallback {

    private PhotoSelectedCallback mCallback;

    public interface PhotoSelectedCallback {

        void onSelected(String[] values);
    }

    public static PhotoWallCallback getInstance() {
        return Holder.SELECTED_CALLBACK;
    }

    public PhotoWallCallback callback(PhotoSelectedCallback callback) {
        this.mCallback = callback;
        return this;
    }

    public void openPhotoWall(Context context, int maxSelected) {
        PhotoActivity.openPhotoActivity(context, maxSelected);
    }

    public void onCallback(String[] values) {
        if (mCallback != null) {
            mCallback.onSelected(values);
        }
        mCallback = null;
    }

    private static final class Holder {
        private static final PhotoWallCallback SELECTED_CALLBACK = new PhotoWallCallback();
    }
}
