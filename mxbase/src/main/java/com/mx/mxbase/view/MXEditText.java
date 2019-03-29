package com.mx.mxbase.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * Created by xj on 2017/9/25.
 */

public class MXEditText extends EditText {
    public MXEditText(Context context) {
        super(context);
    }

    public MXEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MXEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public String getTextMx() {
        String value = getText().toString();
        //文字结尾有特俗字符输入
        for (int h = value.length() - 1; h >= 0; h--) {
            int c = value.charAt(h);
            if (c > 0) {
                value = value.substring(0, h + 1);
                break;
            }
        }
        return value;

    }
}
