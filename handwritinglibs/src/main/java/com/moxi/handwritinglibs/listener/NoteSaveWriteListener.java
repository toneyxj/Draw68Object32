package com.moxi.handwritinglibs.listener;

import com.moxi.handwritinglibs.db.WritPadModel;

/**
 * 手写备忘录笔记保存
 * Created by 夏君 on 2017/2/17.
 */
public interface NoteSaveWriteListener {
    /**
     * 是否保存成功
     * @param is true保存成功
     * @param model 保存文件信息返回，不带imagecontent
     */
    void  isSucess(boolean is,WritPadModel model);
}
