package com.moxi.haierexams.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.moxi.haierexams.R;
import com.moxi.haierexams.adapter.ResultGridAdapter;
import com.moxi.haierexams.cache.ACache;
import com.moxi.haierexams.db.SQLBookUtil;
import com.moxi.haierexams.db.SQLUtil;
import com.moxi.haierexams.db.TestSqlUtil;
import com.moxi.haierexams.model.ChoseExamsModel;
import com.moxi.haierexams.model.ExamsDetails;
import com.moxi.haierexams.model.ExamsDetailsModel;
import com.mx.mxbase.base.BaseActivity;
import com.mx.mxbase.constant.Constant;
import com.mx.mxbase.utils.GsonTools;
import com.mx.mxbase.utils.MXUamManager;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import okhttp3.Call;

/**
 * 错题集界面
 * Created by Archer on 16/10/14.
 */
public class MXErrorExamsActivity extends BaseActivity implements View.OnClickListener {
    @Bind(R.id.ll_base_back)
    LinearLayout llBack;
    @Bind(R.id.tv_base_mid_title)
    TextView tvMidTitle;
    @Bind(R.id.recycler_reply_result)
    GridView recyclerview;
    @Bind(R.id.tv_exams_details_title)
    TextView tvExamsTitle;
    @Bind(R.id.tv_subjective_right)
    TextView tvSubRight;
    @Bind(R.id.recycler_reply_subject_result)
    GridView recyclerSubView;
    @Bind(R.id.tv_chose_title)
    TextView tvChoseTitle;
    @Bind(R.id.tv_subjective_title)
    TextView tvSubTitle;
    @Bind(R.id.ll_base_right)
    LinearLayout llRight;
    @Bind(R.id.tv_base_right)
    TextView tvRight;

    private ResultGridAdapter adapter, bAdapter;
    private ExamsDetailsModel edm;
    public static int page = -1;
    private int examsId;
    private String papId;
    private int totalPage;

    private List<ExamsDetails> examsDetails = new ArrayList<>();
    public String examsTitle;
    private String response;

    @Override
    protected int getMainContentViewId() {
        return R.layout.mx_activity_error_exams;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        init();
    }

