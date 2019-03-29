package com.moxi.haierexams.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.moxi.haierexams.R;
import com.moxi.haierexams.adapter.OptionAdapter;
import com.moxi.haierexams.adapter.TestExamsAdapter;
import com.moxi.haierexams.model.OptionModel;
import com.moxi.haierexams.model.TuiJianSJModel;
import com.mx.mxbase.base.BaseActivity;
import com.mx.mxbase.constant.Constant;
import com.mx.mxbase.interfaces.OnItemClickListener;
import com.mx.mxbase.utils.GsonTools;
import com.mx.mxbase.utils.ListUtils;
import com.mx.mxbase.utils.Log;
import com.mx.mxbase.utils.MXUamManager;
import com.mx.mxbase.utils.Toastor;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import okhttp3.Call;

/**
 * 推荐试题列表展示
 * Created by Archer on 16/8/3.
 */
public class MXExamsListActivity extends BaseActivity implements
        View.OnClickListener {

    @Bind(R.id.recycler_exams_list_top)
    RecyclerView recyclerTop;
    @Bind(R.id.ll_exams_list_back)
    LinearLayout llBack;
    @Bind(R.id.img_exams_list_left)
    ImageView imgPageLeft;
    @Bind(R.id.img_exams_list_right)
    ImageView imgPageRight;
    @Bind(R.id.tv_exams_list_page_count)
    TextView tvPageCount;
    //科目相关
    private List<OptionModel> listKemu = new ArrayList<>();
    private OptionAdapter kmAdapter;

    private String[] strKm = new String[]{"语文", "数学", "英语", "物理", "化学", "生物", "历史", "地理", "政治"};
    private Long[] idKm = new Long[]{1l, 2l, 3l, 4l, 5l, 6l, 7l, 8l, 9l};

    private long secId;
    private long semId;
    private long subId = 1l;//对应为语文

    private int page = -1;
    private int pageCount = -1;

    private GridView tjsjGridView;

    @Override
    protected int getMainContentViewId() {
        return R.layout.mx_activity_exams_list;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        secId = getIntent().getExtras().getInt("secId");
        semId = getIntent().getExtras().getInt("semId");
        init();
    }

    /**
     * 初始化视图
     */
    private void init() {
        page = 0;
        tjsjGridView = (GridView) findViewById(R.id.custom_grid_view_exams_list_content);
        llBack.setOnClickListener(this);
        recyclerTop.setLayoutManager(new GridLayoutManager(this, 6));
        setExAdapter(strKm);
        imgPageLeft.setOnClickListener(this);
        imgPageRight.setOnClickListener(this);
    }

    private void getExaData(String kemuId, final int page) {
        OkHttpUtils.post().url(Constant.HISTORDISS).addParams("appSession", MXUamManager.queryUser(this)).addParams("rows", Integer.MAX_VALUE + "").
                addParams("subId", kemuId).addParams("secId", secId + "").build().connTimeOut(10000).execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                if (MXExamsListActivity.this.isfinish)return;
                Toastor.showToast(MXExamsListActivity.this, "请检查网络后重试");
            }

            @Override
            public void onResponse(String response, int id) {
                if (MXExamsListActivity.this.isfinish)return;
                try {
                    TuiJianSJModel tjsj = GsonTools.getPerson(response, TuiJianSJModel.class);
                    final List<HashMap<String, Object>> result = new ArrayList<>();
                    if (tjsj != null) {
                        for (TuiJianSJModel.Paper paper : tjsj.getResult().getList()) {
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("pap_id", paper.getId());
                            hashMap.put("pap_name", paper.getName());
                            hashMap.put("pap_sub_name", paper.getSubName());
                            hashMap.put("pap_sub_id", paper.getSubId());
                            hashMap.put("pap_is_done", paper.getCprId());
                            result.add(hashMap);
                        }
                        List<List<HashMap<String, Object>>> temp = new ArrayList<List<HashMap<String, Object>>>();
                        temp = ListUtils.splitList(result, 12);
                        //Todo
                        TestExamsAdapter exaAdapter = new TestExamsAdapter(MXExamsListActivity.this, result);
                        tjsjGridView.setAdapter(exaAdapter);
                        final List<List<HashMap<String, Object>>> finalTemp = temp;
                        tjsjGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                                Intent exams = new Intent(MXExamsListActivity.this, ExamsTestActivity.class);
                                exams.putExtra("test_pap_id", finalTemp.get(page).get(position).get("pap_id").toString());
                                exams.putExtra("test_pap_name", finalTemp.get(page).get(position).get("pap_name").toString());
                                exams.putExtra("test_pap_cprId", finalTemp.get(page).get(position).get("pap_is_done").toString());
                                startActivity(exams);
                            }
                        });
                        if (temp.size() > 0) {
                            tvPageCount.setText((page + 1) + "/" + temp.size());
                        } else {
                            tvPageCount.setText("0/0");
                        }
                    } else {
                        Toastor.showToast(MXExamsListActivity.this, tjsj.getMsg());
                    }
                }catch (Exception e){

                }
            }
        });
    }

    /**
     * 设置科目相关
     *
     * @param strKm
     */
    private void setExAdapter(final String[] strKm) {
        listKemu.clear();
        for (int i = 0; i < strKm.length; i++) {
            OptionModel om = new OptionModel();
            if (i == 0) {
                om.setChosen(true);
            } else {
                om.setChosen(false);
            }
            om.setOptionName(strKm[i]);
            om.setOptionDesc(i + "");
            listKemu.add(om);
        }
        kmAdapter = new OptionAdapter(this, listKemu);
        recyclerTop.setAdapter(kmAdapter);
        kmAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                for (int i = 0; i < strKm.length; i++) {
                    if (i == position) {
                        listKemu.get(i).setChosen(true);
                        switch (i) {
                            case 0:
                                page = 0;
                                break;
                            case 1:
                                page = 0;
                                break;
                            case 2:
                                page = 0;
                                break;
                            case 9:
                                page = 0;
                                break;
                            default:
                                page = 0;
                                break;
                        }
                    } else {
                        listKemu.get(i).setChosen(false);
                    }
                    kmAdapter.notifyDataSetChanged();
                }
                subId = idKm[position];
                Log.d("ex", "kmd===>" + subId);
                getExaData(subId + "", page);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {
        getExaData(subId + "", page);
    }

    @Override
    public void onActivityPaused(Activity activity) {
    }

    @Override
    public void onActivityStopped(Activity activity) {
    }

    @Override
    public void onActivitySaveInstanceState(Bundle outState) {

    }

    @Override
    public void onActivityRestoreInstanceState(Bundle savedInstanceState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_PAGE_UP) {
            //上一页
            if (page > 0) {
                page--;
            }
            getExaData(subId + "", page);
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_PAGE_DOWN) {
            //下一页
            try {
                if (page < pageCount) {
                    page++;
                }
                getExaData(subId + "", page);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_exams_list_back:
                this.finish();
                break;
            case R.id.img_exams_list_left:
                if (page > 0) {
                    page--;
                }
                getExaData(subId + "", page);
                break;
            case R.id.img_exams_list_right:
                try {
                    if (page < pageCount) {
                        page++;
                    }
                    getExaData(subId + "", page);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }
}
