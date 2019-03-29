package com.mx.mxbase.base;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/10/31.
 */
public abstract class BaseDialog extends Dialog implements View.OnClickListener{
    public BaseDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        ButterKnife.bind(this);

        initDialog();
    }
    public abstract int getLayoutId();
    public abstract void initDialog();
    @Override
    protected void onStop() {
        super.onStop();
        ButterKnife.unbind(this);
    }
}
