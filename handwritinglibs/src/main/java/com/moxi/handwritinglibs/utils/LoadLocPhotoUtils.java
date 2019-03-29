package com.moxi.handwritinglibs.utils;

import java.io.File;

/**
 * Created by xj on 2018/8/29.
 */

public class LoadLocPhotoUtils  {
    /**
     * 获得保存路径值
     * @param saveCode
     * @param index
     * @return
     */
    public static  String getPicPath(String saveCode, int index){
        File mids=new File(StringUtils.getSDPath()+"NodeCache/");
        if (!mids.exists()){
            mids.mkdirs();
        }
         String  path= StringUtils.getSDPath()+"NodeCache/"+String.valueOf(saveCode+index).hashCode();

        return path;
    }

    public static void removePic(String saveCode,  int index){
        final String  path= getPicPath(saveCode,index);
        com.mx.mxbase.utils.StringUtils.deleteFile(path);
    }
}
