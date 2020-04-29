package com.longrise.android.jssdk.wx.utils.file;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

/**
 * Created by godliness on 2020-04-17.
 *
 * @author godliness
 */
public final class ProgressResponseBody extends ResponseBody {

    private final ResponseBody mBody;
    private final OnFileProgressListener mCallback;
    private BufferedSource mSource;
    private long mContentLength;

    public ProgressResponseBody(ResponseBody body) {
        this(body, null);
    }

    public ProgressResponseBody(ResponseBody body, OnFileProgressListener callback) {
        this.mBody = body;
        this.mCallback = callback;
    }

    @Override
    public MediaType contentType() {
        return mBody.contentType();
    }

    @Override
    public long contentLength() {
        return mContentLength = mBody.contentLength();
    }

    @Override
    public BufferedSource source() {
        if (mSource == null) {
            mSource = Okio.buffer(source(mBody.source()));
        }
        return mSource;
    }

    public void writeTo(String path, String fileName) {
        final FileOutputStream fos;
        final byte[] buffer = new byte[1024 * 4];
        final InputStream stream = byteStream();
        try {
            fos = new FileOutputStream(new File(path, fileName));
            int length;
            while ((length = stream.read(buffer)) != -1) {
                fos.write(buffer, 0, length);
            }
            fos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Source source(Source source) {
        return new ForwardingSource(source) {

            private long mCurrentPosition;

            @Override
            public long read(Buffer sink, long byteCount) throws IOException {
                long readed = super.read(sink, byteCount);
                if (mCallback != null) {
                    mCurrentPosition += readed != -1 ? readed : 0;
                    mCallback.onUploadProgress(mCurrentPosition, mContentLength, mCurrentPosition == mContentLength);
                }
                return readed;
            }
        };
    }
}
