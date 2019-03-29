package com.mx.mxbase.constant;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

/**
 * home事件广播,接收到广播后完全退出程序
 * Created by Administrator on 2016/11/16.
 */
public class HomeKeyEventBrodcast extends BroadcastReceiver {
    String SYSTEM_REASON = "reason";
    String SYSTEM_HOME_KEY = "homekey";
    String SYSTEM_HOME_KEY_LONG = "recentapps";
    private ActivityManager mActivityManager = null;
    private String[] pricessNames = new String[]{"com.neverland.oreader", "com.onyx.reader","com.onyx.kreader"};

    public HomeKeyEventBrodcast(String[] pricessNames) {
        this.pricessNames = pricessNames;
    }

    public HomeKeyEventBrodcast() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        String action = intent.getAction();
        APPLog.e("action=" + action);
        if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
            String reason = intent.getStringExtra(SYSTEM_REASON);
            if (TextUtils.equals(reason, SYSTEM_HOME_KEY)) {
                //表示按了home键,程序到了后台
//                killAll(context);
//                System.exit(0);
                getRunningAppProcessInfo();
                closeApp(mActivityManager);
            }
//            } else if (TextUtils.equals(reason, SYSTEM_HOME_KEY_LONG)) {
//                //表示长按home键,显示最近使用的程序列表
//            }
        }
    }

    public void  closeApp(ActivityManager mActivityManager){};
    // 获得系统进程信息
    private void getRunningAppProcessInfo() {
        for (int i = 0; i < pricessNames.length; i++) {
            try {
                mActivityManager.killBackgroundProcesses(pricessNames[i]);
            }catch (Exception e){}
        }
    }
}
