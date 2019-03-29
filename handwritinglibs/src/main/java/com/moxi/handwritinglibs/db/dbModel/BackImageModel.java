package com.moxi.handwritinglibs.db.dbModel;

/**
 * 背景图片解析数据类
 * Created by xj on 2018/2/9.
 */
public class BackImageModel {
    public long id;
    public String sourcePath;
    public String imageContent;
    public long addTime;
    public long useNumber;
    public String extend;

    public BackImageModel(long id, String sourcePath, String imageContent, long addTime, long useNumber, String extend) {
        this.id = id;
        this.sourcePath = sourcePath;
        this.imageContent = imageContent;
        this.addTime = addTime;
        this.useNumber = useNumber;
        this.extend = extend;
    }
    public BackImageModel(long id, String sourcePath , long addTime, long useNumber, String extend) {
        this.id = id;
        this.sourcePath = sourcePath;
        this.imageContent ="";
        this.addTime = addTime;
        this.useNumber = useNumber;
        this.extend = extend;
    }
}
