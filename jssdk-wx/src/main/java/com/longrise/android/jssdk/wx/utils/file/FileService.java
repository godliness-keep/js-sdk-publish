package com.longrise.android.jssdk.wx.utils.file;

import java.io.File;
import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by godliness on 2020-04-17.
 *
 * @author godliness
 */
public class FileService {

    public static void uploadFile(String url, String localId) {
        uploadFile(url, localId, null);
    }

    public static void uploadFile(String url, String localId, OnFileProgressListener progressListener) {
        final File file = new File(localId);

        final RequestBody requestBody = new MultipartBody.Builder()
                .addFormDataPart("file", file.getName(), RequestBody.create(MediaType.parse(localId), file))
                .build();

        final Request request = new Request.Builder()
                .url(url)
                .post(new ProgressRequestBody(requestBody, progressListener)).build();

        OkHttpHelper.get().newCall(request).enqueue(progressListener);
    }

    public static void downloadFile(String url) {
        downloadFile(url, null);
    }

    public static void downloadFile(String url, final OnFileProgressListener progressListener) {
        final Request request = new Request.Builder()
                .url(url)
                .build();

        OkHttpHelper.getBuilder().addNetworkInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                final Response response = chain.proceed(chain.request());
                return response.newBuilder().body(new ProgressResponseBody(response.body(), progressListener)).build();
            }
        }).build().newCall(request).enqueue(progressListener);
    }
}
