package com.longrise.android.jssdk;

import android.support.annotation.NonNull;
import android.webkit.WebView;

import com.longrise.android.jssdk.core.JsCallManager;
import com.longrise.android.jssdk.sender.IEventListener;
import com.longrise.android.jssdk.sender.base.ICallback;
import com.longrise.android.jssdk.sender.base.SenderAgent;
import com.longrise.android.jssdk.sender.base.SenderImpl;

/**
 * Created by godliness on 2020-04-26.
 *
 * @author godliness
 */
final class RequestEvent<P> extends Request<P> implements IEventListener<P> {

    @SuppressWarnings("unchecked")
    static <P> IEventListener<P> create(String eventName) {
        return new RequestEvent<>().eventName(eventName);
    }

    @Override
    public IEventListener<P> params(P params) {
        setParams(params);
        return this;
    }

    @Override
    public <Callback extends ICallback> SenderAgent callback(Callback callback) {
        return new SenderImpl<>(this).callback(callback);
    }

    @Override
    public void to(@NonNull WebView target) {
        JsCallManager.callJavaScriptEvent(target, this);
    }

    private IEventListener eventName(String eventName) {
        setEventName(eventName);
        return this;
    }

    private RequestEvent() {

    }
}
