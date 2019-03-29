package com.mx.mxbase.utils;

import android.database.sqlite.SQLiteDatabase;

import com.mx.mxbase.constant.APPLog;

import java.io.File;
import java.io.IOException;

/**
 * 数据库工具类
 * Created by Administrator on 2016/5/10.
 */
public class DataUtils {
//    /**
//     * 历史记录数据
//     */
//    public static final String history = "create table if not exists historyCode (title varchar(20),time varchar(40))";
//    /**
//     * 历史记录数据
//     */
//    public static final String test = "create table if not exists testCode (test varchar(20),time varchar(40))";
//    /**
//     * 文件下载保存
//     */
//    public static final String download = "create table if not exists download (savePath varchar(20),downloadPath varchar(20),totalsize varchar(20),downloadSize varchar(20),status varchar(20),time varchar(40),describe varchar(40),imageUrl varchar(40))";
    /**
     * 保存缓存读写数据
     */
    public static final String write = "create table if not exists %s (type varchar(2),image varchar(60),fileName varchar(20),createDate varchar(30),PathsOrName varchar(60))";

    /**
     * 获得db数据库路径
     *
     * @param name 数据库名称
     * @return 返回数据库文件
     */
    public static File getDBPath(String name) {
//        File file = new File(FileUtils.getInstance().getDBMksPath() + name + ".db");
        File file = new File(StringUtils.getSDPath() + name + ".db");
        return file;
    }

    /**
     * 创建db
     *
     * @param DBfile db文件名
     * @param table  创建语句
     * @return 返回是否创建，true表示已存在，false表示不存在
     */
    public static boolean createDBTable(File DBfile, String table) {
        SQLiteDatabase sqlitedb = null;
        boolean save = true;
        if (!DBfile.exists()) {
            try {
                DBfile.createNewFile();
                save = false;
            } catch (IOException e) {
                APPLog.e("保存数据库", e.getMessage());
                return  false;
            }
        }
        sqlitedb = SQLiteDatabase.openOrCreateDatabase(DBfile, null);
        sqlitedb.execSQL(table);
        sqlitedb.close();
        return save;
    }
}
