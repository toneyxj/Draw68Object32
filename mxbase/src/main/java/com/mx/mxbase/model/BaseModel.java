package com.mx.mxbase.model;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

/**
 * model基类
 * Created by Archer on 16/8/1.
 */
public class BaseModel extends DataSupport implements Serializable {
    //错误码
    private int code;
    //错误描述
    private String msg;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
