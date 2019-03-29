package com.moxi.writeNote.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.View;

/**
 * 预览线
 * Created by xj on 2017/6/23.
 */

public class Preview extends View {

    public Preview(Context context) {
        super(context);
        init();
    }
    public Preview(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    private int lineWidth=1;

    public void setLineWidth(int lineWidth) {
        this.lineWidth = lineWidth;
        invalidate();
    }

    private int width;
    private int height;
    private  Path mPath;
    private Paint mPaint;

    private Point pointone;
    private  Point pointTwo;
    private Point pointThree;
    private Point pointFour;

    private void init(){
        mPaint = new Paint();
        mPath = new Path();

        mPaint.setStrokeWidth(lineWidth);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        width=getWidth();
        height=getHeight();


        pointone = new Point(0, height/2);
        pointTwo = new Point((int) (width*0.33), 0);
        pointThree = new Point((int) (width*0.66), height);
        pointFour = new Point(width, height/2);

        mPath.moveTo(pointone.x, pointone.y);// 起点
        mPath.cubicTo(pointTwo.x,pointTwo.y,// 控制点
                pointThree.x,pointThree.y,// 控制点
                pointFour.x,pointFour.y);// 终点
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mPaint.setStrokeWidth(lineWidth);
        //画path
        canvas.drawPath(mPath, mPaint);

    }
}
