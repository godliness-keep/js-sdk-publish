package com.longrise.android.jssdk.stream;

/**
 * Created by godliness on 2020-04-23.
 *
 * @author godliness
 */
public final class Test {

    public static void test() {
        final Stream stream = new Stream("这里应该是一个超大的字符串");
        stream.send();


        /**
         * chunk://sid:cid/data
         *
         * 1、判断是schema为chunk，说明此时为分段传输
         * 2、获取该段所属的流:sid
         * 3、获取该流的段id:cid
         * 4、获取段数据:data
         * 5、拼接相邻的data后，计算其hash值如果等于sid，此时表明该流传输完成，可以分发给目标了
         * */
    }

}
