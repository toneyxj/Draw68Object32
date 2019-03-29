package com.moxi.handwritinglibs.writeUtils;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.google.gson.Gson;
import com.moxi.handwritinglibs.model.WriteModel.WLine;
import com.moxi.handwritinglibs.model.WriteModel.WMoreLine;
import com.moxi.handwritinglibs.model.WriteModel.WPoint;
import com.moxi.handwritinglibs.model.WriteModel.WritePageData;
import com.moxi.handwritinglibs.utils.GZIP;
import com.mx.mxbase.constant.APPLog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xj on 2018/7/17.
 */

public class PenUtils {
    private WritePageData pageData;
    /**
     * 中间保存单条线的类
     */
    @JSONField(serialize = false)
    private WLine middleLine;
    /**
     * 用户撤销和返回功能按钮
     */
    private int backOrLastIndex = 0;
    private long drawSize=0;

    /**
     * 空数据初始化
     */
    private void nullInit() {
        if (pageData == null) {
            pageData = new WritePageData();
        }
        pageData.nullInit();
    }

    public boolean setPageData(WritePageData pageData) {
//        if (pageData!=null&&this.pageData!=null){
//            return true;
//        }
        backOrLastIndex = 0;
        this.pageData = pageData;
        nullInit();

        this.pageData.isNull();
        drawSize=this.pageData.getSize();
        return false;
    }
    public void setDataIsNull(){
        if (pageData == null) {
            pageData = new WritePageData();
        }
        pageData.nullInit();
        this.pageData.isNull();
    }

    public WritePageData getPageData() {
        nullInit();
        return pageData;
    }

    /**
     * 获得绘制一条线的实列
     *
     * @param lineWidth
     */
    public WLine getWline(boolean isStart, int lineWidth) {
        if (middleLine == null || isStart) middleLine = new WLine(lineWidth);
        return middleLine;
    }

    /**
     * 为绘制写线添加数据节点
     *
     * @param lineWidth
     * @param x
     * @param y
     * @param pressure 压感
     */
    public void addPoint(boolean isStart, int lineWidth, float x, float y,int scroolY,float pressure) {
        y+=scroolY;
        getWline(isStart, lineWidth);
        /**
         * 剪切绘制的路径
         */
        if (middleLine.addPoint(x, y,scroolY,pressure)){
            addPathData();
            middleLine=null;
            addPoint(isStart,lineWidth,x,y,0,pressure);
        }
    }

    public long getDrawSize() {
        return drawSize;
    }

    /**
     * 为缓冲添加数据
     */
    public void addPathData() {
        if (middleLine == null) return;
        backOrLastIndex = 0;
        nullInit();
        cutSaveData();
        pageData.drawMiddleLines.add(new WMoreLine(middleLine));
    }

    /**
     * 数据保存间转换
     */
    private void cutSaveData() {
        if (pageData.drawMiddleLines.size() >= 30) {
            WMoreLine wm = pageData.drawMiddleLines.remove(0);
            if (wm.isLineStatus() && wm.MoreLines != null) {//确认是绘制线
                pageData.mainLines.addAll(wm.MoreLines);//添加超过的数据到备用缓冲中
            }
        }
    }

    /**
     * 添加删除线
     *
     * @param lines
     */
    private void addDeleteDatas(List<WLine> lines) {
        if (lines.size() <= 0) return;
        backOrLastIndex =0;
        nullInit();
        cutSaveData();
        pageData.drawMiddleLines.add(new WMoreLine(lines, 1));
    }

    /**
     * 添加线
     *
     * @param ml
     */
    private void addMWLines(WMoreLine ml) {
        backOrLastIndex = 0;
        nullInit();
        cutSaveData();
        pageData.drawMiddleLines.add(ml);
    }

    /**
     * 获取绘制的path数据集合
     *@param moveY Y
     * @return
     */
    public List<WLine> getDrawPaths(int moveY) {
        nullInit();
        List<WLine> paths = new ArrayList<WLine>();
        paths.addAll(pageData.mainLines);

        for (WMoreLine wm : pageData.drawMiddleLines) {
            if (wm.isLineStatus()) {
                paths.addAll(wm.getMoreLines());
            }
        }
        return paths;
    }

    /**
     * 撤销
     */
    public void lastStep() {
        nullInit();
        if (backOrLastIndex >= 29) return;
        if (pageData.drawMiddleLines.size() <= 0) return;
        int size = pageData.drawMiddleLines.size() - 1 - backOrLastIndex;
        if (size < 0) return;
        boolean isLine=pageData.drawMiddleLines.get(size).ChangeLineStatus();
        List<WLine> lines=pageData.drawMiddleLines.get(size).getMoreLines();
        int mSize=lines.size();
        if (isLine&&mSize>1){
            pageData.drawMiddleLines.remove(size);
            //说明是删除线切 不止一条
            //把删除线单个提取并赋值
            for (int i = 0; i < mSize; i++) {
                addMWLines(new WMoreLine(lines.get(i), 0));
            }
            backOrLastIndex+=mSize;
        }else {
            backOrLastIndex++;
        }
        APPLog.e("all size=" + pageData.drawMiddleLines.size(), "index size is " + size + "   backOrLastIndex is " + backOrLastIndex);
    }

    /**
     * 返回
     */
    public void nextStep() {
        nullInit();
        int size = pageData.drawMiddleLines.size();

        if (size <= 0) return;
        if (backOrLastIndex <= 0) {
            return;
        }
        int dd = size - backOrLastIndex;
        backOrLastIndex--;
        if (dd >= size) return;
        pageData.drawMiddleLines.get(dd).ChangeLineStatus();
        APPLog.e("all size=" + pageData.drawMiddleLines.size(), "index size is " + dd + "    backOrLastIndex is " + backOrLastIndex);
    }

