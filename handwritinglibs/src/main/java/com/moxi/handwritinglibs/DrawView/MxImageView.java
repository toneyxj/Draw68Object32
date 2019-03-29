package com.moxi.handwritinglibs.DrawView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.View;

import com.moxi.handwritinglibs.writeUtils.PenUtils;

/**
 * Created by xj on 2018/7/26.
 */

public class MxImageView extends View {
    private Bitmap bitmap;
    public MxImageView(Context context) {
        super(context);
    }

    public MxImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public void setBitmap(Bitmap bitmap){
        this.bitmap=bitmap;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (bitmap!=null){
            int width=getWidth();
            int height=getHeight();

            int w = bitmap.getWidth();
            int h = bitmap.getHeight();
            //计算缩放比列
            float xs = w / (float) w;
            float ys = height / (float) h;
            float fs = xs > ys ? ys : xs;
            if (fs >= 1) {
                fs = 1;
            }
            Matrix matrix = new Matrix();
            matrix.setScale(fs, fs);
            w = (int) (bitmap.getWidth() * fs);
            h = (int) (bitmap.getHeight() * fs);

            int left = ((width - w) > 0 ? (width - w) : 0) / 2;
            int top = ((height - h) > 0 ? (height - h) : 0) / 2;
            matrix.postTranslate(left, top);

            canvas.drawBitmap(bitmap, matrix, PenUtils.getPaint(5));
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (bitmap!=null&&!bitmap.isRecycled())bitmap.recycle();
    }
}
