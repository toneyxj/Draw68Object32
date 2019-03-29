package com.moxi.writeNote.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.moxi.writeNote.R;
import com.moxi.writeNote.WriteBaseActivity;
import com.moxi.writeNote.utils.UserInformation;
import com.moxi.writeNote.utils.WritePaswordRemovalAsy;
import com.moxi.writeNote.view.passwordManagerWindows;
import com.mx.mxbase.constant.APPLog;
import com.mx.mxbase.interfaces.DevicePasswordCallBack;
import com.mx.mxbase.interfaces.InsureOrQuitListener;
import com.mx.mxbase.utils.GetPasswordUtil;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;

/**
 * 设置界面
 */
public class SettingActivity extends WriteBaseActivity implements View.OnClickListener {
    @Bind(R.id.back_item)
    TextView back_item;

    @Bind(R.id.click_switch)
    FrameLayout click_switch;
    @Bind(R.id.switch_hide)
    Switch switch_hide;

    @Bind(R.id.last_insert)
    TextView last_insert;
    @Bind(R.id.next_insert)
    TextView next_insert;

    @Bind(R.id.single_repleace)
    TextView single_repleace;
    @Bind(R.id.total_repleace)
    TextView total_repleace;

    @Bind(R.id.photo_export)
    LinearLayout photo_export;
    @Bind(R.id.photo_export_path)
    TextView photo_export_path;

    @Bind(R.id.pdf_export)
    LinearLayout pdf_export;
    @Bind(R.id.pdf_export_path)
    TextView pdf_export_path;
    private boolean isCheck = false;
    private boolean sourceCheck = false;
    private  String pas;//密码
    private passwordManagerWindows passwordManagerWindows;

