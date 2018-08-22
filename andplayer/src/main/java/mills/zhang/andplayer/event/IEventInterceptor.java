package mills.zhang.andplayer.event;

import java.lang.ref.WeakReference;
import java.util.Iterator;

/**
 * Created by zhangmd on 2018/8/22.
 */

public interface IEventInterceptor {

    boolean intercept(Event event, Iterator<WeakReference<IEventReceiver>> iterator);
}
