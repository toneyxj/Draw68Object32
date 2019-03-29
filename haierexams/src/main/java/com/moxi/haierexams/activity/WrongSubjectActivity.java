package com.moxi.haierexams.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.moxi.haierexams.R;
import com.moxi.haierexams.adapter.SubjectWrongGridAdapter;
import com.moxi.haierexams.http.HttpVolleyCallback;
import com.moxi.haierexams.http.VolleyHttpUtil;
import com.moxi.haierexams.model.WrongExamsModel;
import com.mx.mxbase.base.BaseActivity;
import com.mx.mxbase.constant.Constant;
import com.mx.mxbase.utils.MXUamManager;
import com.mx.mxbase.utils.Toastor;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;

/**
 * Created by zhengdelong on 16/9/22.
 */

public class WrongSubjectActivity extends BaseActivity {

    private LinearLayout ll_base_back;
    private TextView tv_base_back;
    private TextView tv_base_mid_title;
    private GridView wrong_grid;

    @Bind(R.id.title_info)
    TextView tvTitleInfo;

    @Override
    protected int getMainContentViewId() {
        return R.layout.activity_subject_wrong;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        initView();
    }

    private void initView() {
        tv_base_mid_title = (TextView) findViewById(R.id.tv_base_mid_title);
        tv_base_mid_title.setVisibility(View.GONE);
        tv_base_back = (TextView) findViewById(R.id.tv_base_back);
        tv_base_back.setText("错题库");
        ll_base_back = (LinearLayout) findViewById(R.id.ll_base_back);
        ll_base_back.setVisibility(View.VISIBLE);
        wrong_grid = (GridView) findViewById(R.id.wrong_grid);
        ll_base_back.setOnClickListener(backClick);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        HashMap<String, String> param = new HashMap<>();
        param.put("page", "1");
        param.put("rows", "2000");
        param.put("subId", "");
        param.put("appSession", MXUamManager.queryUser(this));
        VolleyHttpUtil.post(this, Constant.HISTORYURL, param, new HttpVolleyCallback() {
            @Override
            public void onSuccess(String data) {
                parseHistoryData(data);
            }

            @Override
            public void onFilad(String msg) {
                Toastor.showToast(WrongSubjectActivity.this, "请检查网络后重试");
            }
        });
    }

    private void parseHistoryData(String data) {
        final List<WrongExamsModel> listWrong = new ArrayList<>();
        try {
            int[] tem = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0};
            JSONObject jsonObject = new JSONObject(data);
            int code = jsonObject.optInt("code", -1);
            if (code == 0) {
                JSONObject jsonObject1 = jsonObject.optJSONObject("result");
                JSONArray jsonArray = jsonObject1.optJSONArray("list");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject liObj = jsonArray.getJSONObject(i);
                    switch (getSubjectNameByTitle(liObj.optString("title"))) {
                        case "语文":
                            tem[0]++;
                            break;
                        case "数学":
                            tem[1]++;
                            break;
                        case "英语":
                            tem[2]++;
                            break;
                        case "物理":
                            tem[3]++;
                            break;
                        case "化学":
                            tem[4]++;
                            break;
                        case "生物":
                            tem[5]++;
                            break;
                        case "历史":
                            tem[6]++;
                            break;
                        case "地理":
                            tem[7]++;
                            break;
                        case "政治":
                            tem[8]++;
                            break;
                        default:
                            break;
                    }
                }
                int taoshu = 0;
                for (int i = 0; i < tem.length; i++) {
                    if (tem[i] != 0) {
                        WrongExamsModel wrongExamsModel = new WrongExamsModel();
                        wrongExamsModel.setCount(tem[i]);
                        wrongExamsModel.setSubjectId((i + 1) + "");
                        wrongExamsModel.setSubjectName(subs[i]);
                        listWrong.add(wrongExamsModel);
                        taoshu += tem[i];
                    }
                }
                if (listWrong.size() > 0) {
                    SubjectWrongGridAdapter subjectWrongGridAdapter = new SubjectWrongGridAdapter(this, listWrong);
                    wrong_grid.setAdapter(subjectWrongGridAdapter);
                    tvTitleInfo.setText("共" + listWrong.size() + "门课程，" + taoshu + "套练习题");
                } else {
                    tvTitleInfo.setText("暂无记录");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            SubjectWrongGridAdapter subjectWrongGridAdapter = new SubjectWrongGridAdapter(this, listWrong);
            wrong_grid.setAdapter(subjectWrongGridAdapter);
            tvTitleInfo.setText("暂无记录");
        }
    }

    View.OnClickListener backClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            WrongSubjectActivity.this.finish();
        }
    };

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {
        initData();
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

    String[] subs = new String[]{"语文", "数学", "英语", "物理", "化学", "生物", "历史", "地理", "政治"};

    private String getSubjectNameByTitle(String title) {
        String name = "";
        for (String sub : subs) {
            if (title.indexOf(sub) >= 0) {
                name = sub;
                return name;
            }
        }
        return name;
    }
}
