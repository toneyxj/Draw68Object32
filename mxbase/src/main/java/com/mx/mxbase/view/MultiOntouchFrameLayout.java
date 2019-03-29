package com.mx.mxbase.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

/**
 * 多点触摸屏蔽
 * Created by 夏君 on 2017/3/20 0020.
 */

public class MultiOntouchFrameLayout extends FrameLayout{
    public MultiOntouchFrameLayout(Context context) {
        super(context);
    }

    public MultiOntouchFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MultiOntouchFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (ev.getPointerCount()>1)return true;
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }
}
