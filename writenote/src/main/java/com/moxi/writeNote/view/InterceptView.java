package com.moxi.writeNote.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

/**
 * Created by Administrator on 2017/3/13 0013.
 */

public class InterceptView extends LinearLayout {
    public InterceptView(Context context) {
        super(context);
    }

    public InterceptView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public InterceptView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    private ClickOther clickOther;

    public void setClickOther(ClickOther clickOther) {
        this.clickOther = clickOther;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction()==MotionEvent.ACTION_DOWN){
            if (clickOther!=null)clickOther.onClickOther();
        }
        return true;
    }
    public interface  ClickOther{
        void onClickOther();
    }
}
