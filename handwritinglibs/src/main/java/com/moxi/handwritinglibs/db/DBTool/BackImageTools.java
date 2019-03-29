package com.moxi.handwritinglibs.db.DBTool;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.moxi.handwritinglibs.db.DBUtils.BackImageUtils;
import com.moxi.handwritinglibs.db.dbModel.BackImageModel;
import com.moxi.handwritinglibs.db.help.DBHelpBackImage;
import com.moxi.handwritinglibs.utils.LLog;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 手写板背景保存调用工具类
 * Created by xj on 2018/2/9.
 */
public class BackImageTools {
    // 初始化类实列
    private static BackImageTools instatnce = null;
    private SQLiteDatabase db;
    /**获取纯文本描述信息*/
    private final String allField=BackImageUtils.ID+","
            +BackImageUtils.IMAGE_SOURCE_PATH+","
            +BackImageUtils.IMAGE_ADD_TIME+","
            +BackImageUtils.IMAGE_USE_NUMBER+","
            +BackImageUtils.IMAGE_EXTEND;
    /**
     * 获得软键盘弹出类实列
     *
     * @return 返回初始化实列
     */
    public static BackImageTools getInstance() {
        if (instatnce == null) {
            synchronized (BackImageTools.class) {
                if (instatnce == null) {
                    instatnce = new BackImageTools();
                }
            }
        }
        return instatnce;
    }
    /**
     * 初始化数据库
     * @param context 传application里面的context
     */
    public void initDB(Context context){
        DBHelpBackImage mySQLiteOpenHelper =DBHelpBackImage.getInstance(context);
        if (db == null) {
            db = mySQLiteOpenHelper.getWritableDatabase();
        }
    }
    public void closeDB(){
        if (db!=null)db.close();
    }
    /**
     * 向数据库插入背景图片数据
     *@param sourcePath 背景图片的原图片路径
     * @param imageContent  图片内容
     */
    public void insert(String sourcePath, String imageContent) {
        ContentValues value = new ContentValues();
        value.put(BackImageUtils.IMAGE_SOURCE_PATH, sourcePath);
        value.put(BackImageUtils.IMAGE_CONTENT, imageContent);
        value.put(BackImageUtils.IMAGE_ADD_TIME, System.currentTimeMillis());
        value.put(BackImageUtils.IMAGE_USE_NUMBER, 0);
        value.put(BackImageUtils.IMAGE_EXTEND,(new JSONObject()).toString());
        db.insert(BackImageUtils.TABLE_NAME, BackImageUtils.ID, value);
    }
    /**
     *查询背景图片的全部数据集合
     */
    public ArrayList<BackImageModel> queryAll() {
        ArrayList<BackImageModel> list = new ArrayList();
        Cursor cursor = db.rawQuery("select "+allField+" from " + BackImageUtils.TABLE_NAME + " order by " + BackImageUtils.IMAGE_ADD_TIME+" desc", null);
        cursor.moveToFirst();
        if (null==cursor)return list;
        while (cursor.moveToNext()){
            list.add(getModel(cursor));
        }
        cursor.close();
        return list;
    }
    public String getBackImageString(long id){
        Cursor cursor = db.rawQuery("select "+BackImageUtils.IMAGE_CONTENT+" from " + BackImageUtils.TABLE_NAME + " where "+BackImageUtils.ID+"="+id, null);
        if (null==cursor)return "";
        String content="";
        if (cursor.moveToNext()){
            content=cursor.getString(0);
        }
        cursor.close();
        return content;
    }
    /**删除自定义背景图片*/
    public void deleteImg(long ...values){
        StringBuilder builder=new StringBuilder();
        for (int i = 0; i < values.length; i++) {
            builder.append(values[i]);
            if (i!= values.length-1){
                builder.append(",");
            }
        }
        String sql="delete "+BackImageUtils.TABLE_NAME+" where "+BackImageUtils.ID+" in ("+builder.toString()+")";
        LLog.e("deleteImg",sql);
        db.execSQL(sql);
    }

    /**
     * 更新对应id的使用number值
     * @param id 数据对应id
     * @param number 数值
     * @param isAdd 是否进行自加
     */
    public void addUserNumber(long id,long number,boolean isAdd){
        ContentValues value = new ContentValues();
        if (isAdd)++number;
        value.put(BackImageUtils.IMAGE_USE_NUMBER,number);
        db.update(BackImageUtils.TABLE_NAME, value, BackImageUtils.ID + "=?", new String[]{String.valueOf(id)});
    }

    private BackImageModel getModel(Cursor cursor){
            long id = cursor.getLong(0);
            String sourcePath = cursor.getString(1);
            long addTime = cursor.getLong(2);
            long useNumber = cursor.getLong(3);
            String extend = cursor.getString(4);
            return new BackImageModel(id, sourcePath, addTime, useNumber, extend);
    }




}
