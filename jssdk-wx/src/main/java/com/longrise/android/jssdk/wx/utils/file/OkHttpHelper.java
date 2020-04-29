package com.longrise.android.jssdk.wx.utils.file;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * Created by godliness on 2020-04-17.
 *
 * @author godliness
 */
public final class OkHttpHelper {

    public static OkHttpClient get() {
        return new OkHttpClient.Builder()
                .connectTimeout(6, TimeUnit.SECONDS)
                .readTimeout(6, TimeUnit.SECONDS)
                .build();
    }

    public static OkHttpClient.Builder getBuilder() {
        return new OkHttpClient.Builder()
                .connectTimeout(6, TimeUnit.SECONDS)
                .readTimeout(6, TimeUnit.SECONDS);
    }
}
