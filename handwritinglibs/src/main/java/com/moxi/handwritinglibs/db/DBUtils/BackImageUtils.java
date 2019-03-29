package com.moxi.handwritinglibs.db.DBUtils;

/**
 * Created by xj on 2018/2/9.
 */

public class BackImageUtils {
    public final static String DATABASE_NAME="WritenoteBackImage.db"; //数据库名称
    public final static int DATABASE_VERSION=1; //数据库默认版本
    public final static String TABLE_NAME="BackImage"; //数据表名称

    /**添加数据id*/
    public final static String ID="id";
    /**原图片路径*/
    public final static String IMAGE_SOURCE_PATH="sourcePath";
    /**图片转文字保存的内容*/
    public final static String IMAGE_CONTENT="imageContent";
    /**添加自定义背景时间*/
    public final static String IMAGE_ADD_TIME="addTime";
    /**使用背景次数*/
    public final static String IMAGE_USE_NUMBER="useNumber";
    /**扩展变量*/
    public final static String IMAGE_EXTEND="extend";

}
