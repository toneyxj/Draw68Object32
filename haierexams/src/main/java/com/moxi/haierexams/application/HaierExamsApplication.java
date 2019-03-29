package com.moxi.haierexams.application;

import com.mx.mxbase.base.MyApplication;
import com.tencent.bugly.crashreport.CrashReport;

/**
 * Created by Archer on 2016/11/28.
 */
public class HaierExamsApplication extends MyApplication {


    @Override
    public void onCreate() {
        super.onCreate();
        CrashReport.initCrashReport(getApplicationContext(), "81894310db", false);
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(float dpValue) {
        final float scale = applicationContext.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
