package com.moxi.writeNote.utils;

import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.mx.mxbase.constant.APPLog;
import com.mx.mxbase.utils.StringUtils;
import com.mx.mxbase.utils.WindowsUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 获得自定义背景图片
 * Created by xj on 2018/4/12.
 */

public class GetBackImgAsy extends AsyncTask<String, Void, List<String>> {
    private CustomBackgroundListener listener;

    public GetBackImgAsy(CustomBackgroundListener listener) {
        this.listener = listener;
    }

    @Override
    protected List<String> doInBackground(String... arg0) {
        List<String> list = new ArrayList<String>();
        String path = StringUtils.getWriteNotePhotoPath();
        if (StringUtils.isNull(path)) return list;
        File file = new File(path);
        if (!file.exists() || !file.isDirectory()) return list;
        File[] files = file.listFiles();
        for (File f : files) {
            String name = f.getName();
            if (name.endsWith(StringUtils.WRITENOTE_BACKGROUNG_END)) {
                //获取Options对象
                BitmapFactory.Options options = new BitmapFactory.Options();
                //仅做解码处理，不加载到内存
                options.inJustDecodeBounds = true;
                //解析文件
                BitmapFactory.decodeFile(f.getAbsolutePath(), options);
                //获取宽高
                int imgWidth = options.outWidth;
                int imgHeight = options.outHeight;

                APPLog.e("imgWidth",imgWidth);
                APPLog.e("imgHeight",imgHeight);

                if (imgWidth== WindowsUtils.WritedrawWidth&&imgHeight==WindowsUtils.WritedrawHeight){
                    list.add(f.getAbsolutePath());
                }
            }
        }

        return list;
    }

    @Override
    protected void onPostExecute(List<String> result) {
        if (null == listener) return;
        //设置回调
        listener.customBack(result);
    }

    public interface CustomBackgroundListener {
        void customBack(List<String> result);
    }
}
