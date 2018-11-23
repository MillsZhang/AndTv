package com.mills.zh.core.decoder;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.media.MediaPlayer.OnVideoSizeChangedListener;
import android.media.PlaybackParams;
import android.os.Build;
import android.os.Bundle;
import android.view.Surface;
import android.view.SurfaceHolder;

import com.orhanobut.logger.Logger;

import java.util.ArrayList;

import com.mills.zh.data.DataSource;
import com.mills.zh.event.BundlePool;
import com.mills.zh.event.Event;
import com.mills.zh.event.EventDataKey;
import com.mills.zh.event.EventDispatcher;

/**
 * Created by zhangmd on 2018/8/22.
 */

public class SysDecoder extends BaseDecoder {

    private EventDispatcher eventDispatcher;

    private MediaPlayer mediaPlayer;
    private DataSource dataSource;
    private int startSeekPos;
    private int bufferPercentage;
    private int videoWidth;
    private int videoHeight;
    private long bandWidth; // 带宽速度

    private int duration;      // 从MediaPlayer获取duration比较耗时

    public SysDecoder(){
        init();
    }

    private void init(){
        startSeekPos = 0;
        bufferPercentage = 0;
        videoWidth = 0;
        videoHeight = 0;
        bandWidth = 0;
        duration = -1;

        mediaPlayer = new MediaPlayer();
    }

    @Override
    public void setEventDispatcher(EventDispatcher eventDispatcher) {
        this.eventDispatcher = eventDispatcher;
    }

    @Override
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;

        duration = -1;
        bufferPercentage = 0;

