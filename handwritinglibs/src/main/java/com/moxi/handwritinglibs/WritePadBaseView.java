package com.moxi.handwritinglibs;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Xfermode;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.moxi.handwritinglibs.listener.DbPhotoListener;
import com.moxi.handwritinglibs.listener.WriteListener;
import com.moxi.handwritinglibs.model.CodeAndIndex;
import com.moxi.handwritinglibs.utils.DbPhotoLoader;
import com.moxi.handwritinglibs.utils.StringUtils;
import com.mx.mxbase.utils.ToastUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * 手写板基本绘制类
 * Created by 夏君 on 2017/2/8.
 */
public class WritePadBaseView extends View  {
    public WritePadBaseView(Context context) {
        super(context);
        initView();
    }

    public WritePadBaseView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
        ToastUtils.getInstance().initToast(context);

    }

    /**
     * 绘制线的宽度集合，删除线宽为最后一个宽度值
     */
    private List<Integer> paintStrokeWidths = new ArrayList<Integer>();
    /**
     * 绘制线的画笔
     */
    private Paint paintDraw = null;
    /**
     * 擦除画笔
     */
    private Paint paintClear = null;
    /**
     * 绘制路径集合
     */
    private List<Path> paths = new ArrayList<Path>();
    /**
     * 绘制线的索引
     */
    private int DrawIndex = 0;
    /**
     * 是否采用笔头擦除线
     */
    private boolean nibWipe = false;
    /**
     * 当前绘制的图片
     */
    private Bitmap bitmap = null;
    /**
     * 是否有画线
     */
    private boolean isCross = false;

    public void setCross(boolean cross) {
        isCross = cross;
    }
    public boolean getCross() {
        return isCross;
    }


    /**
     * 保存唯一标识
     */
    private CodeAndIndex codeAndIndex;
    /**
     * 手指区别
     */
    private boolean fingerDistinction = false;
    /**
     * Y轴移动距离
     */
    private int scroolToY = 0;
    private boolean isNowDraw = true;
    private WriteListener listener;
    public Handler mHandler;
    private ArrayList<Point> drawPoints=new ArrayList<Point>();

    public void setFingerDistinction(boolean fingerDistinction) {
        this.fingerDistinction = fingerDistinction;
    }

