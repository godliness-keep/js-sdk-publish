package com.longrise.android.jssdk.sender.base;

/**
 * Created by godliness on 2020-04-15.
 *
 * @author godliness
 */
public interface ICallback {

    /**
     * Response from the JavaScript method
     *
     * @param result {@link com.longrise.android.jssdk.core.protocol.Result}
     */
    void onValue(String result);
}
