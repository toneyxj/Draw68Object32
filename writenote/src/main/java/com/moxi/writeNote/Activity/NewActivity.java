package com.moxi.writeNote.Activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.moxi.handwritinglibs.WriteSurfaceViewDraw;
import com.moxi.handwritinglibs.asy.DeleteOneFileAsy;
import com.moxi.handwritinglibs.db.WritPadModel;
import com.moxi.handwritinglibs.db.WritePadUtils;
import com.moxi.handwritinglibs.db.index.IndexDbUtils;
import com.moxi.handwritinglibs.listener.DeleteListener;
import com.moxi.handwritinglibs.listener.NoteSaveWriteListener;
import com.moxi.handwritinglibs.listener.WindowRefureshListener;
import com.moxi.handwritinglibs.listener.WriteTagListener;
import com.moxi.handwritinglibs.model.CodeAndIndex;
import com.moxi.handwritinglibs.model.ExtendModel;
import com.moxi.handwritinglibs.utils.BrodcastUtils;
import com.moxi.handwritinglibs.utils.DataUtils;
import com.moxi.handwritinglibs.utils.DbPhotoLoader;
import com.moxi.handwritinglibs.utils.DbWriteModelLoader;
import com.moxi.handwritinglibs.utils.FingerUtils;
import com.moxi.handwritinglibs.utils.LoadLocPhotoUtils;
import com.moxi.handwritinglibs.writeUtils.PenUtils;
import com.moxi.writeNote.Activity.utils.PhotoCutActivity;
import com.moxi.writeNote.R;
import com.moxi.writeNote.WriteBaseActivity;
import com.moxi.writeNote.adapter.WriteNoteBackAdapter;
import com.moxi.writeNote.config.ActivityUtils;
import com.moxi.writeNote.dialog.CustomPenActivity;
import com.moxi.writeNote.dialog.PageSkipActivity;
import com.moxi.writeNote.listener.ChangeToTextListener;
import com.moxi.writeNote.listener.InsertLister;
import com.moxi.writeNote.myScript.MyScriptManager;
import com.moxi.writeNote.utils.BackPhotoUtils;
import com.moxi.writeNote.utils.BrushSettingUtils;
import com.moxi.writeNote.utils.DataUtuls;
import com.moxi.writeNote.utils.InsertFileAsy;
import com.moxi.writeNote.utils.UpdateDrawBackTheard;
import com.moxi.writeNote.utils.UpdateFileIndex;
import com.moxi.writeNote.view.InterceptView;
import com.moxi.writeNote.view.NoGrigView;
import com.mx.mxbase.base.MyApplication;
import com.mx.mxbase.constant.APPLog;
import com.mx.mxbase.dialog.InputDialog;
import com.mx.mxbase.interfaces.InsureOrQuitListener;
import com.mx.mxbase.interfaces.StopScreenListener;
import com.mx.mxbase.utils.StartActivityUtils;
import com.mx.mxbase.utils.StringUtils;
import com.mx.mxbase.utils.ToastUtils;
import com.mx.mxbase.view.WriteDrawLayout;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

import butterknife.Bind;

/**
 * 全新手写笔记控制界面
 * Created by xj on 2017/6/19.
 */

public class NewActivity extends WriteBaseActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener, InterceptView.ClickOther, WindowRefureshListener {
    private static final int START_FILEMANAGER_RESULT = 101;
    private static final int START_CUTPHOTO_RESULT = 102;
    /**
     * 文件信息类
     */
    private WritPadModel model;
    /**
     * 当前绘制索引页
     */
    private int index = 0;
    /**
     * 文件总个数
     */
    private int fileSize = 0;
    /**
     * 标题栏
     */
    @Bind(R.id.onclick_main_layout)
    InterceptView onclick_main_layout;
    @Bind(R.id.complete)
    TextView complete;
    @Bind(R.id.title)
    TextView title;

