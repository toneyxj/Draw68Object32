package com.moxi.writeNote.config;

import com.mx.mxbase.utils.ToastUtils;

/**
 * Created by xiajun on 2017/3/1.
 */

public class ConfigerUtils {
    public static final String hitnInput="输入文件名不能包含/ % - ' \" 空格等特殊字符";
    public static boolean isFail(String name){
        name=name.trim();
        if (name.contains("/")||name.contains("%")||name.contains("-")&&name.equals("")&&name.equals(" ")||name.contains("'")||name.contains("\"")){
            ToastUtils.getInstance().showToastShort("文件名称不能包含/ % - ' \" 空格等特殊字符");
            return true;
        }
//        else if (name.length()>8){
//            ToastUtils.getInstance().showToastShort("文件名长度不能超过8个字符");
//            return true;
//        }
        return false;
    }
}
