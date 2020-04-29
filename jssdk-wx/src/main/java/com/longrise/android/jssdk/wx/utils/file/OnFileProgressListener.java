package com.longrise.android.jssdk.wx.utils.file;

import okhttp3.Callback;

/**
 * Created by godliness on 2020-04-17.
 *
 * @author godliness
 */
public interface OnFileProgressListener extends Callback {

    void onUploadProgress(long current, long total, boolean done);
}
