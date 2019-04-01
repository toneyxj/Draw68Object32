package com.moxi.handwritinglibs.writeUtils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.xrz.Xrzhandwrite;

import com.moxi.handwritinglibs.listener.WindowRefureshListener;
import com.moxi.handwritinglibs.listener.WriteTagListener;
import com.moxi.handwritinglibs.model.WriteModel.WLine;
import com.moxi.handwritinglibs.model.WriteModel.WPoint;
import com.moxi.handwritinglibs.model.WriteModel.WritePageData;
import com.moxi.handwritinglibs.utils.PaintBackUtils;
import com.moxi.handwritinglibs.utils.WriteRunable;
import com.mx.mxbase.constant.APPLog;
import com.mx.mxbase.utils.Log;
import com.mx.mxbase.utils.StringUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xj on 2018/7/16.
 */

public class PenControl implements View.OnTouchListener {

    /**
     * 是否采用笔头擦除线
     */
    public boolean nibWipe = false;
    /**
     * 是否有画线
     */
    public boolean isCross = false;
    /**
     * 启动橡皮擦功能-
     */
    public boolean isNibWipe = false;
    private boolean isfinish = false;
    //View控件
    private SurfaceView surfaceView;

    //控件基本信息
    public int maxwidth;
    public int maxHeight;
    public int padingLeft;
    public int padingTop;
    public int padingRight;
    public int padingBottom;
    /**
     * 绘制线宽
     */
    public int lineWidth = 10;
    /**
     * 删除线宽
     */
    public int deleteLineWidth = 20;
    /**
     * 是否允许绘制
     */
    public boolean canDraw = true;
    /**
     * 单个手写界面保存数据
     */
    private PenUtils pageData;
    /**
     * 绘制已经完毕
     */
    public boolean drawUp = true;
    private WindowRefureshListener refureshListener;
    public WriteTagListener tagListener;
    /**
     * 删除框的位置
     */
    private WPoint deletePoint = new WPoint(-1, -1, 0);
    /**
     * 设置当前背景是否是透明背景，透明背景下PaintBackUtils将失去作用
     */
    private boolean isBackgroundDraw = true;
    private boolean backIsTran = false;

    private Bitmap backBitmap;
    /**
     * 绘制背景
     */
    private PaintBackUtils utils;
    private int drawStyle = 0;
    private String filepath = "";
    /**
     * 是否有过删除动作
     */
    private boolean isdeleted = false;
    public boolean isWorte = true;//是否是手写备忘app

    private int scroolY = 0;
    private int canvasHeight = 0;
    private MyHandler handler;
    private List<String> clickTests = new ArrayList<String>();
    private boolean isAddBacking=false;
    private boolean notToDraw=false;


    public List<String> getClickTests() {
        return clickTests;
    }

    private static class MyHandler extends Handler {

        WeakReference<PenControl> mReference = null;

        MyHandler(PenControl activity) {
            this.mReference = new WeakReference<PenControl>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            PenControl outer = mReference.get();
            if (outer == null || outer.isfinish) {
                Log.e("outer is null");
                return;
            }
            switch (msg.what) {
                case 100://刷新界面
                    outer.refreshGc16();
                    outer.setCanDraw(true);
                    break;
                case 10:
                    outer.surfaceDraw(0);
                    break;
                case 11://设置可画
                    outer.setDraw(true);
                default:
                    break;

            }


        }
    }

    public void setCanvasHeight(int canvasHeight) {
        this.canvasHeight = canvasHeight;
    }

    public void setScroolY(int scroolY) {
        this.scroolY = scroolY;
        haveDraw=false;
        surfaceDraw(1);
        canDraw = false;
        onScrool(true);
    }
    public void setDefaultScrollTo() {
        this.scroolY = 0;
    }


    public void refreshGc16() {
        surfaceView.invalidate(View.EINK_MODE_GC16_FULL);
    }

