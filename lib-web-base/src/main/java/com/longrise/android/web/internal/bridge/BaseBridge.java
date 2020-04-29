package com.longrise.android.web.internal.bridge;

import android.os.Handler;
import android.support.annotation.Nullable;
import android.webkit.ValueCallback;
import android.webkit.WebView;

import com.longrise.android.web.BaseWebActivity;
import com.longrise.android.web.R;
import com.longrise.android.web.utils.ActivityState;
import com.longrise.android.web.utils.CallJsMethods;

import java.lang.ref.WeakReference;

/**
 * Created by godliness on 2019-10-30.
 *
 * @author godliness
 */
public abstract class BaseBridge<T extends BaseWebActivity> {

    private Handler mHandler;
    private WeakReference<T> mTarget;

    public BaseBridge() {

    }

    public String bridgeName() {
        final T target = mTarget.get();
        return target.getString(R.string.string_bridge_name);
    }

    public final void attachTarget(T target) {
        this.mHandler = target.getHandler();
        this.mTarget = new WeakReference<>(target);
    }

    protected final WebView getWebView() {
        if (mTarget != null) {
            return mTarget.get().getWebView();
        }
        return null;
    }

    protected final boolean isFinish() {
        final boolean isAlive = ActivityState.isAlive(mTarget.get());
        if (!isAlive) {
            mHandler.removeCallbacksAndMessages(null);
        }
        return !isAlive;
    }

    @Nullable
    protected final T getTarget() {
        return mTarget != null ? mTarget.get() : null;
    }

    protected final void finishActivity() {
        T target = getTarget();
        if (target != null) {
            target.finish();
        }
    }

    protected final void post(Runnable action) {
        postDelayed(action, 0);
    }

    protected final void postDelayed(Runnable action, int delay) {
        if (!isFinish()) {
            mHandler.postDelayed(action, delay);
        }
    }

    protected final void callJsMethod(String jsMethodName) {
        callJsMethod(jsMethodName, null);
    }

    protected final void callJsMethod(String jsMethodName, String params) {
        callJsMethod(jsMethodName, params, null);
    }

    protected final void callJsMethod(final String jsMethodName, final String params, final ValueCallback<T> valueCallback) {
        if (!isFinish()) {
            post(new Runnable() {
                @Override
                public void run() {
                    final WebView webView = getWebView();
                    CallJsMethods.callJsMethod(webView, jsMethodName, params, valueCallback);
                }
            });
        }
    }
}
