package com.longrise.android.jssdk;

import android.webkit.WebView;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.longrise.android.jssdk.core.JsCallManager;
import com.longrise.android.jssdk.core.protocol.Result;
import com.longrise.android.jssdk.core.protocol.base.AbsDataProtocol;
import com.longrise.android.jssdk.gson.JsonHelper;
import com.longrise.android.jssdk.sender.base.SendersManager;

import java.lang.reflect.Type;

/**
 * Created by godliness on 2020-04-15.
 *
 * @author godliness
 */
public final class Response<T> extends AbsDataProtocol {

    public static final int RESULT_OK = 1;

    @Expose(deserialize = false)
    @SerializedName("result")
    private Result<T> result;

    private T deserialize;

    public static <T> Response<T> create(int callbackId) {
        return new Response<>(callbackId);
    }

    public static Response<String> parseResponse(String json) {
        return parseResponse(json, new TypeToken<Response<String>>() {
        }.getType());
    }

    /**
     * 状态，默认 {@link Response#RESULT_OK}
     */
    public Response<T> state(int state) {
        createResultIfNeed().setState(state);
        return this;
    }

    /**
     * 说明
     */
    public Response<T> desc(String desc) {
        createResultIfNeed().setDesc(desc);
        return this;
    }

    /**
     * 返回值
     */
    public Response<T> result(T result) {
        createResultIfNeed().setResult(result);
        return this;
    }

    public Result<T> getResult() {
        return result;
    }

    public void onJavaScriptCallFinished() {
        SendersManager.getManager().onJavaScriptCallFinished(getCallbackId(), (String) deserialize);
    }

    public void notify(WebView webView) {
        JsCallManager.notifyJavaScriptCallNativeFinished(webView, this);
    }

    public void deserialize(T deserializer) {
        this.deserialize = deserializer;
    }

    public T getDeserialize() {
        return deserialize;
    }

    public Response() {

    }

    public Response(int id) {
        setCallbackId(id);
    }

    private Result<T> createResultIfNeed() {
        if (result == null) {
            result = new Result<>();
        }
        return result;
    }

    private static <T> Response<T> parseResponse(String json, Type type) {
        return JsonHelper.fromJson(json, type);
    }
}
