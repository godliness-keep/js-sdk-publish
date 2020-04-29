package com.longrise.android.web.utils;

import android.text.TextUtils;

/**
 * Created by godliness on 2019-11-19.
 *
 * @author godliness
 */
public class ResUtil {

    /**
     * 根据资源地址获取扩展名
     *
     * @param path 资源地址
     */
    public static String getResExpandName(String path) {
        if (!TextUtils.isEmpty(path)) {
            int length = path.length();
            int index = path.lastIndexOf(".");
            return path.substring(index + 1, length);
        }
        return null;
    }
}
