package com.longrise.android.photowall.adapter;

import android.content.Context;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.longrise.android.photowall.R;
import com.longrise.android.photowall.utils.ImageLoader;

import java.util.List;

/**
 * Created by godliness on 16/3/20.
 * todo 临时编写，待重构
 */
public class PhotoAdapter extends BaseAdapter {

    private final LayoutInflater mInflater;
    private final int mMaxThreadSize;
    private final ArrayMap<Integer, Count> mIndexs = new ArrayMap<>(9);

    private List<String> mImages;
    private OnSelectListener mOnSelectListener;

    public PhotoAdapter(Context cxt) {
        this(cxt, 3);
    }

    public PhotoAdapter(Context cxt, int maxThreadSize) {
        this.mInflater = LayoutInflater.from(cxt);
        this.mMaxThreadSize = maxThreadSize;
    }

    @Override
    public int getCount() {
        return mImages == null ? 0 : mImages.size();
    }

    public void setData(List<String> imageList) {
        this.mImages = imageList;
    }

    @Override
    public Object getItem(int position) {
        return mImages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ImageHolder photoHolder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.activity_photo_item_view, parent, false);
            photoHolder = new ImageHolder(convertView);
            convertView.setTag(photoHolder);
        } else {
            photoHolder = (ImageHolder) convertView.getTag();
        }
        final Count value = mIndexs.get(position);
        if (value != null) {
            photoHolder.setText(String.format("%s", value.value));
        } else {
            photoHolder.setText("");
        }
        photoHolder.mPhotoView.setPosition(position);
        if (!photoHolder.mPhotoView.hasOnClickListeners()) {
            photoHolder.mPhotoView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final int currentPosition = ((PhotoView) v).getPosition();
                    final Count count = mIndexs.remove(currentPosition);
                    if (count == null) {
                        if (mOnSelectListener.onSelected(getPath(currentPosition))) {
                            final int newSize = mIndexs.size() + 1;
                            mIndexs.put(currentPosition, new Count(newSize));
                            photoHolder.setText(String.format("%s", newSize));
                        }
                    } else {
                        mOnSelectListener.onSelected(getPath(currentPosition));
                        changeMinusOne(count);
                    }
                }
            });
        }
        ImageLoader.getInstance(mMaxThreadSize, ImageLoader.Type.FIFO).loadImage(getPath(position), photoHolder.mPhotoView);
        return convertView;
    }


    static final class Count {

        int value;

        Count(int value) {
            this.value = value;
        }
    }

    public void setOnSelectListener(OnSelectListener onSelectListener) {
        this.mOnSelectListener = onSelectListener;
    }

    public interface OnSelectListener {

        /**
         * On Selected
         *
         * @param path current resource
         * @return state
         */
        boolean onSelected(String path);
    }

    private final class ImageHolder {
        private PhotoView mPhotoView;
        private TextView mTvCount;

        private ImageHolder(View host) {
            this.mPhotoView = host.findViewById(R.id.iv_item_photo_adapter);
            this.mTvCount = host.findViewById(R.id.tv_count_item_photo_adapter);
        }

        void setText(String text) {
            mTvCount.setText(text);
            mTvCount.setVisibility(TextUtils.isEmpty(text) ? View.GONE : View.VISIBLE);
        }
    }

    private void changeMinusOne(Count count) {
        final int size = mIndexs.size();
        for(int i = 0; i < size; i++){
            final Count value = mIndexs.valueAt(i);
            if(value.value > count.value){
                value.value--;
            }
        }

//        for (Map.Entry<Integer, Count> entry : mIndexs.entrySet()) {
//            final Count value = entry.getValue();
//            if (value.value > count.value) {
//                value.value--;
//            }
//        }
        notifyDataSetChanged();
    }

    private String getPath(int position) {
        return mImages.get(position);
    }

}
