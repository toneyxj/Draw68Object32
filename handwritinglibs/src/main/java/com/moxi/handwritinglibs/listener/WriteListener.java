package com.moxi.handwritinglibs.listener;

/**
 * 手写板事件监听
 * Created by 夏君 on 2017/3/14 0014.
 */

public interface WriteListener  {
    /**
     * 无效点击
     */
    void  onInvallClick();

    /**
     * 超时未操作
     */
    void onOverTime();
}
