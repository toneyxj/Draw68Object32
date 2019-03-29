package com.mx.mxbase.http;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.mx.mxbase.interfaces.ClearSweepListener;


/**
 * Created by Administrator on 2016/8/15.
 */
public class ClearSweep extends AsyncTask<String, Void, Bitmap> {
    private ClearSweepListener back;// 传入接口
    private Bitmap oldBitmap;
    private int oldColor;
    private int newColor;

    /**
     * 请求构造方法
     *
     * @param back 接口用于得到返回值
     */
    public ClearSweep(ClearSweepListener back,
                      Bitmap oldBitmap, int oldColor, int newColor) {
        this.back = back;
        this.oldBitmap = oldBitmap;
        this.oldColor = oldColor;
        this.newColor = newColor;
    }

    @Override
    protected Bitmap doInBackground(String... arg0) {
        //相关说明可参考 http://xys289187120.blog.51cto.com/3361352/657590/
        Bitmap mBitmap = oldBitmap.copy(Bitmap.Config.ARGB_8888, true);
        //循环获得bitmap所有像素点
        int mBitmapWidth = mBitmap.getWidth();
        int mBitmapHeight = mBitmap.getHeight();
//        int mArrayColorLengh = mBitmapWidth * mBitmapHeight;
//        int[] mArrayColor = new int[mArrayColorLengh];
//        int count = 0;
        for (int i = 0; i < mBitmapHeight; i++) {
            for (int j = 0; j < mBitmapWidth; j++) {
                //获得Bitmap 图片中每一个点的color颜色值
                //将需要填充的颜色值如果不是
                //在这说明一下 如果color 是全透明 或者全黑 返回值为 0
                //getPixel()不带透明通道 getPixel32()才带透明部分 所以全透明是0x00000000
                //而不透明黑色是0xFF000000 如果不计算透明部分就都是0了
                int color = mBitmap.getPixel(j, i);
                //将颜色值存在一个数组中 方便后面修改
                if (color == oldColor) {
                    mBitmap.setPixel(j, i, newColor);  //将白色替换成透明色
                }

            }
        }
        return mBitmap;

    }

    @Override
    protected void onPostExecute(Bitmap result) {// 在doInBackground执行完成后系统会自动调用，result是返回值
        if (back == null) return;
        if (result != null) {
            back.getSweepBitmap(result);
        }
    }

}
