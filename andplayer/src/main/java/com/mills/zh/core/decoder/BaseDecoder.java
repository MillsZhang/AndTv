package com.mills.zh.core.decoder;

import com.mills.zh.core.IPlayer;
import com.mills.zh.event.IEventReceiver;
import com.mills.zh.event.IEventSender;

/**
 * Created by zhangmd on 2018/8/21.
 */

public abstract class BaseDecoder implements IPlayer, IEventSender, IEventReceiver {

    protected int currentState = STATE_IDLE;
    protected int targetState  = STATE_IDLE;



    protected final void updateStatus(int status){
        currentState= status;

        // TODO send event and modify global state
    }
}
