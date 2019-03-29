package com.moxi.handwritinglibs.listener;

/**
 * Created by xj on 2018/7/20.
 */

public interface WindowRefureshListener {
    void onWindowRefureshEnd();

    /**
     * 是否在绘制删除线条
     * @param delete
     */
    void onDrawDelete(boolean delete);
}
