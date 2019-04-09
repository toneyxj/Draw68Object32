package com.moxi.writeNote.Activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.EditText;

import com.moxi.writeNote.R;
import com.moxi.writeNote.WriteBaseActivity;
import com.moxi.writeNote.config.ConfigerUtils;
import com.moxi.writeNote.share.ContentBuilderInterface;
import com.moxi.writeNote.share.ContentUtils;
import com.moxi.writeNote.share.ShareCallBack;
import com.moxi.writeNote.share.YingxiangContent;
import com.mx.mxbase.dialog.InputDialog;
import com.mx.mxbase.dialog.ListDialog;
import com.mx.mxbase.utils.FileUtils;
import com.mx.mxbase.utils.StringUtils;
import com.mx.mxbase.utils.ToastUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
        findViewById(R.id.share).setOnClickListener(this);
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
            case R.id.share:
                try {
                    final String value = edit_txt.getText().toString().trim();
                    if (value.isEmpty()) {
                        ToastUtils.getInstance().showToastShort("当前文本为空");
                        return;
                    }
                    if (v.getId() == R.id.save_txt) {
                        setSaveName(value);
                    } else {
                        setShareName();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    ToastUtils.getInstance().showToastShort(e.getMessage());
                }

                break;
            default:
                break;
        }
    }

    private void setShareName() {
        inputDialog = InputDialog.getdialog(this, "内容标题", "请输入分享笔记标题...", false, new InputDialog.InputListener() {

            @Override
            public void quit() {

            }

            @Override
            public void insure(String name) {
                if (null == name || name.equals("") || name.trim().equals("")) {
                    return;
                }
//                File file = new File(fm, name + ".txt");
//                if (file.exists()) {
//                    ToastUtils.getInstance().showToastShort("文件名已存在，请重新命名");
//                    return;
//                }

                selectShare(name);
                if (inputDialog != null && inputDialog.isShowing()) {
                    inputDialog.dismiss();
                }
            }
        });
    }

    private void selectShare(final String title) {

        ListDialog.getdialog(this, "请选择分享笔记平台", new ListDialog.ClickListItemListener() {
            @Override
            public void onClickItem(final int position) {
                ContentBuilderInterface content = null;
                //笔记内容拼接
                if (position == 0) {//印象笔记
                    content = new YingxiangContent();
                } else {//有道笔记
                    ToastUtils.getInstance().showToastShort("暂未开发...");
                }
                if (content == null) return;
                dialogShowOrHide(true, "");
                List<String> list = new ArrayList<>();
                list.add(edit_txt.getText().toString().trim());
                content.getContent(list, null, new ShareCallBack() {
                    @Override
                    public void shareSavePath(final boolean isSucess, final String path) {
                        if (isFinishing()) return;
                        dialogShowOrHide(false, "");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (!isSucess) {
                                    ToastUtils.getInstance().showToastShort("笔记转换失败！！");
                                    return;
                                }

                                Bundle bundle = new Bundle();
                                bundle.putString("title", title);
                                bundle.putString("content", path);
                                bundle.putString("noteBook",ContentUtils.NoteShareDescribe);
                                bundle.putInt("shareType", 1);
                                bundle.putInt("sdkType", position + 1);

                                Intent intent = new Intent();
                                ComponentName cnInput = new ComponentName("com.moxi.biji", "com.moxi.biji.BijiActivity");
                                intent.setComponent(cnInput);
//                                Intent intent=new Intent("com.moxi.biji.start");
                                intent.putExtras(bundle);
                                startActivity(intent);
                            }
                        });


                    }
                });

            }
        }, "印象笔记", "有道云笔记");
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
                            insureDialog("提示", "笔记保存于：" + save, "确定", 1, true, null);
                        } else {
                            ToastUtils.getInstance().showToastShort("笔记保存失败！");
                        }

                    }
                });
            }
        }).start();
    }
}
