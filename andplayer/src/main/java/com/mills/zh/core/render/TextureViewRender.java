package com.mills.zh.core.render;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.util.AttributeSet;
import android.view.TextureView;
import android.view.TextureView.SurfaceTextureListener;
import android.view.View;

/**
 * Created by zhangmd on 2018/8/22. // TODO
 */

public class TextureViewRender extends TextureView implements IRender, SurfaceTextureListener {

    private IRenderCallback renderCallback;
    private RenderMeasure renderMeasure;

    public TextureViewRender(Context context) {
        this(context, null);
    }

    public TextureViewRender(Context context, AttributeSet attrs) {
        super(context, attrs);
        renderMeasure = new RenderMeasure();
        setSurfaceTextureListener(this);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        renderMeasure.doMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(renderMeasure.getMeasureWidth(), renderMeasure.getMeasureHeight());
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    @Override
    public void setRenderCallback(IRenderCallback renderCallback) {
        this.renderCallback = renderCallback;
    }

    @Override
    public void setVideoRotation(int degree) {

    }

    @Override
    public void setVideoSampleAspectRatio(int videoSarNum, int videoSarDen) {

    }

    @Override
    public void updateAspectRatio(int aspectRatio) {

    }

    @Override
    public void updateVideoSize(int videoWidth, int videoHeight) {

    }

    @Override
    public View getRenderView() {
        return null;
    }

    @Override
    public void release() {

    }
}
