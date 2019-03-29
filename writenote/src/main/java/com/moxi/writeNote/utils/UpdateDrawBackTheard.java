package com.moxi.writeNote.utils;

import android.os.Handler;
import android.os.Message;

import com.moxi.handwritinglibs.db.WritPadModel;
import com.moxi.handwritinglibs.db.WritePadUtils;
import com.moxi.handwritinglibs.model.ExtendModel;
import com.moxi.handwritinglibs.utils.DataUtils;

/**
 * Created by xj on 2017/6/22.
 */

public class UpdateDrawBackTheard implements Runnable {

    private String saveCode;
    private String extend;
    private UpdateListener listener;
    private int index = 0;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                if (listener != null) {
                    listener.onResult();
                }
            }
        }
    };

    public UpdateDrawBackTheard(String saveCode, int index, String extend, UpdateListener listener) {
        this.saveCode = saveCode;
        this.extend = extend;
        this.index = index;
        this.listener = listener;
    }

    @Override
    public void run() {
        if (index < 0) {//全部更换背景模式
            WritePadUtils.getInstance().updateAllExtend(extend, saveCode);
        } else {//单个背景更换模式
            try {
                WritPadModel model = WritePadUtils.getInstance().getWritPadModel(saveCode, index);
                if (model != null) {
                    if (!model.extend.equals(extend))
                        WritePadUtils.getInstance().upDateExtend(extend, model.id);
                }
            } catch (Exception e) {
            }
        }
        WritPadModel model = WritePadUtils.getInstance().getWritPadModel(saveCode, 0);
        if (model != null) {
            ExtendModel exmodel = model.getExtendModel();
            int pagestyle = index == -1 ? 0 : 1;
            if (exmodel.noAllPageReplaceStyle!=pagestyle) {
                exmodel.noAllPageReplaceStyle=pagestyle;
                WritePadUtils.getInstance().upDateTimeAndExtend(model.id, DataUtils.getExtendStr(exmodel));
            }else {
                WritePadUtils.getInstance().upDateTime(model.id);
            }
        }
        handler.sendEmptyMessage(1);
    }

    public interface UpdateListener {
        void onResult();
    }
}
