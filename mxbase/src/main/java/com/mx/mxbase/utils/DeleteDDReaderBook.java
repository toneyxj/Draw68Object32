package com.mx.mxbase.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * 删除当当阅读器里面下载的书记
 * Created by Administrator on 2016/11/17.
 */
public class DeleteDDReaderBook extends AsyncTask<Void,Void,Void> {
    private boolean isSave=false;
    private WeakReference<Context> context;// 上下文
    private String savePath= StringUtils.getSDPath()+"DDcache";
    private operationSucess sucess;

    /**
     * 判断删除记录
     * @param context
     * @param isSave 保存或者比对 保存为true
     */
    public DeleteDDReaderBook(Context context, boolean isSave, operationSucess sucess) {
        this.context = new WeakReference<Context>(context);
        this.isSave=isSave;
        this.sucess=sucess;
    }

    public Context getcontext() {
        final Context context = this.context.get();
        if (context == null) {
            return null;
        }
        return context;
    }

    @Override
    protected Void doInBackground(Void... params) {
        if (getcontext() == null) return null;
        try{
            ContentResolver contentResolver = getcontext().getContentResolver();
            Uri selecturi = Uri.parse("content://com.moxi.bookstore.provider/BookRack");
            Cursor cursor = contentResolver.query(selecturi, null, null, null, "publishtime DESC");
            ArrayList<String> paths = new ArrayList<String>();
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    String path = cursor.getString(6);
//                APPLog.e("当当文件路径path=" + path);
                    paths.add(path);
                }
                cursor.close();
            }
            if (!isSave) {//判断删除
                //获得本地文件的数据集合
                try {
                    if (paths.size()==0)return null;
                    ArrayList<String> saveFiles=new ArrayList<String>();
                    String content= FileUtils.getInstance().readFile(savePath);
                    if (content.equals("")){
                        saveData(paths);
                        return null;
                    }
                    JSONArray array=new JSONArray(content);
                    for (int i = 0; i < array.length(); i++) {
                        saveFiles.add(array.getString(i));
                    }
                    //比对删除
                    for (String path:saveFiles){
                        if (!paths.contains(path)){
                            StringUtils.deleteFile(path);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else {//保存记录
                saveData(paths);
            }
        }catch (Exception e){
        }
        return null;
    }
    private void saveData(ArrayList<String> paths){
        try {
            JSONArray array=new JSONArray();
            for(String saveP:paths){
                array.put(saveP);
            }
//            APPLog.e("保存数据="+array.toString());
            FileUtils.getInstance().writeFile(savePath,array.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (sucess!=null){
            sucess.onSucess();
        }
    }

    public interface  operationSucess{
        void onSucess();
    }
}
