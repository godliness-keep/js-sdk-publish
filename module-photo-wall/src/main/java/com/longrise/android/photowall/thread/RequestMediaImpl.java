package com.longrise.android.photowall.thread;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.util.ArraySet;

import com.longrise.android.photowall.bean.FolderBean;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sujizhong on 16/3/31.
 */
public class RequestMediaImpl extends Thread {

    private Context mCxt;
    private OnMediaLoadListener mOnMediaLoadListener;

    public RequestMediaImpl(Context cxt, OnMediaLoadListener mediacallback) {
        this.mCxt = cxt;
        this.mOnMediaLoadListener = mediacallback;
    }

    @Override
    public void run() {
        ArraySet<String> mContains = new ArraySet<>();
        Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        ContentResolver resolver = mCxt.getContentResolver();
        Cursor cursor = resolver.query(
                mImageUri,
                null,
                MediaStore.Images.Media.MIME_TYPE + " = ? or " + MediaStore.Images.Media.MIME_TYPE + " = ? or " + MediaStore.Images.Media.MIME_TYPE + " = ?",
                new String[]{"image/jpeg", "image/png", "image/jpg"}, MediaStore.Images.Media.DATE_MODIFIED);
        if (cursor != null && cursor.getCount() > 0) {
            MediaData mMediaData = new MediaData();
            while (cursor.moveToNext()) {
                String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                File parentFile = new File(path).getParentFile();
                if (parentFile == null) {
                    continue;
                }
//                if (parentFile.list() == null) {  //耗时严重
//                    continue;
//                }
                String dirPath = parentFile.getAbsolutePath();
                if (mContains.contains(dirPath)) {
                    continue;
                }
                mContains.add(dirPath);
                FolderBean folderBean = new FolderBean();
                //绝对路径
                folderBean.setDir(dirPath);
                //第一张图片路径
                folderBean.setFirstImagePath(path);
                //该路径下图片数量
                int maxSize = parentFile.list(getFilenameFilter()).length;
                folderBean.setCount(maxSize);
                mMediaData.mFolderBeans.add(folderBean);
                //将照片数量最多的始终为第一个
//                if (maxSize > mMediaData.mMaxCount) {
//                    mMediaData.mMaxCount = maxSize;
//                    mMediaData.mCurrentDir = parentFile;
//                    mMediaData.mDir = dirPath;
//                }
            }
            cursor.close();
            callback(mMediaData);
        } else {
            callback(null);
        }
    }

    private FilenameFilter mFilenameFilter;

    private FilenameFilter getFilenameFilter() {
        if (mFilenameFilter == null) {
            mFilenameFilter = new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    if (name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".png")) {
                        return true;
                    }
                    return false;
                }
            };
        }
        return mFilenameFilter;
    }

    public class MediaData {
        public List<FolderBean> mFolderBeans = new ArrayList<>();

        public String mDir;
    }

    public interface OnMediaLoadListener {

        void onMediaLoaded(MediaData mediaData);
    }

    private void callback(MediaData m) {
        if (mOnMediaLoadListener != null) {
            mOnMediaLoadListener.onMediaLoaded(m);
        }
    }
}
