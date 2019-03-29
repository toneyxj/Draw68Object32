package com.moxi.handwritinglibs.db.help;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.moxi.handwritinglibs.db.DBUtils.BackImageUtils;
import com.moxi.handwritinglibs.db.MyContext.CustomPathBackIimageContext;
import com.moxi.handwritinglibs.utils.LLog;
import com.moxi.handwritinglibs.utils.StringUtils;


/**
 * 自定义背景图片的数据库保存
 * Created by xj on 2018/2/9.
 */

public class DBHelpBackImage extends SQLiteOpenHelper {
    private static final  String TAG="DBHelpBackImage";

    private static DBHelpBackImage mInstance;
    /**
     *
     * 此构造函数用来 创建库和表 （第一次执行时，如果没有库、表，将创建库、表，以后将不再创建）
     * @param context  上下文对象
     * @param name 数据库名称
     * @param factory 创建Cursor的工厂类。参数是为了可以自定义Cursor创建
     * @param version 数据库版本号
     */
    private DBHelpBackImage(Context context, String name, SQLiteDatabase.CursorFactory factory,
                   int version) {
            super(new CustomPathBackIimageContext(context, StringUtils.getSDPath()), name, factory, version);
    }

    /**操作数据时，（增删改查）
     *
     * @param context  上下文对象
     */
    private DBHelpBackImage(Context context) {
        this(context, BackImageUtils.DATABASE_NAME, null, BackImageUtils.DATABASE_VERSION);

    }

    /**需要根据版本号来实现修改
     *
     *此构造函数用来 修改库和表 （修改现有的库、表）
     * @param context  上下文对象
     * @param version 数据库版本号
     */
    private DBHelpBackImage(Context context ,int version) {
        this(context,BackImageUtils.DATABASE_NAME,null,version);
    }

    public static  synchronized DBHelpBackImage getInstance(Context context){
        if (mInstance == null) {
            synchronized (DBHelpBackImage.class) {
                if (mInstance == null) {
                    mInstance = new DBHelpBackImage(context);
                }
            }
        }
        return mInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub

        String createTableSQL = "create table " + BackImageUtils.TABLE_NAME + "("
                +BackImageUtils.ID        + " long  primary key autoincrement,"
                +BackImageUtils.IMAGE_SOURCE_PATH     + " text,"
                +BackImageUtils.IMAGE_CONTENT + " text,"
                +BackImageUtils.IMAGE_ADD_TIME + " long,"
                +BackImageUtils.IMAGE_USE_NUMBER + " long,"
                +BackImageUtils.IMAGE_EXTEND + " text"
                +")";
        db.execSQL(createTableSQL); // 执行SQL 需要使用execSQL（）
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //TODO
        LLog.e(TAG,"onUpgrade更新数据库版本时执行");
//        if (oldVersion==1&&newVersion==2) {
//            //版本为2时添加字段
//            db.execSQL("ALTER TABLE "+BackImageUtils.TABLE_NAME+" ADD COLUMN " + BackImageUtils.E_FILEPATHMD5 + " text");
//        }else {
//            String sql = " DROP TABLE IF EXISTS " + BackImageUtils.TABLE_NAME;
//            db.execSQL(sql);
//            onCreate(db);
//
//        }

    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        super.onOpen(db);
        LLog.e(TAG,"onOpen");
    }

}
