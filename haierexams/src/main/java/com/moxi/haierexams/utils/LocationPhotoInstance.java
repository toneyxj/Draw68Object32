package com.moxi.haierexams.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.util.LruCache;

import com.mx.mxbase.utils.FileSaveASY;
import com.mx.mxbase.utils.ImageLoadUtils;
import com.mx.mxbase.utils.StringUtils;

import java.io.File;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

/**
 * Created by Administrator on 2016/11/10.
 */
public class LocationPhotoInstance {
    /**
     * 图片缓存的核心类
     */
    private LruCache<String, Bitmap> mLruCache;
    /**
     * 线程�?
     */
    private ExecutorService mThreadPool;
    /**
     * 线程池的线程数量，默认为1
     */
    private int mThreadCount = 1;
    /**
     * 队列的调度方�?
     */
    private Type mType = Type.LIFO;
    /**
     * 任务队列
     */
    private LinkedList<Runnable> mTasks;
    /**
     * 轮询的线�?
     */
    private Thread mPoolThread;
    private Handler mPoolThreadHander;

    /**
     * 运行在UI线程的handler，用于给ImageView设置图片
     */
    private Handler mHandler;

    /**
     * 引入�?��值为1的信号量，防止mPoolThreadHander未初始化完成
     */
    private volatile Semaphore mSemaphore = new Semaphore(0);

    /**
     * 引入�?��值为1的信号量，由于线程池内部也有�?��阻塞线程，防止加入任务的速度过快，使LIFO效果不明�?
     */
    private volatile Semaphore mPoolSemaphore;

    private static LocationPhotoInstance mInstance;

    /**
     * 队列的调度方�?
     *
     * @author zhy
     */
    public enum Type {
        FIFO, LIFO
    }


    /**
     * 单例获得该实例对�?
     *
     * @return
     */
    public static LocationPhotoInstance getInstance() {

        if (mInstance == null) {
            synchronized (LocationPhotoInstance.class) {
                if (mInstance == null) {
                    mInstance = new LocationPhotoInstance(20, Type.LIFO);
                }
            }
        }
        return mInstance;
    }

    private LocationPhotoInstance(int threadCount, Type type) {
        init(threadCount, type);
    }

