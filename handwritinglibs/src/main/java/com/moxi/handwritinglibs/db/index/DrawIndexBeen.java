package com.moxi.handwritinglibs.db.index;

/**
 * Created by xj on 2017/8/16.
 */

public class DrawIndexBeen {
    /**保存绝对路径-唯一值*/
    public String saveCode;
    /**当前绘制索引值*/
    public int _index;

    public DrawIndexBeen(String saveCode, int _index) {
        this.saveCode = saveCode;
        this._index = _index;
    }
}
