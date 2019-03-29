package com.moxi.handwritinglibs.asy;

import android.os.AsyncTask;
import android.util.Log;

import com.moxi.handwritinglibs.db.WriteCommonModel;
import com.moxi.handwritinglibs.db.WritePadUtils;
import com.moxi.handwritinglibs.listener.CommonSaveWriteListener;
import com.moxi.handwritinglibs.model.WriteModel.WritePageData;

/**
 * 普通保存绘制信息
 * Created by 夏君 on 2017/2/9.
 */
public class SaveCommonWrite extends AsyncTask<String, Void, Boolean> {
    private String name;
    private String saveCode;
    private String bitmap;
    private WritePageData pagedata;
    private CommonSaveWriteListener listener;

    public SaveCommonWrite(String name, String saveCode, String bitmap) {
        this.name = name;
        this.saveCode = saveCode;
        this.bitmap = bitmap;
    }
    public SaveCommonWrite(String name, String saveCode, WritePageData pagedata) {
        this.name = name;
        this.saveCode = saveCode;
        this.pagedata = pagedata;
    }

    public SaveCommonWrite(String name, String saveCode, String bitmap, CommonSaveWriteListener listener) {
        this.name = name;
        this.saveCode = saveCode;
        this.bitmap = bitmap;
        this.listener = listener;
    }

    @Override
    protected Boolean doInBackground(String... arg0) {
        if (pagedata!=null){
            bitmap=pagedata.getSaveDate();
        }
        if (bitmap==null)return false;
        WriteCommonModel model = new WriteCommonModel(name, saveCode, bitmap);
        return WritePadUtils.getInstance().saveData(model);
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        Log.e("SaveCommonWrite-"+saveCode,String.valueOf(aBoolean));
        if (null==listener)return;
        //设置回调
        listener.isSucess(aBoolean,saveCode);
    }
}