    private void init(int threadCount, Type type) {
        // loop thread
        mPoolThread = new Thread() {
            @Override
            public void run() {
                Looper.prepare();

                mPoolThreadHander = new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        mThreadPool.execute(getTask());
                        try {
                            mPoolSemaphore.acquire();
                        } catch (InterruptedException e) {
                        }
                    }
                };
                // 释放�?��信号�?
                mSemaphore.release();
                Looper.loop();
            }
        };
        mPoolThread.start();

        // 获取应用程序�?��可用内存
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheSize = maxMemory / 8;
        mLruCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getRowBytes() * value.getHeight();
            }

            ;
        };

        mThreadPool = Executors.newFixedThreadPool(threadCount);
        mPoolSemaphore = new Semaphore(threadCount);
        mTasks = new LinkedList<Runnable>();
        mType = type == null ? Type.LIFO : type;

    }

    /**
     * 加载图片
     *
     * @param path   图片加载路径
     * @param sucess 代表图片加载完成
     */
    public void loadImage(final String path, final LoadPhotoListener sucess) {
        // set tag
        Handler mHandler = null;
        // UI线程
        if (mHandler == null) {
            mHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    BitmapHolder holder = (BitmapHolder) msg.obj;
                    Bitmap bm = holder.bitmap;
                    String path = holder.path;
                    if (sucess != null) {
                        sucess.onLoadSucess(bm, path);
                    }
                }
            };
        }

        Bitmap bm = getBitmapFromLruCache(path);
        if (bm != null && !bm.isRecycled()) {
            BitmapHolder holder = new BitmapHolder();
            holder.bitmap = bm;
            holder.path = path;
            Message message = Message.obtain();
            message.obj = holder;
            mHandler.sendMessage(message);
        } else {
            final Handler finalMHandler = mHandler;
            addTask(new Runnable() {
                @Override
                public void run() {
                    Bitmap bm = BitmapFactory.decodeFile(path);
                    if (bm != null) {
                        addBitmapToLruCache(path, bm);
                    }
                    BitmapHolder holder = new BitmapHolder();
                    holder.bitmap = getBitmapFromLruCache(path);
                    holder.path = path;
                    Message message = Message.obtain();
                    message.obj = holder;
                    finalMHandler.sendMessage(message);
                    mPoolSemaphore.release();
                }
            });
        }

    }

    private class BitmapHolder {
        Bitmap bitmap;
        String path;
    }

    /**
     * 添加�?��任务
     *
     * @param runnable
     */
    private synchronized void addTask(Runnable runnable) {
        try {
            // 请求信号量，防止mPoolThreadHander为null
            if (mPoolThreadHander == null)
                mSemaphore.acquire();
        } catch (InterruptedException e) {
        }
        mTasks.add(runnable);

        mPoolThreadHander.sendEmptyMessage(0x110);
    }

    /**
     * 取出�?��任务
     *
     * @return
     */
    private synchronized Runnable getTask() {
        if (mType == Type.FIFO) {
            return mTasks.removeFirst();
        } else if (mType == Type.LIFO) {
            return mTasks.removeLast();
        }
        return null;
    }

    /**
     * 单例获得该实例对�?
     *
     * @return
     */
    public static LocationPhotoInstance getInstance(int threadCount, Type type) {

        if (mInstance == null) {
            synchronized (LocationPhotoInstance.class) {
                if (mInstance == null) {
                    mInstance = new LocationPhotoInstance(threadCount, type);
                }
            }
        }
        return mInstance;
    }

    /**
     * 从LruCache中获取一张图片，如果不存在就返回null�?
     */
    private Bitmap getBitmapFromLruCache(String key) {
        return mLruCache.get(key);
    }

    /**
     * �?ruCache中添加一张图�?
     *
     * @param key
     * @param bitmap
     */
    private void addBitmapToLruCache(String key, Bitmap bitmap) {
        if (getBitmapFromLruCache(key) == null) {
            if (bitmap != null)
                mLruCache.put(key, bitmap);
        }
    }

    public void deletePhoto(String path) {
        Bitmap bitmap1 = getBitmapFromLruCache(path);
        if (bitmap1 != null && !bitmap1.isRecycled()) {
            ImageLoadUtils.recycleBitmap(bitmap1);
        }
        mLruCache.remove(path);
    }

    /**
     * 添加图片到内存中
     *
     * @param path   保存图片的路径
     * @param bitmap 图片的图形bitmap
     */
    public void addPhoto(String path, Bitmap bitmap) {
        File file = new File(path.substring(0, path.lastIndexOf("/")));
        if (!file.exists()) {
            file.mkdirs();
        }
        Bitmap bitmap1 = getBitmapFromLruCache(path);
        if (bitmap1 != null && !bitmap1.isRecycled()) {
            ImageLoadUtils.recycleBitmap(bitmap1);
        } else
            mLruCache.put(path, bitmap);
        //保存图片到本地
        new FileSaveASY(bitmap, path).execute();
    }

    /**
     * 删除文件夹下面所有文件
     *
     * @param path 文件夹路径
     */
    public void CleatrPhotoMirks(String path) {
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        File[] files = file.listFiles();
        for (int i = 0; i < files.length; i++) {
            String filePath = files[i].getAbsolutePath();
            Bitmap bitmap1 = getBitmapFromLruCache(filePath);
            if (bitmap1 != null && !bitmap1.isRecycled()) {
                ImageLoadUtils.recycleBitmap(bitmap1);
            }
            mLruCache.remove(filePath);
        }
        StringUtils.deleteFile(file);
    }


    public interface LoadPhotoListener {
        void onLoadSucess(Bitmap bitmap, String path);
    }
}
