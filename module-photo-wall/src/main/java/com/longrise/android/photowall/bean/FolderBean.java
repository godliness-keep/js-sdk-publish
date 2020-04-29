package com.longrise.android.photowall.bean;

/**
 * Created by sujizhong on 16/3/20.
 */
public class FolderBean {

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
        try {
            int lastIndexof = dir.lastIndexOf("/");
            name = dir.substring(lastIndexof);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 当前文件夹路径
     */
    private String dir;

    public String getFirstImagePath() {
        return firstImagePath;
    }

    public void setFirstImagePath(String firstImagePath) {
        this.firstImagePath = firstImagePath;
    }

    /**
     * 第一张图片的路径
     */
    private String firstImagePath;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * 文件夹名称
     */
    private String name;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    /**
     * 文件夹图片数量
     */
    private int count;

}
