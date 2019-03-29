package com.moxi.handwritinglibs.db;

import android.content.ContentValues;
import android.database.Cursor;

import org.litepal.crud.DataSupport;

/**
 * Created by xj on 2017/8/15.
 */

public class DrawIndexUtils {
    private static DrawIndexUtils instatnce = null;

    public static DrawIndexUtils getInstance() {
        if (instatnce == null) {
            synchronized (DrawIndexUtils.class) {
                if (instatnce == null) {
                    instatnce = new DrawIndexUtils();
                }
            }
        }
        return instatnce;
    }
    private final String indexAll = "id,saveCode,_index";
    public final String tableName = "DrawIndexModel";

    public boolean saveData(String saveCode,int _index) {
        return saveData(new DrawIndexModel(saveCode,_index));
    }
    /**
     * 保存数据
     *
     * @param model 保存的数据model
     * @return 是否保存成功
     */
    public boolean saveData(DrawIndexModel model) {
        boolean isSucess=false;
        long id=judgeDataExist(model.saveCode);
        if (id!=-1){
            //已经存在
            isSucess=updataModel(model,id);
        }else {
            //未保存数据
            isSucess= model.save();
        }
        return isSucess;
    }
    /**
     * 更新数据
     * @param model 更新数据model
     * @return 返回是否更新成功
     */
    public boolean updataModel(DrawIndexModel model,long id){
        ContentValues values=new ContentValues();
        values.put("saveCode",model.saveCode);
        values.put("_index",model._index);
        return (DataSupport.update(DrawIndexModel.class,values,id)>0);
    }
    public int getIndex(String saveCode){
        int index = 0;
        Cursor cur = DataSupport.findBySQL("select _index from " + tableName + " where saveCode='" + saveCode+"'" );
        if (cur == null) return index;
        if (cur.moveToNext()){
            index= cur.getInt(0);
        }
        cur.close();
        return index;
    }
    /**
     * 更改名字
     */
    public boolean updateSaveCode(String oldSaveCode,String newSaveCode){
        long id=judgeDataExist(oldSaveCode);
        if (id==-1){
            return saveData(newSaveCode,0);
        }
        ContentValues values=new ContentValues();
        values.put("saveCode",newSaveCode);
        return (DataSupport.update(DrawIndexModel.class,values,id)>0);
    }
    /**
     * 判断数据是否存在
     *
     * @param saveCode 保存唯一标示
     * @return 是否存在该数据
     */
    public long judgeDataExist(String saveCode) {
        long id = -1;
        Cursor cur = DataSupport.findBySQL("select id from " + tableName + " where saveCode='" + saveCode+"'" );
        if (cur == null) return id;
        if (cur.moveToNext()){
            id= cur.getLong(0);
        }
        cur.close();
        return id;
    }
}
