package com.mx.mxbase.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.net.Uri;

import com.mx.mxbase.constant.APPLog;
import com.mx.mxbase.model.CQFileModel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * data/data下文件的读取和删除操作
 * Created by Administrator on 2016/5/6.
 */
public class FileUtils {
    public Context context;

    // 初始化类实列
    private static FileUtils instatnce = null;

    /**
     * 获得软键盘弹出类实列
     *
     * @return 返回初始化实列
     */
    public static FileUtils getInstance() {
        if (instatnce == null) {
            synchronized (FileUtils.class) {
                if (instatnce == null) {
                    instatnce = new FileUtils();
                }
            }
        }
        return instatnce;
    }
    public  List<String> getExtSDCardPathList(){
        List<String> paths = new ArrayList<String>();
        paths.add(StringUtils.isNull(StringUtils.getSDCardPath())?"/mnt/sdcard":StringUtils.getSDCardPath());
//        paths.add("/mnt/sdcard");
        paths.add("/storage/extsd");
        return paths;
    }
    /**
     * 初始化设置
     *
     * @param context
     */
    public static void setInit(Context context) {
        FileUtils.getInstance();
        FileUtils.getInstance().context = context;
        FileUtils.getInstance().getfileMksPath();
        FileUtils.getInstance().getTxtMksPath();
    }

