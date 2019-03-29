package com.moxi.handwritinglibs.utils;

import com.alibaba.fastjson.JSON;
import com.moxi.handwritinglibs.model.ExtendModel;

/**
 * Created by xj on 2017/11/7.
 */

public class DataUtils {
    /**
     * 设置扩展字段
     * @param model
     */
    public static String getExtendStr(ExtendModel model) {
        return JSON.toJSONString(model);
    }

    /**
     * 获得笔记扩展字段
     * @return 返回扩展字段
     */
    public static ExtendModel getExtendModel(String extend){
        if (extend.length()==1){
            try {
                int index=Integer.parseInt(extend);
                return new ExtendModel(index);
            }catch (Exception e){
            }
        }
        try {
            return JSON.parseObject(extend,ExtendModel.class);
        }catch (Exception e){
            return new ExtendModel(0,"");
        }
    }
}
