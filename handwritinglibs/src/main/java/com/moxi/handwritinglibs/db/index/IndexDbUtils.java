package com.moxi.handwritinglibs.db.index;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * 下载数据库工具类
 * Created by 夏君 on 2017/5/22 0022.
 */

public class IndexDbUtils {
    private static IndexDbUtils instatnce;

    public static IndexDbUtils getInstance() {
        if (instatnce == null) {
            synchronized (IndexDbUtils.class) {
                if (instatnce == null) {
                    instatnce = new IndexDbUtils();
                }
            }
        }
        return instatnce;
    }

    private SQLiteDatabase db;
    public void initDb(Context context){
        IndexSqliteHelper helper = IndexSqliteHelper.getInstance(context);
        db = helper.getWritableDatabase();
    }
    public void saveData(String saveCode,int _index) {
         saveData(new DrawIndexBeen(saveCode,_index));
    }
    /**
     * 保存数据
     *
     * @param model 保存的数据model
     * @return 是否保存成功
     */
    public void saveData(DrawIndexBeen model) {
        boolean isSucess=judgeDataExist(model.saveCode);
        if (isSucess){
            //已经存在
            updataModel(model);
        }else {
            //未保存数据
            ContentValues values=new ContentValues();
            values.put("saveCode",model.saveCode);
            values.put("_index",model._index);
            db.insert(DbConfiger.TB_NAME,null,values);
        }
    }
    /**
     * 更新数据
     * @param model 更新数据model
     * @return 返回是否更新成功
     */
    public void updataModel(DrawIndexBeen model){
        ContentValues values=new ContentValues();
//        values.put("saveCode",model.saveCode);
        values.put("_index",model._index);
        db.update(DbConfiger.TB_NAME,values,DbConfiger.saveCode+"=?",new String[]{model.saveCode});
    }

    public int getIndex(String saveCode){
        int index = 0;
        Cursor cur = db.rawQuery("select "+DbConfiger._index+" from " + DbConfiger.TB_NAME + " where "+DbConfiger.saveCode+"='" + saveCode+"'" ,null);
        if (cur == null) return index;
        if (cur.moveToNext()){
            index= cur.getInt(0);
        }
        cur.close();
        return index;
    }
    /**
     * 判断数据是否存在
     *
     * @param saveCode 保存唯一标示
     * @return 是否存在该数据
     */
    public boolean judgeDataExist(String saveCode) {
        String sql="select "+DbConfiger.saveCode+" from " + DbConfiger.TB_NAME + " where "+DbConfiger.saveCode+"='" + saveCode+"'" ;
        Cursor cur =db.rawQuery(sql,null);
        boolean is=false;
        if(cur!=null) {
            is = cur.moveToNext();
            cur.close();
        }
        return is;
    }
    public void deleteFile(String sql){
        db.delete(DbConfiger.TB_NAME,sql,null);
    }
}
