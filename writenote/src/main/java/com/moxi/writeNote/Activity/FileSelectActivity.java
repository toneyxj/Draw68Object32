package com.moxi.writeNote.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.moxi.writeNote.R;
import com.moxi.writeNote.adapter.FileAdapter;
import com.moxi.writeNote.listener.LoadingListener;
import com.moxi.writeNote.utils.FileObtainAsy;
import com.mx.mxbase.base.BaseActivity;
import com.mx.mxbase.utils.DensityUtil;
import com.mx.mxbase.utils.FileUtils;
import com.mx.mxbase.utils.ToastUtils;
import com.mx.mxbase.view.SildeFrameLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * 文件选择
 */
public class FileSelectActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemLongClickListener
        ,AdapterView.OnItemClickListener,SildeFrameLayout.SildeEventListener
{
    /**
     * 启动文件选择模式
     *
     * @param activity    当前界面
     * @param requestCode 请求code标识
     * @param isDir       是否是请求路径
     * @param selectPath  当前选择路径
     */
    public static void startFileSelect(Activity activity, int requestCode, boolean isDir, String selectPath, String title) {
        Intent intent = new Intent(activity, FileSelectActivity.class);
        Bundle bundle = new Bundle();
        bundle.putBoolean("isDir", isDir);
        bundle.putString("selectPath", selectPath);
        bundle.putString("title", title);
        intent.putExtras(bundle);
        activity.startActivityForResult(intent, requestCode);
    }

    @Bind(R.id.back_item)
    TextView back_item;
    @Bind(R.id.index_page)
    TextView index_page;

    @Bind(R.id.silde_layout)
    SildeFrameLayout silde_layout;
    @Bind(R.id.grid_items)
    GridView grid_items;

    @Bind(R.id.sd_card)
    TextView sd_card;
    @Bind(R.id.tf_card)
    TextView tf_card;

    private String selectPath;
    private String title;
    private boolean isDir;

    private final String sdCard = FileUtils.getInstance().getExtSDCardPathList().get(0);
    private final String tfCard = FileUtils.getInstance().getExtSDCardPathList().get(1);
    /**
     * 当前显示页数
     */
    private int CurrentIndex = 0;
    /**
     * 总页数
     */
    private int totalIndex = 1;
    private int pageSize = 30;

    private File sdCardFile = null;
    private File tfCardFile = null;
    private boolean isSdcard = true;

    private List<File> listData = new ArrayList<File>();
    private List<File> sonList = new ArrayList<File>();
    private FileAdapter adapter;

    @Override
    protected int getMainContentViewId() {
        return R.layout.activity_file_select;
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        switch (msg.what) {
            case 1://使用控件高度
                initSonData();
                break;
            default:
                break;
        }

    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            savedInstanceState = getIntent().getExtras();
        }
        selectPath = savedInstanceState.getString("selectPath");
        title = savedInstanceState.getString("title");
        isDir = savedInstanceState.getBoolean("isDir");

        back_item.setText(title);

        back_item.setOnClickListener(this);
        sd_card.setOnClickListener(this);
        tf_card.setOnClickListener(this);

        grid_items.setOnItemLongClickListener(this);
        grid_items.setOnItemClickListener(this);

        silde_layout.setListener(this);

        sdCardFile = new File(sdCard);
        tfCardFile = new File(tfCard);

        setdirFiles();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_item://返回
                onBackPressed();
                break;
            case R.id.sd_card://sd卡文件切换
                if (isSdcard) return;
                isSdcard = true;
                sd_card.setBackgroundResource(R.drawable.di_white_bian_font);
                tf_card.setBackgroundResource(R.color.transparent);
                setdirFiles();
                break;
            case R.id.tf_card://tf卡文件切换
                if (!isSdcard) return;
                isSdcard = false;
                tf_card.setBackgroundResource(R.drawable.di_white_bian_font);
                sd_card.setBackgroundResource(R.color.transparent);
                setdirFiles();
                break;
            default:
                break;
        }
    }

    public void initSonData() {
        if (isfinish) return;
        //计算页数
        totalIndex = listData.size() / pageSize;
        totalIndex += listData.size() % pageSize == 0 ? 0 : 1;

        //计算当前页数
        if (CurrentIndex > totalIndex - 1) {
            CurrentIndex = totalIndex - 1;
        }
        if (CurrentIndex < 0) CurrentIndex = 0;
        if (totalIndex == 0) totalIndex = 1;

        if (listData.size() == 0) {
            adapterItems(listData);
        } else if (totalIndex - 1 == CurrentIndex) {
            adapterItems(listData.subList(CurrentIndex * pageSize, listData.size()));
        } else {
            adapterItems(listData.subList(CurrentIndex * pageSize, (CurrentIndex + 1) * pageSize));
        }
        setShowText();
    }

    private void adapterItems(List<File> listModels) {
        if (listModels == null) return;
        sonList.clear();
        sonList.addAll(listModels);
        if (adapter == null) {
            int itemWidth=(grid_items.getWidth()- DensityUtil.dip2px(this,30)*4)/5;
            int itemHeight=(grid_items.getHeight()- DensityUtil.dip2px(this,30)*5)/6;
            adapter = new FileAdapter(this, sonList, itemWidth, itemHeight);
            grid_items.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * 设置显示当前显示页数
     */
    public void setShowText() {
        index_page.setText(String.valueOf(CurrentIndex + 1) + "/" + totalIndex);
    }

    @Override
    public void onBackPressed() {
        if (isSdcard) {
            if (!sdCardFile.getAbsolutePath().equals(sdCard)) {
                sdCardFile = sdCardFile.getParentFile();
                setdirFiles();
                return;
            }
        } else {
            if (!tfCardFile.getAbsolutePath().equals(tfCard)) {
                tfCardFile = tfCardFile.getParentFile();
                setdirFiles();
                return;
            }
        }
        super.onBackPressed();
    }

    /**
     * 获取文件
     *
     * @return
     */
    private void setdirFiles() {
        listData.clear();
        String titleValue = title + "\t";
        File[] fs = null;
        if (isSdcard) {
            fs = sdCardFile.listFiles();
            titleValue += sdCardFile.getAbsolutePath();
        } else {
            fs = tfCardFile.listFiles();
            titleValue += tfCardFile.getAbsolutePath();
        }
        if (fs != null) {
            if (fs.length>50)
            dialogShowOrHide(true, "");
            new FileObtainAsy(fs, isDir, new LoadingListener() {
                @Override
                public void onLoadingSucess(List<File> fs) {
                    if (isfinish) return;
                    dialogShowOrHide(false, "");
                    listData.addAll(fs);
                    //设置文件显示
                    getHandler().sendEmptyMessageDelayed(1, 100);
                }
            }).execute();
        }else {
            getHandler().sendEmptyMessageDelayed(1, 100);
        }
        //设置 文件路径显示
        back_item.setText(titleValue);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        File f=sonList.get(position);
        if (isDir){
            if (f.isDirectory()){
                backActivity(f.getAbsolutePath());
            }
        }else {
            if (f.isFile()){
                backActivity(f.getAbsolutePath());
            }
        }
        return true;
    }
    private void backActivity(String path){
        Intent in=new Intent();
        in.putExtra("path",path);
        setResult(RESULT_OK,in);
        this.finish();
    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Bundle outState ) {
        outState.putBoolean("isDir", isDir);
        outState.putString("selectPath", selectPath);
        outState.putString("selectPath", title);
    }

    @Override
    public void onActivityRestoreInstanceState(Bundle savedInstanceState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        File f=sonList.get(position);
        if (f.isDirectory()){
            if (isSdcard){
                sdCardFile=f;
            }else {
                tfCardFile=f;
            }
            setdirFiles();
        }
    }

    @Override
    public void onSildeEventLeft() {
        if (CurrentIndex > 0 && (CurrentIndex <= totalIndex - 1)) {
            CurrentIndex--;
            initSonData();
        } else {
            ToastUtils.getInstance().showToastShort("已经是第一页");
        }
    }

    @Override
    public void onSildeEventRight() {
        if (CurrentIndex >= totalIndex - 1) {
            ToastUtils.getInstance().showToastShort("已经是最后一页");
            return;
        } else {
            CurrentIndex++;
            initSonData();
        }
    }
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_PAGE_UP) {
            //上一页
            onSildeEventLeft();
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_PAGE_DOWN) {
            //下一页
            onSildeEventRight();
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }
}
