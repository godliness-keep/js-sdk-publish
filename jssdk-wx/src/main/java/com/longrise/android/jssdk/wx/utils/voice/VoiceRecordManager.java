package com.longrise.android.jssdk.wx.utils.voice;


import com.longrise.android.jssdk.wx.utils.audio.AudioCapturer;

/**
 * Created by godliness on 2020-04-17.
 *
 * @author godliness
 */
public final class VoiceRecordManager {

    private static volatile AudioCapturer sCapturer;

    public interface OnVoiceRecordListener {

        void onVoiceEnd(String localId);
    }

    public static void startRecord() {
        createVoiceCapturerIfNeed().startCapture();
    }

    public static void setVoiceRecordEnd(AudioCapturer.OnCaptureEndListener callback) {
        if (sCapturer != null) {
            sCapturer.setCaptureEndListener(callback);
        }
    }

    public static void stopRecord() {
        stopRecord(null);
    }

    public static void stopRecord(AudioCapturer.OnCaptureEndListener callback) {
        if (sCapturer != null) {
            sCapturer.stopCapture(callback);
            sCapturer = null;
        }
    }

    private static AudioCapturer createVoiceCapturerIfNeed() {
        if (sCapturer == null) {
            synchronized (VoiceRecordManager.class) {
                if (sCapturer == null) {
                    sCapturer = new AudioCapturer();
                }
            }
        }
        return sCapturer;
    }
}
