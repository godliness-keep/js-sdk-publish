package com.longrise.android.mvp.utils;

import android.support.annotation.Nullable;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by godliness on 2019-07-02.
 *
 * @author godliness
 */
@SuppressWarnings("unchecked")
public final class GenericUtil {

    @Nullable
    public static <T> T getT(Object o, int i) {
        final Object obj = o.getClass().getGenericSuperclass();
        if (obj instanceof ParameterizedType) {
            try {
                final Class<T> clz = (Class<T>) ((ParameterizedType) obj).getActualTypeArguments()[i];
                return clz.newInstance();
            } catch (Exception e) {
                MvpLog.print(e);
            }
        }
        return null;
    }
}
