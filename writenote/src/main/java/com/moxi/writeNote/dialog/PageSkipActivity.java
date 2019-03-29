package com.moxi.writeNote.dialog;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.moxi.writeNote.R;
import com.moxi.writeNote.view.ProgressView;
import com.mx.mxbase.base.MyApplication;

public class PageSkipActivity extends Activity implements ProgressView.ProgressListener ,View.OnClickListener{
    /**
     * 启动界面跳转设置
     *
     * @param context  上下文
     * @param page 当前界面
     *             @param totalPage 总界面
     */
    public static void startPageSkip(Activity context, int page,int totalPage) {
        Intent intent = new Intent(context, PageSkipActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("page", page);
        bundle.putInt("totalPage", totalPage);
        intent.putExtras(bundle);
        context.startActivityForResult(intent,10);
    }
    private int page;
    private int totalPage;

    private TextView current_pen_size;
    private ImageView sub;
    private ProgressView current_progress;
    private ImageView add;
    private Button quit;
    private Button insure;
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Bundle bundle = new Bundle();
        bundle.putInt("page", page);
        bundle.putInt("totalPage", totalPage);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_skip);
        getWindow().getAttributes().width = (int) (MyApplication.ScreenWidth * 0.6);

        Bundle bundle=savedInstanceState;
        if (bundle==null){
            bundle=getIntent().getExtras();
        }
        if (bundle==null)this.finish();

        page=bundle.getInt("page");
        totalPage=bundle.getInt("totalPage");

        current_pen_size = (TextView) findViewById(R.id.current_pen_size);
        sub = (ImageView) findViewById(R.id.sub);
        current_progress = (ProgressView) findViewById(R.id.current_progress);
        add = (ImageView) findViewById(R.id.add);
        quit = (Button) findViewById(R.id.quit);
        insure = (Button) findViewById(R.id.insure);

        current_progress.initView(this,totalPage-1,page);
        current_progress.setCurNumber(page);

        sub.setOnClickListener(this);
        add.setOnClickListener(this);
        quit.setOnClickListener(this);
        insure.setOnClickListener(this);
    }

    @Override
    public void onProgress(int size) {
        page=size;
        current_pen_size.setText("当前页面:"+String.valueOf(page+1)+"/"+String.valueOf(totalPage));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.sub:
                current_progress.subOrAdd(false);
                break;
            case R.id.add:
                current_progress.subOrAdd(true);
                break;
            case R.id.quit:
                this.onBackPressed();
                break;
            case R.id.insure:
                Intent intent=new Intent();
                intent.putExtra("page",page);
                setResult(RESULT_OK,intent);
                this.finish();
                break;
            default:
                break;
        }
    }
}
