package com.moxi.handwritinglibs.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.PathEffect;
import android.graphics.Rect;

import com.moxi.handwritinglibs.R;
import com.mx.mxbase.constant.APPLog;
import com.mx.mxbase.utils.LocationImageLoader;

import java.io.File;

/**
 * Created by xj on 2017/6/27.
 */

public class PaintBackUtils {
    private int height = 0;
    private int width = 0;
    private Context context;
    public void setWidthAndHeight(Context context,int height, int width) {
        this.height = height;
        this.width = width;
        this.context=context;
    }

    public void DrawView(final Canvas canvas, int drawStyle, String filepath) {
        if (drawStyle==-1&&(filepath==null||filepath.equals(""))){
            drawStyle=0;
        }
        if (!com.mx.mxbase.utils.StringUtils.isNull(filepath)&&!(new File(filepath)).exists()){
            drawStyle=0;
        }
        switch (drawStyle) {
            case -1://加载本地图片
                APPLog.e("获取图片中");
//                LocationPhotoLoder.getInstance().loadImage(filepath, new Sucess() {
//                    @Override
//                    public void setSucess(Bitmap bitmap, boolean isS) {
//                        APPLog.e("获取图片返回");
//                        canvas.drawBitmap(bitmap, 0, 0, new Paint());
//                    }
//                }, WindowsUtils.WritedrawWidth, WindowsUtils.WritedrawHeight, true);
                if (context!=null) {

                    Bitmap bitmap=LocationImageLoader.getInstance().getBitmapFromLruCache(filepath);
                    APPLog.e("time="+System.currentTimeMillis(),"bitmap is null ="+bitmap==null);
                    if (bitmap==null||bitmap.isRecycled()||bitmap.getWidth()!=width){
                        bitmap=BitmapFactory.decodeFile(filepath);
                        LocationImageLoader.getInstance().addBitmapToLruCache(filepath,bitmap);
                    }
                    canvas.drawBitmap(bitmap, 0, 0, new Paint());
//                    StringUtils.recycleBitmap(bitmap);
                }
                break;
            case 0:
                break;
            case 1:
                initLinesBack(canvas, 14, 11);
                break;
            case 2:
                initNumsLines(canvas, 14);
                break;
            case 3:
                initLineAndRect(canvas, 10);
                break;
            case 4:
                initTitleLinesBack(canvas, 10);
                break;
            case 5:
                initLineBack(canvas, 14);
                break;
            case 6:
                initNoteBack(canvas, 5);
                break;
            case 7:
                initChoiceLine(canvas, 14);
                break;
            case 8:
                initLinesBack(canvas, 30, 25);
                break;
            case 9:
                initFieldLineBack(canvas, 8);
                break;
            case 10:
                initIntersected(canvas);
                break;
            case 11:
                initVerticalLinesBack(canvas, 11);
                break;
            case 12:
                initDrawCircle(canvas, 27);
                break;
            case 13:
                initFourLine(canvas, 9);
                break;
            case 14:
                if (context!=null) {
                    Bitmap bitmap=BitmapFactory.decodeResource(context.getResources(), R.mipmap.day_plan);
                    canvas.drawBitmap(bitmap, 0, 0, new Paint());
                    StringUtils.recycleBitmap(bitmap);
                }
                break;
            case 15:
                if (context!=null) {
                    Bitmap bitmap=BitmapFactory.decodeResource(context.getResources(), R.mipmap.week_paln);
                    canvas.drawBitmap(bitmap, 0, 0, new Paint());
                    StringUtils.recycleBitmap(bitmap);
                }
                break;
            case 16:
                if (context!=null) {
                    Bitmap bitmap=BitmapFactory.decodeResource(context.getResources(), R.mipmap.month_paln);
                    canvas.drawBitmap(bitmap, 0, 0, new Paint());
                    StringUtils.recycleBitmap(bitmap);
                }
                break;
            default:
                break;
        }
    }

