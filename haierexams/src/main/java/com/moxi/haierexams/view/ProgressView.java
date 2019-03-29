package com.moxi.haierexams.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * 进度条View
 * Created by Administrator on 2016/10/25.
 */
public class ProgressView extends View{
    private int maxNumber;
    private int curNumber;
    private int backColor=0xFFCECECE;
    private int progressColor=0xFF505050;
    private RectF rect = new RectF();
    private Paint paint;
    public void setMaxNumber(int maxNumber) {
        this.maxNumber = maxNumber;
        invalidate();
    }

    public void setCurNumber(int curNumber) {
        this.curNumber = curNumber;
        invalidate();
    }

    public void setBackColor(int backColor) {
        this.backColor = backColor;
    }

    public void setProgressColor(int progressColor) {
        this.progressColor = progressColor;
    }

    /**
     * 初始化控件
     * @param backColor 背景颜色
     * @param progressColor 进度颜色
     * @param maxNumber 最大值
     * @param curNumber 当前值
     */
    public void initView(int backColor,int progressColor,int maxNumber,int curNumber){
        this.backColor=backColor;
        this.progressColor=progressColor;
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
        paint.setStyle(Paint.Style.FILL);
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
        //绘制背景颜色
        rect.top = getPaddingTop();
        rect.left = getPaddingLeft();
        rect.bottom = getPaddingTop()+getKitHeight();
        rect.right = getPaddingLeft()+getKitWidth();
        paint.setColor(backColor);
        canvas.drawRoundRect(rect, getKitHeight() / 2, getKitHeight() / 2, paint);

        //绘制进度值
        float progress=curNumber/(float)maxNumber;
        rect.top = getPaddingTop();
        rect.left = getPaddingLeft();
        rect.bottom = getPaddingTop()+getKitHeight();
        rect.right = getPaddingLeft()+getKitWidth()*progress;
        paint.setColor(progressColor);
        canvas.drawRoundRect(rect, getKitHeight()/2, getKitHeight()/2, paint);

    }
}
