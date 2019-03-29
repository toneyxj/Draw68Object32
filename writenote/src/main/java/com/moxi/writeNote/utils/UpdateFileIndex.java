package com.moxi.writeNote.utils;

import android.os.AsyncTask;

import com.moxi.handwritinglibs.db.WritPadModel;
import com.moxi.handwritinglibs.db.WritePadUtils;
import com.mx.mxbase.constant.APPLog;

import java.util.List;

/**
 * 整理手写页数
 * Created by xj on 2017/11/28.
 */

public class UpdateFileIndex extends AsyncTask<String, Void, Boolean> {
    private String saveCode;
    private updateFileListener listener;

    public UpdateFileIndex(String saveCode,updateFileListener listener) {
        this.saveCode = saveCode;
        this.listener=listener;
    }

    @Override
    protected Boolean doInBackground(String... arg0) {
        List<WritPadModel> list = WritePadUtils.getInstance().getListFiles(saveCode);
//        for (int i = 0; i < list.size(); i++) {
//            APPLog.e("index=" + i, list.get(i)._index);
//        }
        for (int i = 0; i < list.size(); i++) {
            WritPadModel model=list.get(i);
            if (model._index!=i) {
                updataIndex(model.id, i);
            }
        }
        return true;
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
        if (listener!=null)
        listener.onUpdateSucess();
    }

    public interface  updateFileListener{
        void onUpdateSucess();
    }
}