package com.longrise.android.demo.mode;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by godliness on 2020-04-10.
 *
 * @author godliness
 */
public class UserBean {

    @Expose
    @SerializedName("name")
    public String name;

    @Expose
    @SerializedName("age")
    public int age;

    @Expose
    @SerializedName("sex")
    public String sex;

    @Override
    public String toString() {
        return "UserBean{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", sex='" + sex + '\'' +
                '}';
    }
}
