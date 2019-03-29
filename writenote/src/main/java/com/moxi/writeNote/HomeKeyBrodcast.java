package com.moxi.writeNote;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.moxi.writeNote.config.ActivityUtils;

/**
 * Created by Administrator on 2017/3/6 0006.
 */

public class HomeKeyBrodcast  extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
            ActivityUtils.getInstance().ClearAllActivity();
        }
    }
}
