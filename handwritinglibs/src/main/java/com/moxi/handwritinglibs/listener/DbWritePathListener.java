package com.moxi.handwritinglibs.listener;

import com.moxi.handwritinglibs.model.WriteModel.WritePageData;

/**
 * Created by xj on 2018/7/17.
 */

public interface DbWritePathListener {
    /**
     * 本地图片加载成功
     * @param saveCode 保存的的唯一标识
     */
    void onLoaderSucess(String saveCode,int index, WritePageData bitmap);
}
