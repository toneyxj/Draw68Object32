package com.moxi.haierexams.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.LinearLayout;

import com.mx.mxbase.constant.APPLog;
import com.mx.mxbase.utils.Log;

/**
 * 自带滑动的现行布局
 * Created by Administrator on 2016/9/29.
 */
public class SlideLinerlayout extends LinearLayout {
    private SlideListener slideListener;
    /**
     * 控件高度
     */
    private int KitHeight = 0;
    /**
     * 界面布局总高度
     */
    private int totalKitHeight = 0;

    /**
     * 每次滑动的高度
     */
    private int slideHeight = 100;
    /**
     * 初始化当前滑动点
     */
    private int currentPointY = 0;

    /**
     * 设置每次滑动高度
     *
     * @param slideHeight
     */
    public void setSlideHeight(int slideHeight) {
        this.slideHeight = slideHeight;
    }

    /**
     * 设置滑动监听
     *
     * @param slideListener
     */
    public void setSlideListener(SlideListener slideListener) {
        this.slideListener = slideListener;
    }

    //判断是否拦截事件
    private int downX;
    private int downY;
    private int tempX;
    private int mTouchSlop;
    /**
     * 滑动拦截方位0左滑动，1上滑动，2右滑动，3下滑动
     */
    private int direction = -1;
    private boolean join = false;

    public SlideLinerlayout(Context context) {
        super(context);
        init(context);
    }

    public SlideLinerlayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SlideLinerlayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        totalKitHeight = 0;
        if (getHeight() != 0) {
            for (int i = 0; i < getChildCount(); i++) {
                totalKitHeight += getChildAt(i).getMeasuredHeight();
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        KitHeight = getMeasuredHeight();
        if (KitHeight > 0) {
            KitHeight -= getPaddingTop() + getPaddingBottom();
        }
//        if (slideHeight == 0)
        Log.e("KitHeight", "KitHeight:" + KitHeight);
        slideHeight = 100;
    }

    /**
     * 事件拦截操作
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int iDeviceId = ev.getDeviceId();
        if (iDeviceId == MotionEvent.TOOL_TYPE_FINGER || iDeviceId >= MotionEvent.TOOL_TYPE_ERASER) {
            return false;
        }
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = tempX = (int) ev.getRawX();
                downY = (int) ev.getRawY();
                APPLog.e("downY=" + downY);
                break;
            case MotionEvent.ACTION_MOVE:
                int moveY = (int) ev.getRawY();
                if (Math.abs(downY - moveY) > mTouchSlop
                        && Math.abs((int) ev.getRawX() - downX) < mTouchSlop) {
                    APPLog.e("moveY=" + moveY);
                    direction = ((downY - moveY) < 0) ? 1 : 3;
                    join = true;
                    return true;
                }
                int moveX = (int) ev.getRawX();
                if (Math.abs(downX - moveX) > mTouchSlop
                        && Math.abs((int) ev.getRawY() - downY) < mTouchSlop) {
                    join = true;
                    direction = ((downX - moveX) < 0) ? 0 : 2;
                    return true;
                }
                break;
        }

        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        APPLog.e("event", event.getAction());
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (join && direction != -1) {
                setListener(direction);
                join = false;
                direction = -1;
            }
        } else if (event.getAction() == MotionEvent.ACTION_MOVE && !join) {
            //左右
            int moveX = (int) event.getRawX();
            if (Math.abs(downX - moveX) > mTouchSlop
                    && Math.abs((int) event.getRawY() - downY) < mTouchSlop) {
                join = true;
                direction = ((downX - moveX) < 0) ? 0 : 2;
                return true;
            }
            //上下
            int moveY = (int) event.getRawY();
            if (Math.abs(downY - moveY) > mTouchSlop
                    && Math.abs((int) event.getRawX() - downX) < mTouchSlop) {
                direction = ((downY - moveY) < 0) ? 1 : 3;
                join = true;
                return true;
            }
        }
        return true;
    }

    private void setListener(int direction) {
        switch (direction) {
            case 0:
                if (slideListener != null)
                    slideListener.moveDirection(true, false, false, false);
                break;
            case 1:
                moveUp();
                break;
            case 2:
                if (slideListener != null)
                    slideListener.moveDirection(false, false, true, false);
                break;
            case 3:
                moveDown();
                break;
            default:
                break;
        }
    }

    /**
     * 返回顶部
     */
    public void moveToTop() {
        currentPointY = 0;
        scrollTo(0, currentPointY);
    }

    public void moveUp() {
        if (currentPointY <= 0) {
            if (slideListener != null)
                slideListener.toTop();
            return;
        }
        currentPointY -= slideHeight;
        if ((currentPointY) < 0) currentPointY = 0;
        scrollTo(0, currentPointY);

        if (slideListener != null)
            slideListener.moveDirection(false, true, false, false);
    }

    public void moveDown() {
        if (currentPointY >= (totalKitHeight - KitHeight)) {
            if (slideListener != null)
                slideListener.toBootom();
            return;
        }

        currentPointY += slideHeight;
        if (currentPointY >= (totalKitHeight - KitHeight))
            currentPointY = (totalKitHeight - KitHeight);
        scrollTo(0, currentPointY);
        if (slideListener != null)
            slideListener.moveDirection(false, false, false, true);
    }

    /**
     * 上下滑动监听
     */
    public interface SlideListener {
        /**
         * 滑动方向
         *
         * @param left  左滑动
         * @param up    上滑动
         * @param right 右滑动
         * @param down  下滑动
         */
        public void moveDirection(boolean left, boolean up, boolean right, boolean down);

        public void toBootom();

        public void toTop();
    }
}
