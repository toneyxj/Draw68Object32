package com.moxi.writeNote.utils;

import android.os.AsyncTask;

import com.moxi.handwritinglibs.db.WritPadModel;
import com.moxi.handwritinglibs.db.WritePadUtils;
import com.moxi.writeNote.config.ConfigInfor;
import com.mx.mxbase.constant.APPLog;

import java.util.List;

/**
 * 针对以前手写加密的数据进行迁移
 * Created by xj on 2017/12/22.
 */

public class WritePaswordRemovalAsy extends AsyncTask<String, Void, Boolean> {
    private String newFolder;
    private RemovalListener listener;
    private boolean destory=false;

    public WritePaswordRemovalAsy(String newFolder,RemovalListener listener) {
        this.newFolder=newFolder;
        this.listener=listener;
    }

    @Override
    protected Boolean doInBackground(String... arg0) {
        List<WritPadModel> list= WritePadUtils.getInstance().getAllListMirks();
        for (int i = 0; i < list.size(); i++) {
            if (destory)return false;
            WritPadModel model=list.get(i);
           CheckModel(model);
        }
        return true;
    }
    private void CheckModel(WritPadModel model){
        if (destory)return;
        if (!model.parentCode.startsWith(ConfigInfor.rootDir)){
            try {
                String parentCode= model.parentCode;
                String saveCode=model.saveCode;
                if (parentCode.equals("")||parentCode.startsWith("/")) {
                    parentCode=newFolder+parentCode;
                    saveCode=newFolder+saveCode;
                }else {
                    //处理数据
                    int files = model.saveCode.indexOf("/");
                    if (files <= 0) return;
                    String folder = model.parentCode.substring(0, files);
                    if (folder.equals(newFolder)) return;

                     parentCode = model.parentCode.replaceFirst(folder, newFolder);
                     saveCode = model.saveCode.replaceFirst(folder, newFolder);
                }
                boolean is= WritePadUtils.getInstance().upFolder(model.id,parentCode,saveCode);
                if (!is){
                    CheckModel(model);
                }
            }catch (Exception e){
                APPLog.e("com.moxi.writeNote.utils.WritePaswordRemovalAsy-字符处理错处",e.getMessage());
            }
        }
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        if (listener!=null){
            listener.onremovalback(aBoolean);
        }
    }

    public void destory(){
        destory=true;
    }
    public interface RemovalListener{
        void onremovalback(boolean is);
    }
}
