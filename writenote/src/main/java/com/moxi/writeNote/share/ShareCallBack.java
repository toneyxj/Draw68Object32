package com.moxi.writeNote.share;

/**
 * Created by Administrator on 2019/3/4.
 */

public interface ShareCallBack {
    /**
     * 分析数据转换回调-异步执行
     * @param isSucess 是否成功
     * @param path 保存文件路径
     */
    void shareSavePath(boolean isSucess, String path);
}
