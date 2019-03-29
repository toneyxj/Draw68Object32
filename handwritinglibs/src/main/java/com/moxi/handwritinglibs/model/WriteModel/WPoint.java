package com.moxi.handwritinglibs.model.WriteModel;

/**
 * Created by xj on 2017/12/5.
 */

public class WPoint {
    public float x;
    public float y;
    public float pressure=0.0f;

    /**
     * 点信息集合
     */
    public WPoint( float x, float y,float pressure) {
        this.x = x;
        this.y = y;
        this.pressure=pressure;
    }
    public void move(float mx,float my){
        x+=mx;
        y+=my;
    }
    public void setPoint(float x, float y){
        this.x = x;
        this.y = y;
    }
    public boolean isLoseOne(){
        return x==-1&&y==-1;
    }

    @Override
    public String toString() {
        return "WPoint{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
    public  int getSize(){
        return 20;
    }
}
