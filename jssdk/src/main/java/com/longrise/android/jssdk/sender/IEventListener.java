package com.longrise.android.jssdk.sender;

import android.support.annotation.NonNull;
import android.webkit.WebView;

import com.longrise.android.jssdk.sender.base.ICallback;
import com.longrise.android.jssdk.sender.base.SenderAgent;

/**
 * Created by godliness on 2020-04-26.
 *
 * @author godliness
 */
public interface IEventListener<P>{

    IEventListener<P> params(P params);

    <Callback extends ICallback> SenderAgent callback(Callback callback);

    void to(@NonNull WebView target);
}
