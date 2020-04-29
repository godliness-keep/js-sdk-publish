package com.longrise.android.web.utils;

import android.os.Build;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.webkit.ValueCallback;
import android.webkit.WebView;

/**
 * Created by godliness on 2019-07-12.
 *
 * @author godliness
 * Call JS code natively
 */
public final class CallJsMethods {

    public static void callJsMethod(WebView webView, String jsMethodName) {
        callJsMethod(webView, jsMethodName, null);
    }

    public static void callJsMethod(WebView webView, String jsMethodName, String params) {
        callJsMethod(webView, jsMethodName, params, null);
    }

    public static <T> void callJsMethod(WebView webView, String jsMethodName, String params, ValueCallback<T> valueCallback) {
        checkNonNull(webView, "webView");
        checkNonNull(jsMethodName, "jsMethodName");

        final StringBuilder method = new StringBuilder("javascript:");
        method.append(jsMethodName);
        if (TextUtils.isEmpty(params)) {
            method.append("()");
        } else {
            method.append("('");
            method.append(params.replaceAll("(\r\n|\r|\n|\n\r)", "</br>"));
            method.append("')");
        }
        performJsMethod(webView, method.toString(), valueCallback);
    }

    @SuppressWarnings("unchecked")
    private static void performJsMethod(@NonNull WebView webView, @NonNull String jsMethod, ValueCallback callback) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webView.evaluateJavascript(jsMethod, callback);
        } else {
            webView.loadUrl(jsMethod);
        }
    }

    private static void checkNonNull(Object object, String name) {
        if (object == null) {
            throw new NullPointerException(name + " must not be null");
        }
    }
}
