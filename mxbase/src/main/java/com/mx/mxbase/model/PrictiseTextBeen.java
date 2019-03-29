package com.mx.mxbase.model;

import java.io.Serializable;

/**
 * 练习数据类
 * Created by Administrator on 2016/8/9.
 */
public class PrictiseTextBeen implements Serializable{
    public String pingyin;
    public String text;

    public PrictiseTextBeen(String pingyin, String text) {
        this.pingyin = pingyin;
        this.text = text;
    }
}
