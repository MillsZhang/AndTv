package com.mills.zh.common.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.mills.zh.common.R;


/**
 * Created by zhangmd on 2018/4/2.
 */

public class FocusImageView extends ImageView {

    private FocusDrawable mFocus;

    public FocusImageView(Context context){
        this(context, null);
    }

    public FocusImageView(Context context, AttributeSet attrs){
        super(context, attrs);

        setWillNotDraw(false);
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TvFocus);
            Drawable drawable = a.getDrawable(R.styleable.TvFocus_focusSrc);
            if (drawable != null) {
                mFocus = new FocusDrawable(drawable);
            }
            a.recycle();
        }
    }

    public void setFocusDrawable(Drawable drawable){
        if(drawable != null){
            mFocus = new FocusDrawable(drawable);
            invalidate();
        }
    }

    public void setFocusDrawable(Resources resources, int resId){
        if(resId != 0){
            mFocus = new FocusDrawable(resources, resId);
            invalidate();
        }
    }

    @Override
    public void draw(Canvas canvas) {
        if (mFocus != null) {
            mFocus.draw(canvas, this);
        }
        super.draw(canvas);
    }

}
