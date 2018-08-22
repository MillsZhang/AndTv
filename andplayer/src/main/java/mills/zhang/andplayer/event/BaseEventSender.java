package mills.zhang.andplayer.event;

/**
 * Created by zhangmd on 2018/8/22.
 */

public abstract class BaseEventSender implements IEventSender{




    public void sendEvent(Event event){
        EventDispatcher.getInstance().sendEvent(event);
    }

    public void sendEvent(Event event, long delay){
        EventDispatcher.getInstance().sendEvent(event, delay);
    }
}