public Bitmap getBaseBitmap(){
    return bitmap;
}
    /**
     * 设置无效点击监听
     *
     * @param listener
     */
    public void setListener(WriteListener listener) {
        this.listener = listener;
    }

    /**
     * 当前是否可以绘制
     *
     * @param nowDraw
     */
    public void setNowDraw(boolean nowDraw) {
        isNowDraw = nowDraw;
    }


    public void recycleBitmap() {
        StringUtils.recycleBitmap(bitmap);
    }

    public void setCodeAndIndex(CodeAndIndex codeAndIndex) {
        this.codeAndIndex = codeAndIndex;
    }

    private static class MyHandler extends Handler {

        WeakReference<WritePadBaseView> mReference = null;

        MyHandler(WritePadBaseView activity) {
            this.mReference = new WeakReference<WritePadBaseView>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            WritePadBaseView outer = mReference.get();
            if (outer == null) {
                return;
            }
            outer.handleMessage(msg);
        }
    }
    private int i=0;
    public void handleMessage(Message msg) {
        if (this.getVisibility() != VISIBLE) {
            mHandler.removeCallbacksAndMessages(null);
            return;
        }
        if (msg.what == 1) {
            setleaveScribbleMode();
            if (!isNowDraw && null != listener) {
                listener.onInvallClick();
            }
            i=0;
            mHandler.sendEmptyMessageDelayed(2, 200);
        } else if (msg.what == 2) {
            if (listener!=null)
            listener.onOverTime();
            mHandler.sendEmptyMessageDelayed(2, 1000);
        }
    }

    private void sendOverDrawTime() {
        mHandler.sendEmptyMessageDelayed(1, 5000);
    }

    public void setSaveCode(String saveCode) {
        if (saveCode.equals(codeAndIndex.saveCode)) return;
        //更改缓存里面的图片信息
        DbPhotoLoader.getInstance().addBitmapToLruCache(saveCode, getIndex(), getBitmap(false));
        DbPhotoLoader.getInstance().clearBitmap(getSaveCode(), getIndex());
        codeAndIndex.saveCode = saveCode;
    }

    public String getSaveCode() {
        return null != codeAndIndex ? codeAndIndex.saveCode : null;
    }

    /**
     * 获得信息保存的index值
     *
     * @return
     */
    public int getIndex() {
        return null != codeAndIndex ? codeAndIndex.index : 0;
    }

    public void setIndex(int index) {
        if (null != codeAndIndex) {
            codeAndIndex.index = index;
        }
    }

    /**
     * 获得缓存的code值
     *
     * @return
     */
    public String getChacheCode() {
        return null != codeAndIndex ? codeAndIndex.saveCode + codeAndIndex.index : null;
    }

    /**
     * 设置绘制画笔的宽度
     *
     * @param paintStrokeWidths 绘制线的宽度集合，删除线宽为最后一个宽度值
     */
    public void setPaintStrokeWidths(List<Integer> paintStrokeWidths) {
        this.paintStrokeWidths.clear();
        this.paintStrokeWidths.addAll(paintStrokeWidths);
        initPaint();
    }

    /**
     * 绘制线的索引值
     *
     * @param drawIndex 索引值 对应绘制线的宽度设置 ，默认0为绘制线-1为擦除
     */
    public void setDrawIndex(int drawIndex) {
        if (drawIndex == DrawIndex) return;
//        if (isCross)
//            CurrentBitmap();
        setleaveScribbleMode();
        DrawIndex = drawIndex;
    }

    private void initView() {
        mHandler = new MyHandler(this);
        //关闭硬件加速
        setLayerType(LAYER_TYPE_SOFTWARE, new Paint());
        //初始化绘制paint
        if (paintStrokeWidths.size() == 0) {
            paintStrokeWidths.add(3);
            //删除线宽
            paintStrokeWidths.add(16);
        }
        initPaint();


    }

    private float maxwidth;
    private float maxHeight;
    private float padingLeft;
    private float padingTop;
    private float padingRight;
    private float padingBottom;

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        updateViewMatrix();
        this.maxwidth = getWidth();
        this.maxHeight = getHeight();
        padingLeft = getPaddingLeft();
        padingTop = getPaddingTop();
        padingRight = getPaddingRight();
        padingBottom = getPaddingBottom();
    }

    @Override
    public void scrollTo(int x, int y) {
        super.scrollTo(x, y);
        this.scroolToY = y;
    }

    /**
     * 初始化绘制画笔的宽度
     */
    private void initPaint() {
        if (paintDraw == null) {
            paintDraw = new Paint();
            paintClear = new Paint();

            //初始化绘制线的画笔
            paintDraw.setStrokeJoin(Paint.Join.ROUND);
            paintDraw.setAntiAlias(true); // 去除锯齿
            paintDraw.setStrokeCap(Paint.Cap.ROUND);
            paintDraw.setStyle(Paint.Style.STROKE);
            paintDraw.setColor(Color.BLACK);
            //初始化擦除线的画笔
            paintClear.setAntiAlias(false); // 去除锯齿
            paintClear.setStyle(Paint.Style.STROKE);
            paintClear.setAlpha(0);
            Xfermode xFermode = new PorterDuffXfermode(PorterDuff.Mode.DST_IN);
            paintClear.setXfermode(xFermode);

        }
        paintDraw.setStrokeWidth(paintStrokeWidths.get(0));
        paintClear.setStrokeWidth(paintStrokeWidths.get(paintStrokeWidths.size() - 1));

        resetPath();
    }

    /**
     * 重置绘制路径
     */
    private void resetPath() {
        paths.clear();
        for (int i = 0; i < paintStrokeWidths.size(); i++) {
            paths.add(new Path());
        }
    }

    /**
     * 获得当前保存绘制路径
     *
     * @return
     */
    private Path getPath() {
        int size = paths.size();
        //笔头擦除或选择了删除返回最后一个绘制路径保存path
        if (nibWipe || DrawIndex == -1) return paths.get(size - 1);
        return paths.get(DrawIndex);
    }

    /**
     * 获得画笔绘制线的宽度
     *
     * @return 返回宽度值
     */
    private int getPaintwidth() {
        int size = paintStrokeWidths.size();
        //笔头擦除或选择了删除返回最后一个绘制路径保存path
        if (nibWipe || DrawIndex == -1) return paintStrokeWidths.get(size - 1);
        return paintStrokeWidths.get(DrawIndex);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (bitmap != null) {
            getThisBitmap();
            if (!bitmap.isRecycled())
                canvas.drawBitmap(bitmap, 0, 0, new Paint());
        }
        for (int i = 0; i < (paths.size() - 1); i++) {
            if (paths.get(i).isEmpty()) continue;
            paintDraw.setStrokeWidth(paintStrokeWidths.get(i));
            canvas.drawPath(paths.get(i), paintDraw);
        }
        int size = paintStrokeWidths.size();
        //擦除线绘制
        if (!(paths.get(size - 1)).isEmpty()) {
            paintClear.setStrokeWidth(paintStrokeWidths.get(size - 1));
            canvas.drawPath(paths.get(size - 1), paintClear);
        }
    }

    public void getThisBitmap(){
        if (bitmap.isRecycled() && codeAndIndex != null) {
            //重新加载
            DbPhotoLoader.getInstance().loaderPhoto(codeAndIndex.saveCode, codeAndIndex.index, new DbPhotoListener() {
                @Override
                public void onLoaderSucess(String saveCode, int index, Bitmap bitmap) {
                    WritePadBaseView.this.bitmap = bitmap;
                }
            });
        }
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
        resetPath();
        invalidate();
    }


    private float mLastTouchX = 0;
    private float mLastTouchY = 0;

    private final RectF mDirtyRect = new RectF();

    private float[] mapPoint(float x, float y) {
        x = Math.min(Math.max(0, x), this.getWidth());
        y = Math.min(Math.max(0, y), this.getHeight());

        final int viewLocation[] = {0, 0};
        this.getLocationOnScreen(viewLocation);
        final Matrix viewMatrix = new Matrix();

        float screenPoints[] = {viewLocation[0] + x, viewLocation[1] + y};
        float dst[] = {0, 0};
        viewMatrix.mapPoints(dst, screenPoints);
        return dst;
    }

    private void getDirtyRect(float historicalX, float historicalY) {
        if (historicalX < mDirtyRect.left) {
            mDirtyRect.left = historicalX;
        } else if (historicalX > mDirtyRect.right) {
            mDirtyRect.right = historicalX;
        }
        if (historicalY < mDirtyRect.top) {
            mDirtyRect.top = historicalY;
        } else if (historicalY > mDirtyRect.bottom) {
            mDirtyRect.bottom = historicalY;
        }
    }

    private void resetDirtyRect(float eventX, float eventY) {
        mDirtyRect.left = Math.min(mLastTouchX, eventX);
        mDirtyRect.right = Math.max(mLastTouchX, eventX);
        mDirtyRect.top = Math.min(mLastTouchY, eventY);
        mDirtyRect.bottom = Math.max(mLastTouchY, eventY);
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        mHandler.removeCallbacksAndMessages(null);
        if (visibility == VISIBLE) {
            sendOverDrawTime();
        }
    }

    /**
     * 擦除
     */
