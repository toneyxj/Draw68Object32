package com.mx.mxbase.base.lifecycle;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.mx.mxbase.R;
import com.mx.mxbase.dialog.HitnDialog;
import com.mx.mxbase.interfaces.InsureOrQuitListener;
import com.mx.mxbase.utils.Log;
import com.mx.mxbase.view.AlertDialog;

import java.lang.ref.WeakReference;

import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/11/1.
 */
public abstract class BaseNoInterfaceActivity extends FragmentActivity {
    public MyHandler mHandler;
    public boolean isfinish=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firstAbove();
        setContentView(getLayoutId()); // set view
        ButterKnife.bind(this);
        mHandler = new MyHandler(this);

        initActivity(savedInstanceState);
    }
    public void firstAbove(){
    }

    public Handler getHandler() {
        return mHandler;
    }
    public abstract void initActivity(Bundle savedInstanceState);

    public abstract int getLayoutId();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mHandler != null) mHandler.removeCallbacksAndMessages(null);
        ButterKnife.unbind(this);
        isfinish=true;
        dialogShowOrHide(false, "");
    }

    private static class MyHandler extends Handler {

        WeakReference<BaseNoInterfaceActivity> mReference = null;

        MyHandler(BaseNoInterfaceActivity activity) {
            this.mReference = new WeakReference<BaseNoInterfaceActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            BaseNoInterfaceActivity outer = mReference.get();
            if (outer == null || outer.isFinishing()) {
                Log.e("outer is null");
                return;
            }

            outer.handleMessage(msg);
        }
    }

    public void handleMessage(Message msg) {
    }

    private HitnDialog dialog;

    public HitnDialog dialogShowOrHide(boolean is,String  hitn) {
        if (isfinish)return null;
        if(dialog!=null){
            dialog.dismiss();
            dialog=null;
        }
        if (is) {
            dialog = new HitnDialog(this, R.style.AlertDialogStyle,hitn);
//            dialog.setMessage(hitn);
            dialog.setCancelable(false);// 是否可以关闭dialog
            dialog.setCanceledOnTouchOutside(false);
            try {
                dialog.show();
            }catch (Exception e){

            }
        }
        return dialog;
    }

    /*
    对软键盘的弹出与隐藏进行处理
    *
            * @param isShow
    */
    protected void showKeyboard(boolean isShow) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (isShow) {
            if (getCurrentFocus() == null) {
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            } else {
                imm.showSoftInput(getCurrentFocus(), 0);
            }
        } else {
            if (getCurrentFocus() != null) {
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    public void insureDialog(String content, Object code, InsureOrQuitListener listener) {
        insureDialog("提示", content, code, listener);
    }

    public void insureDialog(String hitn, String content, Object code, InsureOrQuitListener listener) {
        insureDialog(hitn, content, "确定", "取消", code, listener);
    }

    public void insureDialog(String hitn, String content, String insure, String quit, final Object code, final InsureOrQuitListener listener) {
        //没有问题可以进行移动
        new AlertDialog(this).builder().setTitle(hitn).setCancelable(false).setMsg(content).
                setNegativeButton(insure, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (listener != null) {
                            listener.isInsure(code, true);
                        }
                    }
                }).setPositiveButton(quit, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.isInsure(code, false);
                }
            }
        }).show();
    }
}
