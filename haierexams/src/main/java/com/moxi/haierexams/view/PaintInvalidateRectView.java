package com.moxi.haierexams.view;

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
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.moxi.haierexams.utils.LocationPhotoInstance;
import com.mx.mxbase.utils.Log;
import com.mx.mxbase.utils.StringUtils;


/**
 * Created by Administrator on 2016/8/5.
 */
public class PaintInvalidateRectView extends View {
    /**
     * 是否有画线
     */
    private boolean isCross = false;
    private String filePath = null;//图片文件路径

    public PaintInvalidateRectView(Context context, AttributeSet attrs,
                                   int defStyle) {
        super(context, attrs, defStyle);
        initPaintView();
    }

    public PaintInvalidateRectView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPaintView();
    }

    public PaintInvalidateRectView(Context context) {
        super(context);
        initPaintView();
    }

    /**
     * 保存清除线
     */
//private List<PaintClearLineBeen> paintClearLines=new ArrayList<PaintClearLineBeen>();
    private void initPaintView() {
        //关闭硬件加速
        setLayerType(LAYER_TYPE_SOFTWARE, null);

        mPaint1.setStrokeJoin(Paint.Join.ROUND);
        mPaint2.setStrokeJoin(Paint.Join.ROUND);
        mPaint3.setStrokeJoin(Paint.Join.ROUND);
        mPaint4.setStrokeJoin(Paint.Join.ROUND);
        mPaint5.setStrokeJoin(Paint.Join.ROUND);

        mPaint1.setStrokeMiter(180.0f);
        mPaint2.setStrokeMiter(180.0f);
        mPaint3.setStrokeMiter(180.0f);
        mPaint4.setStrokeMiter(180.0f);
        mPaint5.setStrokeMiter(180.0f);

        mPaint1.setAntiAlias(false); // 去除锯齿
        mPaint1.setStrokeCap(Paint.Cap.ROUND);
        mPaint1.setStrokeWidth(3);
        mPaint1.setStyle(Paint.Style.STROKE);
        mPaint1.setColor(Color.BLUE);

        mPaint2.setAntiAlias(true); // 去除锯齿
        mPaint2.setStrokeWidth(3);
        mPaint2.setStyle(Paint.Style.STROKE);
        mPaint2.setColor(Color.BLUE);

        mPaint3.setAntiAlias(false); // 去除锯齿
        mPaint3.setStrokeWidth(5);
        mPaint3.setStyle(Paint.Style.STROKE);
        mPaint3.setColor(Color.BLUE);

        mPaint4.setAntiAlias(false); // 去除锯齿
        mPaint4.setStrokeWidth(7);
        mPaint4.setStyle(Paint.Style.STROKE);
        mPaint4.setColor(Color.BLUE);

        mPaint5.setAntiAlias(false); // 擦除
        mPaint5.setStrokeWidth(16);
        mPaint5.setStyle(Paint.Style.STROKE);
        mPaint5.setColor(Color.TRANSPARENT);
        Xfermode xFermode = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);
        mPaint5.setXfermode(xFermode);

//        mPaint6.setAntiAlias(false); // 擦除
//        mPaint6.setStrokeWidth(16);
//        mPaint6.setStyle(Paint.Style.STROKE);
//        mPaint6.setColor(Color.TRANSPARENT);

        PaintInvalidateRectView.this.setFocusable(true);
        PaintInvalidateRectView.this.setFocusableInTouchMode(true);
    }

    private int paintStyle = 0;

    private int offest = 0;

    public boolean setPaint(int style) {
        if (paintStyle == style) return false;
        paintStyle = style;
//        offest=getPaintSize();
        return true;
    }

    public Path getmPath() {
        int swi = paintStyle;
        if (isClearLine) swi = 4;
        switch (swi) {
            case 0:
                return mPath1;
            case 1:
                return mPath2;
            case 2:
                return mPath3;
            case 3:
                return mPath4;
            case 4:
                return mPath5;
            default:
                break;
        }
        return mPath1;
    }

    private int getPaintSize() {
        int swi = 0;
        if (isClearLine) swi = 4;
        switch (paintStyle) {
            case 0:
                swi = 3;
                break;
            case 1:
                swi = 3;
                break;
            case 2:
                swi = 5;
                break;
            case 3:
                swi = 7;
                break;
            case 4:
                swi = 16;
                break;
            default:
                break;
        }
        return swi;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (bitmap != null) {
            Paint paint = new Paint();
            try {
                canvas.drawBitmap(bitmap, 0, 0, paint);
            } catch (Exception e) {
                LocationPhotoInstance.getInstance().deletePhoto(filePath);
                LocationPhotoInstance.getInstance().loadImage(filePath, new LocationPhotoInstance.LoadPhotoListener() {
                    @Override
                    public void onLoadSucess(Bitmap bitmap, String path) {
                        PaintInvalidateRectView.this.bitmap = bitmap;
                        invalidate();
                    }
                });
            }

        }
        canvas.drawPath(mPath1, mPaint1);
        canvas.drawPath(mPath2, mPaint2);
        canvas.drawPath(mPath3, mPaint3);
        canvas.drawPath(mPath4, mPaint4);
        canvas.drawPath(mPath5, mPaint5);
    }

    private boolean isClearLine = false;

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
//        int iDeviceId = event.getDeviceId();
        int tooolType = event.getToolType(0);
