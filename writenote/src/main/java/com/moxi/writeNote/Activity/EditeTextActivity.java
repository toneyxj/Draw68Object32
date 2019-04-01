package com.moxi.writeNote.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.EditText;

import com.moxi.writeNote.R;
import com.moxi.writeNote.WriteBaseActivity;
import com.moxi.writeNote.config.ConfigerUtils;
import com.mx.mxbase.dialog.InputDialog;
import com.mx.mxbase.utils.FileUtils;
import com.mx.mxbase.utils.StringUtils;
import com.mx.mxbase.utils.ToastUtils;

import java.io.File;

public class EditeTextActivity extends WriteBaseActivity implements View.OnClickListener {

    public static void startEditeTextActivity(@NonNull Context context, @NonNull String obj) {
        if (obj == null || obj.isEmpty()) return;
        Intent intent = new Intent(context, EditeTextActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("value", obj);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    private InputDialog inputDialog;
    private long clickTime = 0;

    @Override
    protected int getMainContentViewId() {
        return R.layout.activity_edite_text;
    }

    private EditText edit_txt;
    private String value = "";

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        findViewById(R.id.show_title).setOnClickListener(this);
        findViewById(R.id.save_txt).setOnClickListener(this);
        edit_txt = findViewById(R.id.edit_txt);

        if (savedInstanceState == null) {
            savedInstanceState = getIntent().getExtras();
        }
        value = savedInstanceState.getString("value");

        edit_txt.setText(value);
    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Bundle outState) {
        outState.putString("value", value);
    }

    @Override
    public void onActivityRestoreInstanceState(Bundle savedInstanceState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        if (inputDialog != null && inputDialog.isShowing()) {
            inputDialog.dismiss();
        }
    }

    @Override
    public void onClick(View v) {
        if (Math.abs(System.currentTimeMillis() - clickTime) < 1000) {
            return;
        }
        clickTime = System.currentTimeMillis();

        switch (v.getId()) {
            case R.id.show_title:
                onBackPressed();
                break;
            case R.id.save_txt://保存
                try {
                    String value = edit_txt.getText().toString().trim();
                    if (value.isEmpty()) {
                        ToastUtils.getInstance().showToastShort("当前文本为空");
                        return;
                    }
                    setSaveName(value);
                } catch (Exception e) {
                    e.printStackTrace();
                    ToastUtils.getInstance().showToastShort(e.getMessage());
                }
                break;
            default:
                break;
        }
    }

    private void setSaveName(final String note) {
        try {
            final File fm = new File(StringUtils.getSDPath() + "note/");
            if (!fm.exists()) {
                fm.mkdirs();
            }
            inputDialog = InputDialog.getdialog(this, getString(R.string.file_name), ConfigerUtils.hitnInput, false, new InputDialog.InputListener() {

                @Override
                public void quit() {

                }

                @Override
                public void insure(String name) {
                    if (name.equals("")) {
                        return;
                    }
                    if (ConfigerUtils.isFail(name)) return;
                    File file = new File(fm, name + ".txt");
                    if (file.exists()) {
                        ToastUtils.getInstance().showToastShort("文件名已存在，请重新命名");
                        return;
                    }
                    saveNote(note, file.getAbsolutePath());
                    if (inputDialog != null && inputDialog.isShowing()) {
                        inputDialog.dismiss();
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveNote(final String note, final String path) {
        dialogShowOrHide(true, "");
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean is = false;
                try {
                    is = FileUtils.getInstance().writeFile(path, note);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                final boolean finalIs = is;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (isfinish) return;
                        dialogShowOrHide(false, "");
                        String save = path.replace(StringUtils.getSDCardPath(), "");
                        if (finalIs) {
                            insureDialog("提示","笔记保存于：" + save, "确定",1, true, null);
                        } else {
                            ToastUtils.getInstance().showToastShort("笔记保存失败！");
                        }

                    }
                });
            }
        }).start();
    }
}
