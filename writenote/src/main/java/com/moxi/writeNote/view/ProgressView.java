package com.moxi.writeNote.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * 进度条View
 * Created by Administrator on 2016/10/25.
 */
public class ProgressView extends View{
    private int maxNumber;
    private int curNumber;
    private RectF rect = new RectF();
    private Paint paint;
    private ProgressListener listener;
    public void setMaxNumber(int maxNumber) {
        this.maxNumber = maxNumber;
        invalidate();
    }
    public void setCurNumber(int curNumber) {
        if (curNumber>maxNumber){
            curNumber=maxNumber;
        }else if(curNumber<0) {
            curNumber=0;
        }
        this.curNumber = curNumber;
        invalidate();
        if (listener!=null){
            listener.onProgress(curNumber);
        }
    }
    public void subOrAdd(boolean add){
        if (add){
            curNumber++;
        }else {
            curNumber--;
        }
        setCurNumber(curNumber);
    }
    /**
     * 初始化控件
     * @param maxNumber 最大值
     * @param curNumber 当前值
     */
    public void initView(ProgressListener listener,int maxNumber,int curNumber){
        this.listener=listener;
        this.maxNumber=maxNumber;
        this.curNumber=curNumber;
        invalidate();
    }


    public ProgressView(Context context) {
        super(context);
        init();
    }

    public ProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    private void init(){
        paint = new Paint();
        paint.setAntiAlias(true);
    }
    public int getCurNumber(){
        return curNumber;
    }

    private int getKitWidth(){
      return getMeasuredWidth()- getPaddingLeft()-getPaddingRight();
    }
    private int getKitHeight(){
        return getMeasuredHeight()-getPaddingBottom()-getPaddingTop();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2);
        //绘制背景颜色
        rect.top = getPaddingTop();
        rect.left = getPaddingLeft();
        rect.bottom = getPaddingTop()+getKitHeight();
        rect.right = getPaddingLeft()+getKitWidth();
        paint.setColor(Color.BLACK);
        canvas.drawRoundRect(rect, getKitHeight() / 2, getKitHeight() / 2, paint);
        paint.setStyle(Paint.Style.FILL);
        //绘制进度值
        float progress=curNumber/(float)maxNumber;
        rect.top = getPaddingTop();
        rect.left = getPaddingLeft();
        rect.bottom = getPaddingTop()+getKitHeight();
        rect.right = getPaddingLeft()+getKitWidth()*progress;
        paint.setColor(Color.BLACK);
        canvas.drawRoundRect(rect, getKitHeight()/2, getKitHeight()/2, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int eventX= (int) event.getX();
        int eventY= (int) event.getY();

        if (eventX<0||eventX>getKitWidth())return false;
        if (eventY<0||eventY>getKitHeight())return false;
        int  size=(eventX*maxNumber)/getKitWidth();
        Log.e("ProgressView-Event",String.valueOf(size));
        setCurNumber(size);
        return true;
    }

    public interface  ProgressListener{
       void onProgress(int size);
    }
}
