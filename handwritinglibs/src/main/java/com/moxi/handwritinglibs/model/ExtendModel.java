package com.moxi.handwritinglibs.model;

/**
 * 扩展字段集合
 * Created by xj on 2017/11/7.
 */

public class ExtendModel {
    public ExtendModel(){}
    public ExtendModel(int background, String encryptPass) {
        this.background = background;
        this.encryptPass = encryptPass;
    }
    public ExtendModel(int background, String encryptPass, int noAllPageReplaceStyle) {
        this.background = background;
        this.encryptPass = encryptPass;
        this.noAllPageReplaceStyle=noAllPageReplaceStyle;
    }
    public ExtendModel(int background, int noAllPageReplaceStyle) {
        this.background = background;
        this.noAllPageReplaceStyle = noAllPageReplaceStyle;
    }

    public ExtendModel(int background, String encryptPass, int noAllPageReplaceStyle, String backgroundFilePath) {
        this.background = background;
        this.encryptPass = encryptPass;
        this.noAllPageReplaceStyle = noAllPageReplaceStyle;
        this.backgroundFilePath = backgroundFilePath;
    }

    public ExtendModel(int background) {
        this.background = background;
    }

    /**
     * 手写笔记背景索引
     */
    public int background=0;
    /**
     * 是否是加密文件
     */
    public String encryptPass="";
    /**
     * 背景替换模式，默认情况是整页替换0,1代表单页替换
     */
    public int noAllPageReplaceStyle=1;
    /**
     * 自定义背景设置背景文件路径
     */
    public  String backgroundFilePath="";
}
