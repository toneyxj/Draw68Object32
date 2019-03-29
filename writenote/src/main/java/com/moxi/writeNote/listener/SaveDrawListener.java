package com.moxi.writeNote.listener;

import android.app.Dialog;

/**
 * 保存信息提示
 * Created by 夏君 on 2017/2/17.
 */
public interface SaveDrawListener {
    void discard( );
    void cancel();
    void insure(Dialog dialog,String name);
}
