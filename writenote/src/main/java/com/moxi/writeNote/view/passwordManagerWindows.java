package com.moxi.writeNote.view;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.moxi.writeNote.R;
import com.moxi.writeNote.settingPage.PassWordEditText;
import com.moxi.writeNote.settingPage.PassWordKeyboard;
import com.mx.mxbase.utils.DensityUtil;
import com.mx.mxbase.utils.ToastUtils;
import com.mx.mxbase.utils.Toastor;

/**
 * 设备管理器密码验证
 * Created by xj on 2017/12/18.
 */

public class passwordManagerWindows {
    private String[] KEY = new String[]{
            "1", "2", "3",
            "4", "5", "6",
            "7", "8", "9",
            "删除", "0", "完成"
    };
    private Context context;
    private PopupWindow pswPop;

    private PassWordEditText payEditText;
    private TextView tvTips;
    private String password_str;
    private PasswordManagerListener listener;


    public passwordManagerWindows(Context context, String password_str,PasswordManagerListener listener) {
        this.context = context;
        this.password_str = password_str;
        this.listener=listener;
    }


    public void showLockDevicePop( View view) {
        View contentView = LayoutInflater.from(context).inflate(R.layout.mx_pop_set_lock_password, null);
        if (pswPop == null) {
            pswPop = new PopupWindow(contentView, DensityUtil.getScreenW(context) * 8 / 10,
                    WindowManager.LayoutParams.WRAP_CONTENT, true);
        }
        pswPop.setContentView(contentView);
        payEditText = (PassWordEditText) contentView.findViewById(R.id.psw_lock_device);
        tvTips = (TextView) contentView.findViewById(R.id.tv_tips);
        contentView.findViewById(R.id.img_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pswPop != null) {
                    pswPop.dismiss();
                }
                pswPop = null;
            }
        });
        tvTips.setText("输入设备密码");
        PassWordKeyboard keyboard = (PassWordKeyboard) contentView.findViewById(R.id.psw_lock__keyboard);
        keyboard.setKeyboardKeys(KEY);
        keyboard.setOnClickKeyboardListener(new PassWordKeyboard.OnClickKeyboardListener() {
            @Override
            public void onKeyClick(int position, String value) {
                if (position < 11 && position != 9) {
                    payEditText.add(value);
                } else if (position == 9) {
                    payEditText.remove();
                } else if (position == 11) {
                    if (payEditText.getText().length() == 6) {
                    } else {
                        Toastor.showToast(context, "请输入完整设备密码");
                    }
                }
            }
        });

        /**
         * 当密码输入完成时的回调
         */
        payEditText.setOnInputFinishedListener(new PassWordEditText.OnInputFinishedListener() {
            @Override
            public void onInputFinished(String password) {
                checkpop();
            }
        });
        pswPop.setOutsideTouchable(true);
        pswPop.showAtLocation(view, Gravity.CENTER, 0, 0);
    }

    private void checkpop() {
        String pas = payEditText.getText().toString();
        if (pas.equals(password_str)) {
            listener.onInputSucess();
            destory();
        }else {
            payEditText.removeAll();
            ToastUtils.getInstance().showToastShort("输入设备密码错误");
        }
    }
    public void destory(){
        if (pswPop!=null&&pswPop.isShowing()){
            pswPop.dismiss();
        }
        pswPop=null;
    }
public interface PasswordManagerListener{
        void onInputSucess();
    }
}
