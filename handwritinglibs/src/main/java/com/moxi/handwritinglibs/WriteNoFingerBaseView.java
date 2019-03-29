package com.moxi.handwritinglibs;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Xfermode;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.xrz.Xrzhandwrite;

import com.moxi.handwritinglibs.listener.DbPhotoListener;
import com.moxi.handwritinglibs.listener.UpLogInformationInterface;
import com.moxi.handwritinglibs.model.CodeAndIndex;
import com.moxi.handwritinglibs.model.PointModel;
import com.moxi.handwritinglibs.utils.DbPhotoLoader;
import com.moxi.handwritinglibs.utils.StringUtils;
import com.mx.mxbase.constant.APPLog;
import com.mx.mxbase.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 屏蔽掉手指的的手写基础view
 * Created by xj on 2017/6/19.
 */

public class WriteNoFingerBaseView extends View {
    public WriteNoFingerBaseView(Context context) {
        super(context);
        initView();
    }

    public WriteNoFingerBaseView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
        ToastUtils.getInstance().initToast(context);
    }

    /**
     * 绘制线的画笔
     */
    private Paint paintDraw = null;
    /**
     * 绘制线宽
     */
    private int drawLineWidth = 2;
    /**
     * 擦除画笔
     */
    private Paint paintClear = null;
    /**
     * 擦出线宽
     */
    private int clearLineWidth = 10;
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
    /**
     * 启动橡皮擦功能-
     */
    private boolean isNibWipe = false;
    /**
     * 保存唯一标识
     */
    private CodeAndIndex codeAndIndex;
    private Path drawLinePath;
    private Path ClearLinePath;
    final float size = 1;
    final float pressure = 1;
    /**
     * 是否允许绘制
     */
    public boolean canDraw = true;
    Handler handler = new Handler();
    /**
     * 绘制已经完毕
     */
    private boolean drawUp = true;
    /**
     * 上次绘制线风格
     */
    private boolean lastDrawStyle;
    /**
     * 撤销数据集合
     */
    public List<List<PointModel>> historyPoints = new ArrayList<List<PointModel>>();
    /**
     * 恢复数据集合
     */
    public List<List<PointModel>> recoverPoints = new ArrayList<List<PointModel>>();
    private List<PointModel> currentPoints;

    private UpLogInformationInterface upListener=null;

    public void setUpListener(UpLogInformationInterface upListener) {
        this.upListener = upListener;
    }
    public UpLogInformationInterface getUpListener(){
        return upListener;
    }


    private void initView() {
        //关闭硬件加速
        setLayerType(LAYER_TYPE_SOFTWARE, new Paint());
        //临时绘制

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

        paintDraw.setStrokeWidth(drawLineWidth);
        paintClear.setStrokeWidth(clearLineWidth);

        drawLinePath = new Path();
        ClearLinePath = new Path();

    }

    public boolean isDrawUp() {
        return drawUp;
    }

    public void setCodeAndIndex(CodeAndIndex codeAndIndex) {
        this.codeAndIndex = codeAndIndex;
    }

    public void setDrawLineWidth(int drawLineWidth) {
        if (drawLineWidth == this.drawLineWidth) return;
        setCacheBitmap();
        this.drawLineWidth = drawLineWidth;
        paintDraw.setStrokeWidth(this.drawLineWidth);
    }

    public void setClearLineWidth(int clearLineWidth) {
        if (clearLineWidth == this.clearLineWidth) return;
        setCacheBitmap();
        this.clearLineWidth = clearLineWidth;
        paintClear.setStrokeWidth(this.clearLineWidth);
    }

    public void setBitmap(Bitmap bitmap) {
        isCross = bitmap == null;
        this.bitmap = bitmap;
        canDraw = true;
        invalidate();
    }

    public void setCross(boolean isCross) {
        this.isCross = isCross;
    }

    public void setCanDraw(boolean canDraw,int index) {
        this.canDraw = canDraw;
        handler.removeCallbacksAndMessages(null);
        if (canDraw) {
            setDelayEnterScribble();
        } else {
            CurrentBitmap(1);
          setleaveScribble();
        }
    }

    public int getIndex() {
        return codeAndIndex.index;
    }

    public void setNibWipe(boolean nibWipe) {
        isNibWipe = nibWipe;
        setCacheBitmap();
    }

    public boolean isNibWipe() {
        return isNibWipe;
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
        this.maxwidth = getWidth();
        this.maxHeight = getHeight();
        padingLeft = getPaddingLeft();
        padingTop = getPaddingTop();
        padingRight = getPaddingRight();
        padingBottom = getPaddingBottom();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!canDraw) return false;
        if(event.getAction()==MotionEvent.ACTION_DOWN) {
            int iDeviceId = event.getDeviceId();
            int tooolType = event.getToolType(0);
            if (iDeviceId == 2 || tooolType == 1) return false;//手指接触屏幕
            nibWipe = tooolType == 4 && iDeviceId == 1;//用笔头檫线
            setEnterScribble();
        }

        boolean isDrawLine = !(nibWipe || isNibWipe);

        int middleSize = getPaintSize() / 2;
        float eventX = event.getX();
        float eventY = event.getY();
