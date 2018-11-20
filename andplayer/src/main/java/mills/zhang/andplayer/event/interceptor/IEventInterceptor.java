package mills.zhang.andplayer.event.interceptor;

import java.lang.ref.WeakReference;
import java.util.Iterator;

import mills.zhang.andplayer.event.Event;
import mills.zhang.andplayer.event.IEventReceiver;

/**
 * Created by zhangmd on 2018/8/22.
 */

public interface IEventInterceptor {

    /**
     * 拦截事件分发的逻辑
     *  注意：当没有拦截时，不要处理事件，否则会导致Receiver重复处理事件
     * @param event
     * @param iterator
     * @return
     */
    boolean intercept(Event event, Iterator<WeakReference<IEventReceiver>> iterator);
}
