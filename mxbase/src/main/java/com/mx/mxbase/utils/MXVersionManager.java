package com.mx.mxbase.utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

/**
 * Created by Archer on 16/10/8.
 */
public class MXVersionManager {

    //Uri const
    final static String AUTHORITY = "com.moxi.providers.Update";
    final static String PATH = "mxupdate";

    public final static Uri READ_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + PATH);

    final static String UPDATE_NAME = "updatename";         //table field user name
    final static String UPDATE_CODE = "updatecode";       //table field user session
    final static String APP_NAME = "appname";

    /**
     * 插入最近阅读数据
     *
     * @param context
     * @param versionCode
     * @param versionName
     */
    public static void insertReadFile(Context context, String versionCode, String versionName, String appName) {
        //如果已存在就执行更新操作
        if (!queryReadFile(context).equals("")) {
            updateUser(context, versionCode, versionName, appName);
            return;
        }
        ContentValues cv = new ContentValues();
        cv.put(UPDATE_NAME, versionName);
        cv.put(UPDATE_CODE, versionCode);
        cv.put(APP_NAME, "moxi");
        try {
            context.getContentResolver().insert(READ_CONTENT_URI, cv);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 获取已经登陆的session
     *
     * @param context 上下文
     * @return 返回值 返回“”为没有查询到，查询成功直接返回session值
     */
    public static String queryReadFile(Context context) {
        String exist = "";
        Cursor cursor = context.getContentResolver().query(READ_CONTENT_URI, null, "appname=?", new String[]{"moxi"}, null);
        if (cursor == null) {
            return exist;
        }
        if (cursor.moveToFirst()) {
            do {
                exist = cursor.getString(cursor.getColumnIndex(UPDATE_NAME));
            } while (cursor.moveToNext());
            return exist;
        }
        return exist;
    }

    /**
     * 更新用户session值
     *
     * @param context     上下文
     * @param versionCode 用户名
     * @param versionName 用户新获取的session
     */
    public static void updateUser(Context context, String versionCode, String versionName, String appName) {
        //如果查询为空就执行添加操作
        if (queryReadFile(context).equals("")) {
            insertReadFile(context, versionCode, versionName, appName);
            return;
        }
        ContentResolver resolver = context.getContentResolver();
        ContentValues ucv = new ContentValues();
        ucv.put(UPDATE_CODE, versionCode);
        ucv.put(UPDATE_NAME, versionName);
        ucv.put(APP_NAME, "moxi");
        resolver.update(READ_CONTENT_URI, ucv, "appname=?", new String[]{"moxi"});
    }

    /**
     * 删除操作
     *
     * @param context  上下文
     * @param filePath 最近阅读文件路径
     * @return
     */
    public static boolean delReadFile(Context context, String filePath) {
        boolean flag = false;
        try {
            ContentResolver resolver = context.getContentResolver();
            resolver.delete(READ_CONTENT_URI, "appname=?   ", new String[]{"moxi"});
            flag = true;
        } catch (Exception e) {
            flag = false;
        }
        return flag;
    }
}
