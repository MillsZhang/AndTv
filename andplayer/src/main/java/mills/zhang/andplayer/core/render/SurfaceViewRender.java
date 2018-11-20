package mills.zhang.andplayer.core.render;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;

import com.orhanobut.logger.Logger;

import java.lang.ref.WeakReference;

import mills.zhang.andplayer.core.IPlayer;

/**
 * Created by zhangmd on 2018/8/22.
 */

public class SurfaceViewRender extends SurfaceView implements IRender, Callback {

    private IRenderCallback renderCallback;
    private RenderMeasure renderMeasure;

    public SurfaceViewRender(Context context) {
        this(context, null);
    }

    public SurfaceViewRender(Context context, AttributeSet attrs){
        super(context, attrs);
        this.renderMeasure = new RenderMeasure();
        getHolder().addCallback(this);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        renderMeasure.doMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(renderMeasure.getMeasureWidth(), renderMeasure.getMeasureHeight());
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if(renderCallback != null){
            renderCallback.onSurfaceCreated(new RenderHolder(holder), 0, 0);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if(renderCallback != null){
            renderCallback.onSurfaceChanged(new RenderHolder(holder), format, width, height);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if(renderCallback != null){
            renderCallback.onSurfaceDestroy(new RenderHolder(holder));
        }
    }

    @Override
    public void setRenderCallback(IRenderCallback renderCallback) {
        this.renderCallback = renderCallback;
    }

    @Override
    public void setVideoRotation(int degree) {
        Logger.e("surface view not support rotation");
        // TODO
    }

    @Override
    public void setVideoSampleAspectRatio(int videoSarNum, int videoSarDen) {
        if(videoSarNum > 0 && videoSarDen > 0){
            renderMeasure.setVideoSampleAspectRatio(videoSarNum, videoSarDen);
            requestLayout();
        }
    }

    @Override
    public void updateAspectRatio(int aspectRatio) {
        renderMeasure.setAspectRatio(aspectRatio);
        requestLayout();
    }

    @Override
    public void updateVideoSize(int videoWidth, int videoHeight) {
        renderMeasure.setVideoSize(videoWidth, videoHeight);
        if(videoWidth != 0 && videoHeight != 0){
            getHolder().setFixedSize(videoWidth, videoHeight);
        }
        requestLayout();
    }

    @Override
    public View getRenderView() {
        return this;
    }

    @Override
    public void release() {

    }


    private static final class RenderHolder implements IRenderHolder {

        private WeakReference<SurfaceHolder> surfaceHolder;

        public RenderHolder(SurfaceHolder surfaceHolder) {
            this.surfaceHolder = new WeakReference<>(surfaceHolder);
        }

        @Override
        public void bindPlayer(IPlayer player) {
            if (player != null && surfaceHolder.get() != null) {
                player.setDisplay(surfaceHolder.get());
            }
        }
    }
}
