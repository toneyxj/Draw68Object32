package com.mx.mxbase.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Administrator on 2017/4/26 0026.
 */

public class XuLineView extends View {
    public XuLineView(Context context) {
        super(context);
    }

    public XuLineView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public XuLineView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    private int color=Color.GRAY;

    public void setColor(int color) {
        this.color = color;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width=getWidth();
        int height=getHeight();
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(color);
        Path path = new Path();
        if (width>height){
            paint.setStrokeWidth(height);
            path.moveTo(0, height/2);
            path.lineTo(width,height/2);
        }else {
            paint.setStrokeWidth(width);
            path.moveTo( width/2,0);
            path.lineTo(width/2,height);

        }
        PathEffect effects = new DashPathEffect(new float[]{5,5,5,5},1);
        paint.setPathEffect(effects);
        canvas.drawPath(path, paint);
    }
}
