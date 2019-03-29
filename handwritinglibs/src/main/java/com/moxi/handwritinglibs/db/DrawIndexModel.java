package com.moxi.handwritinglibs.db;

import org.litepal.annotation.Column;
import org.litepal.crud.DataSupport;

import java.io.Serializable;

/**
 * Created by xj on 2017/8/15.
 */

public class DrawIndexModel extends DataSupport implements Serializable {
    @Column(unique = true)
    public long id;
    /**保存绝对路径-唯一值*/
    public String saveCode;
    /**当前绘制索引值*/
    public int _index;

    public DrawIndexModel(String saveCode, int _index) {
        this.saveCode = saveCode;
        this._index = _index;
    }
}
