package com.moxi.writeNote;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;

import com.moxi.handwritinglibs.listener.UpLogInformationInterface;
import com.moxi.handwritinglibs.utils.ShareUtils;
import com.mx.mxbase.base.BaseActivity;
import com.mx.mxbase.utils.StorageSizeUtils;
import com.mx.mxbase.utils.StringUtils;
import com.mx.mxbase.utils.ToastUtils;

import java.io.File;

/**
 * Created by Administrator on 2017/3/6 0006.
 */

public abstract class WriteBaseActivity extends BaseActivity implements UpLogInformationInterface{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        ActivityUtils.getInstance().addActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        ActivityUtils.getInstance().ClearActivity(this);
    }

    /**
     * 0多页模式，1单页模式
     *
     * @param back
     */
    public void settingBackground(int back) {
        share.setCache(ShareUtils.BACK_STYLE_SETTING, back);
    }

    /**
     * 插入模式，默认后插模式false,前插模式true
     *
     * @param insert
     */
    public void settingInsertType(boolean insert) {
        share.setCache(ShareUtils.PAGE_INSERT_STYLE, insert);
    }

    /**
     * 获得背景替换模式
     */
    public int getSettingBackground() {
        return share.getInt(ShareUtils.BACK_STYLE_SETTING, 1);
    }

    /**
     * 插入模式，默认后插模式false,前插模式true
     */
    public boolean getInsertStype() {
        return share.getBoolean(ShareUtils.PAGE_INSERT_STYLE);
    }

    /**
     * 获得paf导出路径
     *
     * @return
     */
    public String getPDFExportPath() {
        String path = share.getString(ShareUtils.PDF_EXPORT_PATH);
        File f = new File(path);
        if (path.isEmpty() || !f.exists()) {
            File file = StringUtils.getSDFilePath(ShareUtils.writingPad);
            if (file != null) {
                path = file.getAbsolutePath();
            } else {
                path = "";
            }
        }
        return path;
    }

    /**
     * 获得图片导出路径
     *
     * @return
     */
    public String getPhotoExportPath() {
        String path = share.getString(ShareUtils.PHOTO_EXPORT_PATH);
        File f = new File(path);
        if (path.isEmpty() || !f.exists()) {
            File file = StringUtils.getSDFilePath(ShareUtils.writingPad);
            if (file != null) {
                path = file.getAbsolutePath();
            } else {
                path = "";
            }
        }
        return path;
    }

    /**
     * 设置paf导出路径
     *
     * @return
     */
    public void setPDFExportPath(String path) {
        share.setCache(ShareUtils.PDF_EXPORT_PATH, path);
    }

    /**
     * 设置图片导出路径
     *
     * @return
     */
    public void setPhotoExportPath(String path) {
        share.setCache(ShareUtils.PHOTO_EXPORT_PATH, path);
    }

    /**
     * 内存限制判断
     *
     * @return false 表示内存不足
     */
    public boolean internalJuge() {
        long size = StorageSizeUtils.getFreeInternalMemorySize();
        boolean is = com.moxi.handwritinglibs.utils.StringUtils.INTERNAT_CHOCK <= size;
        return is;
    }

    public void toSettingPasss() {
        try {
            Intent input = new Intent();
            ComponentName cnInput = new ComponentName("com.moxi.haierc", "com.moxi.haierc.activity.DevicePasswordManagerActivity");
            input.setComponent(cnInput);
            startActivity(input);
        } catch (Exception e) {
            ToastUtils.getInstance().showToastShort("没有安装此模块");
        }
    }

    @Override
    public void onUpLog(String title, long time) {
//        JSONObject jsonObject=new JSONObject();
//        try {
//            jsonObject.put("title",title);
//            jsonObject.put("time",time);
//            jsonObject.put("Codeid", ConfigInfor.getCodeID(this));
//            jsonObject.put("time-format", DataUtuls.getTime(time));
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        OkHttpUtils.post().url("http://120.25.193.163/app/eBook/openApi/queryLog").addParams("log", jsonObject.toString())
//                .build().connTimeOut(10000).execute(new StringCallback() {
//            @Override
//            public void onError(Call call, Exception e, int id) {
//            }
//
//            @Override
//            public void onResponse(String response, int id) {
//            }
//        });
    }

}

