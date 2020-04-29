package com.longrise.android.jssdk.wx.mode;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Arrays;

/**
 * Created by godliness on 2020-04-17.
 *
 * @author godliness
 */
public final class ChooseImage {

    @Expose
    @SerializedName("count")
    private int count;

    @Expose
    @SerializedName("sizeType")
    private String[] sizeType;

    @Expose
    @SerializedName("sourceType")
    private String[] sourceType;

    public int getCount() {
        if (count > 9) {
            return 9;
        } else if (count <= 0) {
            return 9;
        }
        return count;
    }

    public String[] getSizeType() {
        return sizeType;
    }

    public String[] getSourceType() {
        return sourceType;
    }

    @Override
    public String toString() {
        return "{" +
                "count=" + count +
                ", sizeType=" + Arrays.toString(sizeType) +
                ", sourceType=" + Arrays.toString(sourceType) +
                '}';
    }
}
