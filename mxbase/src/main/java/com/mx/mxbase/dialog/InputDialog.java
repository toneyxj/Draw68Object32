package com.mx.mxbase.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import com.mx.mxbase.R;
import com.mx.mxbase.base.BaseApplication;
import com.mx.mxbase.view.MXEditText;


/**
 * 输入提示
 * Created by Administrator on 2016/8/24.
 */
public class InputDialog extends Dialog implements View.OnClickListener{
    private TextView titleview;// dialog显示文字控件
    private MXEditText input_new_txt;// dialog显示文字控件
    private Button qiut;// 确定
    private Button insure;// 取消
    private String title;// 标题
    private String hint;// 提示内容
    private  InputListener listener;


    public InputDialog(Context context, int theme, String title, String hint,
                       InputListener listener) {
        super(context, theme);
        this.hint = hint;
        this.title = title;
        this.listener=listener;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.dialog_input);
        input_new_txt = (MXEditText) findViewById(R.id.input_new_txt);
        titleview = (TextView) findViewById(R.id.title);
        qiut = (Button) findViewById(R.id.qiut);
        insure = (Button) findViewById(R.id.insure);

        input_new_txt.setHint(hint);
        titleview.setText(title);

        qiut.setOnClickListener(this);
        insure.setOnClickListener(this);
    }
    public String getTxt(){
        String txt=input_new_txt.getText().toString().trim();
        return txt;
    }


    /**
     * 显示输入提示框
     * @param context 上下文
     * @param title 标题
     * @param hint 输入提示
     * @param listener 监听
     */
    public static InputDialog getdialog(Context context,String title,String hint,
                                 InputListener listener) {
        InputDialog dialog = new InputDialog(context, R.style.dialog,title,
                hint, listener);
        dialog.setCanceledOnTouchOutside(false);
        Window window = dialog.getWindow();
        window.setGravity(Gravity.CENTER);

        window.getDecorView().setPadding(BaseApplication.ScreenWidth/6, 0, BaseApplication.ScreenWidth/6,0);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
        dialog.show();
        return dialog;
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.qiut) {
            if (listener!=null)
            listener.quit();
            closeInput();
            this.dismiss();
        } else if (i == R.id.insure) {
            String value = input_new_txt.getTextMx();

            if (value.equals("")) {
            BaseApplication.Toast("输入不能为空");
                return;
            }
            if (listener!=null)
                listener.insure(value);
            closeInput();
            this.dismiss();
        } else {
        }
    }
    public void closeInput(){
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(input_new_txt.getWindowToken(), 0) ;
    }
    @Override
    protected void onStop() {
        super.onStop();
    }

    public interface  InputListener{
        public void quit();
        public void insure(String input);
    }
}
