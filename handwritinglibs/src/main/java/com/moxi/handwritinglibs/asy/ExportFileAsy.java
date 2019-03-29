package com.moxi.handwritinglibs.asy;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.moxi.handwritinglibs.db.WritPadModel;
import com.moxi.handwritinglibs.db.WritePadUtils;
import com.moxi.handwritinglibs.listener.ExportListener;
import com.moxi.handwritinglibs.model.WriteModel.WLine;
import com.moxi.handwritinglibs.model.WriteModel.WritePageData;
import com.moxi.handwritinglibs.utils.PaintBackUtils;
import com.moxi.handwritinglibs.utils.StringUtils;
import com.moxi.handwritinglibs.writeUtils.PenUtils;
import com.mx.mxbase.utils.WindowsUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;

/**
 * 导出文件异步类
 * Created by 夏君 on 2017/2/28.
 */

public class ExportFileAsy extends AsyncTask<String, Void, Boolean> {
    private WritPadModel model;
    private ExportListener listener;
    private String fileName;
    private WeakReference<Context> wkcontext;

    public ExportFileAsy(Context context,WritPadModel model, String fileName, ExportListener listener) {
        this.model = model;
        this.listener = listener;
        this.fileName = fileName;
        this.wkcontext=new WeakReference<Context>(context);
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (listener==null)return;
            if (msg.what==1){
                listener.onExportHitn(msg.obj.toString());
            }
        }
    };

    @Override
    protected Boolean doInBackground(String... arg0) {
        boolean is = true;
        List<WritPadModel> models = WritePadUtils.getInstance().getListFilesAndImage(model.saveCode);
        File file = new File(fileName);
        if (!file.exists()) file.mkdirs();

        int size = models.size();
        for (int i = 0; i < size; i++) {
            String bitmapString = models.get(i).imageContent;
            if (null == bitmapString || bitmapString.equals("") || bitmapString.equals("null"))
                continue;
            Bitmap bitmap = Bitmap.createBitmap(WindowsUtils.WritedrawWidth, WindowsUtils.WritedrawHeight, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);

            //绘制背景
            PaintBackUtils utils = new PaintBackUtils();
            utils.setWidthAndHeight(wkcontext.get(),bitmap.getHeight(), bitmap.getWidth());
            canvas.drawColor(Color.WHITE);
            utils.DrawView(canvas, models.get(i).getExtendModel().background,models.get(i).getExtendModel().backgroundFilePath);

            WritePageData pageData= PenUtils.getWritePage(bitmapString);
            pageData.nullInit();
            PenUtils penUtils=new PenUtils();
            penUtils.setPageData(pageData);
            List<WLine> lines=penUtils.getDrawPaths(0);
            for (WLine p : lines) {
                p.drawCanvas(canvas);
//                canvas.drawPath(p.getPath(),  PenUtils.getPaint(p.lineWidth));
            }

            Message message = new Message();
            message.what = 1;
            message.obj = "当前导出进度：" + String.valueOf(i + 1) + "/" + size;
            handler.sendMessage(message);

            try {
                String path = fileName + "/" + models.get(i)._index + ".png";
                Log.e("ExportFileAsy-path", path);
                FileOutputStream fileOutputStream = new FileOutputStream(path);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
                fileOutputStream.close();

            } catch (IOException e) {
                e.printStackTrace();
                is = false;
                Message message1 = new Message();
                message1.what = 1;
                message1.obj = "导出出错";
                handler.sendMessage(message1);

            } finally {
                StringUtils.recycleBitmap(bitmap);
            }
        }
        return is;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        if (null == listener) return;
        //设置回调
        listener.onExport(aBoolean);
    }
}
