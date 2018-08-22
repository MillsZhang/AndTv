package mills.zhang.andplayer.event;

import android.os.Bundle;

/**
 * Created by zhangmd on 2018/8/21.
 */

public class Event {
    public static final int EVENT_MARK                              = 0xFF000000;
    // 播放器相关事件
    public static final int EVENT_PLAYER_MARK                       = 0x10000000;
    public static final int EVENT_PLAYER_DATASOURCE_SET             = 0x10000001;
    public static final int EVENT_PLAYER_SURFACE_HOLDER_UPDATE      = 0x10000002;
    public static final int EVENT_PLAYER_SURFACE_UPDATE             = 0x10000003;
    public static final int EVENT_PLAYER_START                      = 0x10000004;
    public static final int EVENT_PLAYER_PAUSE                      = 0x10000005;
    public static final int EVENT_PLAYER_RESUME                     = 0x10000006;
    public static final int EVENT_PLAYER_STOP                       = 0x10000007;
    public static final int EVENT_PLAYER_RESET                      = 0x10000008;
    public static final int EVENT_PLAYER_DESTROY                    = 0x10000009;
    public static final int EVENT_PLAYER_BUFFERING_START            = 0x1000000A;
    public static final int EVENT_PLAYER_BUFFERING_END              = 0x1000000B;
    public static final int EVENT_PLAYER_BUFFERING_UPDATE           = 0x1000000C;
    public static final int EVENT_PLAYER_SEEK_TO                    = 0x1000000D;
    public static final int EVENT_PLAYER_SEEK_COMPLETE              = 0x1000000E;
    public static final int EVENT_PLAYER_VIDEO_RENDER_START         = 0x1000000F;
    public static final int EVENT_PLAYER_PLAY_COMPLETE              = 0x10000010;
    public static final int EVENT_PLAYER_VIDEO_SIZE_CHANGE          = 0x10000011;
    public static final int EVENT_PLAYER_PREPARED                   = 0x10000012;
    public static final int EVENT_PLAYER_BAD_INTERLEAVING           = 0x10000013;
    public static final int EVENT_PLAYER_NOT_SEEK_ABLE              = 0x10000014;
    public static final int EVENT_PLAYER_METADATA_UPDATE            = 0x10000015;
    public static final int EVENT_PLAYER_TIMED_TEXT_ERROR           = 0x10000016;
    public static final int EVENT_PLAYER_UNSUPPORTED_SUBTITLE       = 0x10000017;
    public static final int EVENT_PLAYER_SUBTITLE_TIMED_OUT         = 0x10000018;
    public static final int EVENT_PLAYER_STATUS_CHANGE              = 0x10000019;

    public static final int EVENT_PLAYER_TIMER_UPDATE               = 0x1000001A;
    public static final int EVENT_PLAYER_VIDEO_ROTATION_CHANGED     = 0x1000001B;




    public static final int EVENT_ERROR_MARK                        = 0x20000000;



    // 数据相关事件
    public static final int EVENT_DATA_MARK                         = 0x30000000;
    public static final int EVENT_DATA_LOAD_START                   = 0x30000001;
    public static final int EVENT_DATA_LOAD_END                     = 0x30000002;
    public static final int EVENT_DATA_LOAD_ERROR                   = 0x30000003;

    // 按键事件
    public static final int EVENT_KEYPAD_MARK                       = 0x40000000;


    public static final int EVENT_CUSTOM_MARK                       = 0xA0000000;






    private String eventSource;
    private String eventTarget;
    private int eventCode;
    private Bundle eventData;

    public Event(String eventsource, int eventcode){
        this(eventsource, null, eventcode, null);
    }

    public Event(String eventsource, String eventtarget, int eventcode){
        this(eventsource, eventtarget, eventcode, null);
    }

    public Event(String eventsource, String eventtarget, int eventcode, Bundle eventdata){
        this.eventSource = eventsource;
        this.eventTarget = eventtarget;
        this.eventCode = eventcode;
        this.eventData = eventdata;
    }

    public String getEventSource() {
        return eventSource;
    }

    public String getEventTarget() {
        return eventTarget;
    }

    public int getEventCode() {
        return eventCode;
    }

    public Bundle getEventData() {
        return eventData;
    }

    public boolean isInValidEvent(){
        return (eventCode & EVENT_MARK) == 0;
    }

    public boolean isPlayerEvent(){
        return (eventCode & EVENT_MARK) == EVENT_PLAYER_MARK;
    }

    public boolean isKeyPadEvent(){
        return (eventCode & EVENT_MARK) == EVENT_KEYPAD_MARK;
    }
}