    /**
     * 带有选择框的横线
     *
     * @param canvas
     * @param rows
     */
    private void initChoiceLine(Canvas canvas, int rows) {
        initPaint();
        float padding = 10;
        float lineHeight = (height - padding) / rows;
        float startLine = lineHeight;

        //绘制横线
        for (int i = 0; i <= rows; i++) {
            float y = startLine + lineHeight * i;
            int left = (int) padding + 5;
            int top = (int) y - 20;
            int right = (int) padding + 20;
            int bottom = (int) y - 5;
            Rect rect = new Rect(left, top, right, bottom);
            canvas.drawRect(rect, linePaint);
            canvas.drawLine(padding, y, width - padding, y, linePaint);
        }
    }

    /**
     * 绘制带有数字的横线
     *
     * @param canvas
     */
    private void initNumsLines(Canvas canvas, int rows) {
        initPaint();
        float padding = 10;
        float lineHeight = (height - padding) / rows;
        float startLine = lineHeight;

        Paint linePaint = new Paint();
        linePaint.setAntiAlias(true); // 去除锯齿
        linePaint.setStrokeWidth(1);
        linePaint.setTextSize(16);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setColor(Color.BLACK);

        //绘制横线
        for (int i = 0; i < rows; i++) {
            float y = startLine + lineHeight * i;
            canvas.drawCircle(padding + 15, y - 15, 12, linePaint);
            if (i >= 9) {
                canvas.drawText((i + 1) + "", padding + 6, y - 8, linePaint);
            } else {
                canvas.drawText((i + 1) + "", padding + 10, y - 8, linePaint);
            }
            canvas.drawLine(padding, y, width - padding, y, linePaint);
        }
    }

    /**
     * 画矩形和横线
     *
     * @param canvas
     * @param lines
     */
    private void initLineAndRect(Canvas canvas, int lines) {
        initPaint();
        float padding = 10;
        float highRect = (height - padding * 2) / lines;

        int left = (int) padding;
        int top = (int) padding;
        int right = (int) (width - padding);
        int bottom = (int) (highRect * 3 + padding);
        Rect rect = new Rect(left, top, right, bottom);
        canvas.drawRect(rect, linePaint);

        for (int i = 3; i <= lines; i++) {
            float y = highRect * (i + 1) + padding;
            canvas.drawLine(padding, y, width - padding, y, linePaint);
        }
    }

    /**
     * 田字格
     *
     * @param canvas 绘制画布
     */

    public void initFieldLineBack(Canvas canvas, int cols) {
        initPaint();
        float padding = 10;
        int widthSave = width % cols;
        float trueWidthPadding = padding + widthSave / 2;
        int innerPadding = 6;
        //每个格子宽度
        int fieldWidth = (int) ((width - trueWidthPadding * 2) / cols);
        //分割线高度
        int splitHeight = (height - width) / cols;
        //绘制外边框
        for (int i = 0; i < 8; i++) {
            int left = (int) trueWidthPadding;
            int top = (int) (splitHeight * (i + 1) + fieldWidth * i + padding);
            int right = (int) (width - trueWidthPadding);
            int bottom = top + fieldWidth;
            Rect rect = new Rect(left, top, right, bottom);
            canvas.drawRect(rect, linePaint);
            for (int j = 0; j < 9; j++) {
                int leftj = (int) (trueWidthPadding + j * fieldWidth);
                int topj = top;
                int rightj = leftj;
                int bottomj = top + fieldWidth;
                if (j != 8) {
                    canvas.drawLine(leftj, topj, rightj, bottomj, linePaint);
                    //虚线上面那条虚线
                    canvas.drawLine(leftj + innerPadding, topj + innerPadding, rightj + fieldWidth - innerPadding, topj + innerPadding, xuPaint);
                    //虚线下面那条虚线
                    canvas.drawLine(leftj + innerPadding, bottomj - innerPadding, rightj + fieldWidth - innerPadding, bottomj - innerPadding, xuPaint);
                }
                //虚线中间那条虚线
                canvas.drawLine(leftj - fieldWidth / 2, topj + innerPadding, rightj - fieldWidth / 2, bottomj - innerPadding, xuPaint);
                //虚线左边那条虚线
                canvas.drawLine(leftj - fieldWidth + innerPadding, topj + innerPadding, rightj - fieldWidth + innerPadding, bottomj - innerPadding, xuPaint);
                if (j != 0) {
                    //虚线右边那条虚线
                    canvas.drawLine(leftj - innerPadding, topj + innerPadding, rightj - innerPadding, bottomj - innerPadding, xuPaint);
                }
            }
            canvas.drawLine(left + innerPadding, top + fieldWidth / 2, right - innerPadding, bottom - fieldWidth / 2, xuPaint);
        }
    }


