package com.moxi.handwritinglibs.db;

import java.io.Serializable;

/**
 * 普通手写数据模板
 * Created by 夏君 on 2017/2/9.
 */
public class WriteCommonModel implements Serializable{
    /**
     * 数据库自带唯一标示id
     */
    public long id;
    /**
     * 保存文件的名字
     */
    public String name="save";
    /**
     * 自定义保存唯一标示
     */
    public String saveCode;
    /**
     * 图片内容
     */
    public String imageContent;
    /**
     * 文件最近修改时间
     */
    public long changeTime=System.currentTimeMillis();

    public WriteCommonModel(String name, String saveCode, String imageContent) {
        this.name = name;
        this.saveCode = saveCode;
        this.imageContent = imageContent;
    }

    public WriteCommonModel(long id, String name, String saveCode, String imageContent, long changeTime) {
        this.id = id;
        this.name = name;
        this.saveCode = saveCode;
        this.imageContent = imageContent;
        this.changeTime = changeTime;
    }
}
