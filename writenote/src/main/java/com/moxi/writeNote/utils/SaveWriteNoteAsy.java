package com.moxi.writeNote.utils;

import android.os.AsyncTask;
import android.util.Log;

import com.moxi.handwritinglibs.db.WritPadModel;
import com.moxi.handwritinglibs.db.WritePadUtils;
import com.moxi.writeNote.listener.SaveWriteNoteListener;

import java.util.List;

/**
 * Created by Administrator on 2017/2/17.
 */
public class SaveWriteNoteAsy extends AsyncTask<String, Void, Boolean> {
    private SaveWriteNoteListener listener;
    private WritPadModel model;
    private String temporarySaveCode;

    public SaveWriteNoteAsy(WritPadModel model,String temporarySaveCode,SaveWriteNoteListener listener) {
        this.model=model;
        this.temporarySaveCode=temporarySaveCode;
        this.listener=listener;
    }

    @Override
    protected Boolean doInBackground(String... arg0) {
        boolean is=true;
        List<WritPadModel> list=WritePadUtils.getInstance().getListFilesAndImage(temporarySaveCode);
        for (int i = 0; i < list.size(); i++) {
            WritPadModel padModel=list.get(i);
            padModel.parentCode=model.parentCode;
            padModel.name=model.name;
            padModel.saveCode=model.saveCode;
            is= WritePadUtils.getInstance().saveData(model);
        }

        return is;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        model.imageContent=null;
        Log.e("SaveCommonWrite-"+model.saveCode,String.valueOf(aBoolean));
        if (null==listener)return;
        //设置回调
        listener.isSucess(aBoolean);
    }
}