    @Bind(R.id.change_to_text)
    TextView change_to_text;
    @Bind(R.id.save_write)
    ImageButton save_write;
    @Bind(R.id.back_last)
    ImageButton back_last;
    @Bind(R.id.recover_next)
    ImageButton recover_next;
    @Bind(R.id.screen_light_adjust)//屏幕亮度调节
            ImageButton screen_light_adjust;
    @Bind(R.id.refuresh)
    ImageButton refuresh;
    @Bind(R.id.ash_can)
    ImageButton ash_can;
    @Bind(R.id.add_page)
    ImageButton add_page;
    @Bind(R.id.skin)
    ImageButton skin;
    //绘图切换view
    //橡皮擦
    @Bind(R.id.rubber)
    WriteDrawLayout rubber;
    //铅笔
    @Bind(R.id.pen)
    WriteDrawLayout pen;
    //底部操作按钮
    @Bind(R.id.pen_group)
    RadioGroup pen_group;

    @Bind(R.id.clear_screen)
    ImageButton clear_screen;

    @Bind(R.id.last_page)
    ImageButton last_page;
    @Bind(R.id.show_index)
    TextView show_index;
    @Bind(R.id.next_page)
    ImageButton next_page;
    @Bind(R.id.line)
    View line;
    //绘制View
    @Bind(R.id.write_view)
    WriteSurfaceViewDraw write_view;
    //    @Bind(R.id.write_back)
//    PaintBackView write_back;
    private boolean isClicksetingPen = true;
    /**
     * 没有设置全界面替换模式为true,全页面替换模式为false;
     */
    private int noAllPageReplaceStyle = 0;

    private long clickTime = 0;
    /**
     * activity执行了onstop
     */
    private boolean isStop = false;

    private BrodcastUtils brodcastUtils;
    private InputDialog inputDialog;

    private MyScriptManager scriptManager;

    @Override
    protected int getMainContentViewId() {
        return R.layout.new_activity;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            model = (WritPadModel) savedInstanceState.getSerializable("model");
            if (null == model) {
                android.os.Process.killProcess(android.os.Process.myPid());
                return;
            }
            if (model.id != -1)
                index = savedInstanceState.getInt("index");
        } else {
            Bundle bundle = getIntent().getExtras();
            model = (WritPadModel) bundle.getSerializable("model");
        }
        if (model.id != -1) {
            fileSize = WritePadUtils.getInstance().getTemporarySize(model.saveCode);

            click_item = model.getExtendModel().background;

            noAllPageReplaceStyle = model.getExtendModel().noAllPageReplaceStyle;

            index = IndexDbUtils.getInstance().getIndex(model.saveCode);
            //开启数据初始化
            dialogShowOrHide(true, "");
            new UpdateFileIndex(model.saveCode, new UpdateFileIndex.updateFileListener() {
                @Override
                public void onUpdateSucess() {
                    dialogShowOrHide(false, "");
                    initView();
                }
            }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            String name;
            if (model.name == null || model.name.isEmpty() || model.name.equals("新增界面")) {
                name = DataUtuls.getCurrentTime(System.currentTimeMillis());
            } else {
                name = nameSpil(model.name);
            }
            model.name = name;
            model.saveCode = model.parentCode + "/" + name;
            noAllPageReplaceStyle = getSettingBackground();
            fileSize = 1;

            index = 0;
            initView();
        }
    }

    private boolean stopscreenOpen = false;
    private boolean isDrawing = true;
    private boolean isClickUp = false;

