package com.longrise.android.jssdk.wx.utils;

import android.content.Context;

import com.longrise.android.jssdk.wx.R;


/**
 * Created by godliness on 2020-04-17.
 *
 * @author godliness
 */
public class UrlUtil {

    public static String getUploadImageServiceUrl(Context context) {
        return context.getString(R.string.upload_image_service_url);
    }

    public static String getDownloadImageServiceUrl(Context context) {
        return context.getString(R.string.download_image_service_url);
    }
}
