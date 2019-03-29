package com.mx.mxbase.base;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.mx.mxbase.R;
import com.mx.mxbase.constant.APPLog;
import com.mx.mxbase.dialog.HitnDialog;
import com.mx.mxbase.interfaces.InsureOrQuitListener;
import com.mx.mxbase.view.AlertDialog;

import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/8/2.
 */
public abstract class baseFragment extends Fragment {
public GestureDetector gestureDetector;
    public boolean isFinish=false;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutId(), null);
        ButterKnife.bind(this, view);

        gestureDetector=new GestureDetector(getActivity(), onGestureListener);
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);//返回手势识别触发的事件
            }
        });

        initFragment(view);
        return view;
    }

    /**
     * 初始化控件
     *
     * @param view
     */
    public abstract void initFragment(View view);

    public abstract int getLayoutId();

    private GestureDetector.OnGestureListener onGestureListener =
            new GestureDetector.SimpleOnGestureListener() {
                @Override//此方法必须重写且返回真，否则onFling不起效
                public boolean onDown(MotionEvent e) {
                    APPLog.e("MotionEvent",e.getAction());
                    return false;
                }

                @Override
                public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                    APPLog.e("e1",e1.getAction());
                    APPLog.e("e2",e2.getAction());
                    float x = e2.getX() - e1.getX();
                    float y=e2.getY()- e1.getY();

                    if (Math.abs(x) < 50||Math.abs(y)>50) return false;
                    if (x > 0) {
                        moveLeft();
                    } else if (x < 0) {
                        moveRight();
                    }
                    return super.onFling(e1, e2, velocityX, velocityY);
                }
            };


    public void moveRight() {
        APPLog.e("右边");
    }

    public void moveLeft() {
        APPLog.e("左边");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        isFinish=true;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private HitnDialog dialog;

    public HitnDialog dialogShowOrHide(boolean is,String  hitn) {
        if (isDetached())return null;
        try {
        if(dialog!=null&&dialog.isShowing()){
            dialog.dismiss();
            dialog=null;
        }
        if (is) {
            dialog = new HitnDialog(getActivity(), R.style.AlertDialogStyle,hitn);
//            dialog.setMessage(hitn);
            dialog.setCancelable(false);// 是否可以关闭dialog
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }
        }catch (Exception e){
            APPLog.e("提示框出错");
        }
        return dialog;
    }



    public void insureDialog(String content,Object code,InsureOrQuitListener listener){
        insureDialog("提示", content, code, listener);
    }
    public void insureDialog(String hitn,String content,Object code,InsureOrQuitListener listener){
        insureDialog(hitn, content, "确定", "取消", code, listener);
    }
    public void insureDialog(String hitn,String content,String insure,String quit, final Object code, final InsureOrQuitListener listener){
        //没有问题可以进行移动
        new AlertDialog(getActivity()).builder().setTitle(hitn).setCancelable(false).setMsg(content).
                setNegativeButton(insure, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (listener != null) {
                            listener.isInsure(code,true);
                        }
                    }
                }).setPositiveButton(quit, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.isInsure(code,false);
                }
            }
        }).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        hidDiolag();
    }
    public void hidDiolag(){
        if(dialog!=null&&dialog.isShowing()){
            dialog.dismiss();
            dialog=null;
        }
    }
}
