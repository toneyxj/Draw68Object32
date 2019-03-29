package com.mx.mxbase.model;

import android.graphics.drawable.Drawable;

/**
 * Created by Administrator on 2016/11/22.
 */
public class Programe {
    // 图标
    private Drawable icon;
    // 程序名
    private String name;
    private String pID;
    private String memory;

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPID(String id){
        this.pID = id;
    }
    public  String getPID(){
        return pID;
    }

    public void setMemory(String m){
        this.memory = m;
    }
    public String getMemory(){
        return memory;
    }
}
