package com.moxi.writeNote.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.moxi.writeNote.R;
import com.moxi.writeNote.WriteBaseActivity;
import com.moxi.writeNote.adapter.MyBackgroundAdapter;
import com.moxi.writeNote.utils.GetBackImgAsy;
import com.mx.mxbase.constant.APPLog;
import com.mx.mxbase.utils.StringUtils;
import com.mx.mxbase.utils.ToastUtils;
import com.mx.mxbase.view.SildeFrameLayout;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.Bind;

/**
 * 列表展示自定义背景图片
 */
public class ListMyBackgroundActivity extends WriteBaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener {
    @Override
    protected int getMainContentViewId() {
        return R.layout.activity_list_my_background;
    }

    //    控件初始化
    @Bind(R.id.show_title)
    TextView show_title;
    @Bind(R.id.select)
    TextView select;
    @Bind(R.id.sort)
    TextView sort;

    @Bind(R.id.silde_layout)
    SildeFrameLayout silde_layout;
    @Bind(R.id.back_item)
    GridView back_item;

    @Bind(R.id.selec_control)
    LinearLayout selec_control;
    @Bind(R.id.quit)
    TextView quit;
    @Bind(R.id.delete)
    TextView delete;

    @Bind(R.id.last_page)
    ImageButton last_page;
    @Bind(R.id.show_index)
    TextView show_index;
    @Bind(R.id.next_page)
    ImageButton next_page;

    /**
     * 当前显示页数
     */
    private int pageIndex = 0;
    /**
     * 每页显示个数
     */
    private final int pageSize = 16;
    /**
     * 页面总页数
     */
    private int totalPage;

    private List<String> listData = new ArrayList<String>();
    /**
     * 中间转换model
     */
    private List<String> middleModels = new ArrayList<String>();
    private MyBackgroundAdapter adapter = null;

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        show_title.setOnClickListener(this);
        select.setOnClickListener(this);
        sort.setOnClickListener(this);

        quit.setOnClickListener(this);
        delete.setOnClickListener(this);

        last_page.setOnClickListener(this);
        next_page.setOnClickListener(this);

        silde_layout.setListener(sildeEventListener);
        back_item.setOnItemClickListener(this);

        initList();

    }

    private void initList() {
        new GetBackImgAsy(new GetBackImgAsy.CustomBackgroundListener() {
            @Override
            public void customBack(List<String> result) {
                listData.addAll(result);
                APPLog.e(result.toString());
                changePage();
            }
        }).execute();

    }
    /**
     * 修改页面
     */
    private void changePage() {
        int size = listData.size();
        totalPage = (size / pageSize) + ((size % pageSize == 0) ? 0 : 1);
        if (totalPage <= pageIndex) {
            pageIndex = totalPage - 1;
        }
        initAdapter();
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.show_title:
                onBackPressed();
                break;
            case R.id.quit:
                onClick(select);
                break;
            case R.id.select:
                if (adapter != null) {
                    adapter.startSet(!adapter.isSelect(), back_item);
                    selec_control.setVisibility(adapter.isSelect()?View.VISIBLE:View.INVISIBLE);
                }
                break;
            case R.id.sort:

                break;
            case R.id.delete:
                if (adapter!=null){
                    java.util.Set<String> sets=adapter.getSets();
                    if (sets.size()==0){
                        adapter.startSet(false,back_item);
                    }else {
                        //利用Iterator实现遍历
                        Iterator<String> value = sets.iterator();
                        while (value.hasNext()) {
                            String s = value.next();
                            StringUtils.deleteFile(s);
                            listData.remove(s);
                        }
                        adapter.startSet(false, null);
                        changePage();
                    }
                    selec_control.setVisibility(adapter.isSelect()?View.VISIBLE:View.INVISIBLE);
                }
                break;
            case R.id.last_page:
                moveLeft();
                break;
            case R.id.next_page:
                moveRight();
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (adapter.isSelect()){
            adapter.updateSelect(position,back_item);
        }else {
            String path=middleModels.get(position);
            Intent intent=new Intent();
            intent.putExtra("filePath",path);
            setResult(RESULT_OK,intent);
            onBackPressed();
        }
    }
    private SildeFrameLayout.SildeEventListener sildeEventListener = new SildeFrameLayout.SildeEventListener() {
        @Override
        public void onSildeEventLeft() {
            moveLeft();
        }

        @Override
        public void onSildeEventRight() {
            moveRight();
        }
    };
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_PAGE_UP) {
            //上一页
            moveLeft();
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_PAGE_DOWN) {
            //下一页
            moveRight();
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    /**
     * 初始化adapter
     */
    private void initAdapter() {
        //修改当前页面的索引值
        middleModels.clear();
        if (totalPage > 0 && pageIndex >= 0) {
            if ((totalPage - 1) > pageIndex) {
                middleModels.addAll(listData.subList(pageSize * pageIndex, pageSize * (pageIndex + 1)));
            } else {
                middleModels.addAll(listData.subList(pageSize * pageIndex, listData.size()));
            }
        }
        if (adapter == null) {
            adapter = new MyBackgroundAdapter(this, middleModels);
            back_item.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
        show_index.setText(String.valueOf(pageIndex + 1) + "/" + String.valueOf(totalPage));
    }

    private void moveRight() {
        if (pageIndex >= totalPage - 1) {
            ToastUtils.getInstance().showToastShort("已经是最后一页");
            return;
        } else {
            pageIndex++;
            initAdapter();
        }
    }

    private void moveLeft() {
        if (pageIndex > 0 && (pageIndex <= totalPage - 1)) {
            pageIndex--;
            initAdapter();
        } else {
            ToastUtils.getInstance().showToastShort("已经是第一页");
        }
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

    }

    @Override
    public void onActivityRestoreInstanceState(Bundle savedInstanceState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }
}
