package com.mx.mxbase.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * 无边界listview
 *
 * Created by Administrator on 2016/9/29.
 */
public class NoListView extends ListView {
    public NoListView(Context context) {
        super(context);
    }
    public NoListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NoListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // TODO Auto-generated method stub
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
