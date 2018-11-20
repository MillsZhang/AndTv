package mills.zhang.andplayer.view;

import mills.zhang.andplayer.core.render.IRender;
import mills.zhang.andplayer.data.DataSource;

/**
 * Created by zhangmd on 2018/8/24.
 *  播放器容器对外接口类
 */

public interface IPlayerView {

    void setDataSource(DataSource dataSource);

    void setRenderType(int renderType);
    void setAspectRatio(int aspectRatio);
    boolean switchDecoder(int decoderType);

    void setVolume(float left, float right);
    void setSpeed(float speed);

    IRender getRender();

    boolean isInPlaybackState();
    boolean isPlaying();
    int getCurrentPosition();
    int getDuration();
    int getAudioSessionId();
    int getBufferPercentage();
    int getState();

    void start();
    void start(int msc);
    void pause();
    void resume();
    void seekTo(int msc);
    void stop();
    void stopPlayback();
}
