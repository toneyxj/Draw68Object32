package com.mx.mxbase.model;

/**
 * Created by xj on 2018/1/3.
 */

public class MxTextViewModel {
    /**
     * 当页显示文字
     */
    public CharSequence showTxt;
    /**
     * 当前页数
     */
    public int page;
    /**
     * 显示下标
     */
    public int index;

    public MxTextViewModel(CharSequence showTxt, int page, int index) {
        this.showTxt = showTxt;
        this.page = page;
        this.index = index;
    }
}
