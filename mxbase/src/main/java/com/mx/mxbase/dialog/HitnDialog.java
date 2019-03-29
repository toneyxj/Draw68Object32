package com.mx.mxbase.dialog;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.mx.mxbase.R;
import com.mx.mxbase.base.BaseDialog;
import com.mx.mxbase.interfaces.ClickBackListener;

/**
 * Created by Administrator on 2016/12/9.
 */
public class HitnDialog extends BaseDialog {
    private String contnetStr;
    private int minems=0;
    private ClickBackListener listener;
    public HitnDialog(Context context, int themeResId,String contnet) {
        super(context, themeResId);
        this.contnetStr=contnet;
    }
    public HitnDialog(Context context, int themeResId,String contnet,int minems ) {
        super(context, themeResId);
        this.contnetStr=contnet;
        this.minems=minems;
    }
    public HitnDialog(Context context, int themeResId,String contnet,int minems ,ClickBackListener listener) {
        super(context, themeResId);
        this.contnetStr=contnet;
        this.minems=minems;
        this.listener=listener;
    }

    @Override
    public int getLayoutId() {
        return R.layout.dialog_hitn;
    }
   private TextView content;
   private View space_view;
    @Override
    public void initDialog() {

         content= (TextView) findViewById(R.id.content);
        space_view= (View) findViewById(R.id.space_view);

        if (contnetStr==null||contnetStr.equals("")){
            content.setVisibility(View.GONE);
            space_view.setVisibility(View.VISIBLE);

        }else {
            content.setVisibility(View.VISIBLE);
            space_view.setVisibility(View.GONE);
            content.setText(this.contnetStr);
        }
        if (minems>0) {
            content.setMinEms(minems);
        }
    }

    public void setContnet(String content){
        contnetStr=content;
        if (contnetStr==null||contnetStr.equals("")){
            this.content.setVisibility(View.GONE);
            this.space_view.setVisibility(View.VISIBLE);
        }else {
            this.content.setVisibility(View.VISIBLE);
            this.space_view.setVisibility(View.GONE);
            this.content.setText(this.contnetStr);
        }
    }

    @Override
    public void onClick(View v) {

    }
private long currentTime=0;
    private int click=0;
    @Override
    public void onBackPressed() {
        if (listener!=null)listener.onHitnBackground();
        long inttime=System.currentTimeMillis();
        if ((inttime-currentTime)<1000){

        }
        super.onBackPressed();
    }
}
