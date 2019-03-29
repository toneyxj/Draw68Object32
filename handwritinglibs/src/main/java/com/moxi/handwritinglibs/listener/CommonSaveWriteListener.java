package com.moxi.handwritinglibs.listener;

/**
 * 普通保存绘制监听
 * Created by 夏君 on 2017/2/9.
 */
public interface  CommonSaveWriteListener {
    /**
     * 是否保存成功
     * @param is true保存成功
     * @param saveCode 保存文件的唯一标示
     */
    void  isSucess(boolean is,String saveCode);
}
