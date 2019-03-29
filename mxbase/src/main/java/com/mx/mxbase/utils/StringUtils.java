package com.mx.mxbase.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.IBinder;
import android.os.StatFs;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.TextAppearanceSpan;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.mx.mxbase.base.BaseApplication;
import com.mx.mxbase.constant.APPLog;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2016/8/2.
 */
public class StringUtils {
    public static final String  WRITENOTE_BACKGROUNG_END=".notejpeg";
    public static final String OPENPASSWORDBRODCAST="open.password.brodcast";
    /**
     * 获得当当阅读截屏图片保存路径
     * @param fileName 文件名不带后缀
     * @return 返回保存文件路径
     */
    public static String getScreenShot(String fileName){
        if (isNull(getSDPath()))return "";
        String screenShot=getSDPath()+"/ScreenShot/"+fileName+"/";
        File file=new File(screenShot);
        if (!file.exists())file.mkdirs();
        return screenShot+System.currentTimeMillis()+".png";

    }


    /**
     * 空返回true, 否则false;
     *
     * @param checkStr
     * @return
     */
    public static boolean isNull(String checkStr) {
        if (checkStr == null || checkStr.trim().equals("null")
                || checkStr.length() == 0 || checkStr.trim().equals("")
                || checkStr.equals("[]")) {
            return true;
        }
        return false;
    }

