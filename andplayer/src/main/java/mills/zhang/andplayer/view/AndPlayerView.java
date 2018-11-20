package mills.zhang.andplayer.view;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.orhanobut.logger.Logger;

import mills.zhang.andplayer.core.AndPlayer;
import mills.zhang.andplayer.core.render.IRender;
import mills.zhang.andplayer.core.render.IRender.IRenderCallback;
import mills.zhang.andplayer.core.render.IRender.IRenderHolder;
import mills.zhang.andplayer.core.render.SurfaceViewRender;
import mills.zhang.andplayer.core.render.TextureViewRender;
import mills.zhang.andplayer.data.DataSource;
import mills.zhang.andplayer.event.Event;
import mills.zhang.andplayer.event.EventDispatcher;
import mills.zhang.andplayer.event.IEventReceiver;
import mills.zhang.andplayer.panel.IPanel;

/**
 * Created by zhangmd on 2018/8/21.
 *  播放器容器
 *  包含：事件分发器+解码器+渲染层+各种控制面板
 */

public class AndPlayerView extends FrameLayout implements IPlayerView, IEventReceiver{

    private static final String TAG = "AndPlayerView";

    private EventDispatcher eventDispatcher;

    private FrameLayout renderContainer;
    private FrameLayout panelContainer;

    private AndPlayer player;
    private IRender render;
    private IRenderHolder renderHolder;
    private int renderType = IRender.RENDER_TYPE_TEXTURE_VIEW;

    public AndPlayerView(Context context){
        super(context);

        init(context);
    }

    public void init(Context context){
        eventDispatcher = EventDispatcher.newInstance();

        player = new AndPlayer();
        player.setEventDispatcher(eventDispatcher);

        renderContainer = new FrameLayout(context);
        addView(renderContainer, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        panelContainer = new FrameLayout(context);
        addView(panelContainer, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
    }

    private void setRender(IRender render){
        if(render != null && render.getRenderView() != null){
            this.render = render;

            renderHolder = null;
            player.setSurface(null);
            render.updateAspectRatio(IRender.AspectRatio_FIT_PARENT);
            render.setRenderCallback(renderCallback);

            renderContainer.removeAllViews();
            renderContainer.addView(render.getRenderView(), new LayoutParams(
                    LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT,
                    Gravity.CENTER));
        }
    }


    public void attachPanel(IPanel panel){
        if(panel == null){
            return;
        }
        if(panel instanceof IEventReceiver){
            eventDispatcher.addEventReceiver((IEventReceiver)panel);
        }

        if(panel.getView() != null){
            panelContainer.addView(panel.getView(), new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            ));
        }
    }

    public void detachPanel(IPanel panel){
        if(panel == null){
            return;
        }
        if(panel instanceof IEventReceiver){
            eventDispatcher.removeEventReceiver((IEventReceiver)panel);
        }

        if(panel.getView() != null){
            panelContainer.removeView(panel.getView());
        }
    }

    public void destory(){
        renderContainer.removeAllViews();
        panelContainer.removeAllViews();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        int eventCode = -1;
        switch (keyCode){
            case KeyEvent.KEYCODE_DPAD_UP:
                eventCode = Event.EVENT_KEYPAD_UP;
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                eventCode = Event.EVENT_KEYPAD_DOWN;
                break;
            case KeyEvent.KEYCODE_DPAD_LEFT:
                eventCode = Event.EVENT_KEYPAD_LEFT;
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                eventCode = Event.EVENT_KEYPAD_RIGHT;
                break;
            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_ENTER:
                eventCode = Event.EVENT_KEYPAD_OK;
                break;
            case KeyEvent.KEYCODE_MENU:
                eventCode = Event.EVENT_KEYPAD_MENU;
                break;
        }
        if(eventCode < 0){
            return super.onKeyDown(keyCode, event);
        }

        eventDispatcher.sendEvent(new Event(TAG, eventCode));
        return true;
    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        return super.onKeyLongPress(keyCode, event);
    }

    @Override
    public void setDataSource(DataSource dataSource) {

    }

    @Override
    public void setRenderType(int renderType) {
        if(render == null || renderType != this.renderType){
            this.renderType = renderType;

            if(render != null){
                render.release();
                render = null;
            }

            switch (renderType){
                case IRender.RENDER_TYPE_SURFACE_VIEW:
                    render = new SurfaceViewRender(getContext());
                    break;
                case IRender.RENDER_TYPE_TEXTURE_VIEW:
                    render = new TextureViewRender(getContext());
                    break;
            }

            if(render != null){
                setRender(render);
            }
        }
    }

    IRender.IRenderCallback renderCallback = new IRenderCallback() {
        @Override
        public void onSurfaceCreated(IRenderHolder renderHolder, int width, int height) {
            Logger.d("onSurfaceCreated : width = " + width + ", height = " + height);
            AndPlayerView.this.renderHolder = renderHolder;
            renderHolder.bindPlayer(player);
        }

        @Override
        public void onSurfaceChanged(IRenderHolder renderHolder, int format, int width, int height) {
            Logger.d("onSurfaceChanged");
        }

        @Override
        public void onSurfaceDestroy(IRenderHolder renderHolder) {
            Logger.d("onSurfaceCreated");
            AndPlayerView.this.renderHolder = null;
        }
    };

    @Override
    public void setAspectRatio(int aspectRatio) {

    }

    @Override
    public boolean switchDecoder(int decoderType) {
        return false;
    }

    @Override
    public void setVolume(float left, float right) {

    }

    @Override
    public void setSpeed(float speed) {

    }

    @Override
    public IRender getRender() {
        return null;
    }

    @Override
    public boolean isInPlaybackState() {
        return false;
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
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public int getState() {
        return 0;
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
    public void stopPlayback() {

    }

    @Override
    public String getReceiverName() {
        return TAG;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public boolean filter(Event event) {
        // 如下事件更新Render
        if(event.getEventCode() == Event.EVENT_PLAYER_VIDEO_SIZE_CHANGE
                || event.getEventCode() == Event.EVENT_PLAYER_VIDEO_ROTATION_CHANGED
                || event.getEventCode() == Event.EVENT_PLAYER_PREPARED){
            return true;
        }
        return false;
    }

    @Override
    public boolean handleEvent(Event event) {

        switch (event.getEventCode()){
            case Event.EVENT_PLAYER_VIDEO_SIZE_CHANGE:

                break;
            case Event.EVENT_PLAYER_VIDEO_ROTATION_CHANGED:

                break;
            case Event.EVENT_PLAYER_PREPARED:

                Bundle bundle = event.getEventData();
                // TODO
                break;
        }

        return false;
    }
}
