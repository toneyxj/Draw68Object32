package com.moxi.handwritinglibs.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.moxi.handwritinglibs.BaseSurfaceViewDraw;
import com.mx.mxbase.interfaces.StopScreenListener;

/**
 * Created by xj on 2018/7/26.
 */

public class BrodcastUtils {

    private BaseSurfaceViewDraw viewDraw;
    private Context context;
    private StopScreenListener listener;

    public BrodcastUtils(Context context, BaseSurfaceViewDraw viewDraw,StopScreenListener listener) {
        this.viewDraw = viewDraw;
        this.context = context;
        this.listener=listener;
        initTimerUtils();
    }

    private void initTimerUtils() {
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.xrz.handwrite");
//        filter.addAction(StringUtils.OPENPASSWORDBRODCAST);
        context.registerReceiver(receive, filter);
    }

    /**
     * 下面是屏幕刷新通知
     */
    private BroadcastReceiver receive = new BroadcastReceiver() {

        @Override
        public void onReceive(Context arg0, Intent data) {
            if (data.getAction().equals("com.xrz.handwrite")) {
                viewDraw.setleaveScribbleMode(false, 0);
            }
//            else if(data.getAction().equals(StringUtils.OPENPASSWORDBRODCAST)){
//                //熄屏开机
//                    if (listener!=null){
//                        listener.openScreen();
//                    }
//            }
        }
    };

    public void destory() {
        try {
            context.unregisterReceiver(receive);
        } catch (Exception e) {

        }

    }

}
