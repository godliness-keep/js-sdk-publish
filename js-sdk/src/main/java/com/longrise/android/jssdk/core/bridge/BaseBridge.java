package com.longrise.android.jssdk.core.bridge;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.longrise.android.jssdk.BuildConfig;
import com.longrise.android.jssdk.Request;
import com.longrise.android.jssdk.Response;
import com.longrise.android.jssdk.lifecycle.LifecycleManager;

import java.lang.ref.WeakReference;

/**
 * Created by godliness on 2020-04-09.
 *
 * @author godliness
 */
@SuppressWarnings({"unused", "unchecked", "WeakerAccess"})
public abstract class BaseBridge<T extends Activity> extends BridgeLifecyle<T> implements Handler.Callback, LifecycleManager.OnLifecycleListener {

    private static final String TAG = "BaseBridge";

    private static final int MSG_NOTIFY_NATIVE = 0;
    private static final int MSG_CALL_NATIVE = 1;
    private static final int MSG_CONFIG = 2;

    private WeakReference<WebView> mTarget;
    private final Handler mHandler;
    private boolean mDebug;

    public BaseBridge() {
        this.mHandler = new Handler(this);
    }

    public final void bindTarget(T host, WebView view) {
        super.attachHost(host);
        this.mTarget = new WeakReference<>(view);
    }

    protected boolean onHandleMessage(Message msg) {
        return false;
    }

    @Override
    protected void onDestroy() {
        mHandler.removeCallbacksAndMessages(null);

        if (BuildConfig.DEBUG) {
            Log.e(TAG, "onDestroy");
        }
    }

    protected final Handler getHandler() {
        return mHandler;
    }

    @Nullable
    protected final WebView getWebView() {
        return mTarget.get();
    }

    protected final Message getMessage(int what, Object obj) {
        final Message message = mHandler.obtainMessage(what);
        message.obj = obj;
        return message;
    }

    @JavascriptInterface
    public final void onJavaScriptCallFinished(String resultJson) {
        if (!isFinished()) {
            getMessage(MSG_NOTIFY_NATIVE, Response.parseResponse(resultJson)).sendToTarget();
        }
    }

    @JavascriptInterface
    public final void callNativeFromJavaScript(String requestJson) {
        if (!isFinished()) {
            getMessage(MSG_CALL_NATIVE,  Request.parseRequest(requestJson)).sendToTarget();
        }
    }

    @JavascriptInterface
    public final void config(String message) {
        if (!isFinished()) {
            getMessage(MSG_CONFIG, Request.parseRequest(message, Config.class)).sendToTarget();
        }
    }

    @Override
    public final boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_NOTIFY_NATIVE:
                final Response<String> response = parseObjectFromMessage(msg);
                response.onJavaScriptCallFinished();
                if (mDebug) {
                    showMessage(response.getDeserialize());
                }
                return true;

            case MSG_CALL_NATIVE:
                final Request<String> request = parseObjectFromMessage(msg);
                request.dispatchReceiver(getWebView());
                if (mDebug) {
                    showMessage(request.getParams());
                }
                return true;

            case MSG_CONFIG:
                final Request<Config> config = parseObjectFromMessage(msg);
                this.mDebug = config.getParams().debug;
                return true;

            default:
                return onHandleMessage(msg);
        }
    }

    static final class Config {
        @Expose
        @SerializedName("debug")
        private boolean debug;
    }

    private void showMessage(String content) {
        if (isFinished()) {
            new AlertDialog.Builder(getHost())
                    .setMessage(content)
                    .setCancelable(true).create().show();
        }
    }

    private <Obj> Obj parseObjectFromMessage(Message msg) {
        return (Obj) msg.obj;
    }

}
