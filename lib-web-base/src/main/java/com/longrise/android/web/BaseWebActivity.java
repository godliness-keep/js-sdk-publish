package com.longrise.android.web;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;

import com.longrise.android.mvp.internal.BaseMvpActivity;
import com.longrise.android.mvp.internal.mvp.BasePresenter;
import com.longrise.android.mvp.utils.MvpLog;
import com.longrise.android.web.common.SchemeConsts;
import com.longrise.android.web.internal.BaseWebView;
import com.longrise.android.web.internal.SettingInit;
import com.longrise.android.web.internal.bridge.BaseBridge;
import com.longrise.android.web.internal.bridge.BaseFileChooser;
import com.longrise.android.web.internal.bridge.BaseWebChromeClient;
import com.longrise.android.web.internal.bridge.BaseWebViewClient;
import com.longrise.android.web.internal.webcallback.WebCallback;

/**
 * Created by godliness on 2019-07-09.
 *
 * @author godliness
 */
public abstract class BaseWebActivity<P extends BasePresenter> extends BaseMvpActivity<P> implements
        WebCallback.WebChromeListener, WebCallback.WebViewClientListener, Handler.Callback {

    private static final String TAG = "BaseWebActivity";

    private BaseWebView mWebView;
    private BaseWebViewClient mWebViewClient;
    private BaseWebChromeClient mWebChomeClient;
    private BaseFileChooser mFileChooser;

    /**
     * This Handler is common to all WebView pages
     */
    private final Handler mHandler = new Handler(this);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initWebFrame();
    }

    @Override
    protected abstract void initView();

    @Override
    protected abstract void regEvent(boolean regEvent);

    /**
     * Returns the current WebView instance
     *
     * @return {@link BaseWebView}
     */
    public abstract BaseWebView getWebView();

    protected void onHandleMessage(Message msg) {

    }

    protected BaseBridge getBridge() {
        return null;
    }

    protected BaseWebViewClient getWebViewClient() {
        return null;
    }

    protected BaseWebChromeClient getWebChromeClient() {
        return null;
    }

    protected BaseFileChooser getFileChooser() {
        return null;
    }

    /**
     * {@link BaseWebViewClient#shouldOverrideUrlLoading(WebView, WebResourceRequest)}
     */
    @Override
    public boolean shouldOverrideUrlLoading(String url) {
        MvpLog.e(TAG, "uri: " + url);
        return false;
    }

    protected boolean webViewGoBack(boolean finish) {
        if (mWebView != null) {
            if (mWebView.webViewGoBack()) {
                return true;
            }
        }
        if (finish) {
            finish();
        }
        return false;
    }

    /**
     * Perform page loading in {@link #initView()}
     */
    protected void loadUrl(WebParams params) {
        if (params != null) {
            loadUrl(params.path());
        }
    }

    /**
     * Perform load Web address in {@link #initView()}
     */
    protected void loadUrl(final String path) {
        post(new Runnable() {
            @Override
            public void run() {
                if (mWebView == null) {
                    MvpLog.e(TAG, "mWebView == null");
                    return;
                }
                mWebView.loadUrl(path);
            }
        });
    }

    public final Handler getHandler() {
        return mHandler;
    }

    @Override
    public final boolean handleMessage(Message msg) {
        onHandleMessage(msg);
        if (mFileChooser != null) {
            mFileChooser.onHandleMessage(msg);
        }
        return false;
    }

    protected final void post(Runnable task) {
        postDelayed(task, 0);
    }

    protected final void postDelayed(Runnable task, int delay) {
        mHandler.postDelayed(task, delay);
    }

    /**
     * Notify the WebView to reload
     */
    protected final void notifyWebViewReload() {
        if (mWebView != null) {
            mWebView.reload();
        }
    }

    /**
     * {@link BaseWebChromeClient#openFileChooser}
     */
    @SuppressWarnings("unchecked")
    public final BaseFileChooser createOrGetFileChooser() {
        if (mFileChooser == null) {
            mFileChooser = createFileChooser();
            mFileChooser.attachTarget(this);
        }
        return mFileChooser;
    }

    @Override
    public final boolean onKeyDown(int keyCode, KeyEvent event) {
        if (webViewGoBack(false)) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mWebView != null) {
            mWebView.onResume();
        }
    }

    @Override
    public void finish() {
        if (mWebView != null) {
            mWebView.loadUrl(SchemeConsts.BLANK);
        }
        super.finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mWebView != null) {
            mWebView.onPause();
        }
    }

    @Override
    protected void onDestroy() {
        if (mWebView != null) {
            mWebView.recycle();
        }
        mHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mFileChooser != null) {
            mFileChooser.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void initWebFrame() {
        final BaseWebView webView = getWebView();
        if (webView == null) {
            throw new NullPointerException("getWebView() == null");
        }
        initAndCreateBridge(webView);
    }

    private void initAndCreateBridge(BaseWebView webView) {
        SettingInit.initSetting(webView);
        createWebBridge(webView);
        createWebViewClient(webView);
        createWebChromeClient(webView);
        this.mWebView = webView;
    }

    private BaseFileChooser createFileChooser() {
        this.mFileChooser = getFileChooser();
        if (mFileChooser == null) {
            this.mFileChooser = new BaseFileChooser();
        }
        return mFileChooser;
    }

    @SuppressWarnings("unchecked")
    private void createWebViewClient(WebView view) {
        this.mWebViewClient = getWebViewClient();
        if (mWebViewClient == null) {
            mWebViewClient = new BaseWebViewClient();
        }
        mWebViewClient.attachTarget(this);
        view.setWebViewClient(mWebViewClient);
    }

    @SuppressWarnings("unchecked")
    private void createWebChromeClient(WebView view) {
        this.mWebChomeClient = getWebChromeClient();
        if (mWebChomeClient == null) {
            mWebChomeClient = new BaseWebChromeClient();
        }
        mWebChomeClient.attachTarget(this);
        view.setWebChromeClient(mWebChomeClient);
    }

    @SuppressLint({"JavascriptInterface"})
    @SuppressWarnings("unchecked")
    protected void createWebBridge(WebView view) {
        final BaseBridge bridge = getBridge();
        if (bridge != null) {
            bridge.attachTarget(this);
            // 适用于 API Level 17及以后，之前有安全问题
            view.addJavascriptInterface(bridge, bridge.bridgeName());
        }
    }
}
