package com.mills.zh.core.render;

import android.view.View;

import com.mills.zh.core.IPlayer;

/**
 * Created by zhangmd on 2018/8/22.
 */

public interface IRender {
    // 视频宽高比
    int AspectRatio_16_9 = 1;
    int AspectRatio_4_3 = 2;
    int AspectRatio_MATCH_PARENT = 3;
    int AspectRatio_FILL_PARENT = 4;
    int AspectRatio_FIT_PARENT = 5;
    int AspectRatio_ORIGIN = 6;

    // 渲染器类型
    int RENDER_TYPE_TEXTURE_VIEW = 0;
    int RENDER_TYPE_SURFACE_VIEW = 1;

    void setRenderCallback(IRenderCallback renderCallback);

    void setVideoRotation(int degree);

    void setVideoSampleAspectRatio(int videoSarNum, int videoSarDen);

    void updateAspectRatio(int aspectRatio);

    void updateVideoSize(int videoWidth, int videoHeight);

    View getRenderView();

    void release();

    interface IRenderHolder{
        void bindPlayer(IPlayer player);
    }

    interface IRenderCallback{
        void onSurfaceCreated(IRenderHolder renderHolder, int width, int height);
        void onSurfaceChanged(IRenderHolder renderHolder, int format, int width, int height);
        void onSurfaceDestroy(IRenderHolder renderHolder);
    }
}
