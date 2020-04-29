package com.longrise.android.jssdk.wx.utils.audio;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.SystemClock;

/**
 * Created by godliness on 2019-12-13.
 *
 * @author godliness
 * 用于采集音频数据
 */
public final class AudioCapturer {

    private static final String TAG = "AudioCapturer";

    private static final int DEFAULT_SOURCE = MediaRecorder.AudioSource.MIC;
    private static final int DEFAULT_SAMPLE_RATE = 44100;
    private static final int DEFAULT_CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_STEREO;
    private static final int DEFAULT_AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;

    private AudioRecord mAudioRecord;
    private int mMinBufferSize = 0;

    private Thread mCaptureThread;
    private boolean mIsCaptureStarted = false;
    private volatile boolean mIsLoopExit = false;

    //    private OnVoiceCapturerListener mAudioFrameCapturedListener;
    private OnCaptureEndListener mCaptureEndCallback;

//    public interface OnVoiceCapturerListener {
//
//        void onVoiceCapturing(byte[] audioData);
//
//        void onCapturerEnd();
//    }

    /**
     * 是否已经开始采集
     */
    public boolean isCaptureStarted() {
        return mIsCaptureStarted;
    }

//    /**
//     * 通知外部采集结果
//     */
//    public void setOnAudioFrameCapturedListener(OnVoiceCapturerListener listener) {
//        mAudioFrameCapturedListener = listener;
//    }

    /**
     * 开始采集
     */
    public boolean startCapture() {
        return startCapture(DEFAULT_SOURCE, DEFAULT_SAMPLE_RATE, DEFAULT_CHANNEL_CONFIG,
                DEFAULT_AUDIO_FORMAT);
    }

    /**
     * 开始采集
     */
    public boolean startCapture(int audioSource, int sampleRateInHz, int channelConfig, int audioFormat) {
        if (mIsCaptureStarted) {
            return false;
        }

        mMinBufferSize = AudioRecord.getMinBufferSize(sampleRateInHz, channelConfig, audioFormat);
        if (mMinBufferSize == AudioRecord.ERROR_BAD_VALUE) {
            return false;
        }

        mAudioRecord = new AudioRecord(audioSource, sampleRateInHz, channelConfig, audioFormat, mMinBufferSize);
        if (mAudioRecord.getState() == AudioRecord.STATE_UNINITIALIZED) {
            return false;
        }

        mAudioRecord.startRecording();

        mIsLoopExit = false;
        mCaptureThread = new Thread(new AudioCaptureRunnable());
        mCaptureThread.start();
        mIsCaptureStarted = true;
        return true;
    }

    public interface OnCaptureEndListener {

        void onCaptureEnd();
    }

    public void setCaptureEndListener(OnCaptureEndListener callback) {
        this.mCaptureEndCallback = callback;
    }

    public void stopCapture(OnCaptureEndListener callback) {
        if (mCaptureEndCallback == null) {
            this.mCaptureEndCallback = callback;
        }
        stopCapture();
    }

    /**
     * 停止采集
     */
    public void stopCapture() {
        if (!mIsCaptureStarted) {
            return;
        }

        mIsLoopExit = true;
        try {
            mCaptureThread.interrupt();
            mCaptureThread.join(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (mAudioRecord.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING) {
            mAudioRecord.stop();
        }

        mAudioRecord.release();

        mIsCaptureStarted = false;
//        mAudioFrameCapturedListener = null;
    }

    private class AudioCaptureRunnable implements Runnable {

        @Override
        public void run() {

            while (!mIsLoopExit) {

                byte[] buffer = new byte[mMinBufferSize];

                int ret = mAudioRecord.read(buffer, 0, mMinBufferSize);
                if (ret == AudioRecord.ERROR_INVALID_OPERATION) {
                } else if (ret == AudioRecord.ERROR_BAD_VALUE) {
                } else {
//                    if (mAudioFrameCapturedListener != null) {
//                        mAudioFrameCapturedListener.onAudioFrameCaptured(buffer);
//                    }
                }
                SystemClock.sleep(10);
            }
        }
    }
}
