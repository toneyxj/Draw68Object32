package com.mx.mxbase.utils;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by Archer on 16/7/26.
 */
public class KeyBoard {
    /**
     * 显示软键盘
     *
     * @param context
     * @param view
     */
    public static void showKeyBoard(Context context, View view) {

    }

    /**
     * 隐藏软键盘
     *
     * @param context
     * @param view
     */
    public static void hideKeyBoard(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
    }
}