//        if (isDrawLine) {
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
//        }
        /**
         * 保存绘制路径以及划线风格
         */
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                drawUp = false;
                recoverPoints.clear();

                currentPoints = new ArrayList<PointModel>();
                currentPoints.add(new PointModel(isDrawLine, MotionEvent.ACTION_DOWN, eventX, eventY));
                isCross = true;
//                if (!isDrawLine) {
//                    setleaveScribbleMode(false,4);
//                }
//                if ((isNibWipe == false) && lastDrawStyle != isDrawLine) {
//                    setCacheBitmap();
//                }
//                getPath().moveTo(eventX, eventY);
                mLastTouchX = eventX;
                mLastTouchY = eventY;
                if (isDrawLine) {
//                    float dst[] = mapPoint(eventX, eventY);
//                    EpdController.startStroke(getPaintSize(), dst[0], dst[1], pressure, size, System.currentTimeMillis());
                    Xrzhandwrite.xrzstartStroke(middleSize*2, eventX, eventY, 1, 1,  System.currentTimeMillis());
                }
            }
            break;
            case MotionEvent.ACTION_MOVE:
                DrawLineUp(event, eventX, eventY, isDrawLine, middleSize, false);
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                DrawLineUp(event, eventX, eventY, isDrawLine, middleSize, true);
                lastDrawStyle = isDrawLine;
