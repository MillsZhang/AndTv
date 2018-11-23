package com.mills.zh.core;

import android.view.Surface;
import android.view.SurfaceHolder;

import com.mills.zh.core.decoder.BaseDecoder;
import com.mills.zh.data.DataSource;
import com.mills.zh.event.EventDispatcher;

/**
 * Created by zhangmd on 2018/8/22.
 */

public class AndPlayer implements IPlayer {

    private BaseDecoder decoder;
    private DataSource dataSource;
    private SurfaceHolder surfaceHolder;

    public AndPlayer(){

    }

    public void loadDecoder(){
        // TODO
    }

    public void setEventDispatcher(EventDispatcher eventDispatcher) {
        if(decoder != null){
            decoder.setEventDispatcher(eventDispatcher);
        }
    }

    @Override
    public void setDataSource(DataSource dataSource) {

    }

    @Override
    public void setDisplay(SurfaceHolder surfaceHolder) {
        surfaceHolder = surfaceHolder;

    }

    @Override
    public void setSurface(Surface surface) {

    }

    @Override
    public void setVolume(float left, float right) {

    }

    @Override
    public void setSpeed(float speed) {

    }

    @Override
    public void start() {

    }

    @Override
    public void start(int msc) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void seekTo(int msc) {

    }

    @Override
    public void stop() {

    }

    @Override
    public void reset() {

    }

    @Override
    public void destroy() {

    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean isPlaying() {
        return false;
    }

    @Override
    public int getCurrentPosition() {
        return 0;
    }

    @Override
    public int getDuration() {
        return 0;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }

    @Override
    public int getVideoWidth() {
        return 0;
    }

    @Override
    public int getVideoHeight() {
        return 0;
    }

    @Override
    public int getState() {
        return 0;
    }
}