    /**
     * 保存自定义背景文件夹
     * @return
     */
    public static String getWriteNotePhotoPath(){
       String filePath= StringUtils.getSDPath();
        if (StringUtils.isNull(filePath)){
            ToastUtils.getInstance().showToastShort("无法获取储存卡");
            return "";
        }
        filePath+="writeNote/mybackground";
        File file=new File(filePath);
        if (!file.exists()){
            file.mkdirs();
        }
        return filePath;
    }
    /**
     * 获取sd卡路径
     *
     * @return 返回路径
     */
    public static String getSDPath() {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();// 获取跟目录
        } else {
            return "";
        }
        String path = sdDir.toString() + "/baseData/";
        if (!(new File(path).exists())) {
            new File(path).mkdirs();
        }
        return path;
    }

    /**
     * 根目录开始创建目录
     * @param file
     * @return
     */
    public static File getSDFilePath(String file){
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();// 获取跟目录
        } else {
            ToastUtils.getInstance().showToastShort("内存卡准备有误，请检查！");
            return null;
        }
        File file1=new File(sdDir,file);
        if (!file1.exists()){
            file1.mkdirs();
        }
        return file1;
    }
    /**
     * 获取sd卡路径
     *
     * @return 返回路径
     */
    public static String getSDPath(String filePath) {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();// 获取跟目录
        } else {
            return "";
        }
        String path = sdDir.toString() + "/"+filePath+"/";
        if (!(new File(path).exists())) {
            new File(path).mkdirs();
        }
        return path;
    }

    /**
     * 获得是否有sd卡
     *@param min 最小需要内存数
     * @return 返回true代表当下不宜对sd卡操作
     *
     */
    public static boolean haveSD(int min) {
        if (getSDPath().equals("")) {
            ToastUtils.getInstance().showToastShort("无法进行该操作，请检查SD卡");
            return true;
        }
        String memory = BaseApplication.getMemoryInfo()[1];
        if (memory.contains("MB")) {
            double value = Double.parseDouble(memory.split(" ")[0]);
            if (value < min) {
                ToastUtils.getInstance().showToastShort("内存不足");
                return true;
            }
        } else if (memory.contains("KB")) {
            ToastUtils.getInstance().showToastShort("内存不足");
            return true;
        }
        return false;
    }
    /**
     * 获的时间装换后格式
     *
     * @return返回字符串格式 yyyy-MM-dd HH:mm:ss
     */
    public static String getStringDate(String time) {
        Date currentTime = new Date(Long.parseLong(time));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(currentTime);
        return dateString;
    }
    /**
     * 获的时间装换后格式
     *
     * @return返回字符串格式 yyyy-MM-dd HH:mm:ss
     */
    public static String getStringDate(long time) {
        Date currentTime = new Date(time);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(currentTime);
        return dateString;
    }

    /**
     * 将SD卡文件删除
     *
     * @param file 删除路径
     */
    public static boolean deleteFile(File file) {
        if (file.exists()) {
            if (file.isFile()) {
                file.delete();
            }
            // 如果它是一个目录
            else if (file.isDirectory()) {
                // 声明目录下所有的文件 files[];
                File files[] = file.listFiles();
                for (int i = 0; i < files.length; i++) { // 遍历目录下所有的文件
                    deleteFile(files[i]); // 把每个文件 用这个方法进行迭代
                }
            }
            file.delete();
            return true;
        }
        return false;
    }
    /**
     * 将SD卡文件删除
     *
     * @param file 删除路径
     */
    public static void deleteFile(String file) {
        deleteFile(new File(file));
    }
    /**
     * 回收bitmap
     * @param bitmap
     */
    public static void recycleBitmap(Bitmap bitmap){
        if (bitmap!=null&&!bitmap.isRecycled()){
            bitmap.recycle();
            bitmap=null;
        }
        System.gc();
    }
    /**
     * 获取外置SD卡路径以及TF卡的路径
     * <p>
     * 返回的数据：paths.get(0)肯定是外置SD卡的位置，因为它是primary external storage.
     *
     * @return 所有可用于存储的不同的卡的位置，用一个List来保存
     */
    public static List<String> getExtSDCardPathList() {
        List<String> paths = new ArrayList<String>();
        String extFileStatus = Environment.getExternalStorageState();
        File extFile = Environment.getExternalStorageDirectory();
        //首先判断一下外置SD卡的状态，处于挂载状态才能获取的到
        if (extFileStatus.equals(Environment.MEDIA_MOUNTED)
                && extFile.exists() && extFile.isDirectory()
                && extFile.canWrite()) {
            //外置SD卡的路径
            paths.add(extFile.getAbsolutePath());
        }
        try {
            // obtain executed result of command line code of 'mount', to judge
            // whether tfCard exists by the result
            Runtime runtime = Runtime.getRuntime();
            Process process = runtime.exec("mount");
            InputStream is = process.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line = null;
            int mountPathIndex = 1;
            while ((line = br.readLine()) != null) {
                // format of sdcard file system: vfat/fuse
                if ((!line.contains("fat") && !line.contains("fuse") && !line
                        .contains("storage"))
                        || line.contains("secure")
                        || line.contains("asec")
                        || line.contains("firmware")
                        || line.contains("shell")
                        || line.contains("obb")
                        || line.contains("legacy") || line.contains("data")) {
                    continue;
                }
                String[] parts = line.split(" ");
                int length = parts.length;
                if (mountPathIndex >= length) {
                    continue;
                }
                String mountPath = parts[mountPathIndex];
                if (!mountPath.contains("/") || mountPath.contains("data")
                        || mountPath.contains("Data")) {
                    continue;
                }
                File mountRoot = new File(mountPath);
                if (!mountRoot.exists() || !mountRoot.isDirectory()
                        || !mountRoot.canWrite()) {
                    continue;
                }
                boolean equalsToPrimarySD = mountPath.equals(extFile
                        .getAbsolutePath());
                if (equalsToPrimarySD) {
                    continue;
                }
                //扩展存储卡即TF卡或者SD卡路径
                paths.add(mountPath);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return paths;
    }

    /**
     * 将字符串转成MD5值
     *
     * @param string
     * @return
     */
    public static String stringToMD5(String string) {
        byte[] hash;

        try {
            hash = MessageDigest.getInstance("MD5").digest(
                    string.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }

        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10)
                hex.append("0");
            hex.append(Integer.toHexString(b & 0xFF));
        }

        return hex.toString().toLowerCase();
    }
    /**
     * 新建文件夹路径
     * @param midr
     * @return
     */
    public static String getFilePath(String midr){
        File file=new File(getSDPath()+midr);
        if (!(file.exists())) {
            file.mkdirs();
        }
        return getSDPath()+midr;
    }
    /**
     * 获取sd卡路径
     *
     * @return 返回路径
     */
    public static String getSDCardPath() {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();// 获取跟目录
        } else {
            return "";
        }
        return sdDir.toString();
    }
    /**
     * 关闭软键盘
     *
     * @param context
     */
    public static void closeIMM(Context context, IBinder token) {
        InputMethodManager imm = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        // 得到InputMethodManager的实�?
        if (imm.isActive()) {
            imm.hideSoftInputFromWindow(token,
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘，因为当用户点击EditText时没必要隐藏
     *
     * @param v
     * @param event
     * @return
     */
    public static boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] l = {0, 0};
            v.getLocationInWindow(l);
            int left = l[0], top = l[1], bottom = top + v.getHeight(), right = left
                    + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击EditText的事件，忽略它。
                return false;
            } else {
                return true;
            }
        }
        // 如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditView上，和用户用轨迹球选择其他的焦点
        return false;
    }

    /**
     * 打开本地文件
     * @param openPath
     */
    public static void openLoacationFile(Context context,String openPath){
        try {
            Intent input = new Intent();
            input.putExtra("file", openPath);
            ComponentName cnInput = new ComponentName("com.moxi.locationreader", "com.moxi.locationreader.MainActivity");
            input.setComponent(cnInput);
            context.startActivity(input);
        }catch (Exception e){
            FileUtils.getInstance().openFile(context, new File(openPath));
        }
    }

    /**
     * 拼接字符串颜色
     *
     * @param context        当前上下文
     * @param value          拼接原值
     * @param span           需要拼接的数据集
     * @param changeResource 颜色原值
     * @return 返回拼接后的字符串stple
     */
    public static SpannableStringBuilder getStyle(Context context, String value, String span, int changeResource) {
        SpannableStringBuilder style = new SpannableStringBuilder(value);
        int start = value.indexOf(span);
        int end = start + span.length();
        style.setSpan(new TextAppearanceSpan(context, changeResource), start, end,
                Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        return style;
    }

    /**
     * 内存小于10M
     * @return
     */
    public static boolean isStorageLow10M(){
        StatFs statFs = new StatFs(Environment.getExternalStorageDirectory().getPath());
        long size = statFs.getBlockSize();// 获取分区的大小
        long blocks = statFs.getAvailableBlocks();// 获取可用分区的个数
        long result = blocks * size;
        return result<1024*1024*10;
    }

    /**
     *
     * @param photoType 0,关机，1开机，2待机
     * @return
     */
    public static String getSaveSystemCorrelationPhoto(int photoType){
        String path = "/data/zhangyueeink/";
        switch (photoType){
            case 0:
                path += "shutdown.png";
                break;
            case 1:
                path += "other.png";
                break;
            case 2:
                path += "standy.png";
                break;
            default:
                path += "other1.png";
                break;
        }
        APPLog.e("system_path测试二", path);
        return path;
    }
}
