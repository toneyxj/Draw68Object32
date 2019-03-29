package com.mx.mxbase.utils;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.text.format.Formatter;

import java.io.File;

/**
 * 内存大小计算
 * Created by xj on 2017/11/29.
 */

public class StorageSizeUtils {
    /**
     * 内部存储总大小
     * @return
     */
    public static long getAvailableInternalMemorySize() {

        File path = Environment.getDataDirectory();

        StatFs stat = new StatFs(path.getPath());

        long blockSize = stat.getBlockSize();

        long availableBlocks = stat.getAvailableBlocks();

        return availableBlocks * blockSize;
    }

    /**
     * 内部存储可用空间大小
     * @return
     */
    public static long getFreeInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getFreeBlocks();
        return availableBlocks * blockSize;
    }
    /**
     * 外部存储总大小
     * @return
     */
    public static long getAvailableExternalMemorySize() {

        File path = Environment.getExternalStorageDirectory();

        StatFs stat = new StatFs(path.getPath());

        long blockSize = stat.getBlockSize();

        long availableBlocks = stat.getAvailableBlocks();

        return availableBlocks * blockSize;
    }

    /**
     * 外部存储可用空间大小
     * @return
     */
    public static long getFreeExternalMemorySize() {
        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getFreeBlocks();
        return availableBlocks * blockSize;
    }

    /**
     * 存储空间转换成字符串
     * @param context 上下文
     * @param size 文件大小
     * @return 返回转换后文字
     */
    public static String formatString(Context context,long size){
        return Formatter.formatFileSize(context,size);
    }
}
