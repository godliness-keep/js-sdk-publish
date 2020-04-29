package com.longrise.android.jssdk.wx.utils.file;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;

/**
 * Created by godliness on 2020-04-17.
 *
 * @author godliness
 */
public class ProgressRequestBody extends RequestBody {

    private final RequestBody mBody;
    private final OnFileProgressListener mProgressListener;
    private BufferedSink mSink;

    private long mContentLength;

    public ProgressRequestBody(RequestBody body) {
        this(body, null);
    }

    public ProgressRequestBody(RequestBody body, OnFileProgressListener callback) {
        this.mBody = body;
        this.mProgressListener = callback;
    }

    @Override
    public MediaType contentType() {
        return mBody.contentType();
    }

    @Override
    public long contentLength() throws IOException {
        return mContentLength = mBody.contentLength();
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        if (mSink == null) {
            mSink = Okio.buffer(sink(sink));
        }

        mBody.writeTo(mSink);
        mSink.flush();
    }

    private Sink sink(Sink sink) {

        return new ForwardingSink(sink) {

            private long mCurrent;
            private long mLast = 0;

            @Override
            public void write(Buffer source, long byteCount) throws IOException {
                super.write(source, byteCount);
                if (mProgressListener != null) {
                    mCurrent += byteCount;
                    int now = (int) (mCurrent * 100 / mContentLength);
                    if (mLast < now) {
                        mProgressListener.onUploadProgress(mLast, 100, mContentLength == mCurrent);
                        mLast = now;
                    }
                }
            }
        };
    }

}
