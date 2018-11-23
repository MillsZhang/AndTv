package com.mills.zh.event.interceptor;

import java.lang.ref.WeakReference;
import java.util.Iterator;

import com.mills.zh.event.Event;
import com.mills.zh.event.IEventReceiver;

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
