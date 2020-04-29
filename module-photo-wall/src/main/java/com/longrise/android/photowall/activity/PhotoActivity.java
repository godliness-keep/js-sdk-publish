package com.longrise.android.photowall.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.util.ArraySet;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.longrise.android.photowall.PhotoWallCallback;
import com.longrise.android.photowall.R;
import com.longrise.android.photowall.adapter.PhotoAdapter;
import com.longrise.android.photowall.bean.FolderBean;
import com.longrise.android.photowall.thread.RequestMediaImpl;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Created by godliness on 16/3/20.
 * 照片墙
 * todo 临时编写，待重构
 */
public class PhotoActivity extends AppCompatActivity implements View.OnClickListener, Handler.Callback {

    private static final String EXTRA_SELECT_COUNT = "max_select_count";

    private int mMaxSelectCount;

    private static final int MSG_LOAD_START = 0;
    private static final int MSG_SELECT_TOP = 1;

    public static void openPhotoActivity(Context cxt, int maxSelectCount) {
        final Intent intent = new Intent(cxt, PhotoActivity.class);
        intent.putExtra(EXTRA_SELECT_COUNT, maxSelectCount);
        cxt.startActivity(intent);
    }

    private String mCurrentDirPath = "";
    private final List<String> mImages = new ArrayList<>();
    private List<FolderBean> mFolderBeans = null;

    private ProgressDialog mProgressDialog;

    private PhotoAdapter mPhotoAdapter = null;
    private GridView mGridView = null;
    private View mBack;
    private View mConfirm;
    private TextView mTotal;

    private ArraySet<String> mPosition = new ArraySet<>(9);

    private Handler mHandler = new Handler(this);

    @Override
    public final boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_LOAD_START:
                mProgressDialog.dismiss();
                RequestMediaImpl.MediaData data = (RequestMediaImpl.MediaData) msg.obj;
                mFolderBeans = data.mFolderBeans;
                mCurrentDirPath = data.mDir;
                setAllImageData();
                return true;

            case MSG_SELECT_TOP:
                mGridView.setSelection(0);
                return true;

            default:
                return false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMaxSelectCount = getIntent().getIntExtra(EXTRA_SELECT_COUNT, 9);
        setContentView(R.layout.activity_photo);
        init();
        initEvent();
        verifyStoragePermissions();
    }

    private void initEvent() {
        mBack.setOnClickListener(this);
        mConfirm.setOnClickListener(this);
        mTotal.setOnClickListener(this);
    }

    private void init() {
        mBack = findViewById(R.id.iv_back);
        mConfirm = findViewById(R.id.tv_config);
        mTotal = findViewById(R.id.tv_total);
        mGridView = findViewById(R.id.gv_wall);
        setAdapter();
    }

    private void setAdapter() {
        mPhotoAdapter = new PhotoAdapter(this);
        mPhotoAdapter.setOnSelectListener(new PhotoAdapter.OnSelectListener() {
            @Override
            public boolean onSelected(String path) {
                boolean changed = false;
                if (mPosition.contains(path)) {
                    mPosition.remove(path);
                } else {
                    if (mPosition.size() < mMaxSelectCount) {
                        mPosition.add(path);
                        changed = true;
                    }else{
                        showToast(String.format("最多选择: %d 张", mMaxSelectCount));
                    }
                }
                updateConfrim();
                return changed;
            }
        });
        mGridView.setAdapter(mPhotoAdapter);
    }

    private void requestData() {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            showToast(getString(R.string.sdcard_no_use));
            return;
        }
        mProgressDialog = ProgressDialog.show(this, null, getString(R.string.is_loading));
        new RequestMediaImpl(this, mOnMediaLoadListener).start();
    }

    private RequestMediaImpl.OnMediaLoadListener mOnMediaLoadListener = new RequestMediaImpl.OnMediaLoadListener() {

        @Override
        public void onMediaLoaded(RequestMediaImpl.MediaData mediaData) {
            if (mediaData == null) {
                showToast(getString(R.string.no_getimages));
                return;
            }
            Message msg = new Message();
            msg.what = MSG_LOAD_START;
            msg.obj = mediaData;
            mHandler.sendMessage(msg);
        }
    };

    private void setAllImageData() {
        if (mCurrentDirPath != null && mCurrentDirPath.equals(getString(R.string.all_images))) {
            return;
        }
        mCurrentDirPath = getString(R.string.all_images);
        int totalCount = 0;
        mImages.clear();
        for (FolderBean folderBean : mFolderBeans) {
            File file = new File(folderBean.getDir());
            String path = file.getAbsolutePath();
            List<String> folders = imageFilter(file);
            final int size = folders.size();
            for (int i = size - 1; i >= 0; i--) {
                mImages.add(path + "/" + folders.get(i));
                totalCount++;
            }
        }
        notifyState(getString(R.string.all_images), String.valueOf(totalCount));
    }

    private List<String> imageFilter(final File file) {
        return Arrays.asList(file.list(getFilenameFilter()));
    }

    private FilenameFilter mFilenameFilter;

    private FilenameFilter getFilenameFilter() {
        if (mFilenameFilter == null) {
            mFilenameFilter = new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    if (name.endsWith(".png") || name.endsWith(".jpeg") || name.endsWith(".jpg")) {
                        return true;
                    }
                    return false;
                }
            };
        }
        return mFilenameFilter;
    }


//    private void switchDir(int position) {
//        FolderBean folderBean = mFolderBeans.get(position);
//        String dir = folderBean.getDir();
//        if (dir.equals(mCurrentDirPath)) {
//            return;
//        }
//        mHandler.sendEmptyMessage(MSG_SELECT_TOP);
//        mImages.clear();
//        mCurrentDirPath = dir;
//        File file = new File(dir);
//        String path = file.getAbsolutePath();
//        List<String> images = imageFilter(file);
//        for (String itemImage : images) {
//            mImages.add(path + "/" + itemImage);
//        }
//        String foldername = folderBean.getName();
//        notifyState(foldername.substring(1, foldername.length()), String.valueOf(folderBean.getCount()));
//    }

    private void notifyState(String packName, String imageCount) {
        mPhotoAdapter.setData(mImages);
        mTotal.setText(imageCount + getString(R.string.za));
        mPhotoAdapter.notifyDataSetChanged();
    }


    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    private void collectImages() {
        if (mPosition.size() <= 0) {
            return;
        }
        final int size = mPosition.size();
        final String[] images = new String[size];
        for (int i = 0; i < size; i++) {
            final String path = mPosition.valueAt(i);
            images[i] = path;
        }
        PhotoWallCallback.getInstance().onCallback(images);
        finish();
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();
        if (id == R.id.iv_back) {
            finish();
        } else if (id == R.id.tv_config) {
            collectImages();
        }
    }

    private void updateConfrim() {
        mConfirm.setVisibility(mPosition.size() > 0 ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (permissions[0]) {
            case Manifest.permission.READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    requestData();
                } else {
                    finish();
                }
                break;

            case Manifest.permission.WRITE_EXTERNAL_STORAGE:
                break;

            default:
                break;
        }
    }

    private final int REQUEST_EXTERNAL_STORAGE = 1;
    private final String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
//            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private void verifyStoragePermissions() {
        int permission = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE);
        } else {
            requestData();
        }
    }
}
