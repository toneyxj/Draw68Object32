package com.moxi.writeNote.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.moxi.writeNote.R;
import com.moxi.writeNote.listener.SaveDrawListener;
import com.mx.mxbase.base.BaseApplication;
import com.mx.mxbase.utils.ToastUtils;


/**
 * Created by Administrator on 2016/8/5.
 */
public class SaveDrawDialog extends Dialog implements View.OnClickListener {
    private EditText input_new_txt;// dialog显示文字控件
    private Button qiut;// 确定
    private Button insure;// 取消
    private Button discard;// 取消
    private String content;// 提示内容
    private SaveDrawListener listener;


    public SaveDrawDialog(Context context, int theme, String content,
                          SaveDrawListener listener) {
        super(context, theme);
        this.content = content;
        this.listener=listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.dialog_save_draw);
        input_new_txt = (EditText) findViewById(R.id.input_new_txt);
        qiut = (Button) findViewById(R.id.qiut);
        insure = (Button) findViewById(R.id.insure);
        discard = (Button) findViewById(R.id.discard);
        input_new_txt.setHint(content);

        qiut.setOnClickListener(this);
        insure.setOnClickListener(this);
        discard.setOnClickListener(this);

    }
    public String getTxt(){
        String txt=input_new_txt.getText().toString().trim();
        return txt;
    }


    /**
     * 显示dialog
     *
     * @param context
     *            上下文
     * @param content
     *            提示内容
     * @param listener
     *            如果点击确认返回控件
     */
    public static SaveDrawDialog getdialog(Context context, String content,
                                 SaveDrawListener listener) {
        SaveDrawDialog dialog = new SaveDrawDialog(context, R.style.dialog,
                content, listener);
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
        if (null==listener){
            this.cancel();
            return;
        }
        switch (v.getId()){
            case R.id.qiut:
                closeInput();
                listener.cancel();
                this.dismiss();
                break;
            case R.id.insure:
                String value=input_new_txt.getText().toString().trim();
                if (value.isEmpty()){
                    ToastUtils.getInstance().showToastShort("输入不能为空");
                    return;
                }
                listener.insure(this,value);
                break;
            case R.id.discard:
                closeInput();
                listener.discard();
                this.dismiss();
                break;
        }
    }

    public void closeInput(){
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(input_new_txt.getWindowToken(), 0) ;
    }

}
