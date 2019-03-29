package com.moxi.handwritinglibs.asy;

import android.os.AsyncTask;
import android.util.Log;

import com.moxi.handwritinglibs.db.WritPadModel;
import com.moxi.handwritinglibs.db.index.IndexDbUtils;
import com.moxi.handwritinglibs.listener.DeleteListener;

import org.litepal.crud.DataSupport;

import java.util.List;

/**
 * 多文件删除异步类
 * Created by xiajun on 2017/2/27.
 */

public class DeleteFilesAsy extends AsyncTask<String, Void, Boolean> {
    List<WritPadModel> isSelects;
    private DeleteListener listener;

    public DeleteFilesAsy(List<WritPadModel> isSelects, DeleteListener listener) {
        this.isSelects = isSelects;
        this.listener = listener;
    }

    @Override
    protected Boolean doInBackground(String... arg0) {
        StringBuilder builder = new StringBuilder();
        StringBuilder indexBuilder = new StringBuilder();
        for (int i = 0; i < isSelects.size(); i++) {
            WritPadModel model = isSelects.get(i);
            if (model.isFolder == 0) {
                if (i != 0) {
                    builder.append(" or ");
                    indexBuilder.append(" or ");
                }
                //文件夹
                builder.append(" saveCode like '" + model.saveCode + "/%'");
                indexBuilder.append(" saveCode like '" + model.saveCode + "/%'");
            }
            if (i != 0 || model.isFolder != 1) {
                builder.append(" or ");
                indexBuilder.append(" or ");
            }
            //文件
            builder.append(" (saveCode='" + model.saveCode + "' and isFolder=" + model.isFolder + ")");
            indexBuilder.append(" saveCode='" + model.saveCode + "'");
        }
        Log.e("DeleteFilesAsy", builder.toString());
        Log.e("FilesAsy-indexBuilder", indexBuilder.toString());

//        DataSupport.deleteAll(DrawIndexModel.class,indexBuilder.toString());
        IndexDbUtils.getInstance().deleteFile(indexBuilder.toString());
        return (DataSupport.deleteAll(WritPadModel.class, builder.toString()) > 0);
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        if (null == listener) return;
        //设置回调
        listener.onDelete(aBoolean);
    }
}
