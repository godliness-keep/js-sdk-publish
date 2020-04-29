package com.longrise.android.photowall.adapter;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

/**
 * Created by godliness on 2020-04-18.
 *
 * @author godliness
 */
public final class PhotoView extends AppCompatImageView {

    private int position;

    public PhotoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
