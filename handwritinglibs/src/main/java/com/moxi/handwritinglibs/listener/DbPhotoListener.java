package com.moxi.handwritinglibs.listener;

import android.graphics.Bitmap;

/**
 * 本地图片监听器
 * Created by Administrator on 2017/1/4.
 */
public interface DbPhotoListener {
    /**
     * 本地图片加载成功
     * @param saveCode 保存的的唯一标识
     */
    void onLoaderSucess(String saveCode,int index, Bitmap bitmap);
}
