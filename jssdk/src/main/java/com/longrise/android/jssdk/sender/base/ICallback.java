package com.longrise.android.jssdk.sender.base;

import com.longrise.android.jssdk.core.protocol.Result;

import java.lang.reflect.Type;

/**
 * Created by godliness on 2020-04-29.
 *
 * @author godliness
 */
public interface ICallback<T> {

    void onReceiveValue(Result<T> result);

    Type getType();
}