    private void initView() {
        pen.setallValue(R.mipmap.pencil, false);
        rubber.setallValue(R.mipmap.rubber, false);

        pen.setOnClickListener(penClick);
        rubber.setOnClickListener(penClick);
        onclick_main_layout.setClickOther(this);
        complete.setOnClickListener(this);
        change_to_text.setOnClickListener(this);
        save_write.setOnClickListener(this);
        back_last.setOnClickListener(this);
        recover_next.setOnClickListener(this);
        screen_light_adjust.setOnClickListener(this);
        refuresh.setOnClickListener(this);
        ((RadioButton) findViewById(R.id.pen6)).setOnClickListener(this);
        add_page.setOnClickListener(this);
        ash_can.setOnClickListener(this);
        skin.setOnClickListener(this);
        clear_screen.setOnClickListener(this);
        last_page.setOnClickListener(this);
        next_page.setOnClickListener(this);
        show_index.setOnClickListener(this);
        title.setOnClickListener(this);

        pen_group.setOnCheckedChangeListener(this);

        title.setText(model.name);
//        write_back.setDrawStyle(click_item);
        setDrawBackground(click_item, "");
        initSetWriteNote(false);
        pen.changeStatus(true);

        brodcastUtils = new BrodcastUtils(this, write_view, new StopScreenListener() {
            @Override
            public void openScreen() {
                stopscreenOpen = true;
            }
        });

        write_view.setRefureshListener(this);

        write_view.setTagListener(new WriteTagListener() {

            @Override
            public void WriteTag(boolean isDrawing, String tag) {
                if (isFinishing()) return;
                APPLog.e("WriteTag", "tag:" + tag);
                if (StringUtils.isNull(tag)) {
                    title.setText(model.name);
                } else {
                    title.setText(tag);
                }
                NewActivity.this.isDrawing = isDrawing;
                if (!isDrawing && tag.equals("") && isClickUp) {
                    getHandler().postDelayed(runnable, 200);
                } else {
                    getHandler().removeCallbacks(runnable);
                }
            }
        });
        scriptManager = new MyScriptManager(this, new ChangeToTextListener() {
            @Override
            public void onChangeFaile(String error) {
                dialogShowOrHide(false, "");
                if (error != null && !error.isEmpty())
                    ToastUtils.getInstance().showToastShort(error);
            }

            @Override
            public void onChangeSucess(String value) {
                dialogShowOrHide(false, "");
                APPLog.e("onChangeSucess:" + value);
            }

            @Override
            public void onChangeStart() {
                dialogShowOrHide(true, "");
            }
        });
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            isClickUp = false;
            APPLog.e("WriteTag", "进入设置进入手写模式");
            write_view.onKeysetEnterScribble();
        }
    };


    private boolean isOnDrawing() {
        if (isDrawing) {
        }
        APPLog.e("isDrawing", isDrawing);
        return isDrawing;
    }

    private void setDrawBackground(final int drawStyle, final String filepath) {
        if (write_view == null) return;
        if (write_view.getPenControl() == null || !write_view.isStart) {
            getHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    setDrawBackground(drawStyle, filepath);
                }
            }, 200);
            return;
        } else {
            write_view.getPenControl().setDrawStyle(drawStyle, filepath, ischekPage);
        }
    }

    /**
     * 提出名字中的特殊字符
     *
     * @param filName
     * @return
     */
    private String nameSpil(String filName) {
        String str = filName.replaceAll("[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……& amp;*（）——+|{}【】‘；：”“’。，、？|-]", "");
        return str;
    }

    View.OnClickListener penClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            write_view.setCanDraw(false, 1111111);
            boolean isRubber = false;
            switch (v.getId()) {
                case R.id.pen:
                    isRubber = false;
                    break;
                case R.id.rubber:
                    isRubber = true;
                    break;
                default:
                    break;
            }
            if (write_view.isNibWipe() == isRubber) {
                write_view.setCanDraw(true, 22225);
                return;
            }
            if (write_view.setNibWipe(isRubber)) {
                pen.changeStatus(!isRubber);
                rubber.changeStatus(isRubber);
                setingPenIndex();
            } else {
                write_view.setCanDraw(true, 22223);
            }

        }
    };

    //private boolean  softClick=false;
    private void setingPenIndex() {
        isClicksetingPen = false;
        if (write_view.getPenControl() == null) {
            getHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    setingPenIndex();
                }
            }, 100);
            return;
        }
//        softClick=true;
        if (write_view.isNibWipe()) {
            ((RadioButton) (pen_group.getChildAt(BrushSettingUtils.getInstance(NewActivity.this).pitchOnRubberIndex()))).performClick();
        } else {
            ((RadioButton) (pen_group.getChildAt(BrushSettingUtils.getInstance(NewActivity.this).pitchdrawLineIndex()))).performClick();
        }
        write_view.setClearLineWidth(BrushSettingUtils.getInstance(this).getRubberSize());
        write_view.setDrawLineWidth(BrushSettingUtils.getInstance(this).getDrawLineSize());
        isClicksetingPen = true;
        write_view.setCanDraw(true, 2222);
    }

    /**
     * 设置笔记大小
     *
     * @param group
     * @param checkedId
     */
    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
