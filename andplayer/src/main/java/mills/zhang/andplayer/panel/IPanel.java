package mills.zhang.andplayer.panel;

import android.content.Context;
import android.view.View;

/**
 * Created by zhangmd on 2018/8/21.
 */

public interface IPanel {

    void init(Context context);

    View getView();

    void show();

    void hide();

    boolean isShown();
}