//    private boolean isDraaw = false;
    private void CurrentBitmap() {
        Bitmap bitmap1 = bitmap;
        bitmap = getBitmap(false);
        if (codeAndIndex != null) {
            clearLauch(bitmap1);
        }else {
            StringUtils.recycleBitmap(bitmap1);
        }
        resetPath();
        invalidate();
    }
    public void clearLauch(Bitmap bitmap1){
        DbPhotoLoader.getInstance().addBitmapToLruCache(codeAndIndex.saveCode, codeAndIndex.index,bitmap);
    }

    public void clearScreen() {
        Bitmap bitmap1 = bitmap;
        bitmap = null;
        this.isCross = true;
        resetPath();
        if (codeAndIndex != null) {
            DbPhotoLoader.getInstance().clearBitmap(codeAndIndex.saveCode, codeAndIndex.index);
        }else {
            StringUtils.recycleBitmap(bitmap1);
        }
        invalidate();
    }

    public Bitmap getBitmap(boolean extral) {
        if (!isCross) {
            return null;
        }
        int w = getWidth();
        int h = getHeight();
        Bitmap bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmp);
        c.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        /** 如果不设置canvas画布为白色，则生成透明 */

        layout(getLeft(), getTop(), getRight(), getBottom());
        draw(c);
        if (extral) {
            isCross = false;
        }
        return bmp;
    }

    /**
     * 获得绘制的图片
     *
     * @return
     */
    public Bitmap getBitmap() {
        return getBitmap(true);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mHandler.removeCallbacksAndMessages(null);
        setleaveScribbleMode();
        recycleBitmap();
    }

    private Matrix viewMatrix;

    public void updateViewMatrix() {
        int viewPosition[] = {0, 0};
        getLocationOnScreen(viewPosition);
        viewMatrix = new Matrix();
        viewMatrix.postTranslate(-viewPosition[0], -viewPosition[1]);
    }

    public void onResume() {
        if (this.getVisibility() != VISIBLE) return;
        setNowDraw(true);
        setEnterScribble();
        sendOverDrawTime();
    }

    public void onPause() {
        setleaveScribbleMode();
    }

    public void onstop() {
        if (this.getVisibility() != VISIBLE) return;
        mHandler.removeCallbacksAndMessages(null);
        setleaveScribbleMode();
        setNowDraw(false);
    }

    final float pressure = 1;
    final float size = 1;
    boolean begin = false;
    private boolean isDown = false;
    private boolean judgeDraw = false;

    public void drawRawData(List<Point> drawPoints,Paint paint){

    }

    /**
     * 关闭浮层显示
     */
    public void setleaveScribbleMode() {
        if (this.getVisibility() != VISIBLE) return;
        if (judgeDraw) {
            invalidate();
            judgeDraw = false;
        }
    }

    private void setEnterScribble() {
        if (this.getVisibility() != VISIBLE) return;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (this.getVisibility() != VISIBLE) return false;
        if (!isNowDraw) {
            setleaveScribbleMode();
            return false;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                int iDeviceId = event.getDeviceId();
                if (iDeviceId == 3 && fingerDistinction) {
                    return false;
                }
                isDown = true;
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                break;
            default:
                break;
        }
        return true;
    }

    private int paintSize;
    private int middleSize;
    private Path path;

}

