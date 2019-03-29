package com.moxi.haierexams.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * Created by Archer on 16/10/13.
 */
public class NoRelativeLayout extends RelativeLayout {
    public NoRelativeLayout(Context context) {
        super(context);
    }

    public NoRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NoRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
