package com.longrise.android.jssdk.sender;

import com.longrise.android.jssdk.core.protocol.Result;
import com.longrise.android.jssdk.gson.GenericHelper;
import com.longrise.android.jssdk.sender.base.ICallback;

import java.lang.reflect.Type;

/**
 * Created by godliness on 2020-04-15.
 *
 * @author godliness
 */
public abstract class ResultCallback<T> implements ICallback {

    /**
     * Receive value from JavaScript
     *
     * @param result {@link Result}
     */
    protected abstract void onReceiveValue(Result<T> result);

    @SuppressWarnings("unchecked")
    @Override
    public final void onValue(String result) {
        onReceiveValue((Result<T>) Result.parseResult(result, getType()));
    }

    private Type getType() {
        return GenericHelper.getTypeOfT(this, 0);
    }
}
