package com.moxi.writeNote.config;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/3/6 0006.
 */

public class ActivityUtils {
    private static ActivityUtils instatnce = null;
    public static ActivityUtils getInstance() {
        if (instatnce == null) {
            synchronized (ActivityUtils.class) {
                if (instatnce == null) {
                    instatnce = new ActivityUtils();
                }
            }
        }
        return instatnce;
    }
    private List<Activity> activities=new ArrayList<Activity>();

    public void addActivity(Activity activity){
        activities.add(activity);
    }
    public void ClearActivity(Activity activity){
        activities.remove(activity);
    }
    public void ClearAllActivity(){
        for (Activity activity:activities){
            activity.finish();
        }
        //结束进程
//        android.os.Process.killProcess(android.os.Process.myPid());
    }
}
