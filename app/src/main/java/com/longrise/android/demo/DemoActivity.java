package com.longrise.android.demo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.webkit.WebView;

import com.longrise.android.demo.mode.UserBean;
import com.longrise.android.jssdk.Request;
import com.longrise.android.jssdk.core.protocol.Result;
import com.longrise.android.jssdk.sender.ResultCallback;
import com.longrise.android.web.BaseWebActivity;
import com.longrise.android.web.internal.BaseWebView;

import java.util.List;

/**
 * Demo
 */
public class DemoActivity extends BaseWebActivity {

    private BaseWebView webView;

    @Override
    protected int getLayoutResourceId(@Nullable Bundle savedInstanceState) {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        webView = findViewById(R.id.webView);
        webView.loadUrl("file:///android_asset/main.html");

        findViewById(R.id.call_no_params_no_return).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callFromNativeNoParamsNoReturn();
            }
        });

        findViewById(R.id.call_no_params_with_return).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callFromNativeNoParamsWithReturn();
            }
        });

        findViewById(R.id.call_with_params_no_return).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callFromNativeWithParamsNoReturn();
            }
        });

        findViewById(R.id.call_with_params_with_return).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callFromNativeWithParamsWithReturn();
            }
        });

        final TestReceiver receiver = new TestReceiver();
        receiver.register(this);
    }

    /**
     * 无参无返回值
     */
    private void callFromNativeNoParamsNoReturn() {
        Request.call("callFromNativeNoParamsNoReturn").to(webView);
    }

    /**
     * 有参无返回值
     */
    private void callFromNativeWithParamsNoReturn() {
        final UserBean bean = new UserBean();
        bean.name = "调用JS方法，有返回值";
        bean.age = 20;
        bean.sex = "boy";
        Request.call("callFromNativeWithParamsNoReturn").params(bean).to(webView);
    }

    /**
     * 无参有返回值 todo 无参数类型时无法拥有返回值（事件类型可以）
     */
    private void callFromNativeNoParamsWithReturn() {
        // todo 调用JavaScript无参方法，此时是无法获取return的
//        Request.call("callFromNativeNoParamsWithReturn").to(webView);

        // todo 而事件类型则可以
        final UserBean bean = new UserBean();
        bean.name = "调用JS方法，有返回值";
        bean.age = 20;
        bean.sex = "boy";

        Request.callEvent("open")
                .params(bean)
                .callback(new ResultCallback<List<UserBean>>() {
                    @Override
                    public void onReceiveValue(Result<List<UserBean>> result) {
                        new AlertDialog.Builder(DemoActivity.this)
                                .setMessage("来自JavaScript的Return: " + result.toJson())
                                .setCancelable(true)
                                .show();
                    }
                }).to(webView);
    }

    /**
     * 有参有返回值
     */
    private void callFromNativeWithParamsWithReturn() {
        final UserBean bean = new UserBean();
        bean.name = "调用JS方法，有返回值";
        bean.age = 20;
        bean.sex = "boy";

        Request.call("callFromNativeWithParamsWithReturn")
                .params(bean)
                .callback(new ResultCallback<UserBean>() {
                    @Override
                    public void onReceiveValue(Result<UserBean> result) {
                        new AlertDialog.Builder(DemoActivity.this)
                                .setMessage("来自JavaScript的Return: " + result.toJson())
                                .setCancelable(true)
                                .show();
                    }
                }).to(webView);
    }

    @Override
    protected void createWebBridge(WebView view) {
        final TestBridge bridge = new TestBridge();
        bridge.bindTarget(this, webView);
        view.addJavascriptInterface(bridge, "lrBridge");
    }

    @Override
    protected void regEvent(boolean regEvent) {

    }

    @Override
    public BaseWebView getWebView() {
        return webView;
    }

    @Override
    public void onReceivedTitle(String title) {

    }

    @Override
    public void onProgressChanged(int newProgress) {

    }

    @Override
    public void loadedSuccess() {

    }

    @Override
    public void loadedError() {

    }
}