    //写数据
    public boolean writeFile(String fileName, String writestr) throws IOException {
        createFiles(fileName);
        try {
            File file = new File(fileName);
            FileOutputStream fout = new FileOutputStream(file);

            byte[] bytes = writestr.getBytes();

            fout.write(bytes);

            fout.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    /**
     * 复制单个文件
     *
     * @param oldPath String 原文件路径 如：c:/fqf.txt
     * @param newfile String 复制后路径 如：fqf.txt
     * @return boolean
     */
    public void copyFile(String oldPath, String floder,String newfile) throws IOException {


        int bytesum = 0;
        int byteread = 0;
        File oldfile = new File(oldPath);
        if (oldfile.exists()) { //文件存在时
            File filefloder=null;
            if (floder!=null&&!floder.isEmpty()) {
                 filefloder = new File(floder);
                if (!filefloder.exists()) {
                    filefloder.mkdirs();
                }
            }
            InputStream inStream = new FileInputStream(oldPath); //读入原文件
            FileOutputStream fs=null;
            if (filefloder==null){
                fs = new FileOutputStream((new File(newfile)));
            }else {
                fs = new FileOutputStream((new File(filefloder,newfile)));
            }
            byte[] buffer = new byte[1444];
            int length;
            while ((byteread = inStream.read(buffer)) != -1) {
                bytesum += byteread; //字节数 文件大小
                fs.write(buffer, 0, byteread);
            }
            inStream.close();
        }

    }

    /**
     * 读取文件
     *
     * @param fileName 文件名
     * @return 返回读取的数据
     * @throws IOException
     */
    public String readFile(String fileName) throws IOException {
        createFiles(fileName);
        try {
            File file = new File(fileName);
            StringBuffer sb = new StringBuffer();
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            br.close();
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";

    }

    /**
     * 根据路径删除指定的目录或文件，无论存在与否
     *
     * @param sPath 要删除的目录或文件
     * @return 删除成功返回 true，否则返回 false。
     */
    public boolean DeleteFolder(String sPath) {
        boolean flag = false;
        File file = new File(sPath);
        // 判断目录或文件是否存在
        if (!file.exists()) {  // 不存在返回 false
            return flag;
        } else {
            // 判断是否为文件
            if (file.isFile()) {  // 为文件时调用删除文件方法
                return deleteFile(sPath);
            } else {  // 为目录时调用删除目录方法
                return deleteDirectory(sPath);
            }
        }
    }

    /**
     * 删除单个文件
     * @param   sPath    被删除文件的文件名
     * @return 单个文件删除成功返回true，否则返回false
     */
    public boolean deleteFile(String sPath) {
        boolean flag = false;
        File file = new File(sPath);
        // 路径为文件且不为空则进行删除
        if (file.isFile() && file.exists()) {
            file.delete();
            flag = true;
        }
        return flag;
    }

    /**
     * 删除目录（文件夹）以及目录下的文件
     * @param   sPath 被删除目录的文件路径
     * @return  目录删除成功返回true，否则返回false
     */
    public boolean deleteDirectory(String sPath) {
        //如果sPath不以文件分隔符结尾，自动添加文件分隔符
        if (!sPath.endsWith(File.separator)) {
            sPath = sPath + File.separator;
        }
        File dirFile = new File(sPath);
        //如果dir对应的文件不存在，或者不是一个目录，则退出
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return false;
        }
        boolean flag = true;
        //删除文件夹下的所有文件(包括子目录)
        File[] files = dirFile.listFiles();
        for (int i = 0; i < files.length; i++) {
            //删除子文件
            if (files[i].isFile()) {
                flag = deleteFile(files[i].getAbsolutePath());
                if (!flag) break;
            } //删除子目录
            else {
                flag = deleteDirectory(files[i].getAbsolutePath());
                if (!flag) break;
            }
        }
        if (!flag) return false;
        //删除当前目录
        if (dirFile.delete()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 创建文件
     *
     * @param files
     */
    public void createFiles(String files) throws IOException {
        File data = new File(files);
        if (!data.exists()) {
            data.createNewFile();
        }
    }

    /**
     * 创建目录
     *
     * @param mks
     */
    public void createMks(String mks) {
        File data = new File(mks);
        if (!data.exists()) {
            data.mkdirs();
        }

    }

    /**
     * 获得根目录
     *
     * @return
     */
    public String getDataFilePath() {
        return context.getFilesDir().toString() + "/";
    }

    /**
     * 复制asset文件到指定目录
     *
     * @param filename asset下的路径
     * @param newPath  SD卡下保存路径
     */
    public static void copyAssets(Context context, String filename, String newPath) {
        AssetManager assetManager = context.getAssets();
        InputStream in = null;
        OutputStream out = null;
        try {
            in = assetManager.open(filename);
            String newFileName = newPath + "/" + filename;
            out = new FileOutputStream(newFileName);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;
            out.flush();
            out.close();
            out = null;
        } catch (Exception e) {
            Log.e("tag", e.getMessage());
        }
    }

    /**
     * 获得文本文件保存地址目录
     *
     * @return
     */
    public String getTxtMksPath() {
        String maks = getDataFilePath() + "txt/";
        createMks(maks);
        return maks;
    }


    /**
     * 获得db文件保存地址目录
     *
     * @return
     */
    public String getDBMksPath() {
        String maks = getDataFilePath() + "DB/";
        createMks(maks);
        return maks;
    }

    /**
     * 获得文件保存地址目录
     *
     * @return
     */
    public String getfileMksPath() {
        String file = getDataFilePath() + "files/";
        createMks(file);
        return file;
    }

    /**
     * 获得文件保存地址目录
     *
     * @return
     */
    public String getCacheMksPath() {
        String file = getDataFilePath() + "cache/";
        createMks(file);
        return file;
    }

    /**
     * 获得assest文件路径名
     *
     * @param fileName 文件名
     * @return 返回文件路径
     */
    public String getAssestPath(String fileName) {
        return "file:///android_asset/" + fileName;
    }

    /**
     * 打开文件
     *
     * @param context
     * @param file
     */
    public void openFile(Context context, File file) {
        if (file.length()==0||!file.canRead()){
            ToastUtils.getInstance().showToastShort("文件已损坏，无法打开");
            return;
        }
        APPLog.e("openFile-path",file.getAbsoluteFile());
        Intent intent = null;
        //设置intent的data和Type属性。
        String tempName = file.getName().substring(file.getName().lastIndexOf(".")).toLowerCase();
        if (tempName.equals(".pdf")){
//            ComponentName  toActivity = new ComponentName("com.xrz.ebook","org.geometerplus.zlibrary.ui.android.adobe.AdobeMainActivity");
//             intent = new Intent();
//            intent.setComponent(toActivity);
//            intent.putExtra("BookPath",file.getAbsolutePath());
//            intent.putExtra("BookName",file.getName());
//            intent.setAction("android.fbreader.action.VIEW");
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            Uri uri = Uri.parse(file.getAbsolutePath());
            intent = new Intent(Intent.ACTION_MAIN);
            ComponentName componentName = new ComponentName("com.xrz.pdf", "com.artifex.mupdf.MuPDFActivity");
            intent.setComponent(componentName);
            intent.setData(uri);
            intent.setAction(Intent.ACTION_VIEW);

        }else {
            ComponentName toActivity = new ComponentName("com.xrz.ebook","org.geometerplus.android.fbreader.FBReader");
            intent = new Intent();
            intent.setComponent(toActivity);
            intent.setAction("android.intent.action.VIEW");
            intent.putExtra("BookPath",file.getAbsolutePath());
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }
        context.startActivity(intent);
    }

    /**
     * 根据文件后缀名获得对应的MIME类型。
     *
     * @param file
     */
    public String getMIMEType(File file) {
        String type = "*/*";
        String fName = file.getName();
        //获取后缀名前的分隔符"."在fName中的位置。
        int dotIndex = fName.lastIndexOf(".");
        if (dotIndex < 0) {
            return type;
        }
        /* 获取文件的后缀名 */
        String end = fName.substring(dotIndex, fName.length()).toLowerCase();
        if (end == "") return type;
        return type;
    }
    public String createMkdirs(String mid){
        String path=getDataFilePath()+mid+File.separator;
        File file=new File(path);
        if (!file.exists())file.mkdirs();
        return path;
    }

    /**
     * 打开重庆出版设书籍
     * @param context
     * @param model
     * @return
     */
    public void openCQReader(Context context, CQFileModel model){
        try {
            Intent input = new Intent();
            ComponentName cnInput = new ComponentName("cn.chinaxml.epubreader.reder", "cn.chinalxml.epubreader.reader.ui.MainActivity");
            input.setComponent(cnInput);
            input.putExtra("external",true);
            input.putExtra("resourceId",model.resourceId);
            input.putExtra("resourceKey",model.resurceKey);
            input.putExtra("userId",model.userId);
            input.putExtra("savePath",model.savePath);
            context.startActivity(input);
        }catch (Exception e){
            Toastor.getLongToast(context,"未安装阅读器");
        }

    }

    public String getDownloadSystemPath(){
        String path="";
        try {
//             path= Environment.getDownloadCacheDirectory().getAbsolutePath()+"/update.zip";
             path= StringUtils.getSDPath()+"update.zip";
        }catch (Exception e){
        }
        APPLog.e("getDownloadCacheDirectory",path);
        return path;
    }
}