    /**
     * 设置背景透明
     *
     * @param backIsTran
     */
    public void setBackIsTran(boolean backIsTran) {
        this.backIsTran = backIsTran;
        surfaceDraw(2);
    }
    private boolean setNewDraw=false;

    public void setBackgroundDraw(boolean backgroundDraw, Bitmap bitmap) {
        isBackgroundDraw = backgroundDraw;
        backBitmap = bitmap;
        haveDraw = false;
        setNewDraw=true;
        surfaceDraw(3);
    }

    /**
     * 修改背景index
     *
     * @param drawStyle
     */
    public void setDrawStyle(int drawStyle) {
        this.drawStyle = drawStyle;
        surfaceDraw(4);
    }

    /**
     * 修改背景的 图片
     *
     * @param drawStyle
     * @param filepath
     */
    public void setDrawStyle(int drawStyle, String filepath,boolean ischekPage) {
        this.filepath = filepath;
        this.drawStyle = drawStyle;
//        haveDraw=false;
        if (!ischekPage)
        surfaceDraw(5);
    }

    public void setRefureshListener(WindowRefureshListener refureshListener) {
        this.refureshListener = refureshListener;
    }

    public void setPageData(WritePageData pageData) {
        haveDraw = false;
        if (!thisPenUtils().setPageData(pageData)) {
            if (pageData == null || pageData.dataNull()) {
                isCross = true;
            } else {
                isCross = false;
            }
        }
        surfaceDraw(6);
    }

    public PenUtils thisPenUtils() {
        if (pageData == null) pageData = new PenUtils();
        return pageData;
    }

    public void setCross(boolean cross) {
        isCross = cross;
    }

    public void setCanDraw(final boolean canDraw) {
        if (canDraw) {
            handler.removeMessages(11);
            handler.sendEmptyMessageDelayed(11, 200);
//            this.canDraw = true;
        } else {
            setDraw(canDraw);
        }

    }

    private void setDraw(boolean canDraw) {
        this.canDraw = canDraw;
        if (canDraw) {
//            setEnterScribble();
        } else {
            surfaceDraw(7);
            setleaveScribble();
        }
    }


    public PenControl(final SurfaceView surfaceView) {
        this.surfaceView = surfaceView;
        this.surfaceView.setOnTouchListener(this);
        handler = new MyHandler(this);
    }

