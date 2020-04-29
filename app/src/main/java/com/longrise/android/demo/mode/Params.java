package com.longrise.android.demo.mode;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by godliness on 2020-04-14.
 *
 * @author godliness
 */
public class Params {

    @Expose
    @SerializedName("link")
    private String link;

    @Expose
    @SerializedName("age")
    private String age;

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "{" +
                "link='" + link + '\'' +
                ", age='" + age + '\'' +
                '}';
    }
}
