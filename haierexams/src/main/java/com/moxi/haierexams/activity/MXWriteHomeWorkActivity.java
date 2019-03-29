package com.moxi.haierexams.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Message;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.moxi.haierexams.R;
import com.moxi.haierexams.cache.ACache;
import com.moxi.haierexams.db.SQLBookUtil;
import com.moxi.haierexams.db.SQLUtil;
import com.moxi.haierexams.model.ExamsDetailsModel;
import com.moxi.haierexams.model.OptionModel;
import com.moxi.haierexams.model.SyncExamsModel;
import com.moxi.haierexams.utils.MxgsaTagHandler;
import com.moxi.haierexams.view.SlideLinerlayout;
import com.moxi.handwritinglibs.ExameSurfaceViewDraw;
import com.mx.mxbase.base.BaseActivity;
import com.mx.mxbase.constant.Constant;
import com.mx.mxbase.http.MXHttpHelper;
import com.mx.mxbase.model.BaseModel;
import com.mx.mxbase.utils.Base64Utils;
import com.mx.mxbase.utils.DensityUtil;
import com.mx.mxbase.utils.GsonTools;
import com.mx.mxbase.utils.MXUamManager;
import com.mx.mxbase.utils.Toastor;
import com.mx.mxbase.view.AlertDialog;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import okhttp3.Call;

/**
 * Created by Archer on 16/10/13.
 */
public class MXWriteHomeWorkActivity extends BaseActivity implements View.OnClickListener {

    @Bind(R.id.tv_exams_title_view)
    TextView tvExamsTitle;
    @Bind(R.id.tv_exams_analysis_view)
    TextView tvAnalysis;
    @Bind(R.id.tv_chose_exams_title_view)
    TextView tvChoseTitle;
    @Bind(R.id.tv_chose_exams_analysis_view)
    TextView tvChoseAnalysis;
    @Bind(R.id.ll_base_back)
    LinearLayout llBack;
    @Bind(R.id.tv_base_back)
    TextView tvBack;
    @Bind(R.id.tv_base_mid_title)
    TextView tvMidTitle;
    @Bind(R.id.slide_liner_layout)
    SlideLinerlayout slideLinerLayout;
    @Bind(R.id.slide_chose_liner_layout)
    SlideLinerlayout slideChoseLayout;
    @Bind(R.id.pirv_home_work_achace)
    ExameSurfaceViewDraw writePadCommonView;
    @Bind(R.id.img_home_work_left)
    TextView imgPageLeft;
    @Bind(R.id.img_home_work_right)
    TextView imgPageRight;
    @Bind(R.id.tv_home_work_page_count)
    TextView tvPage;
    @Bind(R.id.radio_group_write_home)
    RadioGroup radioGroup;
    @Bind(R.id.radio_answer_1)
    RadioButton radioBtn1;
    @Bind(R.id.radio_answer_2)
    RadioButton radioBtn2;
    @Bind(R.id.radio_answer_3)
    RadioButton radioBtn3;
    @Bind(R.id.radio_answer_4)
    RadioButton radioBtn4;
    @Bind(R.id.ll_base_right)
    LinearLayout llRight;
    @Bind(R.id.tv_base_right)
    TextView tvRight;
    @Bind(R.id.img_last_zan_wei)
    ImageView imgLast;
    @Bind(R.id.img_next_zan_wei)
    ImageView imgNext;
    @Bind(R.id.ll_bottom_layout)
    LinearLayout llbottomLayout;
    @Bind(R.id.rl_show_or_hide)
    RelativeLayout rlShowHide;

    @Bind(R.id.img_home_work_up)
    TextView imgPageUp;
    @Bind(R.id.img_home_work_down)
    TextView imgPageDown;

    @Bind(R.id.page_up_down_ll)
    LinearLayout pageUpDownLL;

    private ExamsDetailsModel edm;
    private int page = 0;
    private SyncExamsModel sem;
    private String bookId;
    private OptionModel optionModel;
    private String response;//同步练习json数据
    private String historyJson = "";