//        if (!softClick)
        write_view.setCanDraw(false, 1);
        int position = -1;
        switch (checkedId) {
            case R.id.pen0:
                position = 0;
                break;
            case R.id.pen1:
                position = 1;
                break;
            case R.id.pen2:
                position = 2;
                break;
            case R.id.pen3:
                position = 3;
                break;
            case R.id.pen4:
                position = 4;
                break;
            case R.id.pen5:
                position = 5;
                break;
            case R.id.pen6:
                toSettingLine();
                break;
            default:
                break;
        }
        if (position != -1 && write_view.getPenControl() != null) {
            if (write_view.isNibWipe()) {
                int size = BrushSettingUtils.getInstance(NewActivity.this).getRubberIndexSize(position);
                write_view.setClearLineWidth(size);
            } else {
                int size = BrushSettingUtils.getInstance(NewActivity.this).getDrawLineIndexSize(position);
                write_view.setDrawLineWidth(size);
            }
        }
//        if (!softClick)
        write_view.setCanDraw(true, 2);
//        softClick=false;
    }


    private void toSettingLine() {
        if (isClicksetingPen && !isStartResume) {
            write_view.setCanDraw(false, 6);
            isStartResume = true;
            write_view.setStartActivity(true);
            CustomPenActivity.startCustomPen(this, !write_view.isNibWipe());
        }
    }


    @Override
    public void onClick(View v) {
        if (isOnDrawing() && !iswuliClick) return;
        iswuliClick = false;

        if (!write_view.isCanDraw()) return;

        if (Math.abs(System.currentTimeMillis() - clickTime) < 1000) {
//            if (v.getId() != R.id.back_last)
            return;
        }
        if (v.getId() != R.id.back_last
                && v.getId() != R.id.clear_screen
                && v.getId() != R.id.ash_can
                && v.getId() != R.id.last_page
                && v.getId() != R.id.next_page
                ) {
            onClickOther();
            clickTime = System.currentTimeMillis();
        }

        switch (v.getId()) {
            case R.id.complete://完成
                if (!write_view.isDrawUp()) return;
                backActivity();
                break;
            case R.id.change_to_text://文字转换
                try {
                    scriptManager.initMyScript(write_view);
                    scriptManager.changeToText(write_view.getPenControl().getNoPageData());
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case R.id.save_write://保存当前手写

                dialogShowOrHide(true, "");
                write_view.saveWritePad(model, new NoteSaveWriteListener() {
                    @Override
                    public void isSucess(boolean is, WritPadModel model) {
                        dialogShowOrHide(false, "");
                        write_view.setCanDraw(true, 2);
                    }
                });
                break;
            case R.id.title://设置手写页标识
//                inputDialog = InputDialog.getdialog(this, getString(R.string.draw_flag), ConfigerUtils.hitnInput, new InputDialog.InputListener() {
//                    @Override
//                    public void quit() {
//                    }
//
//                    @Override
//                    public void insure(String name) {
//                        if (name.equals("")) {
//                            return;
//                        }
//                        if (ConfigerUtils.isFail(name)) return;
//                        model.pageName=name;
//                        long id=WritePadUtils.getInstance().judgeDataExist(model.saveCode,1,index);
//                        if (id==-1){
//                            write_view.saveWritePad(model, null);
//                        }else {
//                            WritePadUtils.getInstance().upDatePageName(name,id);
//                        }
//                    }
//                });
                break;
            case R.id.back_last://撤销
                int i=10/0;
                write_view.backLastDraw(true);
                break;
            case R.id.recover_next://恢复
                write_view.backLastDraw(false);
                break;
            case R.id.screen_light_adjust://屏幕调节
                if (isClicksetingPen) {
                    write_view.setCanDraw(false, 5);
//                    startActivity(new Intent(this, ScreenLightAdjustActivity.class));
                    StartActivityUtils.sendOpenLight(this);
                }
                break;
            case R.id.pen6://点击设置
                //跳转到设置界面
                toSettingLine();
                break;
            case R.id.show_index:
                write_view.setCanDraw(false, 7);
                PageSkipActivity.startPageSkip(this, index, fileSize);
                break;
            case R.id.refuresh://刷新页面
                write_view.setCanDraw(false, 8);
                PenUtils.refureshActivity(this);
                getHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        write_view.setCanDraw(true, 9);
                    }
                }, 500);
                break;
            case R.id.ash_can://删除界面
                String hitn = fileSize <= 1 ? "仅此一页不可删除" : "删除界面后不可恢复，请确认删除";
                write_view.setCanDraw(false, 10);
                insureDialog(hitn, "delete", new InsureOrQuitListener() {
                    @Override
                    public void isInsure(Object code, boolean is) {
                        if (is && fileSize > 1) {
                            dialogShowOrHide(true, "正在删除");
                            new DeleteOneFileAsy(model, index, new DeleteListener() {
                                @Override
                                public void onDelete(boolean isDelete) {
                                    for (int i = index; i < fileSize; i++) {
                                        DbWriteModelLoader.getInstance().clearBitmap(model.saveCode, i);
                                    }
                                    fileSize = WritePadUtils.getInstance().getTemporarySize(model.saveCode);
                                    initSetWriteNote(false);
                                    dialogShowOrHide(false, "");
                                    write_view.setCanDraw(true, 11);
//                                    write_view.fullRefuresh();

                                }
                            }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        } else {
                            write_view.setCanDraw(true, 12);
                            write_view.fullRefuresh();
                        }
                    }
                });

                break;
            case R.id.add_page://添加
                if (!internalJuge()) return;
                if (fileSize >= 99) {
                    ToastUtils.getInstance().showToastShort("不能添加更多了！！");
                    return;
                }
                write_view.setCanDraw(false, 13);
                dialogShowOrHide(true, "新建中...");
                WritPadModel mod;
                if (noAllPageReplaceStyle == 1) {
                    mod = new WritPadModel(model.name, model.saveCode, model.isFolder, model.parentCode, model._index, "");
                } else {
                    mod = model;
                }
                if (getInsertStype()) {//前插入模式
                    index--;
                }
                new InsertFileAsy(mod, index, new InsertLister() {
                    @Override
                    public void onInsert(boolean isDelete) {
                        if (isfinish) return;
                        if (!isDelete) {
                            if (getInsertStype()) {//前插入模式
                                index++;
                            }
                            ToastUtils.getInstance().showToastShort("新建页面失败！");
                        } else {
                            for (int i = index + 1; i < fileSize; i++) {
                                DbWriteModelLoader.getInstance().clearBitmap(model.saveCode, i);
                            }
                            if (getInsertStype()) {
                                //前插入模式需要重新设置一下保存界面信息
                                write_view.setCodeAndIndex(new CodeAndIndex(model.saveCode, index + 2));
                            }
                            fileSize++;
                            index++;

                            initSetWriteNote(true);
                        }
                        dialogShowOrHide(false, "");
//                        write_view.setCanDraw(true, 14);
                    }
                }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                break;
            case R.id.skin:// 皮肤
                write_view.setCanDraw(false, 15);
                showPopupWindow(skin);

//                List<String> value=write_view.getPenControl().getClickTests();
//                StringBuilder builder=new StringBuilder();
//                for (int i = 0; i < value.size(); i++) {
//                    builder.append(value.get(i));
//                    builder.append("\n");
//                }
//                insureDialog(builder.toString(), "手写状态数据提示",false, new InsureOrQuitListener() {
//                    @Override
//                    public void isInsure(Object code, boolean is) {
//
//                        write_view.fullRefuresh();
//                        write_view.setCanDraw(true, 15);
//                    }
//                });
                break;
            case R.id.clear_screen://清屏
                write_view.setCanDraw(false, 16);
                insureDialog("清除界面后不可恢复，请确认清屏", "delete", false, new InsureOrQuitListener() {
                    @Override
                    public void isInsure(Object code, boolean is) {
                        if (is) {
                            write_view.clearScreen();
                        }
                        write_view.fullRefuresh();
                        write_view.setCanDraw(true, 17);
                    }
                });

                break;
            case R.id.last_page://上一页
                ischekPage = true;
                lastPage();
                break;
            case R.id.next_page://下一页
                ischekPage = true;
                nextPage();
                break;
            default:
                break;
        }

    }

    private boolean ischekPage = false;

    private void initSetWriteNote(boolean saveLast) {
        if (saveLast) {
            write_view.saveWritePad(model, null);
        }
        if (index > (fileSize - 1)) {
            index = fileSize - 1;
        }
        //TODO 设置背景模式
        if (noAllPageReplaceStyle == 1) {
            /**
             * 获得扩展字段信息
             */
            String extent = WritePadUtils.getInstance().getExtent(model.saveCode, index);
            APPLog.e("extent=" + extent);
            //更换赋值给
            model.extend = extent;
            ExtendModel model = DataUtils.getExtendModel(extent);
            if (click_item != model.background || click_item == -1) {
                click_item = model.background;
//                updataBack();
                ischekPage = true;
                setDrawBackground(click_item, model.backgroundFilePath);
            }
        }
        write_view.setSaveCode(model.saveCode, index);

//        model.pageName = WritePadUtils.getInstance().getPageName(model.saveCode, index);
//        title.setText(model.getPageName() );
//        设置当前显示页
        show_index.setText(String.valueOf(index + 1) + "/" + String.valueOf(fileSize));
        ischekPage = false;
    }

    private void toPage() {
        write_view.setCanDraw(false, 100011);
        getHandler().removeMessages(1001);
        getHandler().sendEmptyMessageDelayed(1001, 200);
        show_index.setText(String.valueOf(index + 1) + "/" + String.valueOf(fileSize));
    }

    private void lastPage() {
        if (index <= 0) {
            ischekPage = false;
            if (isClickUp) {
                isClickUp = false;
                write_view.onKeysetEnterScribble();
            }
            return;
        }
        index--;
        initSetWriteNote(true);
    }

    private void nextPage() {
        if (index >= (fileSize - 1)) {
            ischekPage = false;
            if (isClickUp) {
                isClickUp = false;
                write_view.onKeysetEnterScribble();
            }
            return;
        }
        index++;
        initSetWriteNote(true);
    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {
        write_view.setCanDraw(false, 21);
    }

    @Override
    public void onActivityPaused(Activity activity) {
        if (inputDialog != null && inputDialog.isShowing()) {
            inputDialog.dismiss();
        }
    }

    @Override
    public void onActivityStopped(Activity activity) {
    }

    @Override
    public void onActivitySaveInstanceState(Bundle outState) {
        outState.putSerializable("model", model);
        outState.putInt("index", index);
    }

    @Override
    public void onActivityRestoreInstanceState(Bundle savedInstanceState) {

    }

    private boolean isFinish = false;
    private boolean isShowPopWindows = false;

    @Override
    public void onActivityDestroyed(Activity activity) {
        //退出保存信息
        write_view.saveWritePad(model, null);

        ActivityUtils.getInstance().ClearActivity(this);
        dialogShowOrHide(false, "");
        isFinish = true;
        releaseWakeLock();
        if (inputDialog != null && inputDialog.isShowing()) {
            inputDialog.dismiss();
        }
    }

    private int click_item = 0;
    PopupWindow popupWindow;
    private boolean isOther = false;

    private void showPopupWindow(View view) {
        releaseWakeLock();
        isOther = false;
        // 一个自定义的布局，作为显示的内容
        View contentView = LayoutInflater.from(this).inflate(
                R.layout.pop_new_skin_list, null);
        NoGrigView grigView = (NoGrigView) contentView.findViewById(R.id.back_gride);
        RadioGroup group_button = (RadioGroup) contentView.findViewById(R.id.group_button);
        ImageView image_position = (ImageView) contentView.findViewById(R.id.image_position);
        grigView.setOnItemClickListener(clickListener);
        group_button.setOnCheckedChangeListener(checkedChangeListener);
        if (noAllPageReplaceStyle == 1) {
            ((RadioButton) group_button.getChildAt(0)).setChecked(true);
        } else {
            ((RadioButton) group_button.getChildAt(1)).setChecked(true);
        }
        WriteNoteBackAdapter backAdapter = new WriteNoteBackAdapter(this, BackPhotoUtils.getBackMinPhoto(), click_item);
        grigView.setAdapter(backAdapter);

        popupWindow = new PopupWindow(contentView,
                MyApplication.ScreenWidth - 20, LinearLayout.LayoutParams.WRAP_CONTENT, true);

        popupWindow.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!isOther)
                    isOther = event.getAction() == 2;

                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    dismissPopWindow(true);
                    return true;
                }
                return false;
            }
        });
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
//                dismissPopWindow(true);
                write_view.fullRefuresh();
                write_view.setCanDraw(true, 191);
                acquireWakeLock();
            }
        });
        // 设置好参数之后再show
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);

        int[] location = new int[2];
        view.getLocationOnScreen(location);

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) image_position.getLayoutParams();
        int padingtop = -3;
        params.setMargins(location[0] + (skin.getWidth() - 34) / 2, padingtop, 0, 0);
        popupWindow.showAtLocation(view, Gravity.NO_GRAVITY, 10, location[1] - 335);
    }

    private void dismissPopWindow(boolean iswrite) {
        if (popupWindow == null) return;
        if (popupWindow != null) {
            APPLog.e("popupWindow.isShowing()", popupWindow.isShowing());
            if (!popupWindow.isShowing()) {
                popupWindow.showAsDropDown(skin);
            }
            popupWindow.dismiss();
            popupWindow = null;
        }
        write_view.fullRefuresh();
        write_view.setCanDraw(true, 19);
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        switch (msg.what) {
            case 10:
                if (!popupWindow.isShowing()) {
                    write_view.fullRefuresh();
                    write_view.setCanDraw(true, 19);
                }
                break;
            case 1001:
                initSetWriteNote(true);
                break;
            default:
                break;
        }
    }

    RadioGroup.OnCheckedChangeListener checkedChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId) {
                case R.id.only_page://单页
                    noAllPageReplaceStyle = 1;
                    break;
                case R.id.all_page://全部
                    noAllPageReplaceStyle = 0;
                    break;
                default:
                    break;
            }
        }
    };

    public AdapterView.OnItemClickListener clickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (position > 16) {
                if (position == 17) {//添加自定义背景
                    //打开文件管理器进行图片选择
                    Intent input = new Intent();
                    ComponentName cnInput = new ComponentName("com.moxi.filemanager", "com.moxi.filemanager.FileManagerActivity");
                    input.setComponent(cnInput);
                    input.putExtra("pick_attachment", true);
                    input.putExtra("pickAttachStyle", 1);
                    write_view.setStartActivity(true);
                    startActivityForResult(input, START_FILEMANAGER_RESULT);
                } else if (position == 18) {//展示自定义背景列表
                    write_view.setStartActivity(true);
                    startActivityForResult(new Intent(NewActivity.this, ListMyBackgroundActivity.class), START_CUTPHOTO_RESULT);
                }
            } else {
                click_item = position;
                saveUpdataBackground("");
            }
        }
    };

    private void saveUpdataBackground(String backgroundFilePath) {
        ExtendModel mo = model.getExtendModel();
        mo.background = click_item;
        mo.backgroundFilePath = backgroundFilePath;
        mo.noAllPageReplaceStyle = noAllPageReplaceStyle;
        model.setExtend(mo);
        //更新背景
        if (noAllPageReplaceStyle == 1) {
            long _id = WritePadUtils.getInstance().judgeDataExist(model.saveCode, model.isFolder, index);
            if (_id != -1) {
                //更新背景显示
                WritePadUtils.getInstance().upDateExtend(DataUtils.getExtendStr(mo), _id);
            }
        }
        updataBack();
    }

    /**
     * 更新底层画布
     */
    public void updataBack() {
//        write_back.setDrawStyle(click_item, model.getExtendModel().backgroundFilePath);
        setDrawBackground(click_item, model.getExtendModel().backgroundFilePath);
        dismissPopWindow(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 10) {
                //设置界面返回
                index = data.getIntExtra("page", 0);
                initSetWriteNote(true);
            } else if (requestCode == START_FILEMANAGER_RESULT) {
                //文件管理器选择图片返回
                Uri uri = data.getData();
                try {
                    File file = new File(new URI(uri.toString()));
                    if (!file.exists() || !file.canRead() || file.length() == 0) {
                        ToastUtils.getInstance().showToastShort("选择图片无法进行操作");
                    } else {//
                        PhotoCutActivity.startACutctivity(this, file.getAbsolutePath(), START_CUTPHOTO_RESULT);
                    }
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            } else if (requestCode == START_CUTPHOTO_RESULT) {
                //操作完成图片返回路径
                String filePath = data.getStringExtra("filePath");
                //设置背景
                click_item = -1;
                saveUpdataBackground(filePath);
            }
        } else {
            if (requestCode == 10) {
                //设置界面返回刷新屏幕
                write_view.fullRefuresh();
            }
        }
    }
