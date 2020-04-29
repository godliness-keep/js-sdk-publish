package com.longrise.android.demo;

import android.app.Activity;
import android.util.Log;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.longrise.android.jssdk.receiver.IParamsReceiver;
import com.longrise.android.jssdk.receiver.IParamsReturnReceiver;
import com.longrise.android.jssdk.receiver.IReceiver;
import com.longrise.android.jssdk.receiver.IReturnReceiver;
import com.longrise.android.jssdk.receiver.base.EventName;


/**
 * Created by godliness on 2020-04-15.
 *
 * @author godliness
 * 测试接收者
 */
public final class TestReceiver {

    private static final String TAG = "TestReceiver";

    public void register(Activity host) {
        // 无参无返回值
        mReceiver.alive().lifecycle(host);
        // 无参有返回值
        mReturnReceiver.alive().lifecycle(host);
        // 有参无返回值
        mParamsReceiver.alive().lifecycle(host);
        // 有参有返回值
        mParamsReturnReceiver.alive().lifecycle(host);
    }

    /**
     * 普通接收者（无参无返回值）
     */
    private final IReceiver mReceiver = new IReceiver() {

        @EventName("onVoiceRecordEnd")
        @Override
        public void onEvent() {

            // 如果需要返回，可以选择使用
            callback("我是一个粉刷匠，粉刷本领强，我要把那新房子刷的又漂亮，打死你个龟孙");
        }
    };

    /**
     * 含有返回值的接收者
     */
    private final IReturnReceiver<Bean> mReturnReceiver = new IReturnReceiver<Bean>() {

        @EventName("shareQQ")
        public Bean onEvent() {

            final Bean bean = new Bean();
            bean.like = "心，是一个容器，不停地累积";
            bean.sex = "boy";
            return bean;
        }
    };

    /**
     * 含有参数的接收者
     */
    private final IParamsReceiver<Params> mParamsReceiver = new IParamsReceiver<Params>() {
        @Override
        @EventName("shareFeiji")
        public void onEvent(Params params) {

            Log.e(TAG, "接收到JS传递参数：" + params);

            final Bean bean = new Bean();
            bean.like = "心，是一个容器，不停地累积";
            bean.sex = "boy";
            // 如果需要返回，可以选择使用
            callback(bean);
        }
    };

    /**
     * 含参含返回值的接收者
     */
    private final IParamsReturnReceiver<Params, Bean> mParamsReturnReceiver = new IParamsReturnReceiver<Params, Bean>() {

        @EventName("shareQZone")
        public Bean onEvent(Params params) {

            Log.e(TAG, "接收到JS传递参数：" + params);

            final Bean bean = new Bean();
            bean.like = "我是返回";
            bean.sex = "boy";
            return bean;
        }
    };

    private class Params {

        @Expose
        @SerializedName("name")
        private String name;

        @Expose
        @SerializedName("age")
        private int age;

        @Expose
        @SerializedName("sex")
        private String sex;

        @Override
        public String toString() {
            return "Params{" +
                    "name='" + name + '\'' +
                    ", age=" + age +
                    ", sex='" + sex + '\'' +
                    '}';
        }
    }

    private class Bean {

        @Expose
        @SerializedName("like")
        private String like;

        @Expose
        @SerializedName("sex")
        private String sex;

    }
}
