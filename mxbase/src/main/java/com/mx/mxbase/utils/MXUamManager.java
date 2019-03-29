package com.mx.mxbase.utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.view.View;

import com.mx.mxbase.constant.APPLog;
import com.mx.mxbase.constant.Constant;
import com.mx.mxbase.view.AlertDialog;

/**
 * Created by Archer on 16/10/8.
 */
public class MXUamManager {

    //Uri const
    final static String AUTHORITY = "com.moxi.providers.MoxiUser";
    final static String PATH = "mxuser";

    public final static Uri USER_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + PATH);

    final static String USER_NAME = "username";         //table field user name
    final static String USER_SESSION = "appsession";       //table field user session
    final static String USER_TOKEN = "usertoken";//
    final static String USER_B_ID = "userid";
    private static long openTime=0;

    /**
     * 插入appsession到数据库
     *
     * @param context        上下文
     * @param username       用户名
     * @param userAppsession 服务器返回session
     */
    public static void insertUam(Context context, String username, String userAppsession, String ddToken, String publicId) {
        //如果已存在就执行更新操作
        APPLog.e("username", username);
        APPLog.e("userAppsession", userAppsession);
        APPLog.e("ddToken", ddToken);
        APPLog.e("publicId", publicId);
        if (!queryUser(context).equals("")) {
            Log.e("username", username + "存在");
            updateUser(context, username, userAppsession, ddToken, publicId);
            return;
        }
        ContentValues cv = new ContentValues();
        cv.put(USER_NAME, username);
        cv.put(USER_SESSION, userAppsession);
        cv.put(USER_TOKEN, ddToken);
        cv.put(USER_B_ID, publicId);
        context.getContentResolver().insert(USER_CONTENT_URI, cv);
    }


    /**
     * 获取已经登陆的session
     *
     * @param context 上下文
     * @return 返回值 返回“”为没有查询到，查询成功直接返回session值
     */
    public static String queryUser(final Context context) {
        return queryUser(context,true);
    }
    public static String queryUser(final Context context,boolean showHitn) {
        String exist = "";
        try {
            String[] back = {"username", "appsession"};
            Cursor cursor = context.getContentResolver().query(USER_CONTENT_URI, back, "username=?", new String[]{Constant.MAIN_PACKAGE}, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {
                        exist = cursor.getString(cursor.getColumnIndex(USER_SESSION));
                        APPLog.e("userSession", exist);
                    } while (cursor.moveToNext());
                    cursor.close();
                }
            }
        } catch (Exception e) {
        }
        if (showHitn){
            showHitn=Math.abs(System.currentTimeMillis()-openTime)>1000;
            openTime=System.currentTimeMillis();
        }
        if (showHitn&&(exist==null||exist.isEmpty())) {
            new AlertDialog(context).builder().setTitle("登录提示").setMsg("您未登录，请前往登录").setCancelable(true).setNegativeButton("确认", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!ActivityUtils.isContextExisted(context))return;
                    try {
                        Intent intent = context.getPackageManager().getLaunchIntentForPackage(StartActivityUtils.UserPakege);
                        intent.putExtra("flag_version_stu", "教育版");
                        context.startActivity(intent);
                    } catch (Exception e) {
                        Toastor.showToast(context, "没有安装此模块");
                    }
                }
            }).setPositiveButton("取消", new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            }).show();
        }

        return exist;
    }

    /**
     * @param context
     * @return
     */
    public static String querUserBId(Context context) {
        String exist = "";
        String[] back = {"*"};
        Cursor cursor = context.getContentResolver().query(USER_CONTENT_URI, back, "username=?", new String[]{Constant.MAIN_PACKAGE}, null);
        if (cursor == null) {
            return exist;
        }
        if (cursor.moveToFirst()) {
            do {
                exist = cursor.getString(cursor.getColumnIndex(USER_B_ID));
                Log.e("user_b_id", exist);
            } while (cursor.moveToNext());
            return exist;
        }
        return exist;
    }

    /**
     * 获取已经登陆的session
     *
     * @param context 上下文
     * @return 返回值 返回“”为没有查询到，查询成功直接返回session值
     */
    public static String queryDDToken(Context context) {
        String ddToken = "";
        String[] back = {"username", "appsession", "usertoken"};
        Cursor cursor = context.getContentResolver().query(USER_CONTENT_URI, back, "username=?", new String[]{Constant.MAIN_PACKAGE}, null);
        if (cursor == null) {
            Log.e("---", "cursor为空");
            return ddToken;
        }
        if (cursor.moveToFirst()) {
            do {
                ddToken = cursor.getString(cursor.getColumnIndex(USER_TOKEN));
                String name = cursor.getString(cursor.getColumnIndex(USER_NAME));
                APPLog.e("ddToken", ddToken);
            } while (cursor.moveToNext());
            return ddToken;
        }
        return ddToken;
    }

    /**
     * 更新用户session值
     *
     * @param context     上下文
     * @param username    用户名
     * @param userSession 用户新获取的session
     */
    public static void updateUser(Context context, String username, String userSession, String ddToken, String userId) {
        //如果查询为空就执行添加操作
        if (queryUser(context).equals("")) {
            insertUam(context, username, userSession, ddToken, userId);
            return;
        }
        ContentResolver resolver = context.getContentResolver();
        ContentValues ucv = new ContentValues();
        ucv.put(USER_SESSION, userSession);
        ucv.put(USER_TOKEN, ddToken);
        ucv.put(USER_B_ID, userId);
        resolver.update(USER_CONTENT_URI, ucv, "username=?", new String[]{Constant.MAIN_PACKAGE});
    }

    /**
     * 删除操作
     *
     * @param context  上下文
     * @param username 用户名
     * @return
     */
    public static boolean delUam(Context context, String username) {
        boolean flag = false;
        try {
            ContentResolver resolver = context.getContentResolver();
            resolver.delete(USER_CONTENT_URI, "username=?   ", new String[]{Constant.MAIN_PACKAGE});
            flag = true;
            Log.e("delUam", username + "清除session成功");
        } catch (Exception e) {
            flag = false;
        }
        return flag;
    }
}
