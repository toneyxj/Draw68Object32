package com.moxi.writeNote.dialog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.moxi.writeNote.R;
import com.moxi.writeNote.utils.BrushSettingUtils;
import com.moxi.writeNote.view.Preview;
import com.moxi.writeNote.view.ProgressView;
import com.mx.mxbase.base.MyApplication;

/**
 * 自定义画笔
 */
public class CustomPenActivity extends Activity implements ProgressView.ProgressListener ,View.OnClickListener{
    /**
     * 启动自定义画笔设置
     *
     * @param context  上下文
     * @param drawLine 如果是画线为true
     */
    public static void startCustomPen(Context context, boolean drawLine) {
        Intent intent = new Intent(context, CustomPenActivity.class);
        Bundle bundle = new Bundle();
        bundle.putBoolean("drawLine", drawLine);
        ;
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    private boolean drawLine;
    private TextView title;
    private TextView current_pen_size;
    private ImageView sub;
    private ProgressView current_progress;
    private ImageView add;
    private Preview pre_view;
    private Button quit;
    private Button insure;
    private String themStr;

    private int minSize;
    private int MaxSize;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_pen);

        getWindow().getAttributes().width = (int) (MyApplication.ScreenWidth * 0.6);


        Bundle bundle = savedInstanceState;
        if (bundle == null) {
            bundle = getIntent().getExtras();
        }
        drawLine = bundle.getBoolean("drawLine");


        title = (TextView) findViewById(R.id.title);
        current_pen_size = (TextView) findViewById(R.id.current_pen_size);
        sub = (ImageView) findViewById(R.id.sub);
        current_progress = (ProgressView) findViewById(R.id.current_progress);
        add = (ImageView) findViewById(R.id.add);
        pre_view = (Preview) findViewById(R.id.pre_view);
        quit = (Button) findViewById(R.id.quit);
        insure = (Button) findViewById(R.id.insure);


        title.setText(drawLine ? "自定义画笔" : "自定义橡皮擦");
        themStr = drawLine ? "画笔" : "橡皮擦";
        if (drawLine) {
            minSize = 2;
            MaxSize = 20;
        } else {
            minSize = 5;
            MaxSize = 30;
        }
        int curSize=drawLine? BrushSettingUtils.getInstance(this).getDrawLineSize():BrushSettingUtils.getInstance(this).getRubberSize();

        current_progress.initView(this,MaxSize-minSize,curSize-minSize);
        current_progress.setCurNumber(curSize-minSize);

        quit.setOnClickListener(this);
        insure.setOnClickListener(this);
        sub.setOnClickListener(this);
        add.setOnClickListener(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("drawLine", drawLine);
    }

    @Override
    public void onProgress(int size) {
        current_pen_size.setText("当前" + themStr + ":" + String.valueOf(size + minSize));
        pre_view.setLineWidth(size+minSize);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.add:
                current_progress.subOrAdd(true);
                break;
            case R.id.sub:
                current_progress.subOrAdd(false);
                break;
            case R.id.quit:
                onBackPressed();
                break;
            case R.id.insure:
                //保存
                int cur=current_progress.getCurNumber()+minSize;
                if (drawLine){
                    BrushSettingUtils.getInstance(this).setDrawLineSize(cur);
                }else {
                    BrushSettingUtils.getInstance(this).setRubberSize(cur);
                }
                onBackPressed();
                break;
            default:
                break;
        }
    }
}
