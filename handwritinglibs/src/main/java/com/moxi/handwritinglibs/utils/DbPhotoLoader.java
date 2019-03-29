package com.moxi.handwritinglibs.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.LruCache;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.moxi.handwritinglibs.db.DBTool.BackImageTools;
import com.moxi.handwritinglibs.db.WritePadUtils;
import com.moxi.handwritinglibs.listener.DbPhotoListener;
import com.moxi.handwritinglibs.model.WriteModel.WLine;
import com.moxi.handwritinglibs.model.WriteModel.WritePageData;
import com.moxi.handwritinglibs.writeUtils.PenUtils;
import com.mx.mxbase.constant.APPLog;
import com.mx.mxbase.utils.LocationImageLoader;
import com.mx.mxbase.utils.WindowsUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 数据库图片加载
 * Created by 夏君 on 2017/1/4.
 */
public class DbPhotoLoader {
    private static DbPhotoLoader instatnce = null;

    public static DbPhotoLoader getInstance(int threadCount) {
        if (instatnce == null) {
            synchronized (DbPhotoLoader.class) {
                if (instatnce == null) {
                    instatnce = new DbPhotoLoader(threadCount);
                }
            }
        }
        return instatnce;
    }

    public static DbPhotoLoader getInstance() {
        if (instatnce == null) {
            synchronized (DbPhotoLoader.class) {
                if (instatnce == null) {
                    instatnce = new DbPhotoLoader(3);
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
     * 图片缓存的核心类
     */
    private LruCache<String, Bitmap> mLruCache;
    /**
     * 线程池
     */
    private ExecutorService mThreadPool;
    /**
     * 运行在UI线程的handler，用于给ImageView设置图片
     */
    private Handler mHandler;

    private DbPhotoLoader(int threadCount) {
        this.threadCount = threadCount;

        // 获取应用程序最大可用内存
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheSize = maxMemory / 16;
        // 设置图片缓存大小为程序最大可用内存的1/8
        mLruCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount();
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

                    ImageView imageView = holder.imageView;
                    String path = holder.path;
                    int index=holder.index;
                    DbPhotoListener listener = holder.listener;
                    Bitmap bitmap=null;
                    if (index==-1) {//自定义背景图片
                        bitmap = getBitmapFromLruCache(path+String.valueOf(holder.imgSource)+"0");
                    }else {//绘制图片获取
                        bitmap = getBitmapFromLruCache(path + index);
                    }
                    if (imageView != null) {
                        if (index==0) {
                            Object tag = imageView.getTag();
                            if (tag != null) {
                                if (!path.equals(tag.toString())) return;
                            }
                        }
                        if (bitmap!=null)
                        imageView.setImageBitmap(bitmap);
                        else imageView.setImageBitmap(null);
                    }

                    if (listener != null)
                        listener.onLoaderSucess(path,index, bitmap);
                }
            };
        }
    }

    /**
     * 获取原图
     * @param id 背景图片保存id
     * @param sourceImg 是否获取原图
     * @param view  图片覆盖view
     * @param listener  获取图片结果监听
     */
    public void loaderBackPhoto(final long id,final boolean sourceImg, final ImageView view, final DbPhotoListener listener){
        createHandler();
        //获得缓存数据
        Bitmap bm = getBitmapFromLruCache(String.valueOf(id)+sourceImg);
        if (bm != null&&!bm.isRecycled()) {
            //构建传输参数
            ImgBeanHolder holder = new ImgBeanHolder();
            holder.imageView = view;
            holder.path = String.valueOf(id);
            holder.index = -1;
            holder.imgSource=sourceImg;
            holder.listener = listener;

            Message message = Message.obtain();
            message.obj = holder;
            mHandler.sendMessage(message);
        } else {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    Bitmap  bm=null;
                    if (view!=null&&!sourceImg) {
                        bm = zoomImage(StringToBackBitmap(id), getImageViewWidth(view));
                    }else {
                        bm=StringToBackBitmap(id);
                    }
                    APPLog.e("bitmap",bm==null);
                    addBitmapToLruCache(String.valueOf(id)+sourceImg, bm);
                    //构建传输参数
                    ImgBeanHolder holder = new ImgBeanHolder();
                    holder.imageView = view;
                    holder.path = String.valueOf(id);
                    holder.index=-1;
                    holder.imgSource=sourceImg;
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
     * 获取本地图片
     * @param path 背景图片路径
     * @param sourceImg 是否获取原图
     * @param view  图片覆盖view
     * @param listener  获取图片结果监听
     */
    public void locationPhoto(final String path, final boolean sourceImg, final ImageView view, final DbPhotoListener listener){
        APPLog.e(path,view.hashCode());
        createHandler();
        //获得缓存数据
        Bitmap bm = getBitmapFromLruCache(path+String.valueOf(sourceImg)+"0");
        if (bm != null&&!bm.isRecycled()) {
            //构建传输参数
            ImgBeanHolder holder = new ImgBeanHolder();
            holder.imageView = view;
            holder.path = path;
            holder.index = -1;
            holder.imgSource=sourceImg;
            holder.listener = listener;

            Message message = Message.obtain();
            message.obj = holder;
            mHandler.sendMessage(message);
        } else {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    if (isStop)return;
                    Bitmap  bm=null;
                    try{
                        if (view!=null&&!sourceImg) {
                            Object tag=view.getTag();
                            if (tag!=null){
                                if (!path.equals(tag.toString()))return;
                            }
                            bm = zoomImage(BitmapFactory.decodeFile(path), getImageViewWidth(view));
                        }else {
                            bm=BitmapFactory.decodeFile(path);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    addBitmapToLruCache(path+String.valueOf(sourceImg), bm);
                    //构建传输参数
                    ImgBeanHolder holder = new ImgBeanHolder();
                    holder.imageView = view;
                    holder.path = path;
                    holder.index=-1;
                    holder.imgSource=sourceImg;
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
    private boolean isStop=false;
    public void isStopPool(boolean isStop){
        this.isStop=isStop;
    }

    /**
     * 加载本地图片
     *
     * @param path     数据库唯一保存地址
     * @param view     承载图片控件
     * @param listener 加载返回监听
     */
    public void loaderPhoto(final String path, final int index, final ImageView view, final DbPhotoListener listener) {
        // UI线程
        createHandler();
        //获得缓存数据
        Bitmap bm = getBitmapFromLruCache(path+index);
        if (bm != null&&!bm.isRecycled()) {
            //构建传输参数
            ImgBeanHolder holder = new ImgBeanHolder();
            holder.imageView = view;
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
                    Bitmap  bm=null;
                    if (view!=null) {
                        Object tag=view.getTag();
                        if (tag!=null){
                            if (!path.equals(tag.toString()))return;
                        }
                        bm = zoomImage(StringToBitmap(path, index), getImageViewWidth(view));
                    }else {
                        bm=StringToBitmap(path, index);
                    }

                    try {
                        String paths=LoadLocPhotoUtils.getPicPath(path,index);
                        FileOutputStream fileOutputStream = new FileOutputStream(paths);
                        bm.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
                        fileOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    addBitmapToLruCache(path,index, bm);
                    //构建传输参数
                    ImgBeanHolder holder = new ImgBeanHolder();
                    holder.imageView = view;
                    holder.path = path;
                    holder.index=index;
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
     * 加载图片
     *
     * @param saveCode 加载图片路径
     * @param view     承载图片view
     */
    public void loaderPhoto(String saveCode,int index, ImageView view) {
        Bitmap bitmap=getBitmapFromLruCache(saveCode+index);
        if (bitmap!=null&&!bitmap.isRecycled()){
            loaderPhoto(saveCode, index, view, null);
            return;
        }
        String path=LoadLocPhotoUtils.getPicPath(saveCode,index);
        if ((new File(path)).exists()&&view!=null){
//            view.setTag(null);
//            GlideUtils.getInstance().locatonPic(view.getContext(),view,path);
//            clearBitmap(saveCode,0);
//            view.setTag(path);
//            DbPhotoLoader.getInstance().loaderPhoto(path, view);
            LocationImageLoader.getInstance().loadImage(path,view,null);
        }else {
            loaderPhoto(saveCode, index, view, null);
        }
    }

    /**
     * 获得图片原尺寸
     *
     * @param saveCode 图片保存路径
     * @param listener 设置返回监听
     */
    public void loaderPhoto(String saveCode,int index, DbPhotoListener listener) {
        loaderPhoto(saveCode,index, null, listener);
    }

    /**
     * 获得图片原尺寸
     *
     * @param saveCode 图片保存路径
     */
    public void loaderPhoto(String saveCode,int index) {
        loaderPhoto(saveCode,index, null, null);
    }

    /**
     * 从LruCache中获取一张图片，如果不存在就返回null
     */
    public Bitmap getBitmapFromLruCache(String key) {
        if (mLruCache.get(key)!=null&&mLruCache.get(key).isRecycled()){
            mLruCache.remove(key);
            return null;
        }
        return mLruCache.get(key);
    }

    /**
     * lruCache中添加一张图
     *
     * @param key
     * @param bitmap
     */
    public void addBitmapToLruCache(String key, Bitmap bitmap) {
       addBitmapToLruCache(key,0,bitmap);
    }
    /**
     * lruCache中添加一张图
     *
     * @param key
     * @param bitmap
     */
    public void addBitmapToLruCacheNo(String key, Bitmap bitmap) {
        if (key==null||bitmap==null)return;
        mLruCache.put(key, bitmap);
    }
    /**
     * lruCache中添加一张图
     *
     * @param key
     * @param bitmap
     */
    public void addBitmapToLruCache(String key,int _index ,Bitmap bitmap) {
        if (key==null||bitmap==null)return;
        mLruCache.put(key+_index, bitmap);
    }

    /**
     * 清除bitmap缓存
     * @param key
     */
    public void clearBitmap(String key){
      clearBitmap(key,0);
    }
    /**
     * 清除bitmap缓存
     * @param key
     */
    public void clearBitmap(String key,int _index){
        String _key=key+_index;
            mLruCache.remove(_key);
    }
    /**
     * 清除bitmap缓存
     * @param id  图片id
     * @param sourceImg 清除原图缓存用true
     */
    public void clearBackBitmap(String id,boolean sourceImg){
            mLruCache.remove(id+sourceImg);
//        }
    }
    /***
     * 图片的缩放方法
     *
     * @param bgimage
     *            ：源图片资源
     * @return
     */
    public static Bitmap zoomImage(Bitmap bgimage, ImageSize size) {
        if (bgimage==null)return null;
        // 获取这个图片的宽和高
        float width = bgimage.getWidth();
        float height = bgimage.getHeight();
        // 创建操作图片用的matrix对象
        Matrix matrix = new Matrix();
        // 计算宽高缩放率
        float scaleWidth = ((float) size.width) / width;
        float scaleHeight = ((float) size.height) / height;
        // 缩放图片动作
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap bitmap = Bitmap.createBitmap(bgimage, 0, 0, (int) width,
                (int) height, matrix, true);
        com.mx.mxbase.utils.StringUtils.recycleBitmap(bgimage);
        return bitmap;
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
    /**
     * 清除bitmap缓存
     * @param Savecode 保存的唯一标示值
     */
    public void clearSaveCodeBitmap(String Savecode,int total){
        for (int i = 0; i < total; i++) {
            String _key=Savecode+i;
//            Bitmap bitmap1=getBitmapFromLruCache(_key);
//            if (bitmap1 != null) {
//                if (!bitmap1.isRecycled())
//                StringUtils.recycleBitmap(bitmap1);
//                mLruCache.remove(_key);
//            }
            mLruCache.remove(_key);
        }
    }
    private class ImgBeanHolder {
        ImageView imageView;
        int index=0;
        String path;
        boolean imgSource;
        DbPhotoListener listener;
    }

    /**
     * 获得图片信息
     * @param saveCode 保存的唯一标识
     * @return 返回bitmap
     */
    private Bitmap StringToBitmap(String saveCode,int index) {
        Bitmap bitmap = null;
        WritePageData pageData=DbWriteModelLoader.getInstance().getBitmapFromLruCache(saveCode+index);
        if (pageData==null) {
            String bitmapString = WritePadUtils.getInstance().getImageContent(saveCode, index);
            if (null == bitmapString || bitmapString.equals("") || bitmapString.equals("null"))
                return bitmap;
            pageData = PenUtils.getWritePage(bitmapString);
            DbWriteModelLoader.getInstance().addBitmapToLruCache(saveCode,index,pageData);
        }
        bitmap = Bitmap.createBitmap(WindowsUtils.WritedrawWidth, WindowsUtils.WritedrawHeight, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bitmap);
        c.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        pageData.nullInit();
        PenUtils penUtils=new PenUtils();
        penUtils.setPageData(pageData);
        List<WLine> lines=penUtils.getDrawPaths(0);
        for (WLine p : lines) {
            p.drawCanvas(c);
        }
        return bitmap;

    }
    /**
     * 获得图片信息
     * @param id 保存的唯一标识
     * @return 返回bitmap
     */
    private Bitmap StringToBackBitmap(long id) {
        Bitmap bitmap = null;
        String bitmapString = BackImageTools.getInstance().getBackImageString(id);
        if (null == bitmapString||bitmapString.equals("")||bitmapString.equals("null")) return bitmap;
        bitmap = Bitmap.createBitmap(WindowsUtils.WritedrawWidth, WindowsUtils.WritedrawHeight, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bitmap);
        c.drawColor(Color.TRANSPARENT);
        return bitmap;

    }

}
