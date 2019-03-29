package com.moxi.handwritinglibs.utils;

import android.os.Handler;
import android.os.Message;
import android.util.LruCache;

import com.moxi.handwritinglibs.db.WritePadUtils;
import com.moxi.handwritinglibs.listener.DbWritePathListener;
import com.moxi.handwritinglibs.model.WriteModel.WritePageData;
import com.moxi.handwritinglibs.writeUtils.PenUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by xj on 2018/7/16.
 */

public class DbWriteModelLoader {
    private static DbWriteModelLoader instatnce = null;

    public static DbWriteModelLoader getInstance(int threadCount) {
        if (instatnce == null) {
            synchronized (DbPhotoLoader.class) {
                if (instatnce == null) {
                    instatnce = new DbWriteModelLoader(threadCount);
                }
            }
        }
        return instatnce;
    }

    public static DbWriteModelLoader getInstance() {
        if (instatnce == null) {
            synchronized (DbWriteModelLoader.class) {
                if (instatnce == null) {
                    instatnce = new DbWriteModelLoader(3);
                }
            }
        }
        return instatnce;
    }

    /**
     * 开启线程数,默认为5
     */
    private int threadCount = 3;
    /**
     * 缓存的核心类
     */
    private LruCache<String, WritePageData> mLruCache;
    /**
     * 线程池
     */
    private ExecutorService mThreadPool;
    /**
     * 运行在UI线程的handler，用于给ImageView设置图片
     */
    private Handler mHandler;

    private DbWriteModelLoader(int threadCount) {
        this.threadCount = threadCount;

        // 获取应用程序最大可用内存
        long maxMemory = Runtime.getRuntime().maxMemory();
        int cacheSize = (int) (maxMemory / 8);
        // 设置图片缓存大小为程序最大可用内存的1/8
        mLruCache = new LruCache<String, WritePageData>(cacheSize) {
            @Override
            protected int sizeOf(String key, WritePageData bitmap) {
                return bitmap.getSize();
            }
        };
        mThreadPool = Executors.newFixedThreadPool(threadCount);
    }

    private void createHandler() {
        if (mHandler == null) {
            mHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    ImgBeanHolder holder = (ImgBeanHolder) msg.obj;

                    WritePageData pageData;
                    String path = holder.path;
                    int index = holder.index;
                    DbWritePathListener listener = holder.listener;
                    pageData = getBitmapFromLruCache(path + index);
                    if (listener != null)
                        listener.onLoaderSucess(path, index, pageData);
                }
            };
        }
    }

    /**
     * 获取原图
     *
     * @param listener 获取图片结果监听
     */
    public void loaderBackPhoto(final String path, final int index, final DbWritePathListener listener) {
        if (index < 0) return;
        createHandler();
        //获得缓存数据
        WritePageData data = getBitmapFromLruCache(String.valueOf(path + index));
        if (data != null) {
            if (listener == null) return;
            //构建传输参数
            ImgBeanHolder holder = new ImgBeanHolder();
            holder.path = path;
            holder.index = index;
            holder.listener = listener;

            Message message = Message.obtain();
            message.obj = holder;
            mHandler.sendMessage(message);
        } else {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    WritePageData bm = StringToBitmap(path, index);
                    addBitmapToLruCache(path, index, bm);
                    if (listener == null) return;
                    //构建传输参数
                    ImgBeanHolder holder = new ImgBeanHolder();
                    holder.path = path;
                    holder.index = index;
                    holder.listener = listener;

                    Message message = Message.obtain();
                    message.obj = holder;
                    mHandler.sendMessage(message);
                }
            };
            //添加入线程池
            mThreadPool.execute(runnable);
        }
    }

    /**
     * 从LruCache中获取一张图片，如果不存在就返回null
     */
    public WritePageData getBitmapFromLruCache(String key) {
        return mLruCache.get(key);
    }

    /**
     * lruCache中添加一张图
     *
     * @param key
     * @param data
     */
    public void addBitmapToLruCache(String key, WritePageData data) {
        addBitmapToLruCache(key, 0, data);
    }

    /**
     * lruCache中添加一张图
     *
     * @param key
     * @param data
     */
    public void addBitmapToLruCache(String key, int _index, WritePageData data) {
        if (key == null || data == null) return;
        try {
            mLruCache.put(key + _index, data);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 清除bitmap缓存
     *
     * @param key
     */
    public void clearBitmap(String key) {
        clearBitmap(key, 0);
    }

    /**
     * 清除bitmap缓存
     *
     * @param key
     */
    public void clearBitmap(String key, int _index) {
        String _key = key + _index;
        mLruCache.remove(_key);
    }

    /**
     * 清除bitmap缓存
     *
     * @param id        图片id
     * @param sourceImg 清除原图缓存用true
     */
    public void clearBackBitmap(String id, boolean sourceImg) {
        mLruCache.remove(id + sourceImg);
//        }
    }

    /**
     * 清除bitmap缓存
     *
     * @param Savecode 保存的唯一标示值
     */
    public void clearSaveCodeBitmap(String Savecode, int total) {
        for (int i = 0; i < total; i++) {
            String _key = Savecode + i;
            mLruCache.remove(_key);
        }
    }


    private class ImgBeanHolder {
        int index;
        String path;
        DbWritePathListener listener;
    }

    /**
     * 获得图片信息
     *
     * @param saveCode 保存的唯一标识
     * @return 返回bitmap
     */
    private WritePageData StringToBitmap(String saveCode, int index) {
        String bitmapString = WritePadUtils.getInstance().getImageContent(saveCode, index);
        if (null == bitmapString || bitmapString.equals("") || bitmapString.equals("null"))
            return new WritePageData();
        return PenUtils.getWritePage(bitmapString);

    }

}