    private String midStr = "";
    private int cchId;
    private boolean NEEDREFUSE = false;
    private int totalPage = 0;
    private int submitCount = 0;

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        dialogShowOrHide(false, "");
        if (msg.arg1 == 102) {
            if (msg.what == Integer.parseInt(Constant.SUCCESS)) {
                Toastor.showToast(this, "提交成功");
                Intent intent = new Intent(this, MXErrorExamsActivity.class);
                intent.putExtra("cob_exams_title", midStr);
                intent.putExtra("cob_zj_id", cchId);
                startActivity(intent);
                this.finish();
            } else {
                Toastor.showToast(this, "提交失败，请检查网络！");
            }
        }
    }

    @Override
    protected int getMainContentViewId() {
        return R.layout.mx_activity_write_home_work;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        init();
    }

    /**
     * 初始化视图
     */
    private void init() {
        //获取上个界面传递过来的数据
        //  writePadCommonView.setFingerDistinction(true);
        slideLinerLayout.setSlideHeight(600);//设置滑动的高度
        sem = (SyncExamsModel) this.getIntent().getSerializableExtra("sem_info");
        if (sem != null) {
            ACache.get(this).put("sem_id", sem.getCos_sem_id());
        }
        optionModel = (OptionModel) this.getIntent().getSerializableExtra("option_info");
        bookId = this.getIntent().getStringExtra("book_id");
        if (sem == null || optionModel == null) {
            midStr = this.getIntent().getStringExtra("mid_title");
            cchId = this.getIntent().getIntExtra("cch_id", -1);
        } else {
            cchId = optionModel.getId();
            midStr = sem.getCob_pub_name() + sem.getCob_sub_name() + sem.getCob_sec_name() + optionModel.getOptionName();
        }
        tvMidTitle.setText(midStr);

        llRight.setVisibility(View.VISIBLE);
        llBack.setVisibility(View.VISIBLE);
        tvBack.setText("返回");

        llBack.setOnClickListener(this);
        llRight.setOnClickListener(this);
        imgPageLeft.setOnClickListener(this);
        imgPageRight.setOnClickListener(this);

        imgPageUp.setOnClickListener(this);
        imgPageDown.setOnClickListener(this);

        radioBtn1.setOnClickListener(this);
        radioBtn2.setOnClickListener(this);
        radioBtn3.setOnClickListener(this);
        radioBtn4.setOnClickListener(this);

        slideLinerLayout.setSlideHeight(100);

        slideLinerLayout.setSlideListener(new SlideLinerlayout.SlideListener() {
            @Override
            public void moveDirection(boolean left, boolean up, boolean right, boolean down) {
                //  writePadCommonView.setScreenY(slideLinerLayout.getScrollY());
                writePadCommonView.scrollTo(0, slideLinerLayout.getScrollY());
            }

            @Override
            public void toBootom() {
            }

            @Override
            public void toTop() {
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (bookId == null) {
                            bookId = SQLUtil.getInstance(MXWriteHomeWorkActivity.this).getBookIdByCchId(cchId + "");
                        }
                        response = SQLBookUtil.getInstance(getApplicationContext()).getExamsDetails(bookId, cchId + "");
                        if (response.length() > 50) {
                            imgPageLeft.setVisibility(View.VISIBLE);
                            imgPageRight.setVisibility(View.VISIBLE);
                            tvPage.setVisibility(View.VISIBLE);
                            llRight.setVisibility(View.VISIBLE);
                            getHistory(MXUamManager.queryUser(MXWriteHomeWorkActivity.this));
                        } else {
                            imgPageLeft.setVisibility(View.GONE);
                            imgPageRight.setVisibility(View.GONE);
                            tvPage.setVisibility(View.GONE);
                            llRight.setVisibility(View.GONE);
                            Toastor.showToast(MXWriteHomeWorkActivity.this, "此章节题目为空，请重新选择");
                        }
                    }
                });
            }
        }).start();
    }

    /**
     * 获取历史纪录
     *
     * @param appSession
     */
    private void getHistory(String appSession) {
        dialogShowOrHide(true, "数据加载中...");
        HashMap<String, String> param = new HashMap<>();
        param.put("rows", "2000");
        param.put("subId", "");
        param.put("appSession", appSession);
        OkHttpUtils.post().url(Constant.HISTORYURL).params(param).build().connTimeOut(10000).execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                if (MXWriteHomeWorkActivity.this.isfinish)return;
                dialogShowOrHide(false, "数据加载中...");
                String temp = ACache.get(MXWriteHomeWorkActivity.this).getAsString(Constant.HISTORYURL);
                if (temp != null) {
                    historyJson = temp;
                    imgPageLeft.setVisibility(View.VISIBLE);
                    imgPageRight.setVisibility(View.VISIBLE);
                    tvPage.setVisibility(View.VISIBLE);
                    llRight.setVisibility(View.VISIBLE);
                    parseExamsDetails(page, temp);
                } else {
                    historyJson = "";
                    parseExamsDetails(page);
                }
            }

            @Override
            public void onResponse(String response, int id) {
                if (MXWriteHomeWorkActivity.this.isfinish)return;
                dialogShowOrHide(false, "数据加载中...");
                try {
                    ACache.get(MXWriteHomeWorkActivity.this).put(Constant.HISTORYURL, response);
                    historyJson = response;
                    parseExamsDetails(page, response);
                }catch (Exception e){}

            }
        });
    }

    /**
     * 解析同步练习试题
     *
     * @param page         当前页码
     * @param examsDetails 试题json数据
     */
    private void parseExamsDetails(final int page, String examsDetails) {
        edm = GsonTools.getPerson(response, ExamsDetailsModel.class);
        if (edm == null) {
            return;
        }
        //初始化一共显示多少题
        if (Constant.EXAMS_ONE_MORE_TIME != -1) {
            if (edm.getResult().size() >= Constant.EXAMS_ONE_MORE_TIME * 5) {
                totalPage = Constant.EXAMS_ONE_MORE_TIME * 5;
            } else {
                totalPage = edm.getResult().size();
            }
            tvPage.setText((page + 1) + "/" + totalPage);
            Constant.EXAMS_ONE_MORE_TIME = -1;//还原再来五题的标志
            if (page == 0) {
                imgPageLeft.setVisibility(View.GONE);
                if (totalPage - 1 == page) {
                    imgPageRight.setVisibility(View.GONE);
                } else {
                    imgPageRight.setVisibility(View.VISIBLE);
                }
            } else if (totalPage - 1 == page) {
                imgPageLeft.setVisibility(View.VISIBLE);
                imgPageRight.setVisibility(View.GONE);
            } else {
                imgPageLeft.setVisibility(View.VISIBLE);
                imgPageRight.setVisibility(View.VISIBLE);
            }
            if (totalPage - 1 == page) {
                tvRight.setText("提交");
            } else {
                tvRight.setText("");
            }
            disableRadioGroup(radioGroup, true);
            tvChoseAnalysis.setVisibility(View.GONE);
            tvAnalysis.setVisibility(View.GONE);
            showOrHideAnalysis(edm);
        } else if (totalPage != 0) {
            //需要更新submit
            if (totalPage > submitCount * 5) {
                if (page < totalPage - 5) {
                    tvRight.setText("查看结果");
                    disableRadioGroup(radioGroup, false);
                    tvChoseAnalysis.setVisibility(View.VISIBLE);
                    tvAnalysis.setVisibility(View.VISIBLE);
                } else if (totalPage - 1 == page) {
                    tvRight.setText("提交");
                    tvChoseAnalysis.setVisibility(View.GONE);
                    tvAnalysis.setVisibility(View.GONE);
                    disableRadioGroup(radioGroup, true);
                } else {
                    tvRight.setText("");
                    disableRadioGroup(radioGroup, true);
                    tvChoseAnalysis.setVisibility(View.GONE);
                    tvAnalysis.setVisibility(View.GONE);
                }
            } else {
                tvRight.setText("查看结果");
                disableRadioGroup(radioGroup, false);
                tvChoseAnalysis.setVisibility(View.VISIBLE);
                tvAnalysis.setVisibility(View.VISIBLE);
            }
            tvPage.setText((page + 1) + "/" + totalPage);
            if (page == 0) {
                imgPageLeft.setVisibility(View.GONE);
                if (totalPage - 1 == page) {
                    imgPageRight.setVisibility(View.GONE);
                } else {
                    imgPageRight.setVisibility(View.VISIBLE);
                }
            } else if (totalPage - 1 == page) {
                imgPageLeft.setVisibility(View.VISIBLE);
                imgPageRight.setVisibility(View.GONE);
            } else {
                imgPageLeft.setVisibility(View.VISIBLE);
                imgPageRight.setVisibility(View.VISIBLE);
            }
            showOrHideAnalysis(edm);
        } else {
            HashMap<String, String> sync = new HashMap<>();
            sync.put("appSession", MXUamManager.queryUser(this));
            sync.put("cchId", cchId + "");
            OkHttpUtils.post().url(Constant.QUERY_SYNC_HISTORY).params(sync).build().connTimeOut(10000).execute(new StringCallback() {
                @Override
                public void onError(Call call, Exception e, int id) {
                    if (MXWriteHomeWorkActivity.this.isfinish)return;
                    if (edm == null) {
                        totalPage = 0;
                        return;
                    } else {
                        if (edm.getResult().size() > 5) {
                            totalPage = 5;
                        } else {
                            totalPage = edm.getResult().size();
                        }
                    }
                    tvChoseAnalysis.setVisibility(View.GONE);
                    tvAnalysis.setVisibility(View.GONE);
                    tvPage.setText((page + 1) + "/" + totalPage);
                    if (page == 0) {
                        imgPageLeft.setVisibility(View.GONE);
                        if (totalPage - 1 == page) {
                            imgPageRight.setVisibility(View.GONE);
                        } else {
                            imgPageRight.setVisibility(View.VISIBLE);
                        }
                    } else if (totalPage - 1 == page) {
                        imgPageLeft.setVisibility(View.VISIBLE);
                        imgPageRight.setVisibility(View.GONE);
                    } else {
                        imgPageLeft.setVisibility(View.VISIBLE);
                        imgPageRight.setVisibility(View.VISIBLE);
                    }
                    showOrHideAnalysis(edm);
                }

                @Override
                public void onResponse(String response, int id) {
                    if (MXWriteHomeWorkActivity.this.isfinish)return;
                    try {
                        JSONObject result = new JSONObject(response);
                        String coeids = result.getJSONObject("result").getString("coeIds");
                        if (!coeids.equals("")) {
                            submitCount = Integer.parseInt(coeids);
                        }
                        if (submitCount != 0) {
                            //计算总页数
                            if (submitCount * 5 > edm.getResult().size()) {
                                totalPage = edm.getResult().size();
                            } else {
                                totalPage = submitCount * 5;
                            }
                            tvRight.setText("查看结果");
                            disableRadioGroup(radioGroup, false);
                            tvChoseAnalysis.setVisibility(View.VISIBLE);
                            tvAnalysis.setVisibility(View.VISIBLE);
                        } else {
                            totalPage = 5;
                            tvChoseAnalysis.setVisibility(View.GONE);
                            tvAnalysis.setVisibility(View.GONE);
                        }
                        tvPage.setText((page + 1) + "/" + totalPage);//edm.getResult().size()
                        if (page == 0) {
                            imgPageLeft.setVisibility(View.GONE);
                            if (totalPage - 1 == page) {
                                imgPageRight.setVisibility(View.GONE);
                            } else {
                                imgPageRight.setVisibility(View.VISIBLE);
                            }
                        } else if (totalPage - 1 == page) {
                            imgPageLeft.setVisibility(View.VISIBLE);
                            imgPageRight.setVisibility(View.GONE);
                        } else {
                            imgPageLeft.setVisibility(View.VISIBLE);
                            imgPageRight.setVisibility(View.VISIBLE);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        if (edm.getResult().size() > 5) {
                            totalPage = 5;
                        } else {
                            totalPage = edm.getResult().size();
                        }
                        if (totalPage - 1 == page) {
                            tvRight.setText("提交");
                        } else {
                            tvRight.setText("");
                        }
                        tvChoseAnalysis.setVisibility(View.GONE);
                        tvAnalysis.setVisibility(View.GONE);
                        tvPage.setText((page + 1) + "/" + totalPage);
                        if (page == 0) {
                            imgPageLeft.setVisibility(View.GONE);
                            if (totalPage - 1 == page) {
                                imgPageRight.setVisibility(View.GONE);
                            } else {
                                imgPageRight.setVisibility(View.VISIBLE);
                            }
                        } else if (totalPage - 1 == page) {
                            imgPageLeft.setVisibility(View.VISIBLE);
                            imgPageRight.setVisibility(View.GONE);
                        } else {
                            imgPageLeft.setVisibility(View.VISIBLE);
                            imgPageRight.setVisibility(View.VISIBLE);
                        }
                    }
                    showOrHideAnalysis(edm);
                }
            });
        }
    }

    private void showOrHideAnalysis(ExamsDetailsModel edm) {
        //给当前页面赋值
        try {
            if (edm.getResult().get(page).getType() == 6 || edm.getResult().get(page).getType() == 18) {
                slideChoseLayout.setVisibility(View.VISIBLE);
                slideChoseLayout.setSlideHeight(100);
                rlShowHide.setVisibility(View.GONE);
                setTitle((page + 1) + "、" + edm.getResult().get(page).getTitle(), tvChoseTitle);
                setTitle("正确答案为：" + edm.getResult().get(page).getAnswer() + "\t\t解析：" + edm.getResult().get(page).getAnalysis(), tvChoseAnalysis);
                resetRadioGroup(page);
                pageUpDownLL.setVisibility(View.GONE);
            } else {
                slideChoseLayout.setVisibility(View.GONE);
                rlShowHide.setVisibility(View.VISIBLE);
                slideLinerLayout.setSlideHeight(100);
                setTitle((page + 1) + "、" + edm.getResult().get(page).getTitle(), tvExamsTitle);
                setTitle("\n\n解析：" + edm.getResult().get(page).getAnalysis(), tvAnalysis);
                int height = measureView(tvExamsTitle) + measureView(tvAnalysis);

                RelativeLayout.LayoutParams da = (RelativeLayout.LayoutParams) writePadCommonView.getLayoutParams();
                if (height < DensityUtil.getScreenH(this)) {
                    height = DensityUtil.getScreenH(this);//1392
                    pageUpDownLL.setVisibility(View.GONE);
                } else {
                    if (height - 1200 < DensityUtil.getScreenH(this)) {//记算内容高度时增了1200的高度
                        pageUpDownLL.setVisibility(View.GONE);
                    } else {
                        pageUpDownLL.setVisibility(View.VISIBLE);
                    }
                }
                da.height = height;
                writePadCommonView.setLayoutParams(da);
                //  writePadCommonView.setScreenHeight(DensityUtil.getScreenW(this), height);
                writePadCommonView.setCanvasHeight(height);

                writePadCommonView.setSaveCode("exams" + edm.getResult().get(page).getId());
            }
            slideLinerLayout.moveToTop();
            slideChoseLayout.moveToTop();
        } catch (Exception e) {
            e.printStackTrace();
            imgPageLeft.setVisibility(View.GONE);
            imgPageRight.setVisibility(View.GONE);
            tvPage.setVisibility(View.GONE);
            llRight.setVisibility(View.GONE);
        }
    }

    /**
     * 解析同步练习试题
     *
     * @param page 当前页码
     */
    private void parseExamsDetails(final int page) {
        edm = GsonTools.getPerson(response, ExamsDetailsModel.class);
        writePadCommonView.setSaveCode("exams" + edm.getResult().get(page).getId());
        //初始化一共显示多少题
        if (Constant.EXAMS_ONE_MORE_TIME != -1) {
            if (edm.getResult().size() >= Constant.EXAMS_ONE_MORE_TIME * 5) {
                totalPage = Constant.EXAMS_ONE_MORE_TIME * 5;
            } else {
                totalPage = edm.getResult().size();
            }
            tvPage.setText((page + 1) + "/" + totalPage);
            if (page == 0) {
                imgPageLeft.setVisibility(View.GONE);
                if (totalPage - 1 == page) {
                    imgPageRight.setVisibility(View.GONE);
                } else {
                    imgPageRight.setVisibility(View.VISIBLE);
                }
            } else if (totalPage - 1 == page) {
                imgPageLeft.setVisibility(View.VISIBLE);
                imgPageRight.setVisibility(View.GONE);
            } else {
                imgPageLeft.setVisibility(View.VISIBLE);
                imgPageRight.setVisibility(View.VISIBLE);
            }
            Constant.EXAMS_ONE_MORE_TIME = -1;//还原再来五题的标志
            if (totalPage - 1 == page) {
                tvRight.setText("提交");
            } else {
                tvRight.setText("");
            }
            disableRadioGroup(radioGroup, true);
            tvChoseAnalysis.setVisibility(View.GONE);
            tvAnalysis.setVisibility(View.GONE);
            showOrHideAnalysis(edm);
        } else if (totalPage != 0) {
            //需要更新submit
            if (totalPage > submitCount * 5) {
                if (page < totalPage - 5) {
                    tvRight.setText("查看结果");
                    disableRadioGroup(radioGroup, false);
                    tvChoseAnalysis.setVisibility(View.VISIBLE);
                    tvAnalysis.setVisibility(View.VISIBLE);
                } else if (totalPage - 1 == page) {
                    tvRight.setText("提交");
                    tvChoseAnalysis.setVisibility(View.GONE);
                    tvAnalysis.setVisibility(View.GONE);
                    disableRadioGroup(radioGroup, true);
                } else {
                    tvRight.setText("");
                    disableRadioGroup(radioGroup, true);
                    tvChoseAnalysis.setVisibility(View.GONE);
                    tvAnalysis.setVisibility(View.GONE);
                }
            } else {
                tvRight.setText("查看结果");
                disableRadioGroup(radioGroup, false);
                tvChoseAnalysis.setVisibility(View.VISIBLE);
                tvAnalysis.setVisibility(View.VISIBLE);
            }
            tvPage.setText((page + 1) + "/" + totalPage);
            if (page == 0) {
                imgPageLeft.setVisibility(View.GONE);
                imgPageRight.setVisibility(View.VISIBLE);
            } else if (totalPage - 1 == page) {
                imgPageLeft.setVisibility(View.VISIBLE);
                imgPageRight.setVisibility(View.GONE);
            } else {
                imgPageLeft.setVisibility(View.VISIBLE);
                imgPageRight.setVisibility(View.VISIBLE);
            }
            showOrHideAnalysis(edm);
        } else {
            HashMap<String, String> sync = new HashMap<>();
            sync.put("appSession", MXUamManager.queryUser(this));
            sync.put("cchId", cchId + "");
            OkHttpUtils.post().url(Constant.QUERY_SYNC_HISTORY).params(sync).build().connTimeOut(10000).execute(new StringCallback() {
                @Override
                public void onError(Call call, Exception e, int id) {
                    if (MXWriteHomeWorkActivity.this.isfinish)return;
                    totalPage = 5;
                    tvChoseAnalysis.setVisibility(View.GONE);
                    tvAnalysis.setVisibility(View.GONE);
                    tvPage.setText((page + 1) + "/" + totalPage);
                    if (page == 0) {
                        imgPageLeft.setVisibility(View.GONE);
                        imgPageRight.setVisibility(View.VISIBLE);
                    } else if (totalPage - 1 == page) {
                        imgPageLeft.setVisibility(View.VISIBLE);
                        imgPageRight.setVisibility(View.GONE);
                    } else {
                        imgPageLeft.setVisibility(View.VISIBLE);
                        imgPageRight.setVisibility(View.VISIBLE);
                    }
                    showOrHideAnalysis(edm);
                }

                @Override
                public void onResponse(String response, int id) {
                    if (MXWriteHomeWorkActivity.this.isfinish)return;
                    try {
                        JSONObject result = new JSONObject(response);
                        String coeids = result.getJSONObject("result").getString("coeIds");
                        if (!coeids.equals("")) {
                            submitCount = Integer.parseInt(coeids);
                        }
                        if (submitCount != 0) {
                            //计算总页数
                            if (submitCount * 5 > edm.getResult().size()) {
                                totalPage = edm.getResult().size();
                            } else {
                                totalPage = submitCount * 5;
                            }
                            tvRight.setText("查看结果");
                            disableRadioGroup(radioGroup, false);
                            tvChoseAnalysis.setVisibility(View.VISIBLE);
                            tvAnalysis.setVisibility(View.VISIBLE);
                        } else {
                            totalPage = 5;
                            tvChoseAnalysis.setVisibility(View.GONE);
                            tvAnalysis.setVisibility(View.GONE);
                        }
                        tvPage.setText((page + 1) + "/" + totalPage);
                        if (page == 0) {
                            imgPageLeft.setVisibility(View.GONE);
                            imgPageRight.setVisibility(View.VISIBLE);
                        } else if (totalPage - 1 == page) {
                            imgPageLeft.setVisibility(View.VISIBLE);
                            imgPageRight.setVisibility(View.GONE);
                        } else {
                            imgPageLeft.setVisibility(View.VISIBLE);
                            imgPageRight.setVisibility(View.VISIBLE);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        if (edm.getResult().size() > 5) {
                            totalPage = 5;
                        } else {
                            totalPage = edm.getResult().size();
                        }
                        if (totalPage - 1 == page) {
                            tvRight.setText("提交");
                        } else {
                            tvRight.setText("");
                        }
                        tvChoseAnalysis.setVisibility(View.GONE);
                        tvAnalysis.setVisibility(View.GONE);
                        tvPage.setText((page + 1) + "/" + totalPage);
                        if (page == 0) {
                            imgPageLeft.setVisibility(View.GONE);
                            imgPageRight.setVisibility(View.VISIBLE);
                        } else if (totalPage - 1 == page) {
                            imgPageLeft.setVisibility(View.VISIBLE);
                            imgPageRight.setVisibility(View.GONE);
                        } else {
                            imgPageLeft.setVisibility(View.VISIBLE);
                            imgPageRight.setVisibility(View.VISIBLE);
                        }
                    }
                    showOrHideAnalysis(edm);
                }
            });
        }
    }

    /**
     * 重新设置radioGroup
     *
     * @param page 题号
     */
    private void resetRadioGroup(int page) {
        String aa = ACache.get(this).getAsString("cchId" + cchId + page);
        if (TextUtils.isEmpty(aa)) {
            radioGroup.clearCheck();
        } else {
            switch (aa) {
                case "A":
                    radioBtn1.setChecked(true);
                    break;
                case "B":
                    radioBtn2.setChecked(true);
                    break;
                case "C":
                    radioBtn3.setChecked(true);
                    break;
                case "D":
                    radioBtn4.setChecked(true);
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 让radiogroup失去点击效果
     *
     * @param testRadioGroup 目标radiogroup
     * @param able           是否允许点击
     */
    public void disableRadioGroup(RadioGroup testRadioGroup, boolean able) {
        for (int i = 0; i < testRadioGroup.getChildCount(); i++) {
            testRadioGroup.getChildAt(i).setEnabled(able);
            testRadioGroup.getChildAt(i).setClickable(able);
        }
    }

    /**
     * @param title
     * @param view
     */
    private void setTitle(String title, TextView view) {
        Pattern p = Pattern.compile("<img[^>]+src\\s*=\\s*['\"](\\s*)(data:image/)\\S+(base64,)([^'\"]+)['\"][^>]*>");
        Matcher m = p.matcher(title);
        while (m.find()) {
            String str = m.group(4);
            title = title.replace(m.group(), "#@M#@X@" + str + "#@M#@X@");
        }
        String titleResult = title;
        if (titleResult.indexOf("#@M#@X@") > 0) {
            String[] s = titleResult.split("#@M#@X@");
            view.setText("");
            for (int j = 0; j < s.length; j++) {
                if (j % 2 == 0) {
                    view.append(Html.fromHtml(s[j]));
                } else {
                    Bitmap bitmap = Base64Utils.base64ToBitmap(s[j]);
                    ImageSpan imgSpan = new ImageSpan(this, bitmap);
                    SpannableString spanString = new SpannableString("icon");
                    spanString.setSpan(imgSpan, 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    view.append(spanString);
                }
            }
        } else {
            view.setText(Html.fromHtml(title, null, new MxgsaTagHandler(this)));
        }
    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {
        writePadCommonView.onResume();
        if (MXErrorExamsActivity.page != -1) {
            page = MXErrorExamsActivity.page;
            MXErrorExamsActivity.page = -1;
        }
        if (NEEDREFUSE) {
            if (response != null) {
                getHistory(MXUamManager.queryUser(MXWriteHomeWorkActivity.this));
            }
            NEEDREFUSE = false;
        }
    }

    @Override
    public void onActivityPaused(Activity activity) {
        try {
            writePadCommonView.onPause();
            writePadCommonView.onDestory();
            if (edm.getResult().get(page).getType() != 6 && edm.getResult().get(page).getType() != 18) {
                saveBitMap(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityStopped(Activity activity) {
        writePadCommonView.onDestory();
        saveBitMap(2);
        NEEDREFUSE = true;
        dialogShowOrHide(false, "");
    }

    @Override
    public void onActivitySaveInstanceState(Bundle outState) {

    }

    @Override
    public void onActivityRestoreInstanceState(Bundle savedInstanceState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        NEEDREFUSE = false;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_PAGE_UP:
                try {
                    if (edm.getResult().get(page).getType() != 6 && edm.getResult().get(page).getType() != 18) {
                        saveBitMap(4);
                    }
                    if (page > 0) {
                        page--;
                        if (historyJson.equals("")) {
                            parseExamsDetails(page);
                        } else {
                            parseExamsDetails(page, historyJson);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            case KeyEvent.KEYCODE_PAGE_DOWN:
                try {
                    if (edm.getResult().get(page).getType() != 6 && edm.getResult().get(page).getType() != 18) {
                        saveBitMap(5);
                    }
                    if (page < totalPage - 1) {
                        page++;
                        if (historyJson.equals("")) {
                            parseExamsDetails(page);
                        } else {
                            parseExamsDetails(page, historyJson);
                        }
                    }
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
            case R.id.ll_base_back:
                this.finish();
                break;
            case R.id.img_home_work_left:
                try {
                    if (edm.getResult().get(page).getType() != 6 && edm.getResult().get(page).getType() != 18) {
                        saveBitMap(6);
                    }
                    if (page > 0) {
                        page--;
                        if (historyJson.equals("")) {
                            parseExamsDetails(page);
                        } else {
                            parseExamsDetails(page, historyJson);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.img_home_work_up://上翻
                if (writePadCommonView.isOnDraw())
                    writePadCommonView.fullRefuresh();
                slideLinerLayout.moveUp();

                break;
            case R.id.img_home_work_down://下翻
                if (writePadCommonView.isOnDraw())
                    writePadCommonView.fullRefuresh();
                slideLinerLayout.moveDown();

                break;
            case R.id.img_home_work_right:
                try {
                    if (edm.getResult().get(page).getType() != 6 && edm.getResult().get(page).getType() != 18) {
                        saveBitMap(7);
                    }
                    if (page < totalPage - 1) {
                        page++;
                        if (historyJson.equals("")) {
                            parseExamsDetails(page);
                        } else {
                            parseExamsDetails(page, historyJson);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            //提交
            case R.id.ll_base_right:
                if (tvRight.getText().toString().equals("提交")) {
                    new AlertDialog(this).builder().setTitle("提示").setMsg("确认提交?").setCancelable(false).setNegativeButton("确定", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            submitTongbu();
                        }
                    }).setPositiveButton("取消", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                        }
                    }).show();
                } else {
                    Intent intent = new Intent();
                    intent.setClass(this, MXErrorExamsActivity.class);
                    intent.putExtra("cob_zj_id", cchId);
                    intent.putExtra("cob_exams_title", midStr);
                    startActivity(intent);
//                    this.finish();
                }
                break;
            case R.id.radio_answer_1:
                updateAnswer(page, "A");
                break;
            case R.id.radio_answer_2:
                updateAnswer(page, "B");
                break;
            case R.id.radio_answer_3:
                updateAnswer(page, "C");
                break;
            case R.id.radio_answer_4:
                updateAnswer(page, "D");
                break;
            default:
                break;
        }
    }

    /**
     * 提交同步练习
     */
    private void submitTongbu() {
        int tempCoeids = page / 5 + 1;
        HashMap<String, String> sub = new HashMap<>();
        if (sem == null) {
            sub.put("semId", ACache.get(this).getAsString("sem_id"));
        } else {
            sub.put("semId", sem.getCos_sem_id());
        }
        sub.put("cchId", cchId + "");
        sub.put("coeIds", tempCoeids + "");
        if (MXUamManager.queryUser(this).equals("")) {
            Toastor.showToast(this, "请先登录后再试");
            return;
        }
        sub.put("appSession", MXUamManager.queryUser(this));
        MXHttpHelper.getInstance(this).postStringBack(102, Constant.SUBMIT_TB_RESULT, sub, getHandler(), BaseModel.class);
    }

    /**
     * 更新缓存结果
     *
     * @param page
     * @param d
     */
    private void updateAnswer(int page, String d) {
        ACache.get(this).put("cchId" + cchId + page, d);
    }

    /**
     * 保存缓存图片方便上传图片的时候读取
     */
    private void saveBitMap(int i) {
        try {
            writePadCommonView.saveWritePad(edm.getResult().get(page).getId() + "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        writePadCommonView.setSaveCode("");
    }

    /**
     * 计算view的高度
     *
     * @param child
     * @return
     */
    private int measureView(View child) {
        ViewGroup.LayoutParams lp = child.getLayoutParams();
        if (lp == null) {
            lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        int childMeasureWidth = ViewGroup.getChildMeasureSpec(0, 0, lp.width);
        int childMeasureHeight;
        if (lp.height > 0) {
            childMeasureHeight = View.MeasureSpec.makeMeasureSpec(lp.height, View.MeasureSpec.EXACTLY);
        } else {
            childMeasureHeight = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);//未指定
        }
        child.measure(childMeasureWidth, childMeasureHeight);
        int aaa = child.getMeasuredHeight();
        return aaa + 600;
    }
}
