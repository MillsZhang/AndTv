package mills.zhang.andplayer.event.interceptor;

import android.text.TextUtils;

import java.lang.ref.WeakReference;
import java.util.Iterator;

import mills.zhang.andplayer.event.Event;
import mills.zhang.andplayer.event.IEventReceiver;
import mills.zhang.andplayer.panel.IPanel;

/**
 * Created by zhangmd on 2018/8/22.
 * 按键事件优先考虑可见的Panel
 */

public class KeyPadEventInterceptor implements IEventInterceptor {

    @Override
    public boolean intercept(Event event, Iterator<WeakReference<IEventReceiver>> iterator) {

        if(!event.isKeyPadEvent()){
            return false;
        }

        String source = event.getEventSource();
        String target = event.getEventTarget();

        if(target != null){
            return false;
        }

        boolean intercept = false;

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

                if(receiver instanceof IPanel
                        && ((IPanel) receiver).isShown()
                        && receiver.filter(event)){

                    intercept = true;

                    // 如果事件被拦截，则不继续传递
                    if(receiver.handleEvent(event)){
                        break;
                    }
                }
            }
        }

        return intercept;
    }
}
