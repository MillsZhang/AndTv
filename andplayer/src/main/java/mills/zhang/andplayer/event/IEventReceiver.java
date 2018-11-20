package mills.zhang.andplayer.event;

/**
 * Created by zhangmd on 2018/8/21.
 */

public interface IEventReceiver {

    String getReceiverName();

    int getPriority();

    boolean filter(Event event);

    // 返回值决定是否拦截事件分发
    boolean handleEvent(Event event);
}