    /**
     * 米字格
     *
     * @param canvas 绘制画布
     */
    public void initIntersected(Canvas canvas) {
        initPaint();
        int pading = 10;
        //每个格子宽度
        int fieldWidht = (width - pading * 2) / 8;
        //分割线高度
        int splitHeight = (height - width) / 8;
        //绘制外边框
        for (int i = 0; i < 8; i++) {
            int left = pading;
            int top = splitHeight * (i + 1) + fieldWidht * i;
            int right = width - pading;
            int bottom = top + fieldWidht;
            Rect rect = new Rect(left, top, right, bottom);
            canvas.drawRect(rect, linePaint);
            for (int j = 1; j < 9; j++) {
                int leftj = pading + j * fieldWidht;
                int topj = top;
                int rightj = leftj;
                int bottomj = top + fieldWidht;
                if (j != 8)
                    canvas.drawLine(leftj, topj, rightj, bottomj, linePaint);
                //虚线绘制
                canvas.drawLine(leftj - fieldWidht / 2, topj, rightj - fieldWidht / 2, bottomj, xuPaint);
                canvas.drawLine(leftj - fieldWidht, topj, rightj, bottomj, xuPaint);
                canvas.drawLine(leftj, topj, rightj - fieldWidht, bottomj, xuPaint);
            }
            canvas.drawLine(left, top + fieldWidht / 2, right, bottom - fieldWidht / 2, xuPaint);
        }
    }

    private Paint xuPaint;
    private Paint linePaint;

    //初始化画笔
    private void initPaint() {
        if (xuPaint == null) {
            float heightXu = 1f;
            //绘制虚线
            xuPaint = new Paint();
            xuPaint.setColor(Color.BLACK);
            xuPaint.setStrokeWidth(heightXu);
            xuPaint.setAntiAlias(true);
            xuPaint.setStrokeWidth(0.5f);
            xuPaint.setStyle(Paint.Style.STROKE);
            float xuWdith = heightXu * 5;
            float xuWdith10 = heightXu * 10;
            PathEffect effects = new DashPathEffect(new float[]{xuWdith10, xuWdith, xuWdith10, xuWdith}, heightXu);
            xuPaint.setPathEffect(effects);

            linePaint = new Paint();
            linePaint.setAntiAlias(true); // 去除锯齿
            linePaint.setStrokeWidth(1);
            linePaint.setStyle(Paint.Style.STROKE);
            linePaint.setColor(Color.BLACK);
        }
    }

    /**
     * 画横线
     *
     * @param canvas
     * @param lines
     */
    public void initLineBack(Canvas canvas, int lines) {
        initPaint();
        int padding = 10;
        float lineHeight = (height - padding * 2) / lines;
        for (int i = 1; i <= lines; i++) {
            float y = padding + lineHeight * i;
            canvas.drawLine(padding, y, width - padding, y, linePaint);
        }
    }

    /**
     * 画横线和竖线
     *
     * @param canvas
     * @param rows   横线数量
     * @param cols   竖线数量
     */
    public void initLinesBack(Canvas canvas, int rows, int cols) {
        initPaint();
        float padding = 10;
        int widthSave = width % cols;
        float truePadding = padding + widthSave / 2;
        float lineHeight = (height - truePadding * 2) / rows;
        float lineWidth = (width - truePadding * 2) / cols;

        //绘制横线
        for (int i = 0; i <= rows; i++) {
            float y = truePadding + lineHeight * i;
            canvas.drawLine(truePadding, y, width - truePadding, y, linePaint);
        }
        //绘制竖线
        for (int j = 0; j <= cols; j++) {
            float x = lineWidth * j + truePadding;
            canvas.drawLine(x, truePadding, x, height - truePadding, linePaint);
        }
    }

