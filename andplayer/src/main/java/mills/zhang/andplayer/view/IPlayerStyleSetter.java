package mills.zhang.andplayer.view;

import android.graphics.Rect;

/**
 * Created by zhangmd on 2018/8/24.
 */

public interface IPlayerStyleSetter {

    void setRoundRectShape(float radius);

    void setRoundRectShape(Rect rect, float radius);

    void setOvalRectShape();

    void setOvalRectShape(Rect rect);

    void clearShapeStyle();

    void setElevationShadow(float elevation);

    /**
     * must setting a color when set shadow, not transparent.
     * @param backgroundColor
     * @param elevation
     */
    void setElevationShadow(int backgroundColor, float elevation);
}
