package com.mx.mxbase.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

/**
 * Created by xj on 2017/12/19.
 */

public class SystemUtils {

    /**
     * 熄灭屏幕
     */
    public static void putOutScreen(Context context){
        final String DEVICE_GOTO_SLEEP_ACTION = "device_goto_sleep_action";
        context.sendBroadcast(new Intent(DEVICE_GOTO_SLEEP_ACTION));
    }

    public static void StartSystemSetting(Context context,String path){
        try{
            Intent input = new Intent();
            ComponentName cnInput = new ComponentName("com.android.settings", path);
            input.setComponent(cnInput);
            context.startActivity(input);
        }catch (Exception e){
            ToastUtils.getInstance().showToastShort("调用设置："+path+"  失败");
        }
    }
}
