package com.moxi.writeNote.Model;

/**
 * 简单的手写model
 * Created by 夏君 on 2017/2/28.
 */

public class SimpleWriteModel {
    public long id;
    /**
     * 保存文件名
     */
    public String saveCode;
    public String name;
    public int isFloder;
    public String parentCode;

    @Override
    public String toString() {
        return "SimpleWriteModel{" +
                "id=" + id +
                ", saveCode='" + saveCode + '\'' +
                ", name='" + name + '\'' +
                ", isFloder=" + isFloder +
                ", parentCode='" + parentCode + '\'' +
                '}';
    }
}
