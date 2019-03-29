package com.moxi.writeNote.utils;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.moxi.writeNote.Model.loging.LoginUserData;
import com.mx.mxbase.constant.APPLog;
import com.mx.mxbase.constant.Constant;
import com.mx.mxbase.utils.ToastUtils;

import static com.mx.mxbase.utils.MXUamManager.USER_CONTENT_URI;

/**
 * Created by xj on 2017/11/22.
 */

public class UserInformation {
    private static UserInformation instatnce = null;
    private Context context;
    //用户登录数据
    private LoginUserData data;
    //隐藏模式，默认关闭
    private boolean hidePattern = false;
    /**
     * 不同登录模块需要
     */
    private String flag = "标准版";
    private String userEncrypt="";



    public static UserInformation getInstance() {
        if (instatnce == null) {
            synchronized (UserInformation.class) {
                if (instatnce == null) {
                    instatnce = new UserInformation();
                }
            }
        }
        return instatnce;
    }

    /**
     * 设置商务版，标准版
     *
     * @param flag
     */
    public void setFlag(String flag) {
        this.flag = flag;
    }

    public void initUserInfor(Context context) {
        this.context = context;
    }

    /**
     * 设置是否显示隐藏分区
     * @param hidePattern
     */
    public boolean setHidePattern(boolean hidePattern) {
        if (isLoging()) {
            this.hidePattern = hidePattern;
        }else {
            this.hidePattern=false;
        }
        return  this.hidePattern;
    }

    public boolean setHidePattern_value(boolean hidePattern) {
        this.hidePattern=hidePattern;
        return this.hidePattern;
    }

    public boolean isHidePattern() {
        return hidePattern;
    }

    /**
     * 清空加载数据
     */
    public void clear() {
        hidePattern = false;
        userEncrypt = "";
        data = null;
    }

    /**
     * 获得用户个人信息
     */
    public LoginUserData getUserData() {
        if (data == null) {
            String values = queryDDToken();
            if (null != values && !values.equals("")) {//登录数据解析
                JSONObject object = JSON.parseObject(values);
                JSONObject datav = object.getJSONObject("data");
                data = JSON.parseObject(datav.toString(), LoginUserData.class);
            }
        }
        return data;
    }

    /**
     * 获得用户加密文件
     * @return
     */
    public String getUserPassword() {
//        if (isLoging()&&hidePattern) {
//            if (userEncrypt != null &&! userEncrypt.equals("")) {
//                String value = data.getUser().getPhone();
//                userEncrypt = MD5.stringToMD5(value);
//            }
//        }else {
//            userEncrypt="";
//        }
        return userEncrypt;
    }
    public void setUserEncrypt(String pas){
        userEncrypt = MD5.stringToMD5(pas);
    }

    public boolean isLoging() {
        boolean isloging = getUserData() != null;
        if (!isloging) {
            ToastUtils.getInstance().showToastShort("您未登录，请登录后重试");
        }
        return isloging;
    }

    /**
     * 前往登录
     *
     * @return 已经登录返回true
     */
    public boolean toLoging() {
        boolean isloging = getUserData() != null;
        if (!isloging) {
            try {
                Intent intent = context.getPackageManager().getLaunchIntentForPackage("com.moxi.user");
                intent.putExtra("flag_version_stu", flag);
                context.startActivity(intent);
            } catch (Exception e) {
                ToastUtils.getInstance().showToastShort("没有安装此模块");
            }
        }
        return isloging;
    }

    /**
     * 获取已经登陆的session
     *
     * @return 返回值 返回“”为没有查询到，查询成功直接返回session值
     */
    private String queryDDToken() {
        String ddToken = "";
        String[] back = {"username", "appsession", "usertoken"};
        Cursor cursor = context.getContentResolver().query(USER_CONTENT_URI, back, "username=?", new String[]{Constant.MAIN_PACKAGE}, null);
        if (cursor == null) {
            APPLog.e("---", "cursor为空");
            return ddToken;
        }
        if (cursor.moveToFirst()) {
            do {
                ddToken = cursor.getString(cursor.getColumnIndex("usertoken"));
                APPLog.e("userSession", ddToken);
            } while (cursor.moveToNext());
            return ddToken;
        }
        return ddToken;
    }
}