    @Override
    protected int getMainContentViewId() {
        return R.layout.activity_setting;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        back_item.setOnClickListener(this);
        last_insert.setOnClickListener(this);
        next_insert.setOnClickListener(this);
        single_repleace.setOnClickListener(this);
        total_repleace.setOnClickListener(this);
        photo_export.setOnClickListener(this);
        pdf_export.setOnClickListener(this);

        click_switch.setOnClickListener(this);

        isCheck = UserInformation.getInstance().isHidePattern();
        if (savedInstanceState != null) {
            sourceCheck = savedInstanceState.getBoolean("sourceCheck");
        } else {
            sourceCheck = isCheck;
        }
        switch_hide.setChecked(UserInformation.getInstance().isHidePattern());

        settingPageReplace();
        settingPdfPath();
        settingPhotoPath();
        settingInsertStyle();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_item://返回
                onBackPressed();
                break;
            case R.id.click_switch://xc显示隐藏分区内容
                if (isCheck){
                    isCheck = UserInformation.getInstance().setHidePattern_value(false);
                    switch_hide.setChecked(isCheck);
                    return;
                }
                if (passwordManagerWindows!=null)passwordManagerWindows.destory();
                passwordManagerWindows=null;

                new GetPasswordUtil(this, new DevicePasswordCallBack() {
                    @Override
                    public void onSuccess(String password) {
                        APPLog.e("GetPasswordUtil-data",password);
                        try {
                            JSONObject object = new JSONObject(password);
                            pas = object.getString("password");
                            int isOpen = object.getInt("isOpen");
                            if (isOpen != 1) {
                                insureDialog("提示", "请确认是否前往设置界面，开启手写备忘锁", "前往", "取消", "设置", new InsureOrQuitListener() {
                                    @Override
                                    public void isInsure(Object code, boolean is) {
                                        if (is) {
                                            toSettingPasss();
                                        }
                                    }
                                });
                            } else {
                                passwordManagerWindows=new passwordManagerWindows(SettingActivity.this, pas, new passwordManagerWindows.PasswordManagerListener() {
                                    @Override
                                    public void onInputSucess() {

                                        isCheck = UserInformation.getInstance().setHidePattern_value(true);
                                        switch_hide.setChecked(isCheck);
                                        UserInformation.getInstance().setUserEncrypt(pas);
                                        if (!share.getBoolean("first_setting_password_new")){
                                            //第一次进入设置密码进行老数据迁移
                                            dialogShowOrHide(true,"数据迁移");
                                             new WritePaswordRemovalAsy(UserInformation.getInstance().getUserPassword(), new WritePaswordRemovalAsy.RemovalListener() {
                                                 @Override
                                                 public void onremovalback(boolean is) {
                                                     dialogShowOrHide(false,"数据迁移");
                                                     share.setCache("first_setting_password_new",true);
                                                 }
                                             }).execute();
                                        }
                                    }
                                });
                                passwordManagerWindows.showLockDevicePop(click_switch);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFaile(String message) {

                        insureDialog("提示", "请确认是否前往设置界面，设置设备锁密码", "前往", "取消", "设置", new InsureOrQuitListener() {
                            @Override
                            public void isInsure(Object code, boolean is) {
                                if (is) {
                                    toSettingPasss();
                                }
                            }
                        });
                    }
                });
                    isCheck = UserInformation.getInstance().isHidePattern();
                    switch_hide.setChecked(isCheck);
                break;
            case R.id.single_repleace://单页
                settingBackground(1);
                settingPageReplace();
                break;
            case R.id.total_repleace://全部
                settingBackground(0);
                settingPageReplace();
                break;
            case R.id.last_insert://前插
                settingInsertType(true);
                settingInsertStyle();
                break;
            case R.id.next_insert://后插
                settingInsertType(false);
                settingInsertStyle();
                break;
            case R.id.photo_export://导出图片路径
                FileSelectActivity.startFileSelect(this, 10, true, getPhotoExportPath(), "设置");
                break;
            case R.id.pdf_export://导出pdf路径
                FileSelectActivity.startFileSelect(this, 20, true, getPDFExportPath(), "设置");
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (isCheck != sourceCheck) {
            setResult(RESULT_OK);
        }
        this.finish();
    }

    private void settingPageReplace() {
        int pageReplace = getSettingBackground();
        boolean is = pageReplace == 0;//默认全部
        setTextAllImage(single_repleace, !is, R.mipmap.have_select, 2);
        setTextAllImage(total_repleace, is, R.mipmap.have_select, 2);
    }

    private void settingInsertStyle() {
        boolean is = getInsertStype();//默认全部
        setTextAllImage(next_insert, !is, R.mipmap.have_select, 2);
        setTextAllImage(last_insert, is, R.mipmap.have_select, 2);
    }

    private void settingPdfPath() {
        pdf_export_path.setText(getPDFExportPath());
    }

    private void settingPhotoPath() {
        photo_export_path.setText(getPhotoExportPath());
    }

    /**
     * 左边设置图片
     *
     * @param view      设置的文本
     * @param is        显示样式
     * @param trueImage 显示true的image
     * @param position  图片放置方位 0代表左，1代表上，2代表右，其他代表下
     */
    private void setTextAllImage(TextView view, boolean is, int trueImage, int position) {
        if (is) {
            Context context = view.getContext();
            Drawable drawable;
            drawable = context.getResources().getDrawable(trueImage);
            // / 这一步必须要做,否则不会显示.
            drawable.setBounds(0, 0, drawable.getMinimumWidth(),
                    drawable.getMinimumHeight());
            if (position == 0) {
                view.setCompoundDrawables(drawable, null, null, null);
            } else if (position == 1) {
                view.setCompoundDrawables(null, drawable, null, null);
            } else if (position == 2) {
                view.setCompoundDrawables(null, null, drawable, null);
            } else {
                view.setCompoundDrawables(null, null, null, drawable);
            }
        } else {
            view.setCompoundDrawables(null, null, null, null);
        }
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
        if (passwordManagerWindows != null) {
            passwordManagerWindows.destory();
        }
    }

    @Override
    public void onActivitySaveInstanceState(Bundle outState ) {
        outState.putBoolean("sourceCheck", sourceCheck);
    }

    @Override
    public void onActivityRestoreInstanceState(Bundle savedInstanceState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;
        switch (requestCode) {
            case 20://选择pdf
                setPDFExportPath(data.getStringExtra("path"));
                settingPdfPath();
                break;
            case 10://选择图片
                setPhotoExportPath(data.getStringExtra("path"));
                settingPhotoPath();
                break;
            default:
                break;
        }
    }
}
