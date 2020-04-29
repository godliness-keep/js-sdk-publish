package com.longrise.android.jssdk.wx.image;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.longrise.android.jssdk.wx.R;

/**
 * Created by godliness on 2020-04-17.
 *
 * @author godliness
 * 图片预览
 */
public final class PreviewActivity extends AppCompatActivity {

    private static final String EXTRA_URLS = "extra_urls";

    private ViewPager mPager;
    private String[] mUrls;

    public static void preview(Context context, String current, String[] urls) {
        final String[] newUrls = new String[urls.length + 1];
        newUrls[0] = current;
        for (int i = 0; i < urls.length; i++) {
            newUrls[i + 1] = urls[i];
        }
        final Intent intent = new Intent(context, PreviewActivity.class);
        intent.putExtra(EXTRA_URLS, newUrls);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle state) {
        super.onCreate(state);
        if (state == null) {
            getExtraData();
        } else {
            onRestoreState(state);
        }
        setContentView(R.layout.modulewxsdk_activity_preview);
        initView();
    }

    private void initView() {
        mPager = findViewById(R.id.wxjssdk_vp_preview);
        initAdapter();
    }

    private void getExtraData() {
        this.mUrls = getIntent().getStringArrayExtra(EXTRA_URLS);
    }

    private void onRestoreState(Bundle state) {
        this.mUrls = state.getStringArray(EXTRA_URLS);
    }

    private void initAdapter() {
        final PreviewAdapter adapter = new PreviewAdapter(getSupportFragmentManager());
        adapter.setUrls(mUrls);
        mPager.setAdapter(adapter);
    }

    private static final class PreviewAdapter extends FragmentPagerAdapter {

        private String[] mUrls;

        PreviewAdapter(FragmentManager fm) {
            super(fm);
        }

        void setUrls(String[] urls) {
            this.mUrls = urls;
        }

        @Override
        public Fragment getItem(int i) {
            return PreviewFragment.createInstance(mUrls[i]);
        }

        @Override
        public int getCount() {
            return mUrls != null ? mUrls.length : 0;
        }
    }

    public static final class PreviewFragment extends Fragment {

        private static final String EXTRA_INDEX = "index";

        public static PreviewFragment createInstance(String url) {
            final PreviewFragment previewFragment = new PreviewFragment();
            final Bundle extra = new Bundle();
            extra.putString(EXTRA_INDEX, url);
            previewFragment.setArguments(extra);
            return previewFragment;
        }

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            return inflater.inflate(R.layout.modulewxsdk_fragment_preview_item, container, false);
        }

        @Override
        public void onActivityCreated(@Nullable Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            final String url = getArguments().getString(EXTRA_INDEX);
            final ImageView ivPreview = getView().findViewById(R.id.modulejssdk_iv_fragment_preview_item);
            final ProgressBar progressView = getView().findViewById(R.id.modulejssdk_pb_fragment_preview_item);
            Glide.with(this).load(url).listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    progressView.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "加载失败", Toast.LENGTH_SHORT).show();
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    progressView.setVisibility(View.GONE);
                    return false;
                }
            }).into(ivPreview);
        }
    }
}
