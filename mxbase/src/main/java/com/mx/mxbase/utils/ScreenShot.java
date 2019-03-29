package com.mx.mxbase.utils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.view.View;

import com.mx.mxbase.constant.APPLog;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by xj on 2018/1/9.
 */

public class ScreenShot {
    private Activity activity;
    private View view;
    private ScreenShotListener listener;
    private Handler handler = new Handler();

    public ScreenShot(Activity activity, ScreenShotListener listener) {
        if (activity!=null) {
            this.activity = activity;
            view = activity.getWindow().getDecorView();
        }
        this.listener = listener;
    }

    // 获取指定Activity的截屏，保存到png文件
    public Bitmap takeScreenShot() {
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap b1 = view.getDrawingCache();
        return b1;
    }

    // 保存到sdcard
    public void savePic(final Bitmap b, final String strFileName, final boolean isdeletePic) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(strFileName);
                    if (null != fos) {
                        b.compress(Bitmap.CompressFormat.PNG, 100, fos);
                        fos.flush();
                        fos.close();
                    }
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (activity==null){
                                if (isdeletePic) {
                                    StringUtils.recycleBitmap(b);
                                }
                            }else {
                                if (isdeletePic) {
                                    destoryView();
                                }
                                File file = new File(strFileName);
//                                MediaStore.Images.Media.insertImage(activity.getContentResolver(),strFileName, file.getName(), null);
                                Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file));
                                activity.sendBroadcast(intent);
                            }
                            if (listener != null) listener.onShotSucess(strFileName);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    /**
     * @param savePicPath 保存文件路径
     * @param isdeletePic 保存后是否清理内存
     */
    public void shoot(String savePicPath, final boolean isdeletePic) {
        APPLog.e("savePicPath", savePicPath);
        savePic(takeScreenShot(), savePicPath, isdeletePic);
    }

    public void destoryView() {
        view.destroyDrawingCache();
    }

    public interface ScreenShotListener {
        /**
         * 裁剪完成
         *
         * @param filePath 保存文件路径
         */
        void onShotSucess(String filePath);
    }
}
