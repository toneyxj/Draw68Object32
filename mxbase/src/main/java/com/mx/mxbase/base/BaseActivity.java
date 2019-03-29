package com.mx.mxbase.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.text.SpannableStringBuilder;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.mx.mxbase.R;
import com.mx.mxbase.base.block.CommonBlockManager;
import com.mx.mxbase.base.lifecycle.ActivityLifecycleCallbacksCompat;
import com.mx.mxbase.dialog.HitnDialog;
import com.mx.mxbase.interfaces.ClickBackListener;
import com.mx.mxbase.interfaces.InsureOrQuitListener;
import com.mx.mxbase.netstate.NetWorkUtil;
import com.mx.mxbase.netstate.NetworkStateReceiver;
import com.mx.mxbase.utils.ActivitysManager;
import com.mx.mxbase.utils.Log;
import com.mx.mxbase.utils.SharePreferceUtil;
import com.mx.mxbase.utils.StringUtils;
import com.mx.mxbase.view.AlertDialog;

import java.lang.ref.WeakReference;

import butterknife.ButterKnife;

/**
 * @author ChunfaLee(ly09219@gmail.com)
 * @date 2016年8月1日10:52:30
 */
public abstract class BaseActivity extends FragmentActivity implements ActivityLifecycleCallbacksCompat {

    public Context mContext;

    public MyHandler mHandler;

    public CommonBlockManager mCommonBlockManager;

    private Toast toast;
    public SharePreferceUtil share;
    public boolean isfinish=false;