//                if (!isDrawLine) {//
//                    CurrentBitmap(2);
//                    setEnterScribble();
//                    nibWipe = false;
//                } else {
//                    if (isDrawLine) {
//                        float dst[] = mapPoint(eventX, eventY);
//                        EpdController.finishStroke(getPaintSize(), dst[0], dst[1], pressure, size, System.currentTimeMillis());
//                    }
//                }
                drawUp = true;
                historyPoints.add(currentPoints);
                break;
        }
        return true;
    }

    /**
     * 当前是否可以返回
     */
    private boolean isBack = true;

    /**
     * 撤销/恢复
     *
     * @param back 撤销为true
     */
    public void backLastDraw(final boolean back) {
        if (back) {
            if (historyPoints.size() <= 0 && isBack) return;
        } else {
            if (recoverPoints.size() <= 0 && isBack) return;
        }
        if (codeAndIndex != null) {
            isBack = false;
            setCanDraw(false,30);
            DbPhotoLoader.getInstance().loaderPhoto(codeAndIndex.saveCode, codeAndIndex.index, new DbPhotoListener() {
                @Override
                public void onLoaderSucess(String saveCodes, int index, Bitmap bitmap) {

                    if (back) {
                        if (historyPoints.size() <= 0 && isBack) return;
                    } else {
                        if (recoverPoints.size() <= 0 && isBack) return;
                    }

                    WriteNoFingerBaseView.this.bitmap = bitmap;

                    if (back) {
                        recoverPoints.add(historyPoints.remove(historyPoints.size() - 1));
                    } else {
                        historyPoints.add(recoverPoints.remove(recoverPoints.size() - 1));
                    }
                    resetPath();
                    Path path = null;
                    for (List<PointModel> historys : historyPoints) {
                        for (PointModel model : historys) {
                            if (path == null) {
                                path = model.isDrawLine ? drawLinePath : ClearLinePath;
                            }
                            switch (model.moveEvent) {
                                case MotionEvent.ACTION_DOWN:
                                    path.moveTo(model.eventX, model.eventY);
                                    break;
                                case MotionEvent.ACTION_MOVE:
                                    path.lineTo(model.eventX, model.eventY);
                                    break;
                                case MotionEvent.ACTION_UP:
                                    path.lineTo(model.eventX, model.eventY);
                                    break;
                                default:
                                    break;
                            }
                        }
                    }
                    CurrentBitmap(3);
                    setCanDraw(true,31);
                    isBack = true;
                }

            });
        } else {
            return;
        }


    }

    private void DrawLineUp(MotionEvent event, float eventX, float eventY, boolean isDrawLine, int middleSize, boolean end) {
        if (end && eventX == 0 && eventX == 0) {
            eventX = mLastTouchX;
            eventY = mLastTouchY;
        }
        APPLog.e("DrawLineUp eventXL:"+eventX+"   eventY:"+eventY);
//            resetDirtyRect(eventX, eventY);
//        currentPoints.add(new PointModel(isDrawLine, MotionEvent.ACTION_MOVE, eventX, eventY));
//        getPath().lineTo(eventX, eventY);
//
//        int paintSize = getPaintSize();
//        if (!isDrawLine) {
//            postInvalidate((int) (mDirtyRect.left - paintSize),
//                    (int) (mDirtyRect.top - paintSize),
//                    (int) (mDirtyRect.right + paintSize),
//                    (int) (mDirtyRect.bottom + paintSize));
//        }
        Xrzhandwrite.xrzaddStrokePoint(middleSize*2, eventX, eventY, 1, 1,  System.currentTimeMillis());
        mLastTouchX = eventX;
        mLastTouchY = eventY;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (bitmap != null) {
            if (bitmap.isRecycled() && codeAndIndex != null) {
                //重新加载
                DbPhotoLoader.getInstance().loaderPhoto(codeAndIndex.saveCode, codeAndIndex.index, new DbPhotoListener() {
                    @Override
                    public void onLoaderSucess(String saveCode, int index, Bitmap bitmap) {
                       if (upListener!=null)upListener.onUpLog("onDraw-DbPhotoLoader-canDraw="+canDraw,System.currentTimeMillis());
                        WriteNoFingerBaseView.this.bitmap = bitmap;
                        invalidate();
                        setleaveScribbleMode(true,10);
                    }
                });
            }
            if (!bitmap.isRecycled())
                canvas.drawBitmap(bitmap, 0, 0, new Paint());
        }
        if (!drawLinePath.isEmpty()) {
            canvas.drawPath(drawLinePath, paintDraw);
        }
        //擦除线绘制
        if (!ClearLinePath.isEmpty()) {
            canvas.drawPath(ClearLinePath, paintClear);
        }
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

    private float mLastTouchX = 0;
    private float mLastTouchY = 0;

    private final RectF mDirtyRect = new RectF();

    /**
     * 获得当前保存绘制路径
     *
     * @return
     */
    private Path getPath() {
        //笔头擦除或选择了删除返回最后一个绘制路径保存path
        if (nibWipe || isNibWipe) return ClearLinePath;
        return drawLinePath;
    }

    /**
     * 获得当绘制线宽
     *
     * @return
     */
    private int getPaintSize() {
        //笔头擦除或选择了删除返回最后一个绘制路径保存path
        if (nibWipe || isNibWipe) return clearLineWidth;
        return drawLineWidth;
    }

    private void CurrentBitmap(int position) {
//        Bitmap bitmap1 = bitmap;
        Bitmap mbitmap = getBitmap(false, position);
        if (mbitmap != null) {
            if (codeAndIndex != null) {
//                DbPhotoLoader.getInstance().clearBitmap(codeAndIndex.saveCode, codeAndIndex.index);
//                StringUtils.recycleBitmap(bitmap);
                //因为用到撤销和返回上步功能所以不能保存
//                DbPhotoLoader.getInstance().addBitmapToLruCache(codeAndIndex.saveCode, codeAndIndex.index,mbitmap);
            }else {
                StringUtils.recycleBitmap(bitmap);
            }
            bitmap = mbitmap;
        }
        resetPath();
        invalidate();
    }

    /**
     * 设置当前图片进入缓存且清空绘制线历史记录
     */
    public void setCacheBitmap() {
        if (codeAndIndex != null) {
            DbPhotoLoader.getInstance().addBitmapToLruCache(codeAndIndex.saveCode, codeAndIndex.index, bitmap);
        }
        historyPoints.clear();
    }

    public void clearScreen() {
        Bitmap bitmap1 = bitmap;
        bitmap = null;
        this.isCross = true;
        resetPath();
        if (codeAndIndex != null) {
            DbPhotoLoader.getInstance().clearBitmap(codeAndIndex.saveCode, codeAndIndex.index);
        } else {
            StringUtils.recycleBitmap(bitmap1);
        }
        setCacheBitmap();
        invalidate();
    }

    public Bitmap getBitmap(boolean extral, int position) {
        if (!isCross) {
            return null;
        }
        int w = getWidth();
        int h = getHeight();
        if (w==0||h==0)return null;
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
     * 重置绘制路径
     */
    private void resetPath() {
        drawLinePath.close();
        ClearLinePath.close();
        drawLinePath = new Path();
        ClearLinePath = new Path();
    }


    /**
     * 关闭浮层显示
     */
    public void setleaveScribbleMode(boolean setent,int position) {
        handler.removeCallbacksAndMessages(null);
        CurrentBitmap(4);
        setleaveScribble();
        if (setent) {
            canDraw = false;
            setDelayEnterScribble();
        }
    }

    public void setNoBitmapLeaveScribble(boolean setent) {
        handler.removeCallbacksAndMessages(null);
        setleaveScribble();
        if (setent) {
            setDelayEnterScribble();
        }
    }

    public void setDelayEnterScribble() {
        handler.removeCallbacksAndMessages(null);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                setEnterScribble();
                canDraw = true;
            }
        }, 100);
    }

    private void setEnterScribble() {
        if(Xrzhandwrite.xrzGethandwritestate()==0) Xrzhandwrite.xrzEnterOrleave(1);
    }
    private void setleaveScribble() {
        Xrzhandwrite.xrzEnterOrleave(0);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        setleaveScribbleMode(false,5);
    }

}