        try {
            if(mediaPlayer == null){
                mediaPlayer = new MediaPlayer();
            }else{
                stop();
                reset();
                resetListener();
            }

            mediaPlayer.setOnPreparedListener(preparedListener);
            mediaPlayer.setOnVideoSizeChangedListener(videoSizeChangedListener);
            mediaPlayer.setOnCompletionListener(completionListener);
            mediaPlayer.setOnErrorListener(errorListener);
            mediaPlayer.setOnInfoListener(infoListener);
            mediaPlayer.setOnSeekCompleteListener(seekCompleteListener);
            mediaPlayer.setOnBufferingUpdateListener(bufferingUpdateListener);
            updateStatus(STATE_INITIALIZED);

            mediaPlayer.setDataSource(dataSource.getUrl());
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setScreenOnWhilePlaying(true);
            mediaPlayer.prepareAsync();

            // TODO why?
            Bundle bundle = BundlePool.obtain();
            bundle.putSerializable(EventDataKey.SERIALIZABLE_DATA, dataSource);
            sendEvent(Event.EVENT_PLAYER_DATASOURCE_SET, bundle);
        } catch (Exception e){
            e.printStackTrace();
            updateStatus(STATE_ERROR);
            targetState = STATE_ERROR;
        }
    }

    @Override
    public void setDisplay(SurfaceHolder surfaceHolder) {
        try {
            if(available()){
                mediaPlayer.setDisplay(surfaceHolder);
                sendEvent(Event.EVENT_PLAYER_SURFACE_HOLDER_UPDATE);
            }
        }catch (Exception e){
            handleException(e);
        }
    }

    @Override
    public void setSurface(Surface surface) {
        try {
            if(available()){
                mediaPlayer.setSurface(surface);
                sendEvent(Event.EVENT_PLAYER_SURFACE_UPDATE);
            }
        }catch (Exception e){
            handleException(e);
        }
    }

    @Override
    public void setVolume(float left, float right) {
        if(available()){
            mediaPlayer.setVolume(left, right);
        }
    }

    @Override
    public void setSpeed(float speed) {
        if(available() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            PlaybackParams playbackParams = mediaPlayer.getPlaybackParams();
            playbackParams.setSpeed(speed);
            mediaPlayer.setPlaybackParams(playbackParams);
        } else {
            Logger.e("not support play speed setting.");
        }
    }

    @Override
    public void start() {
        try {
            if(isInPlaybackState()){
                mediaPlayer.start();
                updateStatus(STATE_STARTED);
                sendEvent(Event.EVENT_PLAYER_START);
            }
        }catch (Exception e){
            handleException(e);
        }
        targetState = STATE_STARTED;
    }

    @Override
    public void start(int msc) {
        if(available()){
            if(msc > 0){
                startSeekPos = msc;
            }
            // TODO 感觉有问题
            start();
        }
    }

    @Override
    public void pause() {
        try{
            if(isInPlaybackState() && mediaPlayer.isPlaying()){
                mediaPlayer.pause();
                updateStatus(STATE_PAUSED);
                sendEvent(Event.EVENT_PLAYER_PAUSE);
            }
        }catch (Exception e){
            handleException(e);
        }
        targetState = STATE_PAUSED;
    }

    @Override
    public void resume() {
        try {
            if(available() && getState() == STATE_PAUSED){
                mediaPlayer.start();
                updateStatus(STATE_STARTED);
                sendEvent(Event.EVENT_PLAYER_RESUME);
            }
        }catch (Exception e){
            handleException(e);
        }
        targetState = STATE_STARTED;
    }

    @Override
    public void seekTo(int msc) {
        if(isInPlaybackState()){
            mediaPlayer.seekTo(msc);
            startSeekPos = 0;
            Bundle bundle = BundlePool.obtain();
            bundle.putInt(EventDataKey.INT_DATA, msc);
            sendEvent(Event.EVENT_PLAYER_SEEK_TO, bundle);
        }
    }

    @Override
    public void stop() {
        if(isInPlaybackState()){
            mediaPlayer.stop();
            updateStatus(STATE_STOPPED);
            sendEvent(Event.EVENT_PLAYER_STOP);
        }
        targetState = STATE_STOPPED;
    }

    @Override
    public void reset() {
        if(available()){
            mediaPlayer.reset();
            updateStatus(STATE_IDLE);
            sendEvent(Event.EVENT_PLAYER_RESET);
        }
        targetState = STATE_IDLE;
    }

    @Override
    public void destroy() {
        if(available()){
            updateStatus(STATE_END);
            resetListener();
            mediaPlayer.release();
            sendEvent(Event.EVENT_PLAYER_DESTROY);
        }
    }

    @Override
    public int getBufferPercentage() {
        if(available()){
            return bufferPercentage;
        }
        return 0;
    }

    @Override
    public boolean isPlaying() {
        // TODO isInPlaybackState
        if(available() && currentState != STATE_ERROR){
            return mediaPlayer.isPlaying();
        }
        return false;
    }

    @Override
    public int getCurrentPosition() {
        if (isInPlaybackState()) {
            return mediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    @Override
    public int getDuration() {
        if (isInPlaybackState()) {
            if(duration < 0){
                duration = mediaPlayer.getDuration();
            }

            return duration;
        }

        return -1;
    }

    @Override
    public int getAudioSessionId() {
        if(available()){
            return mediaPlayer.getAudioSessionId();
        }
        return 0;
    }

    @Override
    public int getVideoWidth() {
        if(available()){
            return videoWidth;
        }
        return 0;
    }

    @Override
    public int getVideoHeight() {
        // TODO 是否需要提前获取好，有效率问题吗?
        if(available()){
            return videoHeight;
        }
        return 0;
    }

    @Override
    public int getState() {
        return currentState;
    }

    private boolean available(){
        return mediaPlayer != null;
    }

    private boolean isInPlaybackState() {
        // TODO need STATE_END ?
        return (available() &&
                currentState != STATE_ERROR &&
                currentState != STATE_END &&
                currentState != STATE_IDLE &&
                currentState != STATE_STOPPED &&
                currentState != STATE_INITIALIZED);
    }


    private void handleException(Exception e){
        if(e!=null)
            e.printStackTrace();
        reset();
    }

    private void resetListener(){
        if(mediaPlayer != null){
            mediaPlayer.setOnPreparedListener(null);
            mediaPlayer.setOnVideoSizeChangedListener(null);
            mediaPlayer.setOnCompletionListener(null);
            mediaPlayer.setOnErrorListener(null);
            mediaPlayer.setOnInfoListener(null);
            mediaPlayer.setOnBufferingUpdateListener(null);
        }
    }

    MediaPlayer.OnPreparedListener preparedListener = new MediaPlayer.OnPreparedListener() {
        public void onPrepared(MediaPlayer mp) {
            Logger.i("onPrepared...");
            updateStatus(STATE_PREPARED);

            videoWidth = mp.getVideoWidth();
            videoHeight = mp.getVideoHeight();

            Bundle bundle = BundlePool.obtain();
            ArrayList<Integer> list = new ArrayList<Integer>(2);
            list.add(videoWidth);
            list.add(videoHeight);
            bundle.putIntegerArrayList(EventDataKey.INT_LIST_DATA, list);

            sendEvent(Event.EVENT_PLAYER_PREPARED, bundle);

            int seekToPosition = startSeekPos;
            if (seekToPosition != 0) {
                //seek to start position
                mediaPlayer.seekTo(seekToPosition);
                startSeekPos = 0;
            }

            // We don't know the video size yet, but should start anyway.
            // The video size might be reported to us later.
            Logger.d("targetState = " + targetState);
            if (targetState == STATE_STARTED) {
                start();
            } else if(targetState == STATE_PAUSED){
                pause();
            } else if(targetState == STATE_STOPPED
                    || targetState == STATE_IDLE){
                reset();
            }
        }
    };

    MediaPlayer.OnVideoSizeChangedListener videoSizeChangedListener = new OnVideoSizeChangedListener() {
        @Override
        public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
            videoWidth = width;
            videoHeight = height;

            Bundle bundle = BundlePool.obtain();
            ArrayList<Integer> list = new ArrayList<Integer>(2);
            list.add(videoWidth);
            list.add(videoHeight);
            bundle.putIntegerArrayList(EventDataKey.INT_LIST_DATA, list);

            sendEvent(Event.EVENT_PLAYER_VIDEO_SIZE_CHANGE, bundle);
        }
    };

    MediaPlayer.OnCompletionListener completionListener = new OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            updateStatus(STATE_PLAYBACK_COMPLETE);
            targetState = STATE_PLAYBACK_COMPLETE;
            sendEvent(Event.EVENT_PLAYER_PLAY_COMPLETE);
        }
    };

    MediaPlayer.OnErrorListener errorListener = new OnErrorListener() {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            Logger.d("Error: " + what + "," + extra);
            updateStatus(STATE_ERROR);
            targetState = STATE_ERROR;

            int eventCode = Event.EVENT_PLAYER_ERROR_COMMON;
            switch (what){
                case MediaPlayer.MEDIA_ERROR_IO:
                    eventCode = Event.EVENT_PLAYER_ERROR_IO;
                    break;
                case MediaPlayer.MEDIA_ERROR_MALFORMED:
                    eventCode = Event.EVENT_PLAYER_ERROR_MALFORMED;
                    break;
                case MediaPlayer.MEDIA_ERROR_TIMED_OUT:
                    eventCode = Event.EVENT_PLAYER_ERROR_TIMED_OUT;
                    break;
                case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                    eventCode = Event.EVENT_PLAYER_ERROR_UNKNOWN;
                    break;
                case MediaPlayer.MEDIA_ERROR_UNSUPPORTED:
                    eventCode = Event.EVENT_PLAYER_ERROR_UNSUPPORTED;
                    break;
                case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                    eventCode = Event.EVENT_PLAYER_ERROR_SERVER_DIED;
                    break;
                case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:
                    eventCode = Event.EVENT_PLAYER_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK;
                    break;
            }

            sendEvent(eventCode);
            return true;
        }
    };

    MediaPlayer.OnInfoListener infoListener = new OnInfoListener() {
        @Override
        public boolean onInfo(MediaPlayer mp, int what, int extra) {
            switch (what) {
                case MediaPlayer.MEDIA_INFO_VIDEO_TRACK_LAGGING:
                    Logger.d("MEDIA_INFO_VIDEO_TRACK_LAGGING:");
                    break;
                case MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                    Logger.d("MEDIA_INFO_VIDEO_RENDERING_START:");
                    startSeekPos = 0;
                    sendEvent(Event.EVENT_PLAYER_VIDEO_RENDER_START);
                    break;
                case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                    Logger.d("MEDIA_INFO_BUFFERING_START:" + extra);
                    Bundle bundle = BundlePool.obtain();
                    bundle.putLong(EventDataKey.LONG_DATA, bandWidth);
                    sendEvent(Event.EVENT_PLAYER_BUFFERING_START, bundle);
                    break;
                case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                    Logger.d("MEDIA_INFO_BUFFERING_END:" + extra);
                    Bundle bundle1 = BundlePool.obtain();
                    bundle1.putLong(EventDataKey.LONG_DATA, bandWidth);
                    sendEvent(Event.EVENT_PLAYER_BUFFERING_END, bundle1);
                    break;
                case MediaPlayer.MEDIA_INFO_BAD_INTERLEAVING:
                    Logger.d("MEDIA_INFO_BAD_INTERLEAVING:");
                    sendEvent(Event.EVENT_PLAYER_BAD_INTERLEAVING);
                    break;
                case MediaPlayer.MEDIA_INFO_NOT_SEEKABLE:
                    Logger.d("MEDIA_INFO_NOT_SEEKABLE:");
                    sendEvent(Event.EVENT_PLAYER_NOT_SEEKABLE);
                    break;
                case MediaPlayer.MEDIA_INFO_METADATA_UPDATE:
                    Logger.d("MEDIA_INFO_METADATA_UPDATE:");
                    sendEvent(Event.EVENT_PLAYER_METADATA_UPDATE);
                    break;
                case MediaPlayer.MEDIA_INFO_UNSUPPORTED_SUBTITLE:
                    Logger.d("MEDIA_INFO_UNSUPPORTED_SUBTITLE:");
                    sendEvent(Event.EVENT_PLAYER_UNSUPPORTED_SUBTITLE);
                    break;
                case MediaPlayer.MEDIA_INFO_SUBTITLE_TIMED_OUT:
                    Logger.d("MEDIA_INFO_SUBTITLE_TIMED_OUT:");
                    sendEvent(Event.EVENT_PLAYER_SUBTITLE_TIMED_OUT);
                    break;
                case 703/*MediaPlayer.MEDIA_INFO_NETWORK_BANDWIDTH*/:
                    Logger.d("MEDIA_INFO_NETWORK_BANDWIDTH" + extra);
                    bandWidth = extra * 1000;
                    break;
            }
            return true;
        }
    };

    MediaPlayer.OnSeekCompleteListener seekCompleteListener = new OnSeekCompleteListener() {
        @Override
        public void onSeekComplete(MediaPlayer mp) {
            Logger.d("EVENT_CODE_SEEK_COMPLETE");
            sendEvent(Event.EVENT_PLAYER_SEEK_COMPLETE);
        }
    };

    MediaPlayer.OnBufferingUpdateListener bufferingUpdateListener = new OnBufferingUpdateListener() {
        @Override
        public void onBufferingUpdate(MediaPlayer mp, int percent) {
            Bundle bundle = BundlePool.obtain();
            bundle.putInt(EventDataKey.INT_DATA, percent);
            sendEvent(Event.EVENT_PLAYER_BUFFERING_UPDATE, bundle);
        }
    };

    private void sendEvent(int eventcode){
        sendEvent(eventcode, null);
    }

    private void sendEvent(int eventcode, Bundle eventdata){
        if(eventDispatcher != null){
            eventDispatcher.sendEvent(new Event(getSenderName(), eventcode, eventdata));
        }
    }

    @Override
    public String getSenderName() {
        return SysDecoder.class.getSimpleName();
    }

    @Override
    public String getReceiverName() {
        return SysDecoder.class.getSimpleName();
    }

    @Override
    public int getPriority() {
        // TODO
        return 0;
    }

    @Override
    public boolean filter(Event event) {
        return false;
    }

    @Override
    public boolean handleEvent(Event event) {
        return false;
    }
}
