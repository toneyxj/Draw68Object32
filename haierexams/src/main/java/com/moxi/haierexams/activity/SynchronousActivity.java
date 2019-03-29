package com.moxi.haierexams.activity;

import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.moxi.haierexams.R;
import com.moxi.haierexams.adapter.BookTagBaseAdapter;
import com.moxi.haierexams.adapter.SynchronousAdapter;
import com.moxi.haierexams.db.DownDbService;
import com.moxi.haierexams.db.SQLUtil;
import com.moxi.haierexams.model.OptionModel;
import com.moxi.haierexams.model.SyncExamsModel;
import com.moxi.haierexams.model.SynchronousModel;
import com.moxi.haierexams.model.UpdateBean;
import com.moxi.haierexams.view.ProgressView;
import com.moxi.haierexams.view.TagCloudLayout;
import com.mx.mxbase.base.BaseActivity;
import com.mx.mxbase.utils.MXUamManager;
import com.mx.mxbase.utils.Toastor;
import com.mx.mxbase.view.AlertDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;


/**
 * Created by zhengdelong on 16/9/30.
 */

public class SynchronousActivity extends BaseActivity implements View.OnClickListener, ServiceConnection {

    private TextView tv_exams_list_page_count;
    private ImageView img_exams_list_left;
    private ImageView img_exams_list_right;
    private ListView sync_list;
    private List<SynchronousModel> synchrousData = new ArrayList<>();

    private LinearLayout ll_base_back;
    private TextView tv_base_back;

    //每页十条数据
    private int pageCount = 10;
    private int totlePage = 0;
    //当前第几页
    private int currentPage = 1;
    private BookTagBaseAdapter adapterTag;
    private SyncExamsModel syncExamsModel;
    private int chosenBookId;//当前选择的书籍ID
    private DownDbService downDbService;
    private Intent serverceIt;
    private SynchronousAdapter syncadd;

    @Bind(R.id.tag_clound_layout_jc)
    TagCloudLayout tagCloudLayout;
    @Bind(R.id.down_title_tv)
    TextView downTilte;
    @Bind(R.id.tv_exams_title)
    TextView tvExamsTitle;
    @Bind(R.id.tv_base_mid_title)
    TextView tvMidTitle;
    @Bind(R.id.img_download_exams_res)
    ImageView imgDownExamsRes;
    @Bind(R.id.img_del_exams_res)
    ImageView imgDelExamsRes;
    @Bind(R.id.progress_view_down)
    ProgressView progressView;

    private List<Map<String, Object>> maps;


    @Override
    protected int getMainContentViewId() {
        return R.layout.activity_synchronous;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        serverceIt = new Intent();
        serverceIt.setClass(this, DownDbService.class);
        startService(serverceIt);

        initView();
        initData();
        setData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        bindService(serverceIt, this, Service.BIND_AUTO_CREATE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindService(this);
    }

    private void initData() {
        syncExamsModel = (SyncExamsModel) this.getIntent().getSerializableExtra("sem_info");
        List<OptionModel> optionModelList = SQLUtil.getInstance(SynchronousActivity.this).getCourseBookFromDb(syncExamsModel.getCos_sem_id(), syncExamsModel.getCob_pub_id(), syncExamsModel.getCob_sec_id(), syncExamsModel.getCob_sub_id());
        if (optionModelList.size() > 0) {
            optionModelList.get(0).setChosen(true);
            setBookView(optionModelList);
            setChoseBook(optionModelList.get(0));
            tagCloudLayout.setVisibility(View.VISIBLE);
            findViewById(R.id.down_all_btn).setTag(optionModelList);
            //检测更新
            maps = new ArrayList<>();
            for (OptionModel o : optionModelList) {
                File dbfile = new File(DownDbService.getFilePathWithId(o.getId()));
                if (dbfile.exists()) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", o.getId());
                    map.put("dateTime", dbfile.lastModified());
                    maps.add(map);
                }
            }
            if (downDbService != null && maps != null && maps.size() > 0) {
                downDbService.setCallBack(callback);
                downDbService.checkUpdateBook(new Gson().toJson(maps));
            }
        }
    }

