package com.moxi.handwritinglibs.db.index;

/**
 * Created by Administrator on 2017/5/22 0022.
 */

public class DbConfiger {
    public static final String DATABASE_NAME = "IndexDbUtils.db";
    public static final String TB_NAME = "indexDb";
    public final static int DATABASE_VERSION=1; //数据库默认版本
    public static final String tableLine="id,saveCode,_index";
    /**唯一分组*/
    public static final String[] onlyCategory=new String[]{DbConfiger.id,DbConfiger.saveCode,DbConfiger._index};

    public static final String id="id";
    /** 唯一标识 */
    public static final String saveCode="saveCode";
    /** 文件上次打开索引 */
    public static final String _index="_index";

}