    /**
     * 初始化界面
     */
    private void init() {
        tvMidTitle.setText("错题详情");
        examsTitle = getIntent().getStringExtra("cob_exams_title");
        tvExamsTitle.setText(examsTitle);
        llBack.setVisibility(View.VISIBLE);
        llBack.setOnClickListener(this);
        llRight.setOnClickListener(this);

        papId = getIntent().getStringExtra("exams_pap_id");
        if (papId == null) {
            examsId = getIntent().getIntExtra("cob_zj_id", -1);
            final String bookId = SQLUtil.getInstance(this).getBookIdByCchId(examsId + "");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            response = SQLBookUtil.getInstance(getApplicationContext()).getExamsDetails(bookId, examsId + "");
                            parseExams(response);
                        }
                    });
                }
            }).start();
        } else {
            parePaperDetails(papId, examsTitle);
        }
    }

    /**
     * 查询试卷详情
     *
     * @param papId
     * @param examsTitle 试卷标题
     */
    private void parePaperDetails(final String papId, final String examsTitle) {
        llRight.setVisibility(View.GONE);
        tvMidTitle.setText(examsTitle);
        examsDetails = TestSqlUtil.getInstance(this).getExamsDetails(papId);
        final List<ChoseExamsModel> listChoseData = new ArrayList<>();
        final List<ChoseExamsModel> listData = new ArrayList<>();
        if (examsDetails.size() > 0) {
            listChoseData.clear();
            listData.clear();
            int total = 0;
            int right = 0;
            for (int i = 0; i < examsDetails.size(); i++) {
                if (examsDetails.get(i).getType() == 6 || examsDetails.get(i).getType() == 18) {
                    ChoseExamsModel cem = new ChoseExamsModel();
                    cem.setIndex(i);
                    cem.setResultKey("papId" + papId);
                    cem.setExamsDetails(examsDetails.get(i));
                    total++;
                    listChoseData.add(cem);
                    String resultKey = "papId" + papId + i;
                    String tempResult = ACache.get(this).getAsString(resultKey);
                    if (tempResult != null) {
                        try {
                            if (tempResult.equals(examsDetails.get(i).getAnswer())) {
                                right++;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    ChoseExamsModel cem = new ChoseExamsModel();
                    cem.setIndex(i);
                    cem.setExamsDetails(examsDetails.get(i));
                    cem.setResultKey("papId" + papId);
                    listData.add(cem);
                }
            }
            NumberFormat numberFormat = NumberFormat.getInstance();
            numberFormat.setMaximumFractionDigits(2);
            if (total != 0) {
                String result = numberFormat.format((float) right / (float) total * 100);
                tvSubRight.setText(result + "%");
            } else {
                tvSubRight.setText(0 + "%");
            }
            adapter = new ResultGridAdapter(this, listChoseData);
            recyclerview.setAdapter(adapter);
            recyclerview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    Intent intent = new Intent();
                    intent.setClass(MXErrorExamsActivity.this, ExamsTestActivity.class);
                    intent.putExtra("test_pap_id", papId);
                    intent.putExtra("test_pap_name", examsTitle);
                    intent.putExtra("test_pap_cprId", 0 + "");
                    page = listChoseData.get(position).getIndex();
                    startActivity(intent);
                    MXErrorExamsActivity.this.finish();
                }
            });

            bAdapter = new ResultGridAdapter(this, listData);
            recyclerSubView.setAdapter(bAdapter);
            recyclerSubView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    Intent intent = new Intent();
                    intent.setClass(MXErrorExamsActivity.this, ExamsTestActivity.class);
                    intent.putExtra("test_pap_id", papId);
                    intent.putExtra("test_pap_name", examsTitle);
                    intent.putExtra("test_pap_cprId", 0 + "");
                    page = listData.get(position).getIndex();
                    startActivity(intent);
                    MXErrorExamsActivity.this.finish();
                }
            });
        }

    }


    /**
     * 解析错题数据
     *
     * @param response exams详情
     */
    private void parseExams(String response) {
        edm = GsonTools.getPerson(response, ExamsDetailsModel.class);
        examsDetails = edm.getResult();
        dialogShowOrHide(true, "");
        HashMap<String, String> sync = new HashMap<>();
        sync.put("appSession", MXUamManager.queryUser(this));
        sync.put("cchId", examsId + "");
        OkHttpUtils.post().url(Constant.QUERY_SYNC_HISTORY).params(sync).build().connTimeOut(10000).execute(new StringCallback() {

            @Override
            public void onError(Call call, Exception e, int id) {
                if (MXErrorExamsActivity.this.isfinish)return;
                int aaa = (int) ACache.get(MXErrorExamsActivity.this).getAsObject("total_page_show");
                if (aaa != 0) {
                    totalPage = aaa;
                } else {
                    totalPage = edm.getResult().size();
                }
                showErrorExams(totalPage);
                llRight.setVisibility(View.GONE);
            }

            @Override
            public void onResponse(String response, int id) {
                if (MXErrorExamsActivity.this.isfinish)return;
                try {
                    JSONObject result = new JSONObject(response);
                    String coeids = result.getJSONObject("result").getString("coeIds");
                    int temp = Integer.parseInt(coeids);
                    if (temp != 0) {
                        //计算总页数
                        if (temp * 5 >= edm.getResult().size()) {
                            llRight.setVisibility(View.GONE);
                            totalPage = edm.getResult().size();
                        } else {
                            llRight.setVisibility(View.VISIBLE);
                            totalPage = temp * 5;
                            tvRight.setText("再来五题");
                        }
                    } else {
                        llRight.setVisibility(View.GONE);
                        totalPage = edm.getResult().size();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    llRight.setVisibility(View.GONE);
                    totalPage = edm.getResult().size();
                }
                ACache.get(MXErrorExamsActivity.this).put("total_page_show", totalPage);
                showErrorExams(totalPage);
            }
        });
    }

    /**
     * 显示错题信息
     *
     * @param totalPage 一共显示的页码
     */
    private void showErrorExams(int totalPage) {
        final List<ChoseExamsModel> listChoseData = new ArrayList<>();
        final List<ChoseExamsModel> listData = new ArrayList<>();
        if (edm.getResult().size() > 0) {
            listChoseData.clear();
            listData.clear();
            int total = 0;
            int right = 0;
            for (int i = 0; i < totalPage; i++) {
                if (examsDetails.get(i).getType() == 6 || examsDetails.get(i).getType() == 18) {
                    ChoseExamsModel cem = new ChoseExamsModel();
                    cem.setIndex(i);
                    cem.setExamsDetails(examsDetails.get(i));
                    cem.setResultKey("cchId" + examsId);
                    total++;
                    listChoseData.add(cem);
                    String resultKey = "cchId" + examsId + i;
                    String tempResult = ACache.get(this).getAsString(resultKey);
                    if (tempResult != null) {
                        try {
                            if (tempResult.equals(examsDetails.get(i).getAnswer())) {
                                right++;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    ChoseExamsModel cem = new ChoseExamsModel();
                    cem.setIndex(i);
                    cem.setResultKey("cchId" + examsId);
                    cem.setExamsDetails(examsDetails.get(i));
                    listData.add(cem);
                }
            }
            NumberFormat numberFormat = NumberFormat.getInstance();
            numberFormat.setMaximumFractionDigits(2);
            if (total != 0) {
                String result = numberFormat.format((float) right / (float) total * 100);
                tvSubRight.setText(result + "%");
            } else {
                tvSubRight.setText(0 + "%");
            }

            if (listChoseData.size() > 0) {
                tvChoseTitle.setVisibility(View.VISIBLE);
            } else {
                tvChoseTitle.setVisibility(View.GONE);
            }
            adapter = new ResultGridAdapter(this, listChoseData);
            recyclerview.setAdapter(adapter);
            recyclerview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    Intent intent = new Intent();
                    intent.setClass(MXErrorExamsActivity.this, MXWriteHomeWorkActivity.class);
                    intent.putExtra("mid_title", examsTitle);
                    intent.putExtra("cch_id", examsId);
                    page = listChoseData.get(position).getIndex();
                    startActivity(intent);
                    MXErrorExamsActivity.this.finish();
                }
            });

            if (listData.size() > 0) {
                tvSubTitle.setVisibility(View.VISIBLE);
            } else {
                tvSubTitle.setVisibility(View.GONE);
            }

            bAdapter = new ResultGridAdapter(this, listData);
            recyclerSubView.setAdapter(bAdapter);
            recyclerSubView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    Intent intent = new Intent();
                    intent.setClass(MXErrorExamsActivity.this, MXWriteHomeWorkActivity.class);
                    intent.putExtra("mid_title", examsTitle);
                    intent.putExtra("cch_id", examsId);
                    page = listData.get(position).getIndex();
                    startActivity(intent);
                    MXErrorExamsActivity.this.finish();
                }
            });
        }
        dialogShowOrHide(false, "");
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
    public void onActivitySaveInstanceState(Bundle outState) {

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
            case R.id.ll_base_back:
                this.finish();
                break;
            case R.id.ll_base_right:
                int currentIndex = totalPage / 5;
                Constant.EXAMS_ONE_MORE_TIME = currentIndex + 1;
                Intent intent = new Intent();
                intent.setClass(MXErrorExamsActivity.this, MXWriteHomeWorkActivity.class);
                intent.putExtra("mid_title", examsTitle);
                intent.putExtra("cch_id", examsId);
                page = currentIndex * 5;
                startActivity(intent);
                MXErrorExamsActivity.this.finish();
                break;
        }
    }
}
