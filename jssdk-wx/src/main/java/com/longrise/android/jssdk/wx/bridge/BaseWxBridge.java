package com.longrise.android.jssdk.wx.bridge;

import android.app.Activity;
import android.webkit.JavascriptInterface;

import com.longrise.android.jssdk.Request;
import com.longrise.android.jssdk.Response;
import com.longrise.android.jssdk.core.bridge.BaseBridge;
import com.longrise.android.jssdk.wx.image.PreviewActivity;
import com.longrise.android.jssdk.wx.mode.ChooseImage;
import com.longrise.android.jssdk.wx.mode.DownloadImage;
import com.longrise.android.jssdk.wx.mode.PreviewImage;
import com.longrise.android.jssdk.wx.mode.UploadImage;
import com.longrise.android.jssdk.wx.utils.NetUtil;
import com.longrise.android.jssdk.wx.utils.UrlUtil;
import com.longrise.android.jssdk.wx.utils.file.FileService;
import com.longrise.android.jssdk.wx.utils.file.OnFileProgressListener;
import com.longrise.android.jssdk.wx.utils.file.ProgressResponseBody;
import com.longrise.android.photowall.PhotoWallCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;

/**
 * Created by godliness on 2020-04-16.
 *
 * @author godliness
 * 兼容Wechat API
 */
@SuppressWarnings("unused")
public class BaseWxBridge<T extends Activity> extends BaseBridge<T> {

    private static final String TAG = "BaseWxBridge";

    @JavascriptInterface
    public final void onMenuShareWeibo(String message) {
    }

    @JavascriptInterface
    public final void onMenuShareQZone(String message) {
    }

    @JavascriptInterface
    public final void chooseImage(String message) {
        final Request<ChooseImage> request = Request.parseRequest(message, ChooseImage.class);
        final ChooseImage chooseImage = request.getParams();

        PhotoWallCallback.getInstance().callback(new PhotoWallCallback.PhotoSelectedCallback() {
            @Override
            public void onSelected(String[] values) {
                Response.create(request.getCallbackId())
                        .result(values)
                        .notify(getWebView());
            }
        }).openPhotoWall(getHost(), chooseImage.getCount());
    }

    @JavascriptInterface
    public final void previewImage(String message) {
        final Request<PreviewImage> request = Request.parseRequest(message, PreviewImage.class);
        final PreviewImage previewImage = request.getParams();
        PreviewActivity.preview(getHost(), previewImage.getCurrent(), previewImage.getUrls());
    }

    @JavascriptInterface
    public final void uploadImage(String message) {
        final Request<UploadImage> request = Request.parseRequest(message, UploadImage.class);
        final UploadImage uploadImage = request.getParams();

        FileService.uploadFile(UrlUtil.getUploadImageServiceUrl(getHost()), uploadImage.getLocalId(), new OnFileProgressListener() {
            @Override
            public void onUploadProgress(long current, long total, boolean done) {
            }

            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, okhttp3.Response response) throws IOException {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    Response.create(request.getCallbackId())
                            .result(jsonObject.getString("serverId"))
                            .notify(getWebView());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @JavascriptInterface
    public final void downloadImage(String message) {
        final Request<DownloadImage> request = Request.parseRequest(message, DownloadImage.class);
        final DownloadImage downloadImage = request.getParams();
        final String resName = downloadImage.getResName();

        FileService.downloadFile(downloadImage.getServerId(), new OnFileProgressListener() {
            @Override
            public void onUploadProgress(long current, long total, boolean done) {

            }

            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, okhttp3.Response response) throws IOException {
                ((ProgressResponseBody) response.body()).writeTo("", resName);

                Response.create(request.getCallbackId())
                        .result(null)
                        .notify(getWebView());
            }
        });
    }

    @JavascriptInterface
    public final void getLocalImgData(String message) {
        // Android 平台不存在该功能
    }

    @JavascriptInterface
    public final void startRecord() {
    }

    @JavascriptInterface
    public final void stopRecord(String message) {
    }

    @JavascriptInterface
    public final void onVoiceRecordEnd(final String message) {
    }

    @JavascriptInterface
    public final void playVoice(String message) {
    }

    @JavascriptInterface
    public final void pauseVoice(String message) {
    }

    @JavascriptInterface
    public final void stopVoice(String message) {
    }

    @JavascriptInterface
    public final void onVoicePlayEnd(String message) {

    }

    @JavascriptInterface
    public final void uploadVoice(String message) {
    }

    @JavascriptInterface
    public final void downloadVoice(String message) {
    }

    @JavascriptInterface
    public final void translateVoice(String message) {

    }

    @JavascriptInterface
    public final void getNetworkType(String message) {
        final Request request = Request.parseRequest(message, Request.class);
        final String networkType = NetUtil.getNetworkType(getHost());
        Response.create(request.getCallbackId())
                .result(networkType)
                .state(Response.RESULT_OK)
                .notify(getWebView());
    }

    @JavascriptInterface
    public final void openLocation(String message) {
    }

    @JavascriptInterface
    public final void getLocation(String message) {
    }

    @JavascriptInterface
    public final void startSearchBeacons(String message) {

    }

    @JavascriptInterface
    public final void stopSearchBeacons(String message) {

    }

    @JavascriptInterface
    public final void onSearchBeacons(String message) {

    }

    @JavascriptInterface
    public final void closeWindow(String message) {

    }

    @JavascriptInterface
    public final void hideMenuItems(String message) {

    }

    @JavascriptInterface
    public final void showMenuItems(String message) {

    }

    @JavascriptInterface
    public final void hideAllNonBaseMenuItem(String message) {

    }

    @JavascriptInterface
    public final void showAllNonBaseMenuItem(String messgae) {

    }

    @JavascriptInterface
    public final void scanQRCode(String message) {

    }

    @JavascriptInterface
    public final void openProductSpecificView(String message) {

    }

    @JavascriptInterface
    public final void chooseCard(String message) {

    }

    @JavascriptInterface
    public final void addCard(String message) {

    }

    @JavascriptInterface
    public final void openCard(String message) {

    }

    @JavascriptInterface
    public final void chooseWXPay(String message) {

    }

    @JavascriptInterface
    public final void openAddress(String message) {

    }
}
