package com.moxi.handwritinglibs.db.index;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static com.moxi.handwritinglibs.db.index.DbConfiger.TB_NAME;


/**
 * Created by Administrator on 2017/5/22 0022.
 */

public class IndexSqliteHelper extends SQLiteOpenHelper{
    private static IndexSqliteHelper instatnce;

    public static IndexSqliteHelper getInstance(Context context) {
        if (instatnce == null) {
            synchronized (IndexSqliteHelper.class) {
                if (instatnce == null) {
                    instatnce = new IndexSqliteHelper(context);
                }
            }
        }
        return instatnce;
    }

    private static final String TAG="DownloadSqliteHelper";

    public IndexSqliteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }
    public IndexSqliteHelper(Context context){
        this(context, DbConfiger.DATABASE_NAME, null, DbConfiger.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " +
                DbConfiger.TB_NAME + "(" +
                DbConfiger.saveCode + " varchar," +
                DbConfiger._index + " integer" +
                ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TB_NAME);
        onCreate(db);
        Log.e(TAG, "onUpgrade");
    }

    //更新列
    public void updateColumn(SQLiteDatabase db, String oldColumn, String newColumn, String typeColumn) {
        try {
            db.execSQL("ALTER TABLE " +
                    TB_NAME + " CHANGE " +
                    oldColumn + " " + newColumn +
                    " " + typeColumn
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
