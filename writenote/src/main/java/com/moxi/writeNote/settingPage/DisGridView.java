package com.moxi.writeNote.settingPage;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.GridView;

/**
 * Created by King on 2017/12/15.
 */

public class DisGridView extends GridView {
    public DisGridView(Context context) {
        super(context);
    }

    public DisGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DisGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 设置上下不滚动
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_MOVE) {
            return true;//true:禁止滚动
        }

        return super.dispatchTouchEvent(ev);
    }
}
