package com.longrise.android.photowall.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.util.LruCache;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

/**
 * Created by sujizhong on 16/6/24.
 * todo 临时编写
 */
public class ImageLoader {

    private LruCache<String, Bitmap> mLruCache;

    private ExecutorService mThreadPool;
    private LinkedList<Runnable> mTasks;
    private Thread mPoolThread;
    private Handler mPoolThreadHander;

    private Type mType = Type.LIFO;

    private Handler mHandler;

    private volatile Semaphore mSemaphore = new Semaphore(0);

    private volatile Semaphore mPoolSemaphore;

    private static ImageLoader mInstance;

    public enum Type {
        FIFO, LIFO
    }

    private ImageLoader(int threadCount, Type type) {
        init(threadCount, type);
    }

    public static ImageLoader getInstance() {
        if (mInstance == null) {
            synchronized (ImageLoader.class) {
                if (mInstance == null) {
                    mInstance = new ImageLoader(1, Type.LIFO);
                }
            }
        }
        return mInstance;
    }

    public static ImageLoader getInstance(int threadCount, Type type) {
        if (mInstance == null) {
            synchronized (ImageLoader.class) {
                if (mInstance == null) {
                    mInstance = new ImageLoader(threadCount, type);
                }
            }
        }
        return mInstance;
    }

    private void init(int threadCount, Type type) {
        mPoolThread = new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                mPoolThreadHander = new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        mThreadPool.execute(getTask());
                        try {
                            mPoolSemaphore.acquire();
                        } catch (InterruptedException e) {
                        }
                    }
                };
                mSemaphore.release();
                Looper.loop();
            }
        };
        mPoolThread.start();
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheSize = maxMemory / 8;
        mLruCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getRowBytes() * value.getHeight();
            }
        };
        mThreadPool = Executors.newFixedThreadPool(threadCount);
        mPoolSemaphore = new Semaphore(threadCount);
        mTasks = new LinkedList<>();
        mType = type == null ? Type.LIFO : type;
    }

    public void loadImage(final String path, final ImageView imageView) {
        imageView.setTag(path);
        if (mHandler == null) {
            mHandler = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(Message msg) {
                    ImgBeanHolder holder = (ImgBeanHolder) msg.obj;
                    ImageView imageView = holder.mImageView;
                    Bitmap bm = holder.mBitmap;
                    String path = holder.mPath;
                    if (imageView.getTag().toString().equals(path)) {
                        imageView.setVisibility(View.VISIBLE);
                        imageView.setImageBitmap(bm);
                    }
                }
            };
        }

        Bitmap bm = getBitmapFromLruCache(path);
        if (bm != null) {
            ImgBeanHolder holder = new ImgBeanHolder();
            holder.mBitmap = bm;
            holder.mImageView = imageView;
            holder.mPath = path;
            Message message = Message.obtain();
            message.obj = holder;
            mHandler.sendMessage(message);
        } else {
            addTask(new Runnable() {
                @Override
                public void run() {
                    ImageSize imageSize = getImageViewWidth(imageView);
                    int reqWidth = imageSize.mWidth;
                    int reqHeight = imageSize.mHeight;
                    Bitmap bm = decodeSampledBitmapFromResource(path, reqWidth, reqHeight);
                    addBitmapToLruCache(path, bm);
                    ImgBeanHolder holder = new ImgBeanHolder();
                    holder.mBitmap = getBitmapFromLruCache(path);
                    holder.mImageView = imageView;
                    holder.mPath = path;
                    Message message = Message.obtain();
                    message.obj = holder;
                    mHandler.sendMessage(message);
                    mPoolSemaphore.release();
                }
            });
        }
    }

    private synchronized void addTask(Runnable runnable) {
        if (mPoolThreadHander == null) {
            try {
                mSemaphore.acquire();
            } catch (InterruptedException e) {
            }
        }
        mTasks.add(runnable);
        mPoolThreadHander.sendEmptyMessage(0x110);
    }

    private synchronized Runnable getTask() {
        if (mType == Type.FIFO) {
            return mTasks.removeFirst();
        } else if (mType == Type.LIFO) {
            return mTasks.removeLast();
        }
        return null;
    }

    private ImageSize getImageViewWidth(ImageView imageView) {
        ImageSize imageSize = new ImageSize();
        final DisplayMetrics displayMetrics = imageView.getContext().getResources().getDisplayMetrics();
        final LayoutParams params = imageView.getLayoutParams();
        int width = params.width == LayoutParams.WRAP_CONTENT ? 0 : imageView.getWidth();
        if (width <= 0) {
            width = params.width;
        }
        if (width <= 0) {
            width = getImageViewFieldValue(imageView, "mMaxWidth");
        }
        if (width <= 0) {
            width = displayMetrics.widthPixels;
        }
        int height = params.height == LayoutParams.WRAP_CONTENT ? 0 : imageView.getHeight();
        if (height <= 0) {
            height = params.height;
        }
        if (height <= 0) {
            height = getImageViewFieldValue(imageView, "mMaxHeight");
        }
        if (height <= 0) {
            height = displayMetrics.heightPixels;
        }
        imageSize.mWidth = width;
        imageSize.mHeight = height;
        return imageSize;
    }

    private Bitmap getBitmapFromLruCache(String key) {
        return mLruCache.get(key);
    }

    private void addBitmapToLruCache(String key, Bitmap bitmap) {
        if (getBitmapFromLruCache(key) == null) {
            if (bitmap != null) {
                mLruCache.put(key, bitmap);
            }
        }
    }

    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        int width = options.outWidth;
        int height = options.outHeight;
        int inSampleSize = 1;
        if (width > reqWidth || height > reqHeight) {
            int widthRatio = Math.round((float) width / (float) reqWidth);
            int heightRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = Math.max(widthRatio, heightRatio);
        }
        return inSampleSize;
    }

    private Bitmap decodeSampledBitmapFromResource(String pathName, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pathName, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(pathName, options);
        return bitmap;
    }

    private class ImgBeanHolder {
        Bitmap mBitmap;
        ImageView mImageView;
        String mPath;
    }

    private class ImageSize {
        int mWidth;
        int mHeight;
    }

    private static int getImageViewFieldValue(Object object, String fieldName) {
        int value = 0;
        try {
            Field field = ImageView.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            int fieldValue = (Integer) field.get(object);
            if (fieldValue > 0 && fieldValue < Integer.MAX_VALUE) {
                value = fieldValue;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }
}
