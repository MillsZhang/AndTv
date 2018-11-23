package com.mills.zh.core;

import android.view.Surface;
import android.view.SurfaceHolder;

import com.mills.zh.data.DataSource;

/**
 * Created by zhangmd on 2018/8/22.
 */

public interface IPlayer {

    int STATE_END = -2;
    int STATE_ERROR = -1;
    int STATE_IDLE = 0;
    int STATE_INITIALIZED = 1;
    int STATE_PREPARED = 2;
    int STATE_STARTED = 3;
    int STATE_PAUSED = 4;
    int STATE_STOPPED = 5;      // TODO 为了区分idle状态
    int STATE_PLAYBACK_COMPLETE = 6;

    // setting
    void setDataSource(DataSource dataSource);
    void setDisplay(SurfaceHolder surfaceHolder);
    void setSurface(Surface surface);
    void setVolume(float left, float right);
    void setSpeed(float speed);

    // controll
    void start();
    void start(int msc);
    void pause();
    void resume();
    void seekTo(int msc);
    void stop();
    void reset();
    void destroy();

    // info
    int getBufferPercentage();
    boolean isPlaying();
    int getCurrentPosition();
    int getDuration();
    int getAudioSessionId();
    int getVideoWidth();
    int getVideoHeight();
    int getState();

}
