package com.longrise.android.demo;

import android.util.Log;
import android.webkit.JavascriptInterface;

import com.google.gson.reflect.TypeToken;
import com.longrise.android.demo.mode.Params;
import com.longrise.android.demo.mode.UserBean;
import com.longrise.android.jssdk.Request;
import com.longrise.android.jssdk.Response;
import com.longrise.android.jssdk.wx.bridge.BaseWxBridge;

import java.util.List;
import java.util.Map;

/**
 * Created by godliness on 2020-04-09.
 *
 * @author godliness
 */
public final class TestBridge extends BaseWxBridge<DemoActivity> {

    private static final String TAG = "TestBridge";

    /**
     * 无参无返回值
     */
    @JavascriptInterface
    public final void openLight() {
        Log.e(TAG, "openLight");
    }

    /**
     * 有参无返回值
     */
    @JavascriptInterface
    public final void closeLight(String requestMsg) {
        final Request<Params> request = Request.parseRequest(requestMsg, Params.class);

        final Params params = request.getParams();

        Log.e(TAG, "来自JavaScript的参数:" + params);
    }

    /**
     * 无参有返回值
     */
    @JavascriptInterface
    public final void switchLight() {
        Log.e(TAG, "switchLight");

        // 注意该方式将无法实现返回值功能，但是在自定义事件类型时便可实现
    }

    @JavascriptInterface
    public final void takeLight(String requestMsg) {
        final Request<Params> request = Request.parseRequest(requestMsg, Params.class);

        Log.e(TAG, "来自JavaScript的参数:" + request.getParams());


        //通过Response构建返回到JavaScript的调用者
        final UserBean bean = new UserBean();
        bean.name = "我是Native返回";
        bean.age = 18;
        bean.sex = "boy";

        Response.create(request.getCallbackId())
                .state(Response.RESULT_OK)
                .desc("这里说明状态")
                .result(bean)
                .notify(getWebView());
    }

    @JavascriptInterface
    public final void fromJavaScript(String json) {
        final Request<Map<String, String>> request = Request.parseRequest(json, new TypeToken<Map<String, String>>(){});
        Log.e(TAG, "fromJavaScript: " + request.getParams().get("course"));
    }

    /**
     * JavaScript需要Native return
     */
    @JavascriptInterface
    public final void fromJavaScriptWithParamsWithReturn(String json) {

        final Request<List<Params>> request = Request.parseRequest(json, new TypeToken<List<Params>>(){});
        final List<Params> params = request.getParams();
        Log.e(TAG, "params: " + params.toString());

        final UserBean bean = new UserBean();
        bean.sex = "男";
        bean.name = "godliness";
        bean.age = 18;

        Response.create(request.getCallbackId())
                .state(Response.RESULT_OK)
                .desc("有缘千里来相会，无缘对面手难牵，十年修得同船渡，百年修得共枕眠")
                .result(bean)
                .notify(getWebView());
    }

    /**
     * 来自JavaScript的调用，含参数，无需返回值
     */
    @JavascriptInterface
    public final void fromJavaScriptWithParams(String json) {
        Log.e(TAG, "fromJavaScriptWithParams： " + json);
    }

    /**
     * 来自JavaScript的调用，无参无返回值
     */
    public final void fromJavaSript() {
        Log.e(TAG, "fromJavaSript");
    }
}
