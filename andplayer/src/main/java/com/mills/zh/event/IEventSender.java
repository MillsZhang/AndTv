package com.mills.zh.event;

/**
 * Created by zhangmd on 2018/8/21.
 */

public interface IEventSender {


    String getSenderName();

    void setEventDispatcher(EventDispatcher dispatcher);
}
