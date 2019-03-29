package com.moxi.handwritinglibs.db.MyContext;

import android.content.Context;
import android.content.ContextWrapper;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;

/**
 * Created by xj on 2018/2/9.
 */

public class CustomPathBackIimageContext extends ContextWrapper {
    private String dbPath="";
    public CustomPathBackIimageContext(Context base,String path) {
        super(base);
        this.dbPath=dbPath;
    }

    @Override
    public File getDatabasePath(String name) {
       if (dbPath==null||dbPath.equals(""))  return super.getDatabasePath(name);
        File result = new File(dbPath + File.separator + name);
        if (!result.getParentFile().exists()) {
            result.getParentFile().mkdirs();
        }
        return result;
    }

    @Override
    public SQLiteDatabase openOrCreateDatabase(String name, int mode, SQLiteDatabase.CursorFactory factory) {
//        return super.openOrCreateDatabase(name, mode, factory);
//        return SQLiteDatabase.openOrCreateDatabase(getDatabasePath(name),mode,factory);
        return SQLiteDatabase.openOrCreateDatabase(getDatabasePath(name), factory);
    }

    @Override
    public SQLiteDatabase openOrCreateDatabase(String name, int mode, SQLiteDatabase.CursorFactory factory, DatabaseErrorHandler errorHandler) {
//        return super.openOrCreateDatabase(name, mode, factory, errorHandler);
        return SQLiteDatabase.openOrCreateDatabase(getDatabasePath(name).getAbsolutePath(), factory,errorHandler);
    }
}
