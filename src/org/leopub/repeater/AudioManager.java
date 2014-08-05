package org.leopub.repeater;

import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.util.Log;

public class AudioManager {
    public enum Status {
        Unready,
        Playing,
        Paused,
    }

    private static AudioManager sAudioManager = null;

    private MediaPlayer mPlayer;
    private Status  mStatus;

    private AudioManager() {
        mPlayer = new MediaPlayer();
        mStatus = Status.Unready;
    }

    public static AudioManager getInstance() {
        if (sAudioManager == null) {
            sAudioManager = new AudioManager();
        }
        return sAudioManager;
    }

    public void start(String path) {
        try {
            mPlayer.setDataSource(path);
            mPlayer.prepare();
            mPlayer.start();
            mStatus = Status.Playing;
        } catch (Exception e) {
            Log.e("TextActivity", e.getMessage());
        }
    }

    public void pause() {
        mPlayer.pause();
        mStatus = Status.Paused;
    }

    public void resume() {
        mPlayer.start();
        mStatus = Status.Playing;
    }

    public void stop() {
        if (mStatus != Status.Unready) {
            mPlayer.stop();
            mStatus = Status.Unready;
        }
    }

    public void seekBy(int offset) {
        int pos = mPlayer.getCurrentPosition();
        int newPos = pos + offset;
        newPos = Math.max(0, newPos);
        newPos = Math.min(newPos, mPlayer.getDuration());
        mPlayer.seekTo(newPos);
    }

    public Status getStatus() {
        return mStatus;
    }

    public void reset() {
        mPlayer.reset();
        mStatus = Status.Unready;
    }

    @SuppressLint("DefaultLocale")
    public String getPosString() {
        int pos = mPlayer.getCurrentPosition();
        int sec = pos / 1000;
        int min = sec / 60;
        sec = sec % 60;
        return String.format("%d:%02d", min, sec);
    }
}
