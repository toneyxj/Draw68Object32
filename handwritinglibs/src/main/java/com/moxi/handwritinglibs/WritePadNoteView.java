package com.moxi.handwritinglibs;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.AttributeSet;

import com.moxi.handwritinglibs.asy.SaveNoteWrite;
import com.moxi.handwritinglibs.db.WritPadModel;
import com.moxi.handwritinglibs.listener.DbPhotoListener;
import com.moxi.handwritinglibs.listener.NoteSaveWriteListener;
import com.moxi.handwritinglibs.model.CodeAndIndex;
import com.moxi.handwritinglibs.utils.DbPhotoLoader;

/**
 * 手写板基本view
 * Created by 夏君 on 2017/2/17.
 */
public class WritePadNoteView  extends WritePadBaseView{

    public WritePadNoteView(Context context) {
        super(context);
    }

    public WritePadNoteView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    /**
     * 设置保存绘制的唯一标识
     * @param saveCode 保存绘制的唯一标识
     */
    public void setSaveCode(final String saveCode,int index) {
        onResume();
        setCodeAndIndex(new CodeAndIndex(saveCode,index));
        DbPhotoLoader.getInstance().loaderPhoto(saveCode,index, new DbPhotoListener() {
            @Override
            public void onLoaderSucess(String saveCodes,int index, Bitmap bitmap) {
                if (saveCode.equals(saveCodes)) {
                    setBitmap(bitmap);
                    if (bitmap==null){
                        setCross(true);
                    }
                }
                onPause();
            }
        });
    }
    /**
     * 保存笔记
     * @param model 笔记信息
     */
    public void saveWritePad(WritPadModel model, NoteSaveWriteListener listener){
        if (null==getSaveCode()){return;}
        Bitmap bitmap=getBitmap(false);
        model._index=getIndex();
        model.isFolder=1;
        //更改缓存里面的图片信息
//        DbPhotoLoader.getInstance().addBitmapToLruCache(model.saveCode,model._index,bitmap);
        //异步线程修改保存图片数据信息
        new SaveNoteWrite(model,bitmap,listener,null).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
    /**
     * 清除缓存
     */
    public void ClearLuch(){
        DbPhotoLoader.getInstance().clearBitmap(getSaveCode(),getIndex());
    }
}