    private AlertDialog alertDialog;
    private int screenRefresh=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getMainContentViewId() != 0) {
            setContentView(getMainContentViewId()); // set view
        }

        ActivitysManager.getAppManager().addActivity(this);
        mContext = getApplicationContext();
        share= SharePreferceUtil.getInstance(this);
        ButterKnife.bind(this);
        NetworkStateReceiver.registerNetworkStateReceiver(this);
        mHandler = new MyHandler(this);
        mCommonBlockManager = getCommonBlockManager();
        toast=getToast("");
        onActivityCreated(this, savedInstanceState);
    }
    public void showToast(Object value){
        toast.setText(value.toString());
        toast.show();
    }
    /**
     *
     * @param msg
     *            提示内容
     */
    private  <T> Toast getToast(T msg) {
        String msgStr = msg.toString();
        Toast toast = Toast.makeText(getApplicationContext(), msgStr, Toast.LENGTH_SHORT);
        return toast;
    }
    @Override
    protected void onStart() {
        super.onStart();
        onActivityStarted(this);
        if (mCommonBlockManager != null) {
            mCommonBlockManager.onStart();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        onActivityResumed(this);
        isfinish=false;
        if (mCommonBlockManager != null) {
            mCommonBlockManager.onResume();
        }
        if (screenRefresh>=10){
            screenRefresh=0;
//            getWindow().getDecorView().invalidate(View.EINK_UPDATE_MODE_FULL);
        }else {
            screenRefresh++;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        onActivityPaused(this);
        if (mCommonBlockManager != null) {
            mCommonBlockManager.onPause();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        onActivityStopped(this);
        if (mCommonBlockManager != null) {
            mCommonBlockManager.onStop();
        }
        if (alertDialog != null ) {
            alertDialog.hide();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        onActivitySaveInstanceState(outState);
        if (mCommonBlockManager != null) {
            mCommonBlockManager.onSaveInstanceState(outState);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        onActivityRestoreInstanceState(savedInstanceState);
        if (mCommonBlockManager != null) {
            mCommonBlockManager.onRestoreInstanceState(savedInstanceState);
        }
    }

    @Override
    public void onBackPressed() {
        if (mCommonBlockManager != null) {
            if (!mCommonBlockManager.onBackPressed()) {
                super.onBackPressed();
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (mHandler != null) mHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
        onActivityDestroyed(this);
        if (mCommonBlockManager != null) {
            mCommonBlockManager.onDestroy();
        }
        isfinish=true;
        NetworkStateReceiver.unRegisterNetworkStateReceiver(this);
        ActivitysManager.getAppManager().finishActivity(this,false);
        //清除dialog
        if(dialog!=null&&dialog.isShowing()){
            dialog.dismiss();
            dialog=null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mCommonBlockManager != null) {
            mCommonBlockManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    private static class MyHandler extends Handler {

        WeakReference<BaseActivity> mReference = null;

        MyHandler(BaseActivity activity) {
            this.mReference = new WeakReference<BaseActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            BaseActivity outer = mReference.get();
            if (outer == null || outer.isFinishing()) {
                Log.e("outer is null");
                return;
            }

            outer.handleMessage(msg);
        }
    }


    public void gotoActivity(Class<? extends Activity> clazz, boolean finish) {
        Intent intent = new Intent(this, clazz);
        startActivity(intent);
        if (finish) {
            finish();
        }
    }


    public void gotoActivity(Class<? extends Activity> clazz, boolean finish, Bundle bundle) {

        Intent intent = new Intent(this, clazz);
        if (bundle != null) intent.putExtras(bundle);
        startActivity(intent);
        if (finish) {
            finish();
        }
    }


    public void gotoActivity(Class<? extends Activity> clazz, int flags, boolean finish, Bundle bundle) {

        Intent intent = new Intent(this, clazz);
        if (bundle != null) intent.putExtras(bundle);

        intent.addFlags(flags);

        startActivity(intent);
        if (finish) {
            finish();
        }
    }

    public CommonBlockManager getCommonBlockManager() {
        if (mCommonBlockManager == null) {
            mCommonBlockManager = new CommonBlockManager(this);
        }
        return mCommonBlockManager;
    }

    public Handler getHandler() {
        return mHandler;
    }

    public void onConnect(NetWorkUtil.netType type) {
    }


    public void onDisConnect() {
    }


    public void handleMessage(Message msg) {
    }


    protected abstract int getMainContentViewId();

    private HitnDialog dialog;

    public HitnDialog dialogShowOrHide(boolean is,String hitn, ClickBackListener listener) {
        return dialogShowOrHide(is,hitn,0,listener);
    }
    public HitnDialog dialogShowOrHide(boolean is,String hitn) {
        return dialogShowOrHide(is,hitn,0);
    }
    public HitnDialog dialogShowOrHide(boolean is,String  hitn,int minems) {
        return dialogShowOrHide(is,hitn,minems,null);
    }
    public HitnDialog dialogShowOrHide(boolean is, String  hitn, int minems, ClickBackListener listener) {
        if (isfinish)return null;
        if(dialog!=null&&dialog.isShowing()){
            dialog.dismiss();
            dialog=null;
        }
        if (is) {
            dialog = new HitnDialog(this, R.style.AlertDialogStyle,hitn,minems,listener);
//            dialog.setMessage(hitn);
            dialog.setCancelable(false);// 是否可以关闭dialog
            dialog.setCanceledOnTouchOutside(false);
            try {
                dialog.show();
            }catch (Exception e){

            }
        }
        return dialog;
    }
    public void dialogText(String text){
        if (isfinish)return ;
        if(dialog!=null&&dialog.isShowing()){
            dialog.setContnet(text);
        }else {
            dialogShowOrHide(true,text);
        }
    }

    public void insureDialog(String content,Object code,InsureOrQuitListener listener){
        insureDialog("提示",content,code,listener);
    }
    public void insureDialog(String content,Object code,boolean iscan,InsureOrQuitListener listener){
        insureDialog("提示",content,"确定","取消",code,iscan,listener);
    }
    public void insureDialog(String hitn,String content,Object code,InsureOrQuitListener listener){
        insureDialog(hitn,content,"确定","取消",code,listener);
    }
    public void insureDialog(String hitn,String content,String insure,String quit, final Object code, final InsureOrQuitListener listener){
        //没有问题可以进行移动
        insureDialog(hitn,content,insure,quit,code,true,listener);
    }
    public void insureDialog(String hitn,String content,String insure,String quit, final Object code,boolean iscan, final InsureOrQuitListener listener){
        //没有问题可以进行移动
        alertDialog= new AlertDialog(this).builder().setTitle(hitn).setCancelable(iscan).setMsg(content).
                setNegativeButton(insure, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.hide();
                        if (listener != null) {
                            listener.isInsure(code,true);
                        }
                    }
                }).setPositiveButton(quit, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.hide();
                if (listener != null) {
                    listener.isInsure(code,false);
                }
            }
        });
        alertDialog.show();
    }

    /**
     * 单个按钮事件
     * @param hitn
     * @param content
     * @param insure
     * @param code
     * @param listener
     */
    public void insureDialog(String hitn,String content,String insure, final Object code, final InsureOrQuitListener listener){
        //没有问题可以进行移动
        alertDialog= new AlertDialog(this).builder().setTitle(hitn).setCancelable(false).setMsg(content).
                setNegativeButton(insure, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.hide();
                        if (listener != null) {
                            listener.isInsure(code,true);
                        }
                    }
                });
        alertDialog.show();
    }
    public void setAlertContenttyle(SpannableStringBuilder builder){
        if (alertDialog!=null){
            alertDialog.setMsg(builder);
        }
    }
//    @Override
//    protected void onUserLeaveHint() {
//        super.onUserLeaveHint();
//        this.finish();
//    }

    public void hideDialog() {

        if (dialog != null && dialog.isShowing() ) {
            dialog.dismiss();
        }
        if (alertDialog != null ) {
            alertDialog.hide();
        }
        dialog = null;
    }
    /**
     * 对软键盘的弹出与隐藏进行处理
     *
     * @param isShow
     */
    protected void showKeyboard(boolean isShow) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (isShow) {
            if (getCurrentFocus() == null) {
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            } else {
                imm.showSoftInput(getCurrentFocus(), 0);
            }
        } else {
            if (getCurrentFocus() != null) {
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    /**
     * 点击其它地方关闭软键盘
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            // 获得当前得到焦点的View，一般情况下就是EditText（特殊情况就是轨迹求或者实体案件会移动焦点）
            View v = getCurrentFocus();
            if (StringUtils.isShouldHideInput(v, ev)) {
                StringUtils.closeIMM(this, v.getWindowToken());
            }
        }
        return super.dispatchTouchEvent(ev);
    }

}