    /**
     * 延时加载
     */
    public boolean isdelayInit=false;
    /**
     * 初始化手写控件
     */
    public void init_handwrite() {
        if (isdelayInit){
            isdelayInit=false;
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    init_handwrite();
                }
            },300);
        }
        else {
            init_handwriteC();
            if (pageData != null && haveDraw) {
                haveDraw = false;
            }
            surfaceDraw(8);
        }

    }
    private void init_handwriteC(){
        int viewPosition[] = {0, 0};
        surfaceView.getLocationOnScreen(viewPosition);
        Xrzhandwrite.xrzenablePost(surfaceView.getHolder().getSurface(), viewPosition[0], viewPosition[1], surfaceView.getWidth(), surfaceView.getHeight());
    }
    private float lastX;
    private float lastY;

    public void setDown() {
        lastPressure = 0;
        setEnterScribble();
        noDraw(false);
    }

    public boolean setNotToDraw(boolean notToDraw) {
        this.notToDraw = notToDraw;
        return notToDraw;
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        if (!canDraw) return false;
        if (isDrawing)return false;
        if (notToDraw)return false;
//        if (MotionEvent.ACTION_DOWN == event.getAction() || MotionEvent.ACTION_POINTER_DOWN == event.getAction()) {
//            lastPressure = 0;
//            setEnterScribble();
//            noDraw(false);
//        }
        int iDeviceId = event.getDeviceId();
        int tooolType = event.getToolType(0);
        if (iDeviceId == 2 || tooolType == 1) return true;//手指接触屏幕
        //压感参数
        float pressure = event.getPressure();
        nibWipe = tooolType == 4 && iDeviceId == 1;//用笔头檫线
//        if (MotionEvent.ACTION_DOWN == event.getAction())
//            nibWipe = (tooolType == 4 && iDeviceId == 1) && pressure > 0.9;//用笔头檫线
        boolean isDrawLine = !(nibWipe || isNibWipe);

//        String value="isNibWipe="+isNibWipe+"   iDeviceId="+iDeviceId+"  tooolType="+tooolType+"   pressure="+pressure;
//        APPLog.e("value",value);

        float eventX = event.getX();
        float eventY = event.getY();

        if (isDrawLine) {
            lastPressure = PenUtils.getPressure(pressure, lastPressure, lineWidth);
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                if (clickTests.size() > 15) {
                    clickTests.remove(0);
                }
//                clickTests.add(value);

                if (!isDrawLine) {
                    setDeleteRect(event, eventX, eventY);
                } else {
                    pointDraw(eventX, eventY, true, lastPressure);
                    thisPenUtils().addPoint(true, lineWidth, eventX, eventY, scroolY, lastPressure);
                    drawUp = false;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (isDrawLine) {
//                    float lineW=PenUtils.getPointLenght(eventX,eventY,lastX,lastY);
//                    APPLog.e("lineW",lineW);
                    int historySize = event.getHistorySize();
                    int start = historySize / 2;
                    if (historySize % 2 == 0 && start > 0) start -= 1;
                    for (int i = start; i < start + 2; i++) {
                        if (i >= historySize) break;
                        float historicalX = event.getHistoricalX(i);
                        float historicalY = event.getHistoricalY(i);
                        pointDraw(historicalX, historicalY, false, lastPressure);
                        thisPenUtils().addPoint(false, lineWidth, historicalX, historicalY, scroolY, lastPressure);
                        lastPressure = PenUtils.getPressure(pressure, lastPressure, lineWidth);
                    }
                    pointDraw(eventX, eventY, false, lastPressure);
                    thisPenUtils().addPoint(false, lineWidth, eventX, eventY, scroolY, lastPressure);
                } else {
                    setDeleteRect(event, eventX, eventY);
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                lastX = 0;
                lastY = 0;
                if (isDrawLine) {
                    if (event.getAction() != MotionEvent.ACTION_CANCEL) {
                        pointDraw(eventX, eventY, false, lastPressure);
                        thisPenUtils().addPoint(false, lineWidth, eventX, eventY, scroolY, lastPressure);
                    }
                    thisPenUtils().addPathData();//添加数据进入队列
                    noDraw(true);
                    drawUp = true;
                } else {//启动删除
                    canDraw = false;
                    setDeleteRect(event, eventX, eventY);
                    deletePoint.setPoint(-1, -1);
                    setleaveScribble();
                    surfaceDraw(9);
                    canDraw = true;
                }
                break;
        }
        lastX = eventX;
        lastY = eventY;
        return true;
    }


    public void noDraw(boolean is) {
        handler.removeMessages(10);
        if (is)
            handler.sendEmptyMessageDelayed(10, 20000);
    }

    /**
     * 移动界面控制
     *
     * @param is
     */
    private void onScrool(boolean is) {
        handler.removeMessages(100);
        if (is)
            handler.sendEmptyMessageDelayed(100, 1000);
    }

    /**
     * 设置删除的线
     *
     * @param x
     * @param y
     */
    private void setDeleteRect(MotionEvent event, float x, float y) {
        if (deletePoint.x != -1 && Math.abs(x - deletePoint.x) < deleteLineWidth && Math.abs(y - deletePoint.y) < deleteLineWidth) {
            return;
        }
        deletePoint.setPoint(x, y);
        thisPenUtils().deleteData(x, y + scroolY, deleteLineWidth);
        List<WPoint> points = thisPenUtils().getDeletePoint(x, y, deleteLineWidth);
        if (points.size() <= 0) return;
        int index = 1;
        pointDraw(points.get(0).x, points.get(0).y, true, 0);
        if (points.size() <= 1) index = 0;
        for (int i = index; i < points.size(); i++) {
            pointDraw(points.get(i).x, points.get(i).y, false, 0);
        }
//        doubleDraw(null, false,true);
    }

    private float lastPressure = 0;

    private void pointDraw(float eventX, float eventY, boolean isDown, float pressure) {
        boolean isDrawLine = !(nibWipe || isNibWipe);
        if (isDown) lastPressure = 0;
        isCross = true;
        haveDraw = false;
        int middleSize = 0;
        if (eventX < (padingLeft + middleSize)) {
            eventX = padingLeft + middleSize;
        } else if (eventX > (maxwidth - (padingRight + middleSize))) {
            eventX = maxwidth - (padingRight + middleSize);
        }
        if (eventY < (padingTop + middleSize)) {
            eventY = padingTop + middleSize;
        } else if (eventY > (maxHeight - (padingBottom + middleSize))) {
            eventY = maxHeight - (padingBottom + middleSize);
        }
        float ll = 1f;
        if (isDrawLine) {//绘制线
            ll = PenUtils.getLineWidth(lineWidth, pressure);
        }
        if (isDown) {
            Xrzhandwrite.xrzstartStroke(ll, eventX, eventY, 1, 1, System.currentTimeMillis());
        } else {
            Xrzhandwrite.xrzaddStrokePoint(ll, eventX, eventY, 1, 1, System.currentTimeMillis());
        }
    }

    /**
     * 开启手写
     */
    public void setEnterScribble() {
        if (Xrzhandwrite.xrzGethandwritestate() == 0) {
            Xrzhandwrite.xrzEnterOrleave(1);
        }
    }

    /**
     * 返回true手写模式
     *
     * @return
     */
    public boolean isOnDraw() {
        try {
            return Xrzhandwrite.xrzGethandwritestate() != 0;
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 关闭手写
     */
    public void setleaveScribble() {
        try {
            Xrzhandwrite.xrzEnterOrleave(0);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public void refureshWindows(boolean haveDraw) {
        if (this.haveDraw) {
            this.haveDraw = haveDraw;
        }
        surfaceDraw(10);
    }

    public boolean haveDraw = true;
    private boolean isDrawing=true;
    private WriteRunable runable=null;

    private void surfaceDraw(int index) {
        setleaveScribble();
        APPLog.e("PenControl-surfaceDraw1",index);
        if (haveDraw) return;
        haveDraw = true;
        APPLog.e("PenControl-surfaceDraw2",index);
        if (runable!=null){
            runable.setFinish(true);
        }
        canDraw = false;
        isDrawing=true;
        setTagListenerBack(PenUtils.getDrawHitn(index),false);
        final List<WLine> lines = thisPenUtils().getDrawPaths(scroolY);
        runable= new WriteRunable() {
            @Override
            public void run() {
                doubleDraw(this,lines, false, false, true);
                doubleDraw(this,lines, false, false, false);
                if (runable!=null&&this.isFinish())return;
                setCanDraw(true);
                isDrawing=false;
                setTagListenerBack("",true);
                APPLog.e("手写刷新完成");
                setNewDraw=false;
            }
        };
         new Thread(runable).start();
    }

    public void setTagListenerBack(final String tag, boolean toMain){
        if (tagListener==null)return;
        if (toMain) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    tagListener.WriteTag(isDrawing, tag);
                }
            });
        }else {
            tagListener.WriteTag(isDrawing, tag);
        }
    }

    public boolean isCanDraw() {
        return canDraw;
    }

    public void doubleDraw(WriteRunable runable,List<WLine> lines, final boolean isfull, boolean isDelete, boolean first) {
        if (runable!=null&&runable.isFinish())return;
        if (refureshListener != null)
            refureshListener.onDrawDelete(isDelete);
        Canvas mCanvas = surfaceView.getHolder().lockCanvas();
        if (mCanvas == null) return;
        drawPage(runable,mCanvas, lines, isDelete, false, first);
//        new CustomThread( surfaceView.getHolder(),lines,isDelete).start();

        surfaceView.getHolder().unlockCanvasAndPost(mCanvas);
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (isfull) {
                    surfaceView.invalidate(View.EINK_MODE_GC16_FULL);
                } else {
                    surfaceView.invalidate(View.EINK_MODE_DU_PART);
                }
                if (refureshListener != null) {
                    refureshListener.onWindowRefureshEnd();
                }
            }
        });
    }
    /**
     * 获得绘制界面的图片并
     *
     * @return
     */
    public Bitmap getDrawBitmap() {
        int w = maxwidth;
        int h = canvasHeight <= maxHeight ? maxHeight : canvasHeight;
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawPage(null,canvas, null, false, true, false);
        return bitmap;
    }

    /**
     * 绘制当前的数据到画布上面
     *
     * @param mCanvas
     * @param lines
     */
    private void drawPage(WriteRunable runable,Canvas mCanvas, List<WLine> lines, boolean isDelete, boolean getpic, boolean isfirst) {
        if (runable!=null&&runable.isFinish())return;
        if (mCanvas == null) return;
        int width = maxwidth;
        int height = maxHeight;
//        if (isfirst) {
//            mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
//            return;
//        }

        if (isBackgroundDraw) {
            if (backIsTran) {
                if (runable!=null&&runable.isFinish())return;;
                mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            } else {
                if (runable!=null&&runable.isFinish())return;
                mCanvas.drawColor(Color.WHITE);
                if (utils == null) {
                    utils = new PaintBackUtils();
                }
                utils.setWidthAndHeight(surfaceView.getContext(), height, width);
                if (runable!=null&&runable.isFinish())return;
                utils.DrawView(mCanvas, drawStyle, filepath);
            }
        } else {
            if (isDelete) {
                if (runable!=null&&runable.isFinish())return;
                mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            } else {
                if (null == backBitmap || backBitmap.isRecycled()) {
                    mCanvas.drawColor(Color.WHITE, PorterDuff.Mode.CLEAR);
                } else {
                    mCanvas.drawColor(Color.WHITE);
                    int w = backBitmap.getWidth();
                    int h = backBitmap.getHeight();
                    //计算缩放比列
                    float xs = width / (float) w;
                    float ys = height / (float) h;
                    float fs = xs > ys ? ys : xs;
                    if (fs >= 1) {
                        fs = 1;
                    }
                    Matrix matrix = new Matrix();
                    matrix.setScale(fs, fs);
                    w = (int) (backBitmap.getWidth() * fs);
                    h = (int) (backBitmap.getHeight() * fs);

                    int left = ((width - w) > 0 ? (width - w) : 0) / 2;
                    int top = ((height - h) > 0 ? (height - h) : 0) / 2;
                    matrix.postTranslate(left, top);
                    if (runable!=null&&runable.isFinish())return;
                    mCanvas.drawBitmap(backBitmap, matrix, PenUtils.getPaint(lineWidth));
                }
            }
        }
        if (runable!=null&&runable.isFinish())return;
        if (lines == null) {
            lines = thisPenUtils().getDrawPaths(scroolY);
        }
        for (WLine p : lines) {
            if (runable!=null&&runable.isFinish())return;
            if (getpic || scroolY == 0) {
                p.drawCanvas(mCanvas);
            } else {
                p.drawCanvas(mCanvas, scroolY, maxHeight);
            }
        }
        APPLog.e("surfaceDraw-绘制完成");
    }

    /**
     * 获得画线的页面数据，没有画线则返回null
     *
     * @return
     */
    public WritePageData getPageData() {
        if (isCross) {
            return thisPenUtils().getPageData();
        }
        return null;
    }
    public WritePageData getNoPageData() {
            return thisPenUtils().getPageData();
    }

    public void ondestory() {
        StringUtils.recycleBitmap(backBitmap);
        isfinish = true;
        noDraw(false);
        onScrool(false);
    }
}
