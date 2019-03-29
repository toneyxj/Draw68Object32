package com.mx.mxbase.base;

import android.content.Context;

import com.mx.mxbase.utils.SharePreferceUtil;
import com.mx.mxbase.utils.ToastUtils;

import org.litepal.LitePal;

/**
 * Created by Archer on 16/7/26.
 */
public class MyApplication extends BaseApplication {

    public static Context applicationContext;
    private static MyApplication instance;

    public static MyApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        applicationContext = this;
        instance = this;
        SharePreferceUtil.getInstance(applicationContext);
        ToastUtils.getInstance().initToast(applicationContext);
        LitePal.initialize(this);
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(float dpValue) {
        final float scale = applicationContext.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(float pxValue) {
        final float scale = applicationContext.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
}
