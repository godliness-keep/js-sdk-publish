package com.longrise.android.photowall.utils;

import android.os.Environment;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by sujizhong on 16/3/23.
 */
public class FolderUtil {

    public static final String IMAGE_SAVE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/android/";
    private static final String IMAGE_SAVE_NAME = "image0.jpg";
    public static final String IMAGE_SUCESS_RESULT = "pic_result";

    public static final int IMAGE_ACTIVITY_RESULT = 0x000000001;

    public static final int IMAGE_SINGLE_MODE = 0x000000002;
    public static final int IMAGE_MUTIL_MODE = 0x000000003;

    private int mImageSelectMode = IMAGE_MUTIL_MODE;
    private String mPicTime;

    public static void clearFolder(File dir) {
        if (dir.exists()) {
            File[] files = dir.listFiles();
            for (File file : files) {
                if (file.isDirectory()) {
                    clearFolder(file);
                }
                file.delete();
            }
        }
    }

    public void setImageSelectMode(int mode) {
        this.mImageSelectMode = mode;
    }

    public int getImageSelectMode() {
        return mImageSelectMode;
    }

    public String setImageName() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        mPicTime = simpleDateFormat.format(date) + IMAGE_SAVE_NAME;
        return mPicTime;
    }

    public String getImageName() {
        return IMAGE_SAVE_PATH + mPicTime;
    }

    private static FolderUtil mFolderUtil = null;

    public static FolderUtil get() {
        if (mFolderUtil == null) {
            synchronized (FolderUtil.class) {
                if (mFolderUtil == null) {
                    mFolderUtil = new FolderUtil();
                }
            }
        }
        return mFolderUtil;
    }
}
