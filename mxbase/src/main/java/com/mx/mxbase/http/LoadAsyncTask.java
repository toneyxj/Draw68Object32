package com.mx.mxbase.http;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import com.mx.mxbase.constant.APPLog;
import com.mx.mxbase.utils.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Archer on 16/8/4.
 */
public class LoadAsyncTask extends AsyncTask<String, String, String> {
    private String fileName;
    private Handler handler;
    private String filePath;

    /**
     * @param filePath 文件保存地址
     * @param fileName 保存目标文件名
     * @param handler  handler回调
     */
    public LoadAsyncTask(String filePath, String fileName, Handler handler) {
        APPLog.e("filePath="+filePath);
        APPLog.e("fileName="+fileName);
        this.fileName = fileName;
        this.handler = handler;
        this.filePath = filePath;
        checkAndCreateDir();
    }

    /* 后台线程 */
    @Override
    protected String doInBackground(String... params) {
            /* 所下载文件的URL */
        try {
            URL url = new URL(params[0]);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            /* URL属性设置 */
            conn.setRequestMethod("GET");
            /* URL建立连接 */
            conn.connect();
            /* 下载文件的大小 */
            int fileOfLength = conn.getContentLength();
            /* 每次下载的大小与总下载的大小 */
            int totallength = 0;
            int length = 0;
            /* 输入流 */
            InputStream in = conn.getInputStream();
            /*保存文件*/
            File dirFile = new File(filePath, fileName);
            /* 输出流 */
            FileOutputStream out = new FileOutputStream(dirFile);
            /* 缓存模式，下载文件 */
            byte[] buff = new byte[1024 * 1024];
            while ((length = in.read(buff)) > 0) {
                totallength += length;
                String str1 = "" + (int) ((totallength * 100) / fileOfLength);
                publishProgress(str1);
                out.write(buff, 0, length);
                Log.e("while", length + "dadada");
            }
            Log.e("保存文件路径", dirFile.getPath());
            /* 关闭输入输出流 */
            in.close();
            out.flush();
            out.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        Message msg = new Message();
        msg.what = 201;
        msg.obj = Integer.parseInt(values[0]) + "%";
        handler.sendMessage(msg);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Log.e("onPreExecute", "开始下载");
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        Message done = new Message();
        done.what = 200;
        done.obj = filePath + "/" + fileName;
        handler.sendMessage(done);
    }

    /* 检查sdcard并创建目录文件 */
    private void checkAndCreateDir() {
        /* 获取sdcard目录 */
        /* 新文件的目录 */
        File newFile = new File(filePath);
        if (!newFile.exists()) {
            /* 如果文件不存在就创建目录 */
            newFile.mkdirs();
        }
    }
}
