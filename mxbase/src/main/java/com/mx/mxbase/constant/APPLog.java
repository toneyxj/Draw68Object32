package com.mx.mxbase.constant;

import android.util.Log;

/**
 * Created by Administrator on 2016/4/21.
 */
public class APPLog {
    public static final boolean is=true;
    /**
     * 添加系统日志
     * @param hitn
     */
    public static void e(Object hitn){
        if (hitn==null)hitn="输出为空";
        if (is)
        Log.e("系统提醒","提示语："+hitn.toString());
    }

    /**
     * 自定义日志
     * @param type
     * @param hitn
     */
    public static  void  e(Object type,Object hitn){
        if (hitn==null)hitn="输出为空";
        if (is)
        Log.e(type.toString(),hitn.toString());
    }
    /**
     * 添加系统警告日志
     * @param hitn
     */
    public static void d(Object hitn){
        if (hitn==null)hitn="输出为空";
        if (is)
        Log.d("系统警告","提示语："+hitn.toString());
    }

    /**
     * 自定义警告日志
     * @param type
     * @param hitn
     */
    public static  void  d(Object type,Object hitn){
        if (hitn==null)hitn="输出为空";
        if (is)
        Log.d(type.toString(),hitn.toString());
    }

    /**
     * 输出错误日志
     * @param type
     * @param hitn
     * @param e
     */
    public static  void  e(Object type,Object hitn,Exception e){
        if (hitn==null)hitn="输出为空";
        if (is)
        Log.e(type.toString(),hitn.toString(),e);
    }
}