    /**
     * 画横线和竖线
     *
     * @param canvas
     * @param rows   竖线数量
     */
    public void initTitleLinesBack(Canvas canvas, int rows) {
        initPaint();
        int padding = 10;
        int lineHeight = (height - padding * 2) / rows;

        for (int i = 0; i <= rows; i++) {
            int y = lineHeight * i + padding;
            canvas.drawLine(padding, y, width - padding, y, linePaint);
        }
        canvas.drawLine(width / 4, padding, width / 4, lineHeight * rows + padding, linePaint);
        canvas.drawLine(padding, padding, padding, lineHeight * rows + padding, linePaint);
        canvas.drawLine(width - padding, padding, width - padding, lineHeight * rows + padding, linePaint);
    }

    /**
     * 画横线和竖线
     *
     * @param canvas
     * @param rows   横向打点数
     */
    public void initDrawCircle(Canvas canvas, int rows) {
        Paint circlePaint = new Paint();
        circlePaint.setAntiAlias(true); // 去除锯齿
        circlePaint.setStrokeWidth(3);
        circlePaint.setColor(Color.BLACK);
        //点与点之间的间隔
        int size=width/rows;
        //竖向打点数
        int heightSize=(height/size)-1;
        int padingTop=(height%size)/2;
        for (int i = 0; i < heightSize; i++) {
            float circleY = size*(i+1) + padingTop;
            for (int j = 0; j < rows; j++) {
                float rx = size * (j + 1)-6;
                canvas.drawCircle(rx, circleY, 3, circlePaint);
            }
        }
    }

    /**
     * 画乐谱线
     *
     * @param canvas
     * @param rows   竖线数量
     */
    public void initNoteBack(Canvas canvas, int rows) {
        initPaint();
        int pading = 10;
        //分割线高度
        int splitHeight = (height - width) / 10;
        int noteHigth = height / rows - splitHeight;
        //绘制外边框
        for (int i = 0; i < rows; i++) {
            int left = pading;
            int top = noteHigth * i + pading + splitHeight * i;
            int right = width - pading;
            int bottom = top + noteHigth;
            Rect rect = new Rect(left, top, right, bottom);
            canvas.drawRect(rect, linePaint);
            for (int j = 0; j < 8; j++) {
                int lineInnerHegth = noteHigth / 12;
                if (j > 3) {
                    canvas.drawLine(pading, top + lineInnerHegth * (j + 4), right, top + lineInnerHegth * (j + 4), linePaint);
                } else {
                    canvas.drawLine(pading, top + lineInnerHegth * (j + 1), right, top + lineInnerHegth * (j + 1), linePaint);
                }
            }
        }
    }

    /**
     * 画四线格
     *
     * @param canvas
     * @param rows   竖线数量
     */
    public void initFourLine(Canvas canvas, int rows) {
        initPaint();
        int pading = 10;
        //分割线高度
        int splitHeight = (int) (height / (rows * 1.5));
        for (int i = 0; i < rows; i++) {
            float Top = (float) (splitHeight * i * 1.5 + pading);
            canvas.drawLine(pading, Top + splitHeight / 4, width - pading, Top + splitHeight / 4, linePaint);
            canvas.drawLine(pading, Top + splitHeight / 2, width - pading, Top + splitHeight / 2, linePaint);
            canvas.drawLine(pading, Top + splitHeight / 4 * 3, width - pading, Top + splitHeight / 4 * 3, linePaint);
            canvas.drawLine(pading, Top + splitHeight, width - pading, Top + splitHeight, linePaint);
        }
    }

    /**
     * 画竖线
     *
     * @param canvas
     * @param lines  竖线数量
     */
    public void initVerticalLinesBack(Canvas canvas, int lines) {
        initPaint();
        int lineWidth = (width - 20) / lines;

        for (int i = 0; i <= lines; i++) {
            int x = lineWidth * i + 10;
            canvas.drawLine(x, 10, x, height - 10, linePaint);
        }

        canvas.drawLine(10, 10, width - 10, 10, linePaint);
        canvas.drawLine(10, height - 10, width - 10, height - 10, linePaint);
    }
}
