package com.moxi.writeNote.utils;

import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 计时器工具类
 * Created by weiyi on 16/5/19.
 */
public class TimerUtils {
    private TimerE timerE;

    private int startTime;
    private int endTime;
    /**
     * 计时器
     */
    private Timer timer;

    /**
     * 记时回调接口
     */
    private TimeListener listener;

    public enum TimerE {
        UP, DOWN
    }
    private MyHandler handler = new MyHandler(this);

    private static class MyHandler extends Handler {
        private WeakReference<TimerUtils> reference;

        public MyHandler(TimerUtils context) {
            reference = new WeakReference<TimerUtils>(context);
        }

        @Override
        public void handleMessage(Message msg) {
            TimerUtils activity = (TimerUtils) reference.get();
            if (activity != null) {
                activity.handleMessage(msg);
            }
        }

    }

        public void handleMessage(Message msg) {
            if (startTime == endTime) {
                stopTimer();
                if (listener!=null)
                listener.TimeEnd();
            } else {
                if (listener!=null)
                listener.cuttentTime(startTime);
            }
        }

    /**
     * 时间控件初始化
     *
     * @param timerE    时间处理方式
     * @param startTime 开始时间
     * @param endTime   结束时间
     */
    public TimerUtils(TimerE timerE, int startTime, int endTime, TimeListener listener) {
        this.timerE = timerE;
        this.startTime = startTime;
        this.endTime = endTime;
        this.listener = listener;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(int endTime) {
        this.endTime = endTime;
    }

    /**

     * 开始计时
     */
    public void startTimer() {
        if (timer == null) {
            timer = new Timer();
        }

        TimerTask timerTask = new TimerTask() {
            // 倒数10秒
            @Override
            public void run() {
                // 定义一个消息传过去
                Message msg = new Message();
                if (timerE == TimerE.DOWN) {
                    msg.what = startTime--;
                } else {
                    msg.what = startTime++;
                }
                handler.sendMessage(msg);
            }
        };
        if (listener!=null)
            listener.cuttentTime(startTime);
        timer.schedule(timerTask, 1000, 1000);
    }

    /**
     * 停止计时
     */
    public void stopTimer() {
        if (timer!=null) {
            timer.cancel();
            timer = null;
            handler.removeCallbacksAndMessages(null);
        }
    }


    /**
     * 时分秒转换
     * @param time 当前处理时间
     * @return 返回时分秒数组
     */
    public int[] getTimeAndMinuteAndSecond(int time){
        int[] tms=new int[3];
        tms[0]=time/3600;
        tms[1]=(time/60)%60;
        tms[2]=time%60;
        return tms;
    }

    public interface TimeListener {
        /**
         * 当前时间
         *
         * @param time 显示时间
         */
        public void cuttentTime(int time);

        /**
         * 计结束
         */
        public void TimeEnd();
    }

}
