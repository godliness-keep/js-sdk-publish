package com.longrise.android.jssdk.wx.utils.audio;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

/**
 * Created by godliness on 2019-12-13.
 *
 * @author godliness
 * 播放采集的数据
 */
public final class AudioPlayer {

    private static final int DEFAULT_STREAM_TYPE = AudioManager.STREAM_MUSIC;
    private static final int DEFAULT_SAMPLE_RATE = 44100;
    private static final int DEFAULT_CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_STEREO;
    private static final int DEFAULT_AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;
    private static final int DEFAULT_PLAY_MODE = AudioTrack.MODE_STREAM;

    private boolean mIsPlayStarted = false;
    private int mMinBufferSize = 0;
    private AudioTrack mAudioTrack;

    public boolean startPlayer() {
        return startPlayer(DEFAULT_STREAM_TYPE, DEFAULT_SAMPLE_RATE, DEFAULT_CHANNEL_CONFIG, DEFAULT_AUDIO_FORMAT);
    }

    /**
     * 创建AudioTrack
     */
    public boolean startPlayer(int streamType, int sampleRateInHz, int channelConfig, int audioFormat) {
        if (mIsPlayStarted) {
            return false;
        }

        mMinBufferSize = AudioTrack.getMinBufferSize(sampleRateInHz, channelConfig, audioFormat);
        if (mMinBufferSize == AudioTrack.ERROR_BAD_VALUE) {
            return false;
        }

        mAudioTrack = new AudioTrack(streamType, sampleRateInHz, channelConfig, audioFormat, mMinBufferSize, DEFAULT_PLAY_MODE);
        if (mAudioTrack.getState() == AudioTrack.STATE_UNINITIALIZED) {
            return false;
        }

        return mIsPlayStarted = true;
    }

    public int getMinBufferSize() {
        return mMinBufferSize;
    }

    /**
     * 停止播放
     */
    public void stopPlayer() {
        if (!mIsPlayStarted) {
            return;
        }
        if (mAudioTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING) {
            mAudioTrack.stop();
        }
        mAudioTrack.release();
        mIsPlayStarted = false;
    }

    /**
     * 播放
     */
    public boolean play(byte[] audioData, int offsetInBytes, int sizeInBytes) {
        if (!mIsPlayStarted) {
            return false;
        }
        if (sizeInBytes < mMinBufferSize) {
            return false;
        }
        if (mAudioTrack.write(audioData, offsetInBytes, sizeInBytes) != sizeInBytes) {
        }
        mAudioTrack.play();
        return true;
    }
}
