package com.moxi.writeNote.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by xj on 2017/6/21.
 */

public class DataUtuls {
    public static String getCurrentTime(long date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        String str = format.format(new Date(date));
        return str;
    }
    public static String getTime(long date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss_SSS");
        String str = format.format(new Date(date));
        return str;
    }


}
