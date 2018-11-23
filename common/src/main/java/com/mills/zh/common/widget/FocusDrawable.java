package com.mills.zh.common.widget;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.DrawableContainer;
import android.util.StateSet;
import android.view.View;

/**
 * Class FocusDrawable
 * @author yuanfeng
 */
public class FocusDrawable {
    private static final int[] ENABLED_FOCUSED_STATE_SET = {
        android.R.attr.state_enabled,
        android.R.attr.state_focused
    };

    private final Rect mPadding;
    private final Drawable mDrawable;

    /**
     * Constructor
     * @param drawable The focus <tt>Drawable</tt>.
     * @see #FocusDrawable(Resources, int)
     */
    public FocusDrawable(Drawable drawable) {
        mDrawable = drawable;
        mPadding  = new Rect();
        mDrawable.getPadding(mPadding);
    }

    /**
     * Constructor
     * @param res The <tt>Resources</tt>.
     * @param resId The resource id of the drawable to load.
     * @see #FocusDrawable(Drawable)
     */
    public FocusDrawable(Resources res, int resId) {
        this(res.getDrawable(resId));
    }

    public Rect getPadding(){
    	return mPadding;
    }
    
    /**
     * Draw this drawable.
     * @param canvas The <tt>Canvas</tt> to draw into.
     * @param view The <tt>View</tt> will be draw focus.
     */
    public void draw(Canvas canvas, View view) {
        if (mDrawable.isStateful()) {
            mDrawable.setState(view.getDrawableState());
            drawDrawable(canvas, view);
        } else if (StateSet.stateSetMatches(ENABLED_FOCUSED_STATE_SET, view.getDrawableState())) {
            drawDrawable(canvas, view);
        }
    }

    public void drawDrawable(Canvas canvas, View view) {
        Drawable drawable = mDrawable;
        if (mDrawable instanceof DrawableContainer) {
            drawable = ((DrawableContainer)mDrawable).getCurrent();
            drawable.getPadding(mPadding);
        }

        drawable.setBounds(-mPadding.left, -mPadding.top, mPadding.right + view.getWidth(), mPadding.bottom + view.getHeight());
        drawable.draw(canvas);
    }
}