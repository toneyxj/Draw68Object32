package com.mx.mxbase.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * 开启新的activity跳转
 * Created by xj on 2018/1/10.
 */

public class StartActivityUtils {
    public static final String UserPakege="com.moxi.user";//"com.moxi.mxuser"
    public static final String DDReader="com.moxi.bookstore";//"com.moxi.mxuser"
    public static final  String brodcastLight="com.statusbar.broadcast";
    /**
     * 启动图片描图
     *
     * @param context     上下文
     * @param backImgPath 背景图路径
     * @param title       标题
     * @param titleShow   当前是否显示状态栏
     */
    public static void startPicPostil(Context context, String backImgPath, String title, boolean titleShow) {
        try {
            Intent input = new Intent();
            ComponentName cnInput = new ComponentName("com.moxi.writeNote", "com.moxi.writeNote.Activity.PicPostilActivity");
            input.setComponent(cnInput);

            Bundle bundle = new Bundle();
            bundle.putString("backImgPath", backImgPath);
            bundle.putString("title", title);
            bundle.putBoolean("titleShow", titleShow);
            input.putExtras(bundle);

            context.startActivity(input);
        } catch (Exception e) {
            ToastUtils.getInstance().showToastShort("没有安装此模块");
        }
    }
    /**
     * 启动个人中心登录绑定三方账号界面
     *
     * @param context     上下文
     */
    public static void startDDUerBind(Context context) {
        try {
            Intent input = new Intent();
            ComponentName cnInput = new ComponentName(StartActivityUtils.DDReader, "com.mx.user.activity.DDUserBindActivity");
            input.setComponent(cnInput);
            context.startActivity(input);
        } catch (Exception e) {
            ToastUtils.getInstance().showToastShort("没有安装此模块");
        }
    }

    /**
     * 跳转到金山词霸翻译的界面
     */
    public static void StartCiBa(Context context,String word){
        try {
            Intent input = new Intent();
            ComponentName cnInput = new ComponentName("com.moxi.bookstore", "com.moxi.bookstore.activity.JinShanCiBaActivity");
            input.setComponent(cnInput);
            Bundle bundle=new Bundle();
            bundle.putString("word",word);
            input.putExtras(bundle);
            context.startActivity(input);
        } catch (Exception e) {
            ToastUtils.getInstance().showToastShort("没有安装此模块");
        }
    }
    public static void sendOpenLight(Context context){
        Intent intent=new Intent(brodcastLight);
        intent.putExtra("name","backlight");
        context.sendBroadcast(intent);
    }


}