//        if (iDeviceId != MotionEvent.TOOL_TYPE_FINGER) {
//            return false;
//        }
        if (tooolType == 4) {
            isClearLine = true;
        }
        int iDeviceId = event.getDeviceId();
        Log.e("-----",iDeviceId+"");
        if (iDeviceId == MotionEvent.TOOL_TYPE_FINGER || iDeviceId >= MotionEvent.TOOL_TYPE_ERASER) {
            onTouchEvent(event);
            return true;
        } else {
            return false;
        }
//        onTouchEvent(event);
//        return true;
    }

    /**
     * 是否绘制清楚线
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float eventX = event.getX() - offest;
        float eventY = event.getY() - offest;
        /**
         * 保存绘制路径以及划线风格
         */
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                getmPath().moveTo(eventX, eventY);
                mLastTouchX = eventX;
                mLastTouchY = eventY;
            }
            break;
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP: {
                isCross = true;
                resetDirtyRect(eventX, eventY);
                int historySize = event.getHistorySize();
                for (int i = 0; i < historySize; i++) {
                    float historicalX = event.getHistoricalX(i) - offest;
                    float historicalY = event.getHistoricalY(i) - offest;
                    getDirtyRect(historicalX, historicalY);
                    getmPath().lineTo(historicalX, historicalY);
                }
                getmPath().lineTo(eventX, eventY);

                int paintSize = getPaintSize();
                postInvalidate((int) (mDirtyRect.left - paintSize),
                        (int) (mDirtyRect.top - paintSize),
                        (int) (mDirtyRect.right + paintSize),
                        (int) (mDirtyRect.bottom + paintSize));

                mLastTouchX = eventX;
                mLastTouchY = eventY;

                if ((event.getAction() == MotionEvent.ACTION_UP) && (isClearLine || paintStyle == 4)) {//
                    CurrentBitmap();
                    isClearLine = false;
                }
            }
            break;
        }
        return true;
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
    private Paint mPaint1 = new Paint();
    private Paint mPaint2 = new Paint();
    private Paint mPaint3 = new Paint();
    private Paint mPaint4 = new Paint();
    private Paint mPaint5 = new Paint();
//    private Paint mPaint6 = new Paint();

    private Path mPath1 = new Path();
    private Path mPath2 = new Path();
    private Path mPath3 = new Path();
    private Path mPath4 = new Path();
    private Path mPath5 = new Path();


    private Bitmap bitmap = null;

    public void setBitmap(Bitmap bitmap) {
//        if (this.bitmap != null) {
//            StringUtils.recycleBitmap(this.bitmap);
//        }
        this.bitmap = bitmap;
        invalidate();
    }

    private void CurrentBitmap() {
        Bitmap bitmap1 = bitmap;
        bitmap = getBitmap(false);
        if (bitmap1 != null) {
            StringUtils.recycleBitmap(bitmap1);
        }

        clearPath();
        invalidate();
    }

    private void clearPath() {
        mPath1 = new Path();
        mPath2 = new Path();
        mPath3 = new Path();
        mPath4 = new Path();
        mPath5 = new Path();
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

        layout(0, 0, w, h);
        draw(c);
        if (extral) {
            isCross = false;
        }
        return bmp;
    }

    public Bitmap getBitmap() {
        return getBitmap(true);
    }

    /**
     * 初始化显示绘制的图形
     *
     * @param bitmap
     */
    public void initBitmap(Bitmap bitmap, String filePath) {
        clearPath();
        this.filePath = filePath;
        setBitmap(bitmap);

    }
}