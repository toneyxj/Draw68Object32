package com.moxi.haierexams.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.moxi.haierexams.R;
import com.moxi.haierexams.adapter.OptionAdapter;
import com.moxi.haierexams.adapter.PeriodAdapter;
import com.moxi.haierexams.db.SQLUtil;
import com.moxi.haierexams.model.ChoseResultModel;
import com.moxi.haierexams.model.OptionModel;
import com.mx.mxbase.base.BaseActivity;
import com.mx.mxbase.interfaces.OnItemClickListener;
import com.mx.mxbase.utils.GsonTools;
import com.mx.mxbase.utils.Toastor;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * 个性条件选择界面
 * Created by Archer on 16/8/10.
 */
public class MXRequirementOptionActivity extends BaseActivity implements View.OnClickListener {

    @Bind(R.id.ll_requirement_option_back)
    LinearLayout llBack;
    @Bind(R.id.recycler_requirement_option_xueduan)
    RecyclerView recyclerXued;
    @Bind(R.id.recycler_requirement_option_xueqi)
    RecyclerView recyclerXueq;
    @Bind(R.id.recycler_requirement_option_jiaocai)
    RecyclerView recyclerJc;
    @Bind(R.id.tv_requirement_option_sure)
    TextView tvSure;

    //学段
    private String[] strPeriod = new String[]{"小学", "初中", "高中"};
    private PeriodAdapter periodAdapter;
    private List<OptionModel> listPeriod = new ArrayList<>();
    private String chosenPeriod = "";
    private int chosenPeriodId;

    //学期
    private String[] strXq = new String[]{"高一上", "高一下", "高二上", "高二下", "", "高三上", "高三下", ""};

    private List<OptionModel> listXueq = new ArrayList<>();
    private OptionAdapter xqAdapter;
    private String chosenXq = "";
    private int chosenXqId;
    //教材
    private String[] strJiaocai = new String[]{"公共版", "人教版", "鲁教版", "清华大学出版社"};
    private List<OptionModel> listJc = new ArrayList<>();
    private OptionAdapter jcAdapter;
    private String chosenJc = "";

    private boolean allowBack = false;

    @Override
    protected int getMainContentViewId() {
        return R.layout.mx_activity_requirement_option;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        init();
    }


    /**
     * 初始化视图
     */
    private void init() {
        //设置点击事件
        llBack.setOnClickListener(this);
        tvSure.setOnClickListener(this);
        //设置学段相关
        recyclerXued.setLayoutManager(new GridLayoutManager(this, 3));
        setPeriodAdapter();
        //设置学期相关
        recyclerXueq.setLayoutManager(new GridLayoutManager(this, 4));
        setXueqAdapter("1");
        //设置教材相关
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerJc.setLayoutManager(layoutManager);
        setJcAdapter(strJiaocai);
    }

    /**
     * 设置教材相关
     *
     * @param strJiaocai
     */
    private void setJcAdapter(String[] strJiaocai) {
        listJc.clear();
        for (int i = 0; i < strJiaocai.length; i++) {
            OptionModel om = new OptionModel();
            om.setChosen(false);
            om.setOptionName(strJiaocai[i]);
            om.setOptionDesc(i + "");
            listJc.add(om);
        }
        jcAdapter = new OptionAdapter(this, listJc);
        recyclerJc.setAdapter(jcAdapter);
        jcAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                for (int i = 0; i < listJc.size(); i++) {
                    if (i == position) {
                        listJc.get(position).setChosen(true);
                        chosenJc = listJc.get(position).getOptionName();
                    } else {
                        listJc.get(i).setChosen(false);
                    }
                }
                allowBack = true;
                jcAdapter.notifyDataSetChanged();
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
    }

    /**
     * 设置学期相关
     *
     * @param
     */
    private void setXueqAdapter(String studyId) {
        listXueq.clear();
        listXueq = SQLUtil.getInstance(this).getSemesterFromDb(studyId);
        xqAdapter = new OptionAdapter(this, listXueq);
        recyclerXueq.setAdapter(xqAdapter);
        xqAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                for (int i = 0; i < listXueq.size(); i++) {
                    if (i == position) {
                        listXueq.get(position).setChosen(true);
                        chosenXq = listXueq.get(position).getOptionName();
                        chosenXqId = listXueq.get(position).getId();
                    } else {
                        listXueq.get(i).setChosen(false);
                    }
                }
                allowBack = true;
                xqAdapter.notifyDataSetChanged();
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
    }

    /**
     * 设置学段相关
     *
     * @param
     */
    private void setPeriodAdapter() {
        listPeriod.clear();
        listPeriod = SQLUtil.getInstance(this).getStudySectionFromDb();
        periodAdapter = new
                PeriodAdapter(this, listPeriod);
        recyclerXued.setAdapter(periodAdapter);
        periodAdapter.setOnItemClickListener(new OnItemClickListener() {
                                                 @Override
                                                 public void onItemClick(View view, int position) {
                                                     for (int i = 0; i < listPeriod.size(); i++) {
                                                         if (i == position) {
                                                             listPeriod.get(position).setChosen(true);
                                                             chosenPeriod = listPeriod.get(position).getOptionName();
                                                             chosenPeriodId = listPeriod.get(position).getId();

                                                             chosenXq = "";
                                                             chosenXqId = 0;
                                                             setXueqAdapter(chosenPeriodId + "");
                                                         } else {
                                                             listPeriod.get(i).setChosen(false);
                                                         }
                                                     }
                                                     allowBack = true;
                                                     periodAdapter.notifyDataSetChanged();
                                                 }

                                                 @Override
                                                 public void onItemLongClick(View view, int position) {

                                                 }
                                             }
        );
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_requirement_option_back:
                this.finish();
                break;
            case R.id.tv_requirement_option_sure:
                if (allowBack) {
                    if (chosenXqId == 0 || chosenPeriodId == 0) {
                        Toastor.showToast(this, "请选择筛选条件");
                        return;
                    }
                    Intent result = new Intent();
                    result.putExtra("tv_chosen_value", chosenPeriod + " "
                            + chosenXq + " " + chosenJc);
                    result.putExtra("chosenXqId", chosenXqId);
                    result.putExtra("chosenPeriodId", chosenPeriodId);
                    result.putExtra("chosenXq", chosenXq);
                    result.putExtra("chosenPeriod", chosenPeriod);

                    ChoseResultModel crm = new ChoseResultModel();
                    crm.setChoseValue(chosenPeriod + " " + chosenXq + " " + chosenJc);
                    crm.setPeriodId(chosenPeriodId);
                    crm.setXQID(chosenXqId);
                    crm.setPeroidDesc(chosenPeriod);
                    crm.setXQDesc(chosenXq);

                    share.setCache("chose_requirement_option_value", GsonTools.obj2json(crm));

                    this.setResult(1001, result);
                    finish();
                } else {
                    Toastor.showToast(this, "请选择筛选条件");
                }
                break;
            default:
                break;
        }
    }
}