    /**
     * 目前处理，删除全部使用点删除的形式
     *
     * @param x
     * @param y
     */
    public void deleteData(float x, float y, int lineWidth) {
        nullInit();
        List<WLine> deleteLines = new ArrayList<WLine>();
        int mi = lineWidth / 2;
        RectF rectF = new RectF();
        rectF.set(x - mi, y - mi, x + mi, y + mi);

        try {
            /**
             * 计算已经保存的线
             */
            for (WLine l : pageData.mainLines) {
                boolean is = PathUtils.getPathIntersect(rectF, l);
                if (is)
                    deleteLines.add(l);
            }
            //删除在主绘制保存数据里面的线
            pageData.mainLines.removeAll(deleteLines);

            List<WMoreLine> middleDeletes = new ArrayList<WMoreLine>();

            for (int i = 0; i < pageData.drawMiddleLines.size(); i++) {
                WMoreLine wm = pageData.drawMiddleLines.get(i);
                if (wm.isLineStatus()) {//可删除绘制路径
                    for (WLine l : wm.getMoreLines()) {
                        boolean is = PathUtils.getPathIntersect(rectF, l);
//                        APPLog.e("deleteData", is);
                        if (is) {
                            deleteLines.add(l);
                            if (!middleDeletes.contains(wm))
                                middleDeletes.add(wm);
                        }
                    }
                }
            }
            pageData.drawMiddleLines.removeAll(middleDeletes);

            addDeleteDatas(deleteLines);

        } catch (Exception e) {

        }
    }

    public String getSaveDate() {
        return pageData.getSaveDate();
    }


    /**
     * 获得绘制删除矩形框
     *
     * @param x         当前点击点
     * @param y
     * @param lineWidth
     * @return
     */
    public List<WPoint> getDeletePoint(float x, float y, int lineWidth) {
        int mi = lineWidth / 2;
        List<WPoint> wps = new ArrayList<WPoint>();
        wps.add(new WPoint(x - mi, y - mi,0));//left_top
        wps.add(new WPoint(x + mi, y - mi,0));//right-top
        wps.add(new WPoint(x + mi, y + mi,0));//bottom-right
        wps.add(new WPoint(x - mi, y + mi,0));//bottom-left
        wps.add(new WPoint(x - mi, y - mi,0));//left_top
        return wps;
    }
    /**
     * 获得绘制删除矩形框
     *
     * @param x         当前点击点
     * @param y
     * @param lineWidth
     * @return
     */
    public RectF getRectPoint(float x, float y, int lineWidth) {
        int mi = lineWidth / 2;
        RectF rectF = new RectF();
        rectF.set(x - mi, y - mi, x + mi, y + mi);
        return rectF;
    }


    public static String getWritePageDate(WritePageData data) {
        if (data == null) return "";
        data.nullInit();
        // 使用new方法
        try {
            Gson gson = new Gson();
            // toJson 将bean对象转换为json字符串
            String jsonStr = GZIP.gzip(gson.toJson(data, WritePageData.class));
            return jsonStr;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
    private static JSONObject getWLineObject(WLine line){
        JSONObject obj=new JSONObject();
        try {
            obj.put("lw",line.lineWidth);
            obj.put("sy",line.scroolY);
            List<WPoint> points=line.getPoints();
            int ps=points.size();
            JSONArray pointArray=new JSONArray();
            for (int i = 0; i < ps; i++) {
                WPoint point=points.get(i);
                JSONObject pobj=new JSONObject();
                pobj.put("x",point.x);
                pobj.put("y",point.y);
                pobj.put("p",point.pressure);
                pointArray.add(pobj);
            }
            obj.put("ls",pointArray);
        }catch (Exception e){
            e.printStackTrace();
        }
        return obj;
    }

    public static WritePageData getWritePage(String data) {
        try {
            Gson gson = new Gson();
            WritePageData pageData = gson.fromJson(GZIP.gunzip(data), WritePageData.class);
            return pageData;
        }catch (Exception e){
            return new WritePageData();
        }
    }

    /**
     * 获得对应的画笔
     *
     * @param points_width
     * @return
     */
    public static Paint getPaint(int points_width) {
        Paint mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.BLACK);
//        mPaint.setXfermode(null);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(points_width);

        return mPaint;
    }
    public static void refureshActivity(Activity activity){
        if (activity==null)return;
        activity.getWindow().getDecorView().invalidate(View.EINK_MODE_GC16_FULL);
    }
    public static float getPressure(float pressure,float lastPressure,int lineWidth ){
        if (lastPressure<=0)return pressure;
            float pxf=0.1f/lineWidth;
            if (Math.abs(pressure-lastPressure)>=pxf){
                if (lastPressure>pressure){
                    lastPressure-=pxf;
                }else {
                    lastPressure+=pxf;
                }
                pressure=lastPressure;
            }
        if (pressure>=0.9){
            pressure=0.9f;
        }
//        APPLog.e("pressure",pressure);
        return pressure;
    }
    public static float getLineWidth(int lineWidth,float pressure){
        if (pressure<=0){
            return lineWidth;
        }
        float line=(lineWidth*1.5f)*pressure;
        if (line<2)line=2;
        return line;
    }
    public static float getPointLenght(float x1,float y1, float x2,float y2){
        if ((x1==0&&y1==0)||(x2==0&&y2==0))return 0.0f;
      return (float) Math.abs(Math.sqrt(Math.pow(x1-x2,2)+Math.pow(y1-y2,2)));
    }
    public static String getDrawHitn(int state){
        String value="执行中，请稍候...";
//        switch (state){
//            case 9:
//                value="执行中，请稍候...";
//                break;
//            default:
//                value="笔记绘制中...";
//                break;
//
//        }
        return value;
    }
}
