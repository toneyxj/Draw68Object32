package com.mx.mxbase.interfaces;

/**
 * 确定或删除
 * Created by Administrator on 2016/8/31.
 */
public interface InsureOrQuitListener  {
    /**
     * true为确认 false 为删除\
     * @param  code 标记值
     * @param is 是否是确认
     */
    public void isInsure(Object code,boolean is);
}
