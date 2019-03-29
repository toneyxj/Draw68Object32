package com.moxi.writeNote.utils;

import android.os.AsyncTask;

import com.moxi.handwritinglibs.db.WritPadModel;
import com.moxi.handwritinglibs.db.WritePadUtils;
import com.moxi.writeNote.listener.InsertLister;
import com.mx.mxbase.constant.APPLog;

/**
 * 文件插入
 * Created by xj on 2017/9/7.
 */

public class InsertFileAsy extends AsyncTask<String, Void, Boolean> {
    private WritPadModel model;
    private InsertLister listener;
    private int index;

    public InsertFileAsy(WritPadModel model, int index, InsertLister listener) {
        this.model = model;
        this.listener = listener;
        this.index = index + 1;
    }

    @Override
    protected Boolean doInBackground(String... arg0) {
        WritePadUtils.getInstance().updateIndex(model.saveCode, index);
        long id = WritePadUtils.getInstance().judgeDataExist(model.saveCode, model.isFolder, index);
        if (id > 0) {
            //修改界面的添加没有成功
            return false;
        }else {
            saveModel();
        }
        return true;
    }

    private void saveModel() {
        model._index = index;
        model.isFolder = 1;
        boolean issave = WritePadUtils.getInstance().saveData(model);
        if (!issave) {
            saveModel();
        }
    }

    private void updataIndex(long id, int index) {
        boolean is = WritePadUtils.getInstance().upDateIndex(id, index);
        if (!is) {
            updataIndex(id, index);
        }
        APPLog.e("修改成功与否index=" + index, is);
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        if (null == listener) return;
        //设置回调
        listener.onInsert(aBoolean);
    }
}
