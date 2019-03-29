package com.mx.mxbase.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.LinearLayout;

/**
 * Created by Administrator on 2016/8/4.
 */
public class LinerlayoutInter extends LinearLayout {
    private LinerLayoutInter layoutInter;

    public void setLayoutInter(LinerLayoutInter layoutInter) {
        this.layoutInter = layoutInter;
    }

    public LinerlayoutInter(Context context) {
        super(context);
        init(context);
    }

    public LinerlayoutInter(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public LinerlayoutInter(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    private int downX;
    private int downY;
    private int tempX;
    private int mTouchSlop;
    private boolean moveLeft;
    private  boolean join=false;
    /**
     * 事件拦截操作
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = tempX = (int) ev.getRawX();
                downY = (int) ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                int moveX = (int) ev.getRawX();
                if (Math.abs(downX - moveX) > mTouchSlop
                        && Math.abs((int) ev.getRawY() - downY) < mTouchSlop) {
                    join=true;
                    moveLeft = (downX - moveX) < 0;
                    return true;
                }
                break;
        }

        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction()==MotionEvent.ACTION_UP){
            if (layoutInter != null&&join) {
                if (moveLeft) {
                    layoutInter.moveLeft();
                } else {
                    layoutInter.moveRight();
                }
                join=false;
            }
        }else if(event.getAction()==MotionEvent.ACTION_MOVE&&!join){
            int moveX = (int) event.getRawX();
            if (Math.abs(downX - moveX) > mTouchSlop
                    && Math.abs((int) event.getRawY() - downY) < mTouchSlop) {
                join=true;
                moveLeft = (downX - moveX) < 0;
                return true;
            }
        }
        return true;
    }

    public interface LinerLayoutInter {
        public void moveRight();

        public void moveLeft();
    }
}
