package com.mills.zh.event;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.text.TextUtils;

import com.mills.zh.event.interceptor.IEventInterceptor;
import com.orhanobut.logger.Logger;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by zhangmd on 2018/8/21.
 */

public class EventDispatcher {

    private static final int MSG_WHAT_NORMAL_EVENT = 1;

    private ArrayList<WeakReference<IEventReceiver>> eventReceivers;
    private Comparator<WeakReference<IEventReceiver>> eventComparator;

    private Set<IEventInterceptor> eventInterceptors;

    private HandlerThread dispatcherThread;
    private Handler dispatcherHandler;


    private EventDispatcher(){
        eventReceivers = new ArrayList<WeakReference<IEventReceiver>>();
        eventComparator = new Comparator<WeakReference<IEventReceiver>>() {
            @Override
            public int compare(WeakReference<IEventReceiver> o1, WeakReference<IEventReceiver> o2) {
                IEventReceiver e1 = o1.get();
                IEventReceiver e2 = o2.get();
                if(e1 == null || e2 == null){
                    return 0;
                }

                if(e1.getPriority() > e2.getPriority()){
                    return -1;
                } else if(e1.getPriority() < e2.getPriority()){
                    return 1;
                } else {
                    return 0;
                }
            }
        };

        dispatcherThread = new HandlerThread("EventDispatcher");
        dispatcherThread.start();
        dispatcherHandler = new Handler(dispatcherThread.getLooper()){
            @Override
            public void handleMessage(Message msg) {

                switch (msg.what){
                    case MSG_WHAT_NORMAL_EVENT:
                        Event event = (Event) msg.obj;
                        dispatchEvent(event);
                        break;
                }
            }
        };

        eventInterceptors = new LinkedHashSet<IEventInterceptor>();
    }
    public static EventDispatcher newInstance(){
        return new EventDispatcher();
    }

    public synchronized boolean addEventReceiver(IEventReceiver receiver){
        Iterator<WeakReference<IEventReceiver>> iterator = eventReceivers.iterator();
        while (iterator.hasNext()){
            WeakReference<IEventReceiver> ref = iterator.next();
            if(ref == null || ref.get() == null){
                iterator.remove();
            } else if(receiver == ref.get()){
                return false;
            }
        }

        eventReceivers.add(new WeakReference<IEventReceiver>(receiver));
        sort();
        return true;
    }

    public synchronized boolean removeEventReceiver(IEventReceiver receiver){
        return eventReceivers.remove(receiver);
    }

    public synchronized void clearEventReceiver(){
        eventReceivers.clear();
    }

    private void sort(){
        eventReceivers.sort(eventComparator);
    }

    // TODO 完善拦截器机制，防止事件重复处理
    public void addEventInterceptor(IEventInterceptor interceptor){
        eventInterceptors.add(interceptor);
    }

    public void removeEventInterceptor(IEventInterceptor interceptor){
        eventInterceptors.remove(interceptor);
    }

    public void clearEventInterceptor(){
        eventInterceptors.clear();
    }

    private boolean interceptEvent(Event event){
        if(eventInterceptors.isEmpty()){
            return false;
        }

        Iterator<IEventInterceptor> iterator = eventInterceptors.iterator();
        while (iterator.hasNext()){
            IEventInterceptor interceptor = iterator.next();
            if(interceptor.intercept(event, eventReceivers.iterator())){
                return true;
            }
        }
        return false;
    }

    private synchronized void dispatchEvent(Event event){
        if(eventReceivers.size() <= 0){
            return;
        }

        if(interceptEvent(event)){
            Logger.i("dispatchEvent event be intercepted");
            return;
        }

        String source = event.getEventSource();
        String target = event.getEventTarget();

        Iterator<WeakReference<IEventReceiver>> iterator = eventReceivers.iterator();
        while (iterator.hasNext()){
            WeakReference<IEventReceiver> ref = iterator.next();
            if(ref == null || ref.get() == null){
                iterator.remove();
            } else {
                IEventReceiver receiver = ref.get();
                // 发送者和接收者是同一对象不处理
                if(source != null && TextUtils.equals(source, receiver.getReceiverName())){
                    continue;
                }
                // 找到指定接收者后不继续传递事件
                if(target != null && TextUtils.equals(target, receiver.getReceiverName())){
                    if(receiver.filter(event)){
                        receiver.handleEvent(event);
                    }
                    break;
                }
                // 过滤事件
                if(receiver.filter(event)){
                    // 如果事件被拦截，则不继续传递
                    if(receiver.handleEvent(event)){
                        break;
                    }
                }
            }
        }
    }

    public void sendEvent(Event event){
        sendEvent(event, 0);
    }

    public void sendEvent(Event event, long delay){

        if(event == null || event.isInValidEvent()){
            return;
        }

        Message message = Message.obtain();
        message.what = MSG_WHAT_NORMAL_EVENT;
        message.obj = event;

        if(delay > 0){
            dispatcherHandler.sendMessageDelayed(message, delay);
        } else {
            dispatcherHandler.sendMessage(message);
        }
    }
}
