package com.mx.mxbase.http;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.mx.mxbase.constant.Constant;
import com.mx.mxbase.utils.GsonTools;
import com.mx.mxbase.utils.Log;
import com.mx.mxbase.utils.Toastor;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zhy.http.okhttp.request.RequestCall;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Archer on 16/8/1.
 */
public class MXHttpHelper<T> {
    public static MXHttpHelper instance = null;
    public Context context;


    public static MXHttpHelper getInstance(Context context) {
        if (instance == null) {
            instance = new MXHttpHelper(context.getApplicationContext());
        }
        return instance;
    }

    public MXHttpHelper(Context context) {
        // When I wrote this, only God and I understood what I was doing. Now, God only knows
        this.context = context;
        OkHttpUtils.head().headers(setHeaders());
    }

    private HashMap<String, String> setHeaders() {
        HashMap<String, String> headers = new HashMap<String, String>();
        return headers;
    }

    /**
     * Request By PostString
     */
    public synchronized void postStringBack(final int flag, final String url, HashMap<String, String> parames, final Handler handler, final Class<T> obj) {
        OkHttpUtils.post().url(url).headers(setHeaders()).params(parames).build().
                connTimeOut(Constant.CONNECT_TIMEOUT).execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                Message msg = new Message();
                msg.what = 99999;
                msg.arg1 = flag;
                msg.obj = "网络请求失败，请重试";
                handler.sendMessage(msg);
            }

            @Override
            public void onResponse(String response, int id) {
                try {
                    JSONObject json = new JSONObject(response);
                    String status = json.getString("code");
                    String message = json.getString("msg");
                    if (Constant.SUCCESS.equals(status)) {
                        T t = (T) GsonTools.getPerson(response, obj);
                        Message msg = new Message();
                        msg.what = Integer.parseInt(status);
                        msg.obj = t;
                        msg.arg1 = flag;
                        handler.sendMessage(msg);
                    } else {
                        Message erroMsg = new Message();
                        erroMsg.what = Integer.parseInt(status);
                        erroMsg.obj = message;
                        erroMsg.arg1 = flag;
                        handler.sendMessage(erroMsg);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void downloadFile(String url, String filePath, String fileName) {
        OkHttpUtils.get().url(url).build().execute(new FileCallBack(filePath, fileName) {
            @Override
            public File saveFile(Response response, int id) throws IOException {
                Toastor.showToast(context, "保存文件");
                return super.saveFile(response, id);
            }

            @Override
            public void onBefore(Request request, int id) {
                super.onBefore(request, id);
                Toastor.showToast(context, "开始下载请等待");
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                Log.e("onError---", e.toString());
            }

            @Override
            public void onResponse(File response, int id) {
                Log.e("onResponse---", response.toString());
            }
        });
    }

    /**
     * Request By PostString
     */
    public void post(String url, StringCallback stringCallback) {
        OkHttpUtils.post().url(url).headers(setHeaders()).addFile("", "", null).build().execute(stringCallback);
    }

    /**
     * 取消网络请求
     *
     * @param call
     */
    public void cancelRequest(RequestCall call) {
        OkHttpUtils.getInstance().cancelTag(call);
    }

}
