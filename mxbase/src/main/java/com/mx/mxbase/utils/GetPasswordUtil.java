package com.mx.mxbase.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.text.TextUtils;

import com.mx.mxbase.interfaces.DevicePasswordCallBack;

/**
 * Created by King on 2017/12/18.
 */

public class GetPasswordUtil {
    private Context context;
    private Messenger mService;
    private boolean isConn;
    private static final int MSG_SUM = 0x110;
    private DevicePasswordCallBack devicePasswordCallBack;

    public GetPasswordUtil(Context context, DevicePasswordCallBack devicePasswordCallBack) {
        this.context = context;
        this.devicePasswordCallBack = devicePasswordCallBack;
        bindServiceInvoked();
    }

    private Messenger mMessenger = new Messenger(new Handler() {
        @Override
        public void handleMessage(Message msgFromServer) {
            switch (msgFromServer.what) {
                case MSG_SUM:
                    Bundle bundle = msgFromServer.getData();
                    if (bundle == null) {
                        return;
                    }
                    String messageReturn = bundle.getString("password");
                    if (devicePasswordCallBack != null && !TextUtils.isEmpty(messageReturn)) {
                        devicePasswordCallBack.onSuccess(messageReturn);
                    } else {
                        devicePasswordCallBack.onFaile("未设置设备密码");
                    }
                    unbindServiceInvoked();
                    break;
            }
            super.handleMessage(msgFromServer);
        }
    });

    private void bindServiceInvoked() {
        Intent intent = new Intent();
        intent.setAction("com.moxi.aidl.getpassword");
        context.bindService(intent, mConn, Context.BIND_AUTO_CREATE);
    }

    private ServiceConnection mConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = new Messenger(service);
            isConn = true;
            if (isConn) {
                Message msgFromClient = Message.obtain(null, MSG_SUM);
                msgFromClient.replyTo = mMessenger;
                if (isConn) {
                    //往服务端发送消息
                    try {
                        mService.send(msgFromClient);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
            isConn = false;
            if (devicePasswordCallBack != null) {
                devicePasswordCallBack.onFaile("未设置设备密码");
                unbindServiceInvoked();
            }
        }
    };

    private void unbindServiceInvoked() {
        Intent intent = new Intent();
        intent.setAction("com.moxi.aidl.getpassword");
        context.unbindService(mConn);
        mService = null;
        isConn = false;
    }
}
