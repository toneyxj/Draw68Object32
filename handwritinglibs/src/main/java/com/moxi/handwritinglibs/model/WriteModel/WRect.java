package com.moxi.handwritinglibs.model.WriteModel;

import android.graphics.RectF;

/**
 * Created by xj on 2018/7/25.
 */

public class WRect {
    public float top=0;
    public float left=0;
    public float right=0;
    public float bottom=0;

    public void setPoint(float x, float y){
        if (top==0||top>y)top=y;
        if (left==0||left>x)left=x;
        if (right==0||right<x)right=x;
        if (bottom==0||bottom<y)bottom=y;
    }

    /**
     * 是否可以裁剪线段，裁剪标准为200个像素面积
     * @return
     */
    public boolean isCut(){

       return (Math.abs(right-left)*Math.abs(top-bottom))>=40000;
    }
    public RectF getRectF(){
        return new RectF(left,top,right,bottom);
    }
    public boolean isZore(){
        return top==0&&left==0&&right==0&&bottom==0;
    }

    @Override
    public String toString() {
        return "WRect{" +
                "top=" + top +
                ", left=" + left +
                ", right=" + right +
                ", bottom=" + bottom +
                '}';
    }
    public int getSize(){
        return 24;
    }

}
