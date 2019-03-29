package com.moxi.handwritinglibs;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.AttributeSet;

import com.moxi.handwritinglibs.asy.SaveCommonWrite;
import com.moxi.handwritinglibs.listener.DbWritePathListener;
import com.moxi.handwritinglibs.model.CodeAndIndex;
import com.moxi.handwritinglibs.model.WriteModel.WritePageData;
import com.moxi.handwritinglibs.utils.BrodcastUtils;
import com.moxi.handwritinglibs.utils.DbWriteModelLoader;
import com.mx.mxbase.interfaces.StopScreenListener;
import com.mx.mxbase.utils.StringUtils;
import com.mx.mxbase.utils.ToastUtils;

/**
 * Created by xj on 2018/7/26.
 */

public class ExameSurfaceViewDraw extends BaseSurfaceViewDraw {
    private Handler handler = new Handler();
    private BrodcastUtils brodcastUtils;
    private boolean isstop=false;
    private String  thisSaveCode="";
    private boolean isfinish=false;

    public ExameSurfaceViewDraw(Context context) {
        super(context);
        initview(context);
    }

    public ExameSurfaceViewDraw(Context context, AttributeSet attrs) {
        super(context, attrs);
        initview(context);
    }

    private void initview(Context context) {
        brodcastUtils = new BrodcastUtils(context, this, new StopScreenListener() {
            @Override
            public void openScreen() {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (isstop)return;
                        fullRefuresh();
                    }
                },300);
            }
        });
        setBack();
        setDrawLineWidth(5);
    }

    private void setBack() {
        if (getPenControl() == null || !isStart) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    setBack();
                }
            }, 200);
        } else {
            getPenControl().setBackIsTran(true);
            getPenControl().isWorte=false;
        }
    }

    /**
     * 设置唯一标识，读取手写记录
     *
     * @param saveCode
     */
    public void setSaveCode(final String saveCode) {
        if (getPenControl() == null || !isStart) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    setSaveCode(saveCode);
                }
            }, 200);
            return;
        }
        this.thisSaveCode=saveCode;
        getPenControl().setleaveScribble();
        setCodeAndIndex(new CodeAndIndex(saveCode, 0));
        DbWriteModelLoader.getInstance().loaderBackPhoto(saveCode, 0, new DbWritePathListener() {
            @Override
            public void onLoaderSucess(String saveCodea, int index, WritePageData bitmap) {
                if (thisSaveCode.equals(saveCodea)&&!isfinish) {
                    setDefaultScrollTo();
                    getPenControl().setPageData(bitmap);
                    getPenControl().setleaveScribble();
                }
            }
        });
    }

    /**
     * 保存笔记
     *
     * @param name 笔记名称
     */
    public void saveWritePad(String name) {
        if (StringUtils.isStorageLow10M()){
            ToastUtils.getInstance().showToastShort("内存不足保存笔记可能失败哟！！");
            return;
        }
        WritePageData pageData = getPenControl().getPageData();
        if (pageData == null) {
            return;
        }
//        String pageStr = pageData.getSaveDate();
        //更改缓存里面的图片信息
        DbWriteModelLoader.getInstance().addBitmapToLruCache(getSaveCode(), pageData);
        //异步线程修改保存图片数据信息
        new SaveCommonWrite(name, getSaveCode(), pageData).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void onPause() {
        setCanDraw(false, 130);
    }

    public void onResume() {
        setCanDraw(true, 131);
    }

    public void onDestory() {
        setleaveScribbleMode(false, 132);
        isstop=true;
        brodcastUtils.destory();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        isstop=true;
        handler.removeCallbacksAndMessages(null);
        getPenControl().refureshWindows(false);
        brodcastUtils.destory();
        isfinish=true;
    }

}
