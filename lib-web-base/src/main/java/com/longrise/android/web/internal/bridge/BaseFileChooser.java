package com.longrise.android.web.internal.bridge;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;

import com.longrise.android.mvp.utils.MvpLog;
import com.longrise.android.web.BaseWebActivity;
import com.longrise.android.web.utils.ActivityState;

import java.lang.ref.WeakReference;

/**
 * Created by godliness on 2019-07-09.
 *
 * @author godliness
 */
public class BaseFileChooser<T extends BaseWebActivity> {

    private static final String TAG = "BaseFileChooser";

    private static final int REQUEST_CODE_VERSION_LESS_LOLLIPOP = 20;
    private static final int REQUEST_CODE_VERSION_LOLLIPOP = 21;

    private WeakReference<T> mTarget;
    private Handler mHandler;

    private ValueCallback<Uri> mUploadFile;
    private ValueCallback<Uri[]> mFilePathCallback;

    public BaseFileChooser() {

    }

    public void onHandleMessage(Message msg) {

    }

    protected boolean dispatchActivityOnResult(int requestCode, int resultCode, Intent data) {
        return false;
    }

    public final void attachTarget(T target) {
        this.mTarget = new WeakReference<>(target);
        this.mHandler = target.getHandler();
    }

    protected final T getTarget() {
        return mTarget.get();
    }

    protected final boolean isFinishing() {
        final boolean isAlive = ActivityState.isAlive(mTarget.get());
        if (!isAlive) {
            mHandler.removeCallbacksAndMessages(null);
        }
        return !isAlive;
    }

    protected final Handler getHandler() {
        return mHandler;
    }

    protected final void post(Runnable action) {
        postDelayed(action, 0);
    }

    protected final void postDelayed(Runnable action, int delay) {
        if (!isFinishing()) {
            mHandler.postDelayed(action, delay);
        }
    }

    /**
     * {@link BaseWebActivity#onActivityResult(int, int, Intent)}
     */
    public final void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        if (isFinishing() || data == null || dispatchActivityOnResult(requestCode, resultCode, data)) {
            onReceiveValueEnd();
            return;
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            if (mUploadFile != null) {
                mUploadFile.onReceiveValue(resultCode == Activity.RESULT_OK ? data.getData() : null);
                mUploadFile = null;
            }
        } else {
            if (mFilePathCallback != null) {
                mFilePathCallback.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, data));
                mFilePathCallback = null;
            }
        }
    }

    protected final void onReceiveValueEnd() {
        if (mUploadFile != null) {
            mUploadFile.onReceiveValue(null);
            mUploadFile = null;
        }
        if (mFilePathCallback != null) {
            mFilePathCallback.onReceiveValue(null);
            mFilePathCallback = null;
        }
    }

    /**
     * For Android Version < 5.0  todo 4.4版本是个bug
     */
    final void openFileChooser(ValueCallback<Uri> uploadFile, String acceptType, String capture) {
        if (isFinishing()) {
            return;
        }
        onReceiveValueEnd();
        this.mUploadFile = uploadFile;
        final Intent target = new Intent(Intent.ACTION_GET_CONTENT);
        target.addCategory(Intent.CATEGORY_OPENABLE);
        target.setType(acceptType);
        try {
            getTarget().startActivityForResult(Intent.createChooser(target, "File Browser"), REQUEST_CODE_VERSION_LESS_LOLLIPOP);
        } catch (Exception e) {
            onReceiveValueEnd();
            MvpLog.print(e);
        }
    }

    /**
     * For Android Version >= 5.0
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    final void onShowFileChooser(ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
        if (isFinishing()) {
            return;
        }
        onReceiveValueEnd();
        this.mFilePathCallback = filePathCallback;
        try {
            final Intent intent = fileChooserParams.createIntent();
            getTarget().startActivityForResult(intent, REQUEST_CODE_VERSION_LOLLIPOP);
        } catch (Exception e) {
            onReceiveValueEnd();
            MvpLog.print(e);
        }
    }
}
