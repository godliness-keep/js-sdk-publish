package com.longrise.android.photowall.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.lang.reflect.Field;

/**
 * Created by sujizhong on 16/3/23.
 */
public class BitmapUtil {

    private static BitmapUtil mBitmapUtil = null;
    private static final int IMAGEREAULT = 0x00000000;

    private BitmapUtil(){}

    protected Handler mHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case IMAGEREAULT:
                    ImageInfor imageInfor = (ImageInfor) msg.obj;
                    imageInfor.mImage.setImageBitmap(imageInfor.mBitmap);
                    break;

                default:
                    break;
            }

        }
    };

    public static BitmapUtil getBitmapFactory(){
        if(mBitmapUtil == null){
            synchronized (BitmapUtil.class){
                mBitmapUtil = new BitmapUtil();
            }
        }
        return mBitmapUtil;
    }

    public void loadLocalImage(final String path, final ImageView v){
        new Thread(){
            @Override
            public void run() {
                if(TextUtils.isEmpty(path)){
                }
                ImageSize imageSize = getImageSize(v);
                Bitmap bitmap = decodeBitmapFromLocalPath(path, imageSize.mImageWidth, imageSize.mImageHeight);
                Message msg = new Message();
                msg.what = IMAGEREAULT;
                ImageInfor imageInfor = new ImageInfor();
                imageInfor.mBitmap = bitmap;
                imageInfor.mImage = v;
                msg.obj = imageInfor;
                mHandler.sendMessage(msg);
            }
        }.start();

    }

    private ImageSize getImageSize(ImageView imageView){
        ImageSize imageSize = new ImageSize();
        ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
        DisplayMetrics displayMetrics = imageView.getContext().getResources().getDisplayMetrics();
        int width = imageView.getWidth();
        if (width <= 0) {
            width = layoutParams.width;
        }
        if (width <= 0) {
            width = getImageViewFieldMaxValue(imageView, "mMaxWidth");
        }
        if (width <= 0) {
            width = displayMetrics.widthPixels;
        }
        int height = imageView.getHeight();
        if (height <= 0) {
            height = layoutParams.height;
        }
        if (height <= 0) {
            height = getImageViewFieldMaxValue(imageView, "mMaxHeight");
        }
        if (height <= 0) {
            height = displayMetrics.heightPixels;
        }
        imageSize.mImageWidth = width;
        imageSize.mImageHeight = height;
        return imageSize;

    }

    private Bitmap decodeBitmapFromLocalPath(String path, int mImageWidth, int mImageHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;  //获取图片的大小 不加载到内存中
        BitmapFactory.decodeFile(path, options);  //得到实际图片
        options.inSampleSize = caculateInSampleSize(options, mImageWidth, mImageHeight);

        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        return bitmap;
    }

    public int caculateInSampleSize(BitmapFactory.Options op, int reqWidth, int reqHeight) {
        int width = op.outWidth;
        int height = op.outHeight;
        int inSampleSize = 1;
        if (width > reqWidth && height > reqHeight) {
            int widthRadio = Math.round(width * 1.0f / reqWidth);
            int heightRadio = Math.round(height * 1.0f / reqHeight);
            inSampleSize = Math.max(widthRadio, heightRadio);
        }
        return inSampleSize;
    }

    private static final int getImageViewFieldMaxValue(Object o, String filedName) {
        int value = 0;
        try {
            Field fieled = ImageView.class.getDeclaredField(filedName);
            fieled.setAccessible(true);
            int defaultValue = fieled.getInt(o);
            if (defaultValue > 0 && defaultValue < Integer.MAX_VALUE) {
                value = defaultValue;
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return value;
    }

    private class ImageSize{
        public int mImageWidth;
        public int mImageHeight;
    }

    private class ImageInfor{
        public Bitmap mBitmap = null;
        public ImageView mImage = null;
    }

    public interface onImageLoadResult{
        void onImageReault(Bitmap bitmap);
    }

}
