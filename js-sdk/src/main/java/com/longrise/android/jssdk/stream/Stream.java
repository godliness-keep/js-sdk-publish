package com.longrise.android.jssdk.stream;

import android.support.annotation.NonNull;

/**
 * Created by godliness on 2020-04-23.
 *
 * @author godliness
 */
public class Stream extends BaseStream {

    protected Stream(String value) {
        super(value);
    }

    protected Stream(int geometry, String value) {
        super(geometry, value);
    }

    @Override
    protected void request(@NonNull Chunk[] chunks) {
        // 在这里发送

        // todo 待完善
    }

}
