package com.moxi.handwritinglibs.asy;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.StatFs;

import com.moxi.handwritinglibs.db.WritPadModel;
import com.moxi.handwritinglibs.db.WritePadUtils;
import com.moxi.handwritinglibs.listener.NoteSaveWriteListener;
import com.moxi.handwritinglibs.listener.UpLogInformationInterface;
import com.moxi.handwritinglibs.model.WriteModel.WritePageData;
import com.moxi.handwritinglibs.utils.BitmapOrStringConvert;
import com.mx.mxbase.constant.APPLog;
import com.mx.mxbase.utils.ToastUtils;

import java.io.File;

/**
 * 保存笔记
 * Created by 夏君 on 2017/2/17.
 */
public class SaveNoteWrite extends AsyncTask<String, Void, Boolean> {
    private WritPadModel model;
    private Bitmap bitmap;
    private WritePageData data;
    private NoteSaveWriteListener listener;
    private UpLogInformationInterface upListener;
    private String log="";
    private String StringStr=null;

    public SaveNoteWrite(WritPadModel model, Bitmap bitmap,NoteSaveWriteListener listener,UpLogInformationInterface upListener) {
        this.model=new WritPadModel(model.name,model.saveCode,model.isFolder,model.parentCode,model._index,model.extend);
        this.bitmap=bitmap;
        this.listener=listener;
        this.upListener=upListener;
    }
    public SaveNoteWrite(WritPadModel model, WritePageData data,NoteSaveWriteListener listener) {
        this.model=new WritPadModel(model.name,model.saveCode,model.isFolder,model.parentCode,model._index,model.extend);
        this.data=data;
        this.listener=listener;
        this.upListener=null;
    }
    public SaveNoteWrite(WritPadModel model, String data,NoteSaveWriteListener listener) {
        this.model=new WritPadModel(model.name,model.saveCode,model.isFolder,model.parentCode,model._index,model.extend);
        this.StringStr=data;
        this.listener=listener;
        this.upListener=null;
    }

    @Override
    protected Boolean doInBackground(String... arg0) {
        if ((null==bitmap||bitmap.isRecycled())&&data==null&&StringStr==null){
            model.imageContent="";
            return true;
        }
        if (null!=bitmap&&!bitmap.isRecycled()) {
            model.imageContent = BitmapOrStringConvert.convertIconToString(bitmap);
        }else if (data!=null){
            model.imageContent=data.getSaveDate();
        }else{
            model.imageContent=StringStr;
        }
        if (model!=null)
//        APPLog.e("保存数据的大小：", StorageUtil.getPrintSize(model.imageContent.length()));
        if ( model.imageContent==null)return false;
        return WritePadUtils.getInstance().saveData(model);
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {

        model.imageContent=null;
        APPLog.e("SaveCommonWrite-"+model.saveCode,String.valueOf(aBoolean));
        if (readSystem()<1024&&!aBoolean){
            ToastUtils.getInstance().showToastShort("内存不足存储失败");
        }
        if (upListener!=null)upListener.onUpLog(log,System.currentTimeMillis());
        if (null==listener)return;
        //设置回调
        listener.isSucess(aBoolean,model);
    }
  private   long  readSystem() {
        File root = Environment.getRootDirectory();
        StatFs sf = new  StatFs(root.getPath());
        long  blockSize = sf.getBlockSize();
        long  blockCount = sf.getBlockCount();
        long  availCount = sf.getAvailableBlocks();
      return availCount*blockSize/ 1024;
    }
}
