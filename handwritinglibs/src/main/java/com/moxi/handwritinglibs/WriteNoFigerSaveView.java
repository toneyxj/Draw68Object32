package com.moxi.handwritinglibs;

import android.content.Context;
import android.util.AttributeSet;

import com.moxi.handwritinglibs.db.WritPadModel;
import com.moxi.handwritinglibs.listener.NoteSaveWriteListener;
import com.moxi.handwritinglibs.model.CodeAndIndex;

/**
 * Created by xj on 2017/6/21.
 */

public class WriteNoFigerSaveView extends WriteNoFingerBaseView{

    public WriteNoFigerSaveView(Context context) {
        super(context);
    }

    public WriteNoFigerSaveView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    /**
     * 设置保存绘制的唯一标识
     * @param saveCode 保存绘制的唯一标识
     */
    public void setSaveCode(final String saveCode,int index) {
        setleaveScribbleMode(false,2);
        setCodeAndIndex(new CodeAndIndex(saveCode,index));
        if (getUpListener()!=null){
            getUpListener().onUpLog("setSaveCode-start-1="+saveCode,System.currentTimeMillis());
        }
//        DbPhotoLoader.getInstance().loaderPhoto(saveCode,index, new DbPhotoListener() {
//            @Override
//            public void onLoaderSucess(String saveCodes,int index, Bitmap bitmap) {
////                if (getUpListener()!=null){
////                    getUpListener().onUpLog("setSaveCode-start-2="+saveCodes,System.currentTimeMillis());
////                }
////                if (saveCode.equals(saveCodes)) {
////                    setBitmap(bitmap);
////                    setleaveScribbleMode(true,3);
////                }
//            }
//        });
    }
    /**
     * 保存笔记
     * @param model 笔记信息
     */
    public void saveWritePad(WritPadModel model, NoteSaveWriteListener listener){
//        historyPoints.clear();
//        Bitmap bitmap=getBitmap(true,0);
//        if (bitmap==null) {
//            if (listener!=null){
//                listener.isSucess(true,model);
//            }
//            return;
//        }
//        setCross(false);
//        model._index=getIndex();
//        model.isFolder=1;
//        Log.e(model.saveCode,String.valueOf(model._index));
//        //更改缓存里面的图片信息
//        DbPhotoLoader.getInstance().addBitmapToLruCache(model.saveCode,model._index,bitmap);
//        //异步线程修改保存图片数据信息
//        new SaveNoteWrite(model,bitmap,listener,getUpListener()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

}
