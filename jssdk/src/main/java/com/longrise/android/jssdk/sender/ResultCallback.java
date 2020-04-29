package com.longrise.android.jssdk.sender;

import com.longrise.android.jssdk.gson.GenericHelper;
import com.longrise.android.jssdk.sender.base.ICallback;

import java.lang.reflect.Type;

/**
 * Created by godliness on 2020-04-29.
 *
 * @author godliness
 */
public abstract class ResultCallback<T> implements ICallback<T> {

    @Override
    public final Type getType() {
        return GenericHelper.getTypeOfT(this, 0);
    }

}
