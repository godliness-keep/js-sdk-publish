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
public interface IMethodListener<P> {

    /**
     * 携带参数
     */
    <Callback extends ICallback> SenderAgent<Callback> params(P params);

    /**
     * Call to target
     */
    void to(@NonNull WebView target);
}
