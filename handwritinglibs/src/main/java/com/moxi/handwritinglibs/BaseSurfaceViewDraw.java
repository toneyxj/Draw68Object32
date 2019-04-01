package com.moxi.handwritinglibs;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.moxi.handwritinglibs.listener.WindowRefureshListener;
import com.moxi.handwritinglibs.listener.WriteTagListener;
import com.moxi.handwritinglibs.model.CodeAndIndex;
import com.moxi.handwritinglibs.utils.DbWriteModelLoader;
import com.moxi.handwritinglibs.writeUtils.PenControl;
import com.mx.mxbase.constant.APPLog;
import com.mx.mxbase.utils.ToastUtils;

/**
 * Created by xj on 2018/7/16.
 */

public class BaseSurfaceViewDraw extends SurfaceView implements SurfaceHolder.Callback {
    private PenControl penControl;
    private boolean isStartActivity=false;
    private WriteTagListener tagListener;

    public void setTagListener(WriteTagListener tagListener) {
        this.tagListener = tagListener;
        if (getPenControl()==null)return;
        getPenControl().tagListener=this.tagListener;
    }

    public void setStartActivity(boolean startActivity) {
        isStartActivity = startActivity;
    }

    public PenControl getPenControl() {
        return penControl;
    }
    public boolean isStart=false;

    /**
     * 保存唯一标识
     */
    private CodeAndIndex codeAndIndex;

    public void setCodeAndIndex(CodeAndIndex codeAndIndex) {
        this.codeAndIndex = codeAndIndex;
    }
    public String getSaveCode(){
        if (codeAndIndex!=null)return codeAndIndex.saveCode;
        return "";
    }

    public int getIndex() {
        return codeAndIndex.index;
    }

    public BaseSurfaceViewDraw(Context context) {
        super(context);
        init();
    }

    public BaseSurfaceViewDraw(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BaseSurfaceViewDraw(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }
    private void init(){
        this.setZOrderOnTop(true);
        this.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        penControl = new PenControl(this);
        getHolder().addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        penControl.init_handwrite();
        isStart=true;
        getPenControl().tagListener=this.tagListener;
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
    }

    @Override
    public void scrollTo(int x, int y) {
        if (getPenControl()==null)return;
        getPenControl().setScroolY(y);

    }

    /**
     * 设置画布高度
     */
    public void setCanvasHeight(int height){
        if (getPenControl()==null)return;
        getPenControl().setCanvasHeight(height);
    }
    public void setDefaultScrollTo(){
        if (getPenControl()==null)return;
        getPenControl().setDefaultScrollTo();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        penControl.setleaveScribble();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        penControl.maxwidth = getWidth();
        penControl.maxHeight = getHeight();
        penControl.padingLeft = getPaddingLeft();
        penControl.padingTop = getPaddingTop();
        penControl.padingRight = getPaddingRight();
        penControl.padingBottom = getPaddingBottom();
//        if (isStart){
//            penControl.init_handwrite();
//        }
    }

    public boolean isNibWipe() {
        if (getPenControl()==null)return false;
        return getPenControl().isNibWipe;
    }

    public boolean setNibWipe(boolean isNibWipe) {
        if (getPenControl()==null)return false;
        getPenControl().isNibWipe = isNibWipe;
        return true;
    }

    public void setClearLineWidth(int line) {
        if (getPenControl()==null)return;
        getPenControl().deleteLineWidth = line*2;
    }

    public void setDrawLineWidth(int line) {
        if (getPenControl()==null)return;
        getPenControl().lineWidth = line;
    }

    public void setCanDraw(boolean can, int index) {
        APPLog.e("setCanDraw="+can,index);
        if (getPenControl()==null)return;
        getPenControl().setCanDraw(can);
    }

    public void isdelayInit(boolean is){
        if (getPenControl()==null)return ;
         getPenControl().isdelayInit=is;
    }
    public boolean isDrawUp() {
        if (getPenControl()==null)return false;
        return getPenControl().drawUp;
    }
    public Bitmap getBitmap(){
        if (getPenControl()==null)return null;
        return getPenControl().getDrawBitmap();
    }

    //true 撤消，false 恢复
    public void backLastDraw(boolean is) {
        if (getPenControl()==null)return;
        APPLog.e("backLastDraw  is",is);
        if (is){
            getPenControl().thisPenUtils().lastStep();
        }else {
            getPenControl().thisPenUtils().nextStep();
        }
        getPenControl().refureshWindows(false);
    }
    public void setleaveScribbleMode(boolean is,int index){
        APPLog.e("setleaveScribbleMode-index="+index);
        if (getPenControl()==null)return;
        getPenControl().refureshWindows(true);
        if (is) {
            setCanDraw(true,12);
        }

    }

    /**
     * 刷新界面，退出手写模式
     */
    public void fullRefuresh(){
        if (isStartActivity) {
            return;
        }
        if (getPenControl()==null)return;
        getPenControl().refureshWindows(false);
    }
    /**
     * 返回true手写模式
     * @return
     */
    public boolean isOnDraw(){
        return getPenControl().isOnDraw();
    }
    /**
     * 清空数据
     */
    public void clearScreen() {
        if (getPenControl()==null)return;
        if (codeAndIndex!=null)
        DbWriteModelLoader.getInstance().clearBitmap(codeAndIndex.saveCode,codeAndIndex.index);
        getPenControl().setPageData(null);
        setleaveScribbleMode(false,5);
    }
    public void setRefureshListener(WindowRefureshListener refureshListener){
        if (getPenControl()==null)return;
        getPenControl().setRefureshListener(refureshListener);
    }
    public boolean isCanDraw() {
        if (getPenControl()==null)return false;
        return getPenControl().isCanDraw();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (MotionEvent.ACTION_DOWN == event.getAction() || MotionEvent.ACTION_POINTER_DOWN == event.getAction()) {
            if (getPenControl() == null) return false;

            int iDeviceId = event.getDeviceId();
            int tooolType = event.getToolType(0);
            boolean nibWipe = tooolType == 4 && iDeviceId == 1;//用笔头檫线

            if (!nibWipe) {
                int size = getPenControl().getNoPageData().getSize();
                if (getPenControl().setNotToDraw(size >= 1048576 * 1.2)) {
                    ToastUtils.getInstance().showToastShort("手绘内容过多，无法添加");
                } else {
                    getPenControl().setDown();
                }
            }else {
                getPenControl().setDown();
            }
        }
        return super.dispatchTouchEvent(event);
    }
    public void onKeysetEnterScribble(){
                getPenControl().setEnterScribble();
                getPenControl().noDraw(true);

    }

    /**
     * 16阶灰度值刷新调用
     * @param view
     */
    public void einkGC16Full (View view){
        view.invalidate(View.EINK_MODE_GC16_FULL);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (getPenControl()!=null) {
            getPenControl().ondestory();
            getPenControl().setleaveScribble();
        }
        getHolder().removeCallback(this);
    }
}
