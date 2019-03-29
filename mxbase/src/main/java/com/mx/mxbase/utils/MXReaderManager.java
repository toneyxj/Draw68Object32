package com.mx.mxbase.utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.mx.mxbase.constant.Constant;

/**
 * Created by Archer on 16/10/8.
 */
public class MXReaderManager {

    //Uri const
    final static String AUTHORITY = "com.moxi.providers.moxiread";
    final static String PATH = "mxread";

    public final static Uri READ_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + PATH);

    final static String FILE_PATH = "filepath";         //table field user name
    final static String USER_TOKEN = "usertoken";//

    /**
     * 插入最近阅读数据
     *
     * @param context
     * @param filePath
     * @param userAppsession
     */
    public static void insertReadFile(Context context, String filePath, String userAppsession) {
        //如果已存在就执行更新操作
        if (!queryReadFile(context).equals("")) {
            updateUser(context, filePath, userAppsession);
            return;
        }
        Log.e("insertReadFile", "开始执行插入操作");
        ContentValues cv = new ContentValues();
        cv.put(FILE_PATH, filePath);
        cv.put(USER_TOKEN, userAppsession);
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
        String[] back = {"filepath", "usertoken"};
        Cursor cursor = context.getContentResolver().query(READ_CONTENT_URI, back, "usertoken=?", new String[]{Constant.MAIN_PACKAGE}, null);
        if (cursor == null) {
            Log.e("---", "cursor为空");
            return exist;
        }
        if (cursor.moveToFirst()) {
            do {
                exist = cursor.getString(cursor.getColumnIndex(FILE_PATH));
                Log.e("filepath", exist);
            } while (cursor.moveToNext());
            return exist;
        }
        return exist;
    }

    /**
     * 更新用户session值
     *
     * @param context     上下文
     * @param filePath    用户名
     * @param userSession 用户新获取的session
     */
    public static void updateUser(Context context, String filePath, String userSession) {
        //如果查询为空就执行添加操作
        if (queryReadFile(context).equals("")) {
            insertReadFile(context, filePath, userSession);
            return;
        }
        ContentResolver resolver = context.getContentResolver();
        ContentValues ucv = new ContentValues();
        ucv.put(USER_TOKEN, userSession);
        ucv.put(FILE_PATH, filePath);
        resolver.update(READ_CONTENT_URI, ucv, "usertoken=?", new String[]{Constant.MAIN_PACKAGE});
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
            resolver.delete(READ_CONTENT_URI, "usertoken=?   ", new String[]{Constant.MAIN_PACKAGE});
            flag = true;
            Log.e("delReadFile", filePath + "清除session成功");
        } catch (Exception e) {
            flag = false;
        }
        return flag;
    }
}
