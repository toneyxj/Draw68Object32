package com.moxi.writeNote.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.moxi.handwritinglibs.utils.PaintBackUtils;
import com.mx.mxbase.constant.APPLog;

import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Administrator on 2016/8/8.
 */
public class PaintBackView extends View {
    public final static String parentCode="PaintBackView-saveCode";

    private PaintBackUtils utils;
    private Handler handler=new Handler();
    private String filepath="";
    public PaintBackView(Context context) {
        super(context);
    }

    public PaintBackView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PaintBackView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 绘制地板的风格0背景为空，1背景为米字格，2背景为横线
     */
    private int drawStyle = 0;

    public void setDrawStyle(  int drawStyle) {
        this.drawStyle = drawStyle;
        invalidate();

    }
    public void setDrawStyle( int drawStyle,String filepath) {
        this.filepath=filepath;
        this.drawStyle = drawStyle;
        invalidate();
    }
    public void setshow(int show){
        if (getVisibility()==show){
            return;
        }
        setVisibility(show);
        if (getVisibility()==VISIBLE)
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (utils==null){
            utils=new PaintBackUtils();
        }
        utils.setWidthAndHeight(getContext(),getHeight(),getWidth());
        utils.DrawView(canvas,drawStyle,filepath);
    }

    /**
     * 保存手写背景图片仔细
     */
    public void saveWritePad() {
        final Bitmap bitmap = getBitmap();
        try {
            String path= com.mx.mxbase.utils.StringUtils.getSDPath()+"image"+drawStyle+".png";
            Log.e("ExportFileAsy-path",path);
            FileOutputStream fileOutputStream = new FileOutputStream(path);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        APPLog.e("drawStyle",drawStyle);
//        drawStyle++;
//        if (drawStyle>=14){
//            drawStyle=13;
//        }else {
//            saveWritePad();
//        }
    }
    public Bitmap getBitmap() {
        int w = getWidth();
        int h = getHeight();
        Bitmap bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmp);
        c.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        /** 如果不设置canvas画布为白色，则生成透明 */

        layout(getLeft(), getTop(), getRight(), getBottom());
        draw(c);
        return bmp;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        handler.removeCallbacksAndMessages(null);
    }
}