    private void setChoseBook(OptionModel option) {
        this.chosenBookId = option.getId();
        loadDataWithBookId();
        File dbfile = new File(DownDbService.getFilePathWithId(option.getId()));
        imgDelExamsRes.setTag(option);
        imgDownExamsRes.setTag(option);
        if (!dbfile.exists()) {
            findViewById(R.id.down_btn).setVisibility(View.GONE);
            imgDownExamsRes.setImageResource(R.mipmap.mx_img_to_down);
            downTilte.setText("未下载");
            imgDownExamsRes.setClickable(true);
            findViewById(R.id.down_btn).setTag(option);
        } else if (option.isUpdate()) {
            findViewById(R.id.down_btn).setVisibility(View.GONE);
            imgDownExamsRes.setClickable(true);
            imgDownExamsRes.setImageResource(R.mipmap.mx_img_to_down);
            downTilte.setText("有更新");
            findViewById(R.id.down_btn).setTag(option);
        } else {
            imgDownExamsRes.setClickable(false);
            imgDownExamsRes.setImageResource(R.mipmap.mx_img_had_down);
        }
    }

    private void initView() {
        tvMidTitle.setText("同步练习");
        tv_exams_list_page_count = (TextView) findViewById(R.id.tv_exams_list_page_count);
        img_exams_list_left = (ImageView) findViewById(R.id.img_exams_list_left);
        img_exams_list_right = (ImageView) findViewById(R.id.img_exams_list_right);
        img_exams_list_left.setOnClickListener(img_exams_list_leftClick);
        img_exams_list_right.setOnClickListener(img_exams_list_rightClick);
        tv_base_back = (TextView) findViewById(R.id.tv_base_back);
        ll_base_back = (LinearLayout) findViewById(R.id.ll_base_back);
        ll_base_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SynchronousActivity.this.finish();
            }
        });
        sync_list = (ListView) findViewById(R.id.sync_list);
        ll_base_back.setVisibility(View.VISIBLE);

        findViewById(R.id.down_btn).setOnClickListener(this);
        findViewById(R.id.down_all_btn).setOnClickListener(this);
        imgDownExamsRes.setOnClickListener(this);
        imgDelExamsRes.setOnClickListener(this);
    }

    /**
     * 书本选择
     */
    private void setBookView(final List<OptionModel> optionModelList) {
        adapterTag = new BookTagBaseAdapter(this, optionModelList);
        tvExamsTitle.setText(syncExamsModel.getCob_sec_name() + " " + syncExamsModel.getCob_sub_name() + " " + optionModelList.get(0).getOptionName());
        adapterTag.setOnItemClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = Integer.valueOf(view.getTag().toString());
                for (int i = 0; i < optionModelList.size(); i++) {
                    if (i == position) {
                        optionModelList.get(i).setChosen(true);
                        tvExamsTitle.setText(syncExamsModel.getCob_sec_name() + " " + syncExamsModel.getCob_sub_name() + " " + optionModelList.get(i).getOptionName());
                        setChoseBook(optionModelList.get(i));
                    } else {
                        optionModelList.get(i).setChosen(false);
                    }
                }
                adapterTag.notifyDataSetChanged();
            }
        });
        tagCloudLayout.setAdapter(adapterTag);
    }

    /**
     * 下载db文件
     */
    private void loadDataWithBookId() {
        List<OptionModel> titleList = SQLUtil.getInstance(SynchronousActivity.this).getCourseChapterMenuFromDb(chosenBookId + "");
        SynchronousModel sync;
        synchrousData.clear();
        for (int i = 0; i < titleList.size(); i++) {
            sync = new SynchronousModel();
            int ids = titleList.get(i).getId();
            String title = titleList.get(i).getOptionName();
            sync.setParentId(chosenBookId);
            sync.setId(ids);
            sync.setTitle(title);
            synchrousData.add(sync);
        }
        setData();
    }

    private void setData() {
        if (synchrousData == null)
            synchrousData = new ArrayList<>();
        tv_base_back.setText("好题天天练");
        tv_base_back.setVisibility(View.VISIBLE);

        if (synchrousData.size() <= pageCount) {
            tv_exams_list_page_count.setText("1" + "/" + "1");
            syncadd = new SynchronousAdapter(this, synchrousData, chosenBookId + "");
            sync_list.setAdapter(syncadd);
        } else {
            syncadd = new SynchronousAdapter(this, synchrousData, chosenBookId + "");
            sync_list.setAdapter(syncadd);
            int l = synchrousData.size() / pageCount;
            int a = synchrousData.size() % pageCount;
            if (a > 0) {
                l = l + 1;
            }
            totlePage = l;
            tv_exams_list_page_count.setText("1" + "/" + totlePage);
        }
        syncadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogShowOrHide(true, "数据加载中...");
                final OptionModel synchronousModel = (OptionModel) view.getTag();
                File dbfile = new File(DownDbService.getFilePathWithId(chosenBookId));
                if (dbfile.exists()) {
                    HashMap<String, String> sync = new HashMap<String, String>();
                    sync.put("appSession", MXUamManager.queryUser(SynchronousActivity.this));
                    sync.put("cchId", synchronousModel.getId() + "");
                    Intent writeIntent = new Intent(SynchronousActivity.this, MXWriteHomeWorkActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("sem_info", syncExamsModel);
                    bundle.putSerializable("option_info", synchronousModel);
                    bundle.putString("book_id", chosenBookId + "");
                    writeIntent.putExtras(bundle);
                    startActivity(writeIntent);
                } else {
                    dialogShowOrHide(false, "");
                    Toast.makeText(getApplicationContext(), "请下载后，再阅读", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setCurrentPageData(List<SynchronousModel> currentPageData, int state) {
        SynchronousAdapter syncadd = new SynchronousAdapter(this, currentPageData, chosenBookId + "");
        sync_list.setAdapter(syncadd);
        if (state == 1) {
            currentPage++;
        } else {
            currentPage--;
        }
        syncadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogShowOrHide(true, "数据加载中...");
                final OptionModel synchronousModel = (OptionModel) view.getTag();
                File dbfile = new File(DownDbService.getFilePathWithId(chosenBookId));
                if (dbfile.exists()) {
                    HashMap<String, String> sync = new HashMap<String, String>();
                    sync.put("appSession", MXUamManager.queryUser(SynchronousActivity.this));
                    sync.put("cchId", synchronousModel.getId() + "");
                    Intent writeIntent = new Intent(SynchronousActivity.this, MXWriteHomeWorkActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("sem_info", syncExamsModel);
                    bundle.putSerializable("option_info", synchronousModel);
                    bundle.putString("book_id", chosenBookId + "");
                    writeIntent.putExtras(bundle);
                    startActivity(writeIntent);
                } else {
                    dialogShowOrHide(false, "");
                    Toast.makeText(getApplicationContext(), "请下载后，再阅读", Toast.LENGTH_SHORT).show();
                }
            }
        });
        tv_exams_list_page_count.setText(currentPage + "/" + totlePage);
    }

    View.OnClickListener img_exams_list_rightClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                if (currentPage * pageCount - 1 == synchrousData.size() - 1) {
                    return;
                }
                //最后一页的数据小于pageCount
                if (pageCount > (synchrousData.size() - (currentPage * pageCount))) {
                    int fromIndex = (currentPage) * pageCount;
                    int endIndex = synchrousData.size();
                    setCurrentPageData(synchrousData.subList(fromIndex, endIndex), 1);
                } else {
                    int fromIndex = (currentPage) * pageCount;
                    int endIndex = (currentPage + 1) * pageCount;
                    setCurrentPageData(synchrousData.subList(fromIndex, endIndex), 1);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    View.OnClickListener img_exams_list_leftClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (currentPage == 1) {
                return;
            }
            int fromIndex = (currentPage - 1) * pageCount - pageCount;
            int endIndex = (currentPage - 1) * pageCount;
            setCurrentPageData(synchrousData.subList(fromIndex, endIndex), 0);
        }
    };

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {
        dialogShowOrHide(false, "数据加载中...");
    }

    @Override
    public void onActivityStopped(Activity activity) {
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

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_download_exams_res:
            case R.id.down_btn://下载
                OptionModel option = (OptionModel) view.getTag();
                List<Integer> bookids = new ArrayList<>();
                bookids.add(option.getId());
                if (downDbService != null) {
                    downDbService.setCallBack(callback);
                    downDbService.downBookFileWithIds(bookids);
                }
                break;
            case R.id.img_del_exams_res:
                OptionModel aaa = (OptionModel) view.getTag();
                final File dbfile = new File(DownDbService.getFilePathWithId(aaa.getId()));
                if (dbfile.exists()) {
                    new AlertDialog(this).builder().setTitle("提示").setMsg("确认删除下载的资源文件?").setCancelable(false).setNegativeButton("确定", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dbfile.delete();
                            imgDownExamsRes.setClickable(true);
                            imgDownExamsRes.setImageResource(R.mipmap.mx_img_to_down);
                        }
                    }).setPositiveButton("取消", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                        }
                    }).show();
                } else {
                    Toastor.showToast(this, "没有要删除的文件");
                }
                break;
            case R.id.down_all_btn://全部下载
                final List<OptionModel> options = (List<OptionModel>) view.getTag();
                if (options == null) {
                    return;
                }
                new AlertDialog(this).builder().setTitle("提示").setMsg("确认全部下载?").setCancelable(false).setNegativeButton("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        List<Integer> allBookids = new ArrayList<>();
                        for (OptionModel o : options) {
                            File dbfile = new File(DownDbService.getFilePathWithId(o.getId()));
                            if (dbfile.exists()) {
                                if (o.isUpdate()) {
                                    allBookids.add(o.getId());
                                }
                            } else {
                                allBookids.add(o.getId());
                            }
                        }
                        if (downDbService != null) {
                            downDbService.setCallBack(callback);
                            downDbService.downBookFileWithIds(allBookids);
                        }
                    }
                }).setPositiveButton("取消", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                }).show();
                break;
        }
    }

    DownDbService.DownDbCallback callback = new DownDbService.DownDbCallback() {

        @Override
        public void onEnd(List<Integer> successIds, List<Integer> failureBookIds) {

            if (successIds != null) {
                findViewById(R.id.down_ll).setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), "下载成功", Toast.LENGTH_SHORT).show();
                imgDownExamsRes.setClickable(false);
                List<OptionModel> optionModelList = adapterTag.getData();
                imgDownExamsRes.setImageResource(R.mipmap.mx_img_had_down);
                if (optionModelList != null) {
                    for (Integer id : successIds) {
                        for (OptionModel om : optionModelList) {
                            if (id == om.getId()) {
                                om.setUpdate(false);
                                continue;
                            }
                        }
                    }
                    adapterTag.setData(optionModelList);
                    adapterTag.notifyDataSetChanged();
                }
            } else {
                Toast.makeText(getApplicationContext(), "下载失败", Toast.LENGTH_SHORT).show();
                downTilte.setText("下载失败,点击重试");
                imgDownExamsRes.setClickable(true);
                imgDownExamsRes.setImageResource(R.mipmap.mx_img_to_down);
            }
        }

        @Override
        public void onStart(String error) {
            if (error == null) {
                downTilte.setText("准备下载");
            } else {
                downTilte.setText(error);
            }
            findViewById(R.id.down_ll).setVisibility(View.VISIBLE);
        }

        @Override
        public void onProgress(int i, long l, long l1, int index, int count) {
            if (count == 1) {
                progressView.setCurNumber((int) l);
                progressView.setMaxNumber((int) l1);
                downTilte.setText("正在下载");
            } else {
                downTilte.setText(l + "---" + l1 + "-------(" + index + "/" + count + ")");
            }
            findViewById(R.id.down_btn).setVisibility(View.GONE);
            findViewById(R.id.down_all_btn).setVisibility(View.GONE);
            findViewById(R.id.down_ll).setVisibility(View.VISIBLE);
        }

        @Override
        public void onUpdate(List<UpdateBean> updates) {
            List<OptionModel> optionModelList = adapterTag.getData();
            if (optionModelList != null && updates != null) {
                for (UpdateBean bean : updates) {
                    for (OptionModel om : optionModelList) {
                        if (bean.getId() == om.getId()) {
                            om.setUpdate(true);
                            continue;
                        }
                    }
                }
                adapterTag.setData(optionModelList);
                adapterTag.notifyDataSetChanged();
                findViewById(R.id.down_all_btn).setTag(optionModelList);
            }
        }
    };

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        downDbService = ((DownDbService.MyBinder) iBinder).getService();
        if (downDbService != null)
            downDbService.setCallBack(callback);
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {

    }
}
