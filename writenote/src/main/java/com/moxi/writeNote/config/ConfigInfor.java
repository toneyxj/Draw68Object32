package com.moxi.writeNote.config;

import android.content.Context;
import android.provider.Settings;

/**
 * Created by Administrator on 2017/2/16.
 */
public class ConfigInfor {
    public static final String rootDir="writeNoteSD";
    /**
     * 临时保存路径
     */
    public static final String temporaryPath="com.moxi.writeNote.Activity.WritePadNoteView";
    private static String codeID=null;
    public static String getCodeID(Context context){
        if (codeID==null||codeID.equals("")){
             codeID = Settings.System.getString(context.getContentResolver(), Settings.System.ANDROID_ID);
        }
        return codeID;
    }
}
