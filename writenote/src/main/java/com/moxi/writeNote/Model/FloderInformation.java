package com.moxi.writeNote.Model;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/2/16.
 */
public class FloderInformation implements Serializable {
    /**
     * 手写笔记保存文件夹信息
     */
    public String floder;
    /**
     * 文件夹名称
     */
    public String name;
    /**
     * 页面索引值
     */
    public int pageIndex;

    public FloderInformation(String floder, String name, int pageIndex) {
        this.floder = floder;
        this.name = name;
        this.pageIndex = pageIndex;
    }

    public FloderInformation() {
    }
}
