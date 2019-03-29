package com.mx.mxbase.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.util.LruCache;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.mx.mxbase.interfaces.Sucess;

import java.io.File;
import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

/**
 * Created by Administrator on 2016/8/8.
 */
public class LocationPhotoLoder {
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

    private static LocationPhotoLoder mInstance;

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
    public static LocationPhotoLoder getInstance() {

        if (mInstance == null) {
            synchronized (LocationPhotoLoder.class) {
                if (mInstance == null) {
                    mInstance = new LocationPhotoLoder(3, Type.LIFO);
                }
            }
        }
        return mInstance;
    }

    private LocationPhotoLoder(int threadCount, Type type) {
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
     * @param path
     * @param sucess 代表图片加载完成
     */
    public void loadImage(final String path, final Sucess sucess) {
        loadImage(path, sucess, 0, 0, true);
    }

    /**
     * 记载图片
     *
     * @param path   图片路径
     * @param sucess 加载回调
     * @param width  设定图片宽度
     * @param height 设定图片高度
     * @param isMax  是否最大裁剪
     */
    public void loadImage(final String path, final Sucess sucess, final int width, final int height, final boolean isMax) {
        // set tag
        Handler mHandler = null;
        // UI线程
        if (mHandler == null) {
            mHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    BitmapHolder holder = (BitmapHolder) msg.obj;
                    String path = holder.path;
                    Bitmap bm = getBitmapFromLruCache(path);
                    if (sucess != null)
                        sucess.setSucess(bm, true);
                }
            };
        }

        Bitmap bm = getBitmapFromLruCache(path);
        if (bm != null) {
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
                    File file=new File(path);
                    Bitmap bm;
                    if (width != 0 && height != 0) {
                        bm = decodeSampledBitmapFromResource(path, width, height, isMax);
                    } else if(file.length()>=(1024*2)){//如果图片的大小大于3M进行图片处理
                        bm = decodeSampledBitmapFromResource(path, (int) (1080*1.5), (int) (1440*1.5), isMax);
                    }else {
                        bm = BitmapFactory.decodeFile(path);
                    }
                    if (bm != null) {
                        addBitmapToLruCache(path, bm);
                    }

                    BitmapHolder holder = new BitmapHolder();
//                    holder.bitmap = getBitmapFromLruCache(path);
                    holder.path = path;
                    Message message = Message.obtain();
                    message.obj = holder;
                    finalMHandler.sendMessage(message);
                    mPoolSemaphore.release();
                }
            });
        }

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
    public static LocationPhotoLoder getInstance(int threadCount, Type type) {

        if (mInstance == null) {
            synchronized (LocationPhotoLoder.class) {
                if (mInstance == null) {
                    mInstance = new LocationPhotoLoder(threadCount, type);
                }
            }
        }
        return mInstance;
    }


    /**
     * 根据ImageView获得适当的压缩的宽和�?
     *
     * @param imageView
     * @return
     */
    private ImageSize getImageViewWidth(ImageView imageView) {
        ImageSize imageSize = new ImageSize();
        final DisplayMetrics displayMetrics = imageView.getContext()
                .getResources().getDisplayMetrics();
        final ViewGroup.LayoutParams params = imageView.getLayoutParams();

        int width = params.width == ViewGroup.LayoutParams.WRAP_CONTENT ? 0 : imageView
                .getWidth(); // Get actual image width
        if (width <= 0)
            width = params.width; // Get layout width parameter
        if (width <= 0)
            width = getImageViewFieldValue(imageView, "mMaxWidth"); // Check
        // maxWidth
        // parameter
        if (width <= 0)
            width = displayMetrics.widthPixels;
        int height = params.height == ViewGroup.LayoutParams.WRAP_CONTENT ? 0 : imageView
                .getHeight(); // Get actual image height
        if (height <= 0)
            height = params.height; // Get layout height parameter
        if (height <= 0)
            height = getImageViewFieldValue(imageView, "mMaxHeight"); // Check
        // maxHeight
        // parameter
        if (height <= 0)
            height = displayMetrics.heightPixels;
        imageSize.width = width;
        imageSize.height = height;
        return imageSize;

    }

    /**
     * 从LruCache中获取一张图片，如果不存在就返回null
     */
    private Bitmap getBitmapFromLruCache(String key) {
        return mLruCache.get(key);
    }

    /**
     * lruCache中添加一张图
     *
     * @param key
     * @param bitmap
     */
    public void addBitmapToLruCache(String key, Bitmap bitmap) {
        if (getBitmapFromLruCache(key) == null) {
            if (bitmap != null)
                mLruCache.put(key, bitmap);
        }
    }

    /**
     * 计算inSampleSize，用于压缩图�?
     *
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    private int calculateInSampleSize(BitmapFactory.Options options,
                                      int reqWidth, int reqHeight, boolean isMax) {
        // 源图片的宽度
        int width = options.outWidth;
        int height = options.outHeight;
        int inSampleSize = 1;
        if (isMax) {
            if (width > reqWidth && height > reqHeight) {
                // 计算出实际宽度和目标宽度的比
                int widthRatio = Math.round((float) width / (float) reqWidth);
                int heightRatio = Math.round((float) height / (float) reqHeight);
                inSampleSize = Math.max(widthRatio, heightRatio);
            }
        } else {
            if (width > reqWidth || height > reqHeight) {
                // 计算出实际宽度和目标宽度的比
                int widthRatio = Math.round((float) width / (float) reqWidth);
                int heightRatio = Math.round((float) height / (float) reqHeight);
                inSampleSize = Math.max(widthRatio, heightRatio);
            }
        }
        return inSampleSize;
    }

    /**
     * 根据计算的inSampleSize，得到压缩后图片
     *
     * @param pathName
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    private Bitmap decodeSampledBitmapFromResource(String pathName,
                                                   int reqWidth, int reqHeight, boolean isMax) {
        Bitmap bitmap=null;
        try {
            // 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(pathName, options);
            // 调用上面定义的方法计算inSampleSize�?
            options.inSampleSize = calculateInSampleSize(options, reqWidth,
                    reqHeight, isMax);
            // 使用获取到的inSampleSize值再次解析图�?
            options.inJustDecodeBounds = false;
             bitmap = BitmapFactory.decodeFile(pathName, options);
        }catch (Exception e){
        }
        return bitmap;
    }

    private class BitmapHolder {
        Bitmap bitmap;
        String path;
    }

    private class ImageSize {
        int width;
        int height;
    }

    /**
     * 反射获得ImageView设置的最大宽度和高度
     *
     * @param object
     * @param fieldName
     * @return
     */
    private static int getImageViewFieldValue(Object object, String fieldName) {
        int value = 0;
        try {
            Field field = ImageView.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            int fieldValue = (Integer) field.get(object);
            if (fieldValue > 0 && fieldValue < Integer.MAX_VALUE) {
                value = fieldValue;

                Log.e("TAG", value + "");
            }
        } catch (Exception e) {
        }
        return value;
    }

    public void clearCatch(String path) {
//    Bitmap bitmap=getBitmapFromLruCache(path);
//    if (bitmap!=null){
        mLruCache.remove(path);
//    }
    }
}
