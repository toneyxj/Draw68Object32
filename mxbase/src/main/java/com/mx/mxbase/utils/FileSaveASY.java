package com.mx.mxbase.utils;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 异步保存文件
 * Created by Administrator on 2016/11/10.
 */
public class FileSaveASY extends AsyncTask<Void, Void, Boolean> {
    private Bitmap bitmap;
    private String path;
    private saveSucess saveSucess;

    public FileSaveASY(Bitmap bitmap, String path) {
        initdata(bitmap,path,null);
    }
    public FileSaveASY(Bitmap bitmap, String path,saveSucess saveSucess) {

       initdata(bitmap,path,saveSucess);
    }
    private void initdata(Bitmap bitmap, String path,saveSucess saveSucess) {
        this.bitmap = bitmap;
        this.path = path;
        this.saveSucess=saveSucess;
        File file = new File(path.substring(0, path.lastIndexOf("/")));
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        if (null==bitmap||bitmap.isRecycled())return false;
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(
                    path);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }finally {
            StringUtils.recycleBitmap(bitmap);
        }
        return true;
    }

    @Override
    protected void onPostExecute(Boolean aVoid) {
        super.onPostExecute(aVoid);
        if (saveSucess!=null){
            saveSucess.onSaveSucess(aVoid);
        }
    }

    public interface saveSucess{
        void onSaveSucess(boolean is);
    }
}
