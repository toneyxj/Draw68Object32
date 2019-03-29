package com.mx.mxbase.view;

import android.content.Context;
import android.graphics.Rect;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * 左右滑动事件处理
 * Created by 夏君 on 2017/3/15 0015.
 */

public class SildeFrameLayout extends FrameLayout{
    private int downX;
    private int downY;
    private int tempX;
    private int mTouchSlop;
    private List<ViewPager> mViewPagers;
    private SildeEventListener listener;
    private boolean noViewpager=false;

    /**
     * 左右滑动监听
     * @param listener
     */
    public void setListener(SildeEventListener listener) {
        this.listener = listener;
    }

    public SildeFrameLayout(Context context) {
        super(context);
        init(context);
    }

    public SildeFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SildeFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
       init(context);
    }
    private void init(Context context){
        mTouchSlop =  ViewConfiguration.get(context).getScaledTouchSlop();
    }
    public void setnoViewPager(boolean is){
        noViewpager=is;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mViewPagers=getAlLViewPager(this);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        ViewPager viewPager=getTouchViewPager(ev);
        if (listener==null||(viewPager!=null&&viewPager.getCurrentItem()!=0&&!noViewpager)) {
            return super.onInterceptTouchEvent(ev);
        }
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX  = (int) ev.getRawX();
                downY = (int) ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                int moveX = (int) ev.getRawX();
                int moveY = (int) ev.getRawY();
                // 满足此条件屏蔽SildingFinishLayout里面子类的touch事件
                if (Math.abs(moveX - downX) > mTouchSlop&&(Math.abs((int) ev.getRawY() - downY) < mTouchSlop)) {
                    return true;
                }
                if (Math.abs(moveY - downY) > mTouchSlop&&(Math.abs((int) ev.getRawX() - downX) < mTouchSlop)) {
                    return true;
                }
                break;
            default:
                break;
        }

        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                int moveX = (int) event.getRawX();
                int moveY = (int) event.getRawY();
                if (Math.abs(moveX - downX)>Math.abs(moveY - downY)){
                    if (moveX - downX > mTouchSlop){
                        listener.onSildeEventLeft();
                    }else if (downX - moveX > mTouchSlop){
                        listener.onSildeEventRight();
                    }
                }else {
                    if (moveY-downY> mTouchSlop){
                        listener.onSildeEventLeft();
                    }else if (downY-moveY> mTouchSlop){
                        listener.onSildeEventRight();
                    }
                }
                break;
            default:
                break;
        }
        return true;
    }
    /**
     * 返回我们touch的ViewPager
     * @param ev
     * @return
     */
    private ViewPager getTouchViewPager(MotionEvent ev){
        if(mViewPagers == null || mViewPagers.size() == 0){
            return null;
        }
        Rect mRect = new Rect();
        for(ViewPager v : mViewPagers){
            v.getHitRect(mRect);

            if(mRect.contains((int)ev.getX(), (int)ev.getY())){
                return v;
            }
        }
        return null;
    }
    /**
     * 获取SwipeBackLayout里面的ViewPager的集合
     * @param parent
     */
    private List<ViewPager> getAlLViewPager(ViewGroup parent){
        List<ViewPager> mViewPagers=new ArrayList<ViewPager>();
        int childCount = parent.getChildCount();
        for(int i=0; i<childCount; i++){
            View child = parent.getChildAt(i);
            if(child instanceof ViewPager){
                mViewPagers.add((ViewPager)child);
            }else if(child instanceof ViewGroup){
                mViewPagers.addAll(getAlLViewPager((ViewGroup)child));
            }
        }
        return mViewPagers;
    }
    /**
     * 滑动事件监听
     */
    public interface SildeEventListener{
        void onSildeEventLeft();
        void onSildeEventRight();
    }
}
