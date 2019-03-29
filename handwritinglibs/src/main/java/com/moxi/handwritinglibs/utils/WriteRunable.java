package com.moxi.handwritinglibs.utils;

import com.mx.mxbase.constant.APPLog;

/**
 * Created by xj on 2018/9/26.
 */

public  abstract class WriteRunable implements Runnable {
    /**
     * 设置运行终结
     */
    private boolean finish=false;

    public boolean isFinish() {
        if (finish)
        APPLog.e("出现了刷新为true了","finish=true");
        return finish;
    }

    public void setFinish(boolean finish) {
        this.finish = finish;
    }
}