//    @Override
//    public boolean dispatchKeyEvent(KeyEvent event) {
//        APPLog.e();
//        return true;
//    }

    private boolean iswuliClick = false;

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (!write_view.isDrawUp()) return true;
        if (keyCode == KeyEvent.KEYCODE_POWER) {
            isShowPopWindows = true;
            write_view.setCanDraw(false, 20);
        } else if (keyCode == KeyEvent.KEYCODE_PAGE_UP || keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            //上一页
//            onClickOther();
            save_write.setPressed(false);
            iswuliClick = true;
            isClickUp = true;
            onClick(last_page);
//            lastPage();
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_PAGE_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            //下一页
//            onClickOther();
            save_write.setPressed(false);
            iswuliClick = true;
            isClickUp = true;
            onClick(next_page);
//            nextPage();
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (!write_view.isDrawUp()) return true;
            backActivity();
            return true;
        }
        return true;
//        return super.onKeyUp(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        backActivity();
    }

    private void backActivity() {
        write_view.setCanDraw(false, 113);
//        write_view.setleaveScribbleMode(false, 0);
        dialogShowOrHide(true, "更新中...");
        int ii = index;
        if (noAllPageReplaceStyle == 0) {
            ii = -1;
        }
        new Thread(new UpdateDrawBackTheard(model.saveCode, ii, model.extend, new UpdateDrawBackTheard.UpdateListener() {
            @Override
            public void onResult() {
                if (isfinish) return;
                dialogText("保存中...");
                IndexDbUtils.getInstance().saveData(model.saveCode, index);
//                DrawIndexUtils.getInstance().saveData(model.saveCode,index);
                write_view.saveWritePad(model, new NoteSaveWriteListener() {
                    @Override
                    public void isSucess(boolean is, WritPadModel model) {
                        if (isfinish) return;
                        dialogShowOrHide(false, "保存中...");
                        if (write_view.onepageChange) {
                            DbPhotoLoader.getInstance().clearBitmap(model.saveCode, 0);
                            LoadLocPhotoUtils.removePic(model.saveCode, 0);
                        }
                        NewActivity.this.finish();
                    }
                });
            }
        })).start();

    }

    @Override
    protected void onStop() {
        super.onStop();
        APPLog.e("onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        write_view.setleaveScribbleMode(false, 1000);
        brodcastUtils.destory();
        scriptManager.remove();
    }

    @Override
    protected void onPause() {
        super.onPause();
        APPLog.e("onPause");
        isStop = true;
        dismissPopWindow(false);
        if (write_view != null) {
            write_view.setCanDraw(false, 21);
        }
        FingerUtils.openOrOff(true);
        releaseWakeLock();
    }

    private boolean isStartResume = false;

    @Override
    protected void onResume() {
        super.onResume();
        FingerUtils.openOrOff(false);
        APPLog.e("onResume");
        acquireWakeLock();
        setingPenIndex();
        write_view.setStartActivity(false);
        getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (windowFocus)
                    write_view.setCanDraw(true, 22);
            }
        }, 300);
        if (isStartResume) {
            write_view.fullRefuresh();
            isStartResume = false;
        }
        isStop = false;
    }

    private boolean windowFocus = false;

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        windowFocus = hasFocus;
        if (hasFocus) {
            getHandler().removeCallbacksAndMessages(null);
            isShowPopWindows = false;
            write_view.setCanDraw(true, 23);
        } else {
            write_view.setCanDraw(false, 24);
        }
    }

    @Override
    public void onClickOther() {
        if (!(popupWindow != null && popupWindow.isShowing())) {
            write_view.setleaveScribbleMode(true, 1);
        }
    }

//    PowerManager.WakeLock wakeLock = null;

    //获取电源锁，保持该服务在屏幕熄灭时仍然获取CPU时，保持运行
    private void acquireWakeLock() {
//        if (null == wakeLock || !wakeLock.isHeld()) {
//            APPLog.e("开启WakeLock");
//            PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
//            wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, "PostLocationService");
//            if (null != wakeLock) {
//                wakeLock.acquire();
//            }
//        }
    }

    //释放设备电源锁
    private void releaseWakeLock() {
//        if (null != wakeLock && wakeLock.isHeld()) {
//            APPLog.e("释放WakeLock");
//            wakeLock.release();
//            wakeLock = null;
//        }
    }

    @Override
    public void onWindowRefureshEnd() {
//        getWindow().getDecorView().invalidate(View.EINK_MODE_GC16_PART);
    }

    @Override
    public void onDrawDelete(boolean delete) {
//        write_back.setshow(delete?View.VISIBLE:View.INVISIBLE);
    }
}
