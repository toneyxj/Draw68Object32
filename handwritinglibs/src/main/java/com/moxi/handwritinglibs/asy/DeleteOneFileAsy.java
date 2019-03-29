package com.moxi.handwritinglibs.asy;

import android.os.AsyncTask;
import android.util.Log;

import com.moxi.handwritinglibs.db.WritPadModel;
import com.moxi.handwritinglibs.db.WritePadUtils;
import com.moxi.handwritinglibs.listener.DeleteListener;

import java.util.List;

/**
 * Created by xiajun on 2017/2/27.
 */

public class DeleteOneFileAsy extends AsyncTask<String, Void, Boolean> {
    private WritPadModel model;
    private DeleteListener listener;
    private int index;

    public DeleteOneFileAsy(WritPadModel model,int index, DeleteListener listener) {
        this.model=model;
        this.listener=listener;
        this.index=index;
    }

    @Override
    protected Boolean doInBackground(String... arg0) {
     List<WritPadModel> list= WritePadUtils.getInstance().getListFiles(model.saveCode);
        Log.e("allList",list.toString());
        Log.e("index",String.valueOf(index));
        if (index>(list.size()-1))return true;
        boolean is=false;
        if (WritePadUtils.getInstance().deleteFileById(list.get(index).id)){
            list.remove(index);
            for (int i = 0; i < list.size(); i++) {
                WritePadUtils.getInstance().upDateIndex(list.get(i).id,i);
            }
            is=true;
        }
        return is;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        if (null==listener)return;
        //设置回调
        listener.onDelete(aBoolean);
    }
}
