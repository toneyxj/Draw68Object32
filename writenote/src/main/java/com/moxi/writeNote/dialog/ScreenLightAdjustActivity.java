package com.moxi.writeNote.dialog;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.moxi.writeNote.R;
import com.moxi.writeNote.view.ProgressView;
import com.mx.mxbase.base.MyApplication;
import com.mx.mxbase.constant.APPLog;

/**
 * 屏幕亮度调节
 */
public class ScreenLightAdjustActivity extends Activity implements View.OnClickListener,ProgressView.ProgressListener {
        private TextView current_pen_size;
    private ImageView sub;
    private ProgressView current_progress;
    private ImageView add;
    Integer intValues[] = {0,2,10,16,32,48,64,80,96,112,128};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_light_adjust);
        getWindow().getAttributes().width = (int) (MyApplication.ScreenWidth * 0.6);

        current_pen_size = (TextView) findViewById(R.id.current_pen_size);
        sub = (ImageView) findViewById(R.id.sub);
        current_progress = (ProgressView) findViewById(R.id.current_progress);
        add = (ImageView) findViewById(R.id.add);

        sub.setOnClickListener(this);
        add.setOnClickListener(this);
        current_progress.initView(this,10,gecurrentIndex());

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add:
                current_progress.subOrAdd(true);
                break;
            case R.id.sub:
                current_progress.subOrAdd(false);
                break;
            default:
                break;
        }
    }

    @Override
    public void onProgress(int size) {
        current_pen_size.setText("当前亮度："+size);
        if (size>=intValues.length)size=intValues.length-1;
        saveBrightness(this,size);
    }

    private int gecurrentIndex(){
        int now = getScreenBrightness(this);
        APPLog.e("gecurrentIndex-now",now);
        int index=0;
        int size=intValues.length;
        if (now<128) {
            for (int i = 0; i < size; i++) {
                if (now == intValues[i]) {
                    index = i;
                    break;
                } else if ( i< (size - 1) && now < intValues[i + 1]&&now > intValues[i]){
                    index=i;
                    break;
                }else {
                    index=10;
                }
            }
        }else {
            index=10;
        }
        current_pen_size.setText("当前亮度："+index);
        return index;
    }
    /**
     * 获取屏幕的亮度
     */
    private int getScreenBrightness(Context context) {
        int nowBrightnessValue = 0;
//        ContentResolver resolver = context.getContentResolver();
//        try {
//            nowBrightnessValue = android.provider.Settings.System.getInt(resolver, Settings.System.SCREEN_BRIGHTNESS);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        try {
            nowBrightnessValue=Settings.System.getInt(getContentResolver(),Settings.System.SCREEN_BRIGHTNESS_MODE);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        return nowBrightnessValue;
    }
    /**
     * 保存亮度设置状态，退出app也能保持设置状态
     */
    private void saveBrightness(Context context, int brightness) {
//        ContentResolver resolver = context.getContentResolver();
//        Uri uri = android.provider.Settings.System.getUriFor(Settings.System.SCREEN_BRIGHTNESS);
//        android.provider.Settings.System.putInt(resolver, Settings.System.SCREEN_BRIGHTNESS, brightness);
//        resolver.notifyChange(uri, null);
        Settings.System.putInt(getContentResolver(),Settings.System.SCREEN_BRIGHTNESS,brightness);
    }
}
