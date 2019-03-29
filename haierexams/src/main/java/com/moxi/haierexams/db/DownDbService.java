package com.moxi.haierexams.db;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.cooler.download.DownloadListener;
import com.cooler.download.DownloadManager;
import com.cooler.download.DownloadRequest;
import com.moxi.haierexams.model.UpdateBean;
import com.moxi.haierexams.model.UpdateResult;
import com.mx.mxbase.constant.Constant;
import com.mx.mxbase.utils.FileUtils;
import com.mx.mxbase.utils.GsonTools;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;

public class DownDbService extends Service {
    private static String SERVERS_PATH = "http://cqmoxi.oss-cn-shenzhen.aliyuncs.com" + "/CourseExerciseDB/";
    public static String localDBPath = "/mnt/sdcard/exams/dbdata";
//public static String localDBPath = FileUtils.getInstance().getDataFilePath();
    private DownloadManager downloadManager;

    private DownDbCallback calback;
    private List<Integer> bookIds;
    private boolean isColock = false;
    private int index = 0;

    private List<Integer> failureBookIds;
    private List<Integer> successIds;

    public class MyBinder extends Binder {
        public DownDbService getService() {
            return DownDbService.this;
        }
    }

    public interface DownDbCallback {
        public void onEnd(List<Integer> successIds, List<Integer> failureBookIds);

        public void onStart(String error);

        public void onProgress(int i, long l, long l1, int index, int count);

        public void onUpdate(List<UpdateBean> updates);
    }

    public void setCallBack(DownDbCallback calback) {
        this.calback = calback;
    }

    public DownDbService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        File destDir = new File(localDBPath);
        if (!destDir.exists()) {
            destDir.mkdirs();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    public void downBookFileWithIds(List<Integer> bookIds) {
        if (isColock) {
            if (calback != null)
                calback.onStart("已经开启");
            return;
        }
        isColock = true;
        this.bookIds = bookIds;
        if (bookIds == null || bookIds.size() == 0) {
            if (calback != null)
                calback.onStart("书本id为空");
            return;
        }

        index = 0;
        if (successIds != null) {
            successIds.clear();
        }
        if (failureBookIds != null) {
            failureBookIds.clear();
        }
        if (calback != null)
            calback.onStart("开始下载");
        myDownloadManager();

    }

    private void myDownloadManager() {
        DownloadManager downloadManager = getDownloadManager();
        DownloadRequest downloadRequest = new DownloadRequest();
        downloadRequest.setRetryTime(5);
        downloadRequest.setDownloadListener(new DownloadListener() {
            @Override
            public void onStart(int i, long l) {
                Log.d("TAG", "onStart");
                if (calback != null)
                    calback.onStart("开始下载");
            }

            @Override
            public void onRetry(int i) {
                Log.d("TAG", "onRetry");
                if (calback != null)
                    calback.onStart("尝试连接服务……" + i);
            }

            @Override
            public void onProgress(int i, long l, long l1) {
                if (calback != null) {
                    calback.onProgress(i, l, l1, index, bookIds.size());
                }
            }

            @Override
            public void onSuccess(int i, String s) {
                if (successIds == null) {
                    successIds = new ArrayList<Integer>();
                }
                successIds.add(bookIds.get(index));
                //SQLUtil.getInstance(getApplicationContext()).setBookWithId(localDBPath, String.valueOf(bookIds.get(index)));
                index++;
                if (index < bookIds.size()) {
                    myDownloadManager();
                } else {
                    isColock = false;
                    if (calback != null) {
                        calback.onEnd(successIds, failureBookIds);
                    }
                }
                Log.d("TAG", "onSuccess");
            }

            @Override
            public void onFailure(int i, int i1, String s) {
                if (failureBookIds == null) {
                    failureBookIds = new ArrayList<Integer>();
                }
                failureBookIds.add(bookIds.get(index));
                index++;
                if (index < bookIds.size()) {
                    myDownloadManager();
                } else {
                    isColock = false;
                    if (calback != null) {
                        calback.onEnd(successIds, failureBookIds);
                    }
                }
                Log.d("TAG", "onFailure==>" + s);
            }
        });
        downloadRequest.setUrl(SERVERS_PATH + bookIds.get(index) + ".db");
        downloadRequest.setDestinationPath(getFilePathWithId(bookIds.get(index)));
        downloadManager.add(downloadRequest);
    }

    public static String getFilePathWithId(int bookId) {
        return localDBPath + File.separator + bookId + ".db";
    }

    public DownloadManager getDownloadManager() {
        if (downloadManager == null) {
            synchronized (DownloadManager.class) {
                downloadManager = new DownloadManager();
            }
        }
        return downloadManager;
    }

    /**
     * 检测书本的更新
     *
     * @param jsonId
     */
    public void checkUpdateBook(String jsonId) {
        Log.i("TAG", jsonId);
        HashMap<String, String> parames = new HashMap<>();
        parames.put("update", jsonId);
        OkHttpUtils.post().url(Constant.SUBMIT_DB_UPDATE).params(parames).build().
                connTimeOut(Constant.CONNECT_TIMEOUT).execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                com.mx.mxbase.utils.Log.e("Exception", e.getMessage());
            }

            @Override
            public void onResponse(String response, int id) {
                try {
                    com.mx.mxbase.utils.Log.e("onResponse", response);
                    JSONObject json = new JSONObject(response);
                    String status = json.getString("code");
                    String message = json.getString("msg");
                    if (Constant.SUCCESS.equals(status)) {
                        UpdateResult updateResult = (UpdateResult) GsonTools.getPerson(response,UpdateResult.class);
                        if (updateResult!=null&&updateResult.getResult() != null && updateResult.getResult().size() > 0) {
                            if (calback != null) {
                                calback.onUpdate(updateResult.getResult());
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
