package com.moxi.handwritinglibs;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.AttributeSet;

import com.moxi.handwritinglibs.asy.SaveNoteWrite;
import com.moxi.handwritinglibs.db.WritPadModel;
import com.moxi.handwritinglibs.listener.DbWritePathListener;
import com.moxi.handwritinglibs.listener.NoteSaveWriteListener;
import com.moxi.handwritinglibs.model.CodeAndIndex;
import com.moxi.handwritinglibs.model.WriteModel.WritePageData;
import com.moxi.handwritinglibs.utils.DbWriteModelLoader;
import com.mx.mxbase.constant.APPLog;
import com.mx.mxbase.utils.StringUtils;
import com.mx.mxbase.utils.ToastUtils;

/**
 * Created by xj on 2018/7/17.
 */

public class WriteSurfaceViewDraw extends BaseSurfaceViewDraw {
    public boolean onepageChange=false;
    Handler handler=new Handler();
    private int  thisIndex=0;
    private boolean isfinish=false;
    public WriteSurfaceViewDraw(Context context) {
        super(context);
    }

    public WriteSurfaceViewDraw(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public void setSaveCode(final String saveCode, final int index) {
//        setCanDraw(false,100001);
        if (getPenControl()==null||getPenControl().tagListener==null||!isStart){
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    setSaveCode(saveCode,index);
                }
            },200);
            return;
        }
        APPLog.e("当前手写index="+index);
        thisIndex=index;
        getPenControl().setleaveScribble();
        setCodeAndIndex(new CodeAndIndex(saveCode,index));
        getPenControl().setTagListenerBack("笔记初始化...",false);
        DbWriteModelLoader.getInstance().loaderBackPhoto(saveCode, index, new DbWritePathListener() {
            @Override
            public void onLoaderSucess(String saveCodea, int index, WritePageData bitmap) {
                if (thisIndex==index&&!isfinish) {
                    getPenControl().setTagListenerBack("",false);
                    APPLog.e("获取到了信息"+System.currentTimeMillis());
                    getPenControl().setPageData(bitmap);
                    getPenControl().setleaveScribble();
                }
            }
        });
    }
    /**
     * 保存笔记
     * @param model 笔记信息
     */
    public void saveWritePad(WritPadModel model, NoteSaveWriteListener listener){
        if (StringUtils.isStorageLow10M()){
            ToastUtils.getInstance().showToastShort("内存不足保存笔记可能失败哟！！");
        }

        WritePageData pageData= getPenControl().getPageData();
        if (pageData==null) {
            if (listener!=null){
                listener.isSucess(true,model);
            }
            return;
        }
        getPenControl().setCross(false);
        model._index=getIndex();
        model.isFolder=1;
        APPLog.e(model.saveCode,String.valueOf(model._index));
        //更改缓存里面的图片信息
        DbWriteModelLoader.getInstance().addBitmapToLruCache(model.saveCode,model._index,pageData);
        if (model._index==0){
            onepageChange=true;
        }
        //异步线程修改保存图片数据信息
        new SaveNoteWrite(model,pageData,listener).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        handler.removeCallbacksAndMessages(null);
        getPenControl().refureshWindows(false);
        isfinish=true;
    }
}
