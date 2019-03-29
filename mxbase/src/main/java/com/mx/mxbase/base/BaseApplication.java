package com.mx.mxbase.base;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.text.format.Formatter;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Toast;

import com.mx.mxbase.constant.APPLog;
import com.mx.mxbase.netstate.NetChangeObserver;
import com.mx.mxbase.netstate.NetWorkUtil;
import com.mx.mxbase.netstate.NetworkStateReceiver;
import com.mx.mxbase.utils.ActivitysManager;
import com.mx.mxbase.utils.FileUtils;

import org.litepal.LitePalApplication;

import java.io.File;

/**
 * Created by Archer on 16/7/26.
 */
public class BaseApplication extends LitePalApplication {
    private NetChangeObserver mNetChangeObserver;
    public Activity mCurrentActivity;
    public static  int ScreenWidth;
    public static  int ScreenHeight;
    @Override
    public void onCreate() {
        super.onCreate();
        registerNetWorkStateListener();// 注册网络状态监测器
        FileUtils.getInstance().setInit(getApplicationContext());
        ScreenWidth=getWidthOrHeight(0);
        ScreenHeight=getWidthOrHeight(1);
    }
    private void registerNetWorkStateListener() {
        mNetChangeObserver = new NetChangeObserver() {
            @Override
            public void onConnect(NetWorkUtil.netType type) {
                super.onConnect(type);
                try {
                    BaseApplication.this.onConnect(type);
                } catch (Exception e) {
                    // TODO: handle exception
                }
            }

            @Override
            public void onDisConnect() {
                super.onDisConnect();
                try {
                    BaseApplication.this.onDisConnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        };
        NetworkStateReceiver.registerObserver(mNetChangeObserver);
    }

    /**
     * 当前没有网络连接通知
     */
    public void onDisConnect() {
        mCurrentActivity = ActivitysManager.getAppManager().currentActivity();
        if (mCurrentActivity != null) {
            if (mCurrentActivity instanceof BaseActivity) {
                ((BaseActivity) mCurrentActivity).onDisConnect();
            }
        }
    }

    /**
     * 网络连接连接时通知
     */
    protected void onConnect(NetWorkUtil.netType type) {
        mCurrentActivity = ActivitysManager.getAppManager().currentActivity();
        if (mCurrentActivity != null) {
            if (mCurrentActivity instanceof BaseActivity) {
                ((BaseActivity) mCurrentActivity).onConnect(type);
            }
        }
    }
    /**
     *
     * @param msg
     *            提示内容
     */
    public static <T> void Toast(T msg) {
        String msgStr = msg.toString();
        Toast toast = null;
        toast = Toast.makeText(getContext(), msgStr, Toast.LENGTH_SHORT);
        toast.show();
    }
    /**
     * 根据路径获取内存状态
     * @return
     */
    public static String[] getMemoryInfo() {
        File path = Environment.getExternalStorageDirectory();
        // 获得一个磁盘状态对象
        StatFs stat = new StatFs(path.getPath());

        long blockSize = stat.getBlockSize();   // 获得一个扇区的大小

        long totalBlocks = stat.getBlockCount();    // 获得扇区的总数

        long availableBlocks = stat.getAvailableBlocks();   // 获得可用的扇区数量

        // 总空间
        String totalMemory =  Formatter.formatFileSize(getContext(), totalBlocks * blockSize);
        // 可用空间
        String availableMemory = Formatter.formatFileSize(getContext(), availableBlocks * blockSize);
        String[] values=new String[2];
        values[0]=totalMemory;
        values[1]=availableMemory;
        APPLog.e("总空间: " + totalMemory + "\n可用空间: " + availableMemory);
        return values;
    }
    /**
     *
     * @param value
     *            传入0得到宽度，传入其他数得到高度
     * @return 宽度/高度
     */
    @SuppressWarnings("deprecation")
    private  int getWidthOrHeight(int value) {
        Display mDisplay = ((WindowManager) getApplicationContext()
                .getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        if (value == 0) {
            return mDisplay.getWidth();
        }
        return mDisplay.getHeight();
    }
}
