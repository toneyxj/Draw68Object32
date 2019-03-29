package com.moxi.handwritinglibs.model.WriteModel;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;

import com.moxi.handwritinglibs.writeUtils.PenUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xj on 2018/7/16.
 */

public class WLine {
    /**
     * 绘制点集合
     */
    public List<WPoint> points;
    /**
     * 线宽
     */
    public int lineWidth = 10;
    /**
     * 绘制时移动距离
     */
    public int scroolY=0;
    /**
     * 记录线段框的范围
     */
    public WRect wRect=new WRect();

    public List<WPoint> getPoints() {
        return points;
    }

    public WLine(int lineWidth) {
        this.lineWidth = lineWidth;
    }

    public WLine() {
    }

    public boolean addPoint(float x, float y,int scroolY,float pressure) {
        if (points == null) {
            points = new ArrayList<WPoint>();
        }
        this.scroolY=scroolY;
        if (points.size()>0){
            WPoint point=points.get(points.size()-1);
            if (Math.abs(point.x-x)<1&&Math.abs(point.y-y)<1){
                point.pressure=pressure;
            }else {
                points.add(new WPoint(x, y,pressure));
            }
        }else {
            points.add(new WPoint(x, y,pressure));
        }
        wRect.setPoint(x,y);

        return wRect.isCut();
    }

    /**
     * 获得绘制路径矩形框
     *
     * @return
     */
    public RectF getRectF() {
        if (wRect.isZore()){
            for (WPoint p:points){
                wRect.setPoint(p.x,p.y);
            }
        }
        return  wRect.getRectF();
    }

    public void isNull(){
//        wRect=null;
    }

    /**
     * 线绘制到画布上面
     * @param canvas
     */
    public void drawCanvas(Canvas canvas){
        drawCanvas(canvas,0,0);
    }
    /**
     * 线绘制到画布上面
     * @param canvas
     */
    public void drawCanvas(Canvas canvas,int scroolY,int height){
        if (points==null||points.size()==0)return;
        Paint myPaint= PenUtils.getPaint(lineWidth);
        Path path;
        int i=0;
        WPoint fP=points.get(0);
        WPoint fTwo;
        int size=points.size();
        if (size>1)i=1;
        for (; i < size; i++) {
            fTwo=points.get(i);
            float ll = PenUtils.getLineWidth(lineWidth,fTwo.pressure);
            myPaint.setStrokeWidth(ll);
            

//            PathEffect pathEffect1=new DashPathEffect(new float[] { 10, 0.5f,
//                    8, 0.3f,
//                    6,0.2f,
//                    4,0.1f
//            }, 2);
//            PathEffect pathEffect2=new CornerPathEffect()new float[] { 3, 3f,
//            }, 2);
//            PathEffect pathEffect2=new DiscretePathEffect(ll, ll*0.7f);
                   /*
         * 绘制路径
         */
//            Path p = new Path();
//            p.addCircle(0, 0, ll/2, Path.Direction.CCW);
//            PathEffect pathEffect2 = new PathDashPathEffect(p, (float) (ll*0.7), 1, PathDashPathEffect.Style.ROTATE);
//            PathEffect pathEffect=new SumPathEffect(pathEffect1,pathEffect2);
//            myPaint.setPathEffect(pathEffect);
            if (scroolY==0) {
                canvas.drawLine(fP.x, fP.y, fTwo.x, fTwo.y, myPaint);
            }else {
                if (getNoDraw(fP.y,fTwo.y,scroolY,height)) {
                    path = new Path();
                    path.moveTo(fP.x, fP.y);
                    path.lineTo(fTwo.x, fTwo.y);
                    path.offset(0, -scroolY);

                    canvas.drawPath(path, myPaint);
                }
            }
            fP=fTwo;
        }
    }
    private boolean getNoDraw(float one,float two,int scroolY,int height){
        int min=scroolY;
        int max=min+height;
        return (one>min&&max>one)||(two>min&&max>two);
    }

    @Override
    public String toString() {
        return "WLine{" +
                "points=" + points +
                ", lineWidth=" + lineWidth +
                ", scroolY=" + scroolY +
                ", wRect=" + wRect +
                '}';
    }
    public int getSize(){
        int size=8+wRect.getSize();
        if (points!=null) {
            for (int i = 0; i < points.size(); i++) {
                size += points.get(i).getSize();
            }

        }
        return size+8;
    }
}
