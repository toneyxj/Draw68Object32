package com.moxi.writeNote.Activity;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.moxi.handwritinglibs.WritePadNoteView;
import com.moxi.handwritinglibs.asy.DeleteOneFileAsy;
import com.moxi.handwritinglibs.db.WritPadModel;
import com.moxi.handwritinglibs.db.WritePadUtils;
import com.moxi.handwritinglibs.listener.DeleteListener;
import com.moxi.handwritinglibs.listener.NoteSaveWriteListener;
import com.moxi.handwritinglibs.listener.WriteListener;
import com.moxi.handwritinglibs.utils.DbPhotoLoader;
import com.moxi.writeNote.R;
import com.moxi.writeNote.WriteBaseActivity;
import com.moxi.writeNote.config.ActivityUtils;
import com.moxi.writeNote.config.ConfigInfor;
import com.moxi.writeNote.config.ConfigerUtils;
import com.moxi.writeNote.dialog.SaveDrawDialog;
import com.moxi.writeNote.listener.SaveDrawListener;
import com.moxi.writeNote.view.InterceptView;
import com.moxi.writeNote.view.PaintBackView;
import com.mx.mxbase.constant.APPLog;
import com.mx.mxbase.interfaces.InsureOrQuitListener;
import com.mx.mxbase.utils.ToastUtils;
import com.mx.mxbase.view.WriteDrawLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;


public class WriteNoteActivity extends WriteBaseActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener ,WriteListener {
    /**
     * 文件信息类
     */
    private WritPadModel model;
    /**
     * 当前绘制索引页
     */
    private int index = 0;
    /**
     * 标题栏
     */
    @Bind(R.id.onclick_main_layout)
    InterceptView onclick_main_layout;
    @Bind(R.id.complete)
    TextView complete;
    @Bind(R.id.title)
    TextView title;
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
    Button clear_screen;
    @Bind(R.id.last_page)
    ImageButton last_page;
    @Bind(R.id.show_index)
    TextView show_index;
    @Bind(R.id.next_page)
    ImageButton next_page;
    //绘制View
    @Bind(R.id.write_pad_note_layout)
    RelativeLayout write_pad_note_layout;
    @Bind(R.id.write_back)
    PaintBackView write_back;

    List<Integer> widths = new ArrayList<Integer>();
    private List<WritePadNoteView> noteViews = new ArrayList<WritePadNoteView>();
    private int fileSize = 0;
    //画笔线索引
    private int penIndex = 0;
    //当前状态是否是擦出
    private boolean isRubber = false;
    private boolean isCanAdd=true;
    @Override
    protected int getMainContentViewId() {
        return R.layout.activity_write_note;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

        //获得初始化数据
        if (savedInstanceState != null) {
            model = (WritPadModel) savedInstanceState.getSerializable("model");
            if (null == model) {
                android.os.Process.killProcess(android.os.Process.myPid());
                return;
            }
            if (model.id != -1)
                index = savedInstanceState.getInt("index");
            noteViews.clear();
            write_pad_note_layout.removeAllViews();
        } else {
            Bundle bundle = getIntent().getExtras();
            model = (WritPadModel) bundle.getSerializable("model");
        }
        if (model.id != -1) {
            fileSize = WritePadUtils.getInstance().getTemporarySize(model.saveCode);
            DbPhotoLoader.getInstance().clearBitmap(model.saveCode, 0);
        }
        initView();
        title.setText(model.name);
        ActivityUtils.getInstance().addActivity(this);
    }

    private void initView() {
        pen.setallValue(R.mipmap.pencil, false);
        rubber.setallValue(R.mipmap.rubber, false);

        pen.setOnClickListener(penClick);
        rubber.setOnClickListener(penClick);

        complete.setOnClickListener(this);
        refuresh.setOnClickListener(this);
        add_page.setOnClickListener(this);
        ash_can.setOnClickListener(this);
        skin.setOnClickListener(this);
        clear_screen.setOnClickListener(this);
        last_page.setOnClickListener(this);
        next_page.setOnClickListener(this);

        pen_group.setOnCheckedChangeListener(this);
        ((RadioButton) (pen_group.getChildAt(pen_group.getChildCount() - 1))).setChecked(true);
        //设置线宽
        widths.add(3);
        widths.add(4);
        widths.add(5);
        widths.add(6);
        widths.add(7);
        widths.add(8);
        widths.add(16);
        click_item = Integer.parseInt(model.extend);
        updataBack();

        onclick_main_layout.setClickOther(new InterceptView.ClickOther() {
            @Override
            public void onClickOther() {
                if (getNoteView()!=null)
                getNoteView().setleaveScribbleMode();
            }
        });
        getHandler().sendEmptyMessageDelayed(3,500);
    }

    View.OnClickListener penClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.pen://铅笔
                    isRubber = false;
                    break;
                case R.id.rubber://橡皮擦
                    isRubber = true;
                    break;
                default:
                    break;
            }
            pen.changeStatus(!isRubber);
            rubber.changeStatus(isRubber);
            getNoteView().setDrawIndex(isRubber ? -1 : penIndex);
        }
    };


    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.pen0:
                penIndex = 5;
                break;
            case R.id.pen1:
                penIndex = 4;
                break;
            case R.id.pen2:
                penIndex = 3;
                break;
            case R.id.pen3:
                penIndex = 2;
                break;
            case R.id.pen4:
                penIndex = 1;
                break;
            case R.id.pen5:
                penIndex = 0;
                break;
            default:
                break;
        }
        if (!isRubber && noteViews.size() > 0) {
            getNoteView().setDrawIndex(isRubber ? -1 : penIndex);
        }
    }
    private SaveDrawDialog drawDialog;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.complete://完成
                if (model.id == -1) {
                    drawDialog=SaveDrawDialog.getdialog(this, ConfigerUtils.hitnInput, saveDrawListener);
                } else {
                    dialogShowOrHide(true, "文件保存中...");
                    saveIndex = 0;
                    saveWriteNote(model);
                }
                break;
            case R.id.refuresh://刷新页面
                refureshWindow(20);
                break;
            case R.id.ash_can://删除界面
                final int _size = fileSize > noteViews.size() ? fileSize : noteViews.size();
                if (_size <= 1) {
                    ToastUtils.getInstance().showToastShort("当前文件只有一个界面不可删除");
                    return;
                }
                insureDialog("请确认删除本界面,删除后不可恢复", "删除界面", new InsureOrQuitListener() {
                    @Override
                    public void isInsure(Object code, final boolean is) {
                        final int size = _size - 1;
                        if (is) {
                            if (model.id == -1) {
                                for (int i = index; i < noteViews.size(); i++) {
                                    noteViews.get(i).ClearLuch();
                                }
                                write_pad_note_layout.removeView(noteViews.get(index));
                                noteViews.remove(index);
                                for (int i = index; i < noteViews.size(); i++) {
                                    noteViews.get(i).setIndex(i);
                                }
                                index = (index >= size) ? (size - 1) : index;
                                initSetWriteNote();
                                refureshWindow(1000);
                            } else {
                                dialogShowOrHide(true, "正在删除");
                                new DeleteOneFileAsy(model, index, new DeleteListener() {
                                    @Override
                                    public void onDelete(boolean isDelete) {
                                        if (isDelete) {
                                            write_pad_note_layout.removeView(noteViews.get(index));
                                            noteViews.get(index).ClearLuch();
                                            noteViews.remove(index);
                                            for (int i = index; i < noteViews.size(); i++) {
                                                noteViews.get(i).ClearLuch();
                                                noteViews.get(i).setIndex(i);
                                            }
                                            index = (index >= size) ? (size - 1) : index;
                                            fileSize--;
                                            initSetWriteNote();
                                            refureshWindow(1000);
                                        } else {
                                            ToastUtils.getInstance().showToastShort("删除失败");
                                        }
                                        dialogShowOrHide(false, "");
                                    }
                                }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                            }
                        }
                    }
                });
                break;
            case R.id.add_page://添加
                if (!isCanAdd)return;
                int _index = noteViews.size() > fileSize ? noteViews.size() : fileSize;
                if (_index >= 8) {
                    ToastUtils.getInstance().showToastShort("一个文件最多八页笔记");
                    return;
                }
                for (int i = noteViews.size(); i < _index; i++) {
                    index = i;
                    addNoteWrite(false,1);
                }
                index = _index;
                addNoteWrite(true,2);
                refureshWindow(1000);
                break;
            case R.id.skin:// 皮肤
                showPopupWindow(v);
                break;
            case R.id.clear_screen://清屏
                insureDialog("请确认清除屏幕内容", "清屏", new InsureOrQuitListener() {
                    @Override
                    public void isInsure(Object code, boolean is) {
                        if (is){
                            //保存信息
                            getNoteView().clearScreen();
                            refureshWindow(1000);
                        }
                    }
                });
                break;
            case R.id.last_page://上一页
                lastPage();
                break;
            case R.id.next_page://下一页
                nextPage();
                break;
            default:
                break;
        }
    }

    /**
     * 添加笔记界面
     *
     * @param isAdd 是否显示
     */
    private void addNoteWrite(boolean isAdd,int i) {
        WritePadNoteView noteView = new WritePadNoteView(this);
        ViewGroup.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        noteView.setLayoutParams(params);
        noteView.setPaintStrokeWidths(widths);
        if (model.id == -1 || model.saveCode.isEmpty()) {
            noteView.setSaveCode(getTemporaryPath(), index);
        } else {
            noteView.setSaveCode(model.saveCode, index);
        }
        noteView.setListener(this);
        write_pad_note_layout.addView(noteView);
        noteViews.add(noteView);
        //设置索引值
        if (isAdd)
            initSetWriteNote();
    }

    /**
     * 获得当前绘制的view
     *
     * @return
     */
    public WritePadNoteView getNoteView() {
        if (noteViews.size()==0)return null;
        return noteViews.get(index);
    }

    private void lastPage() {
        getNoteView().setleaveScribbleMode();
        if (index <= 0) {
            ToastUtils.getInstance().showToastShort("已经是第一页");
            return;
        }
        index--;
        initSetWriteNote();
    }

    private void nextPage() {
        getNoteView().setleaveScribbleMode();
        if (index >= (noteViews.size() - 1) && index >= (fileSize - 1)) {
            ToastUtils.getInstance().showToastShort("已经是最后一页");
            return;
        }
        index++;
        initSetWriteNote();
    }

    private void initSetWriteNote() {
        if (index >= (noteViews.size())) {
            addNoteWrite(true,3);
            return;
        }
        for (int i = 0; i < noteViews.size(); i++) {
            if (i!=index)
            noteViews.get(i).setVisibility(View.INVISIBLE);
        }
        getNoteView().setDrawIndex(isRubber ? -1 : penIndex);
        WritePadNoteView noteView = noteViews.get(index);
        noteView.setVisibility(View.VISIBLE);
        //设置当前显示页
        int size = fileSize > noteViews.size() ? fileSize : noteViews.size();
        show_index.setText(String.valueOf(index + 1) + "/" + String.valueOf(size));
    }

    SaveDrawListener saveDrawListener = new SaveDrawListener() {
        @Override
        public void discard() {
            WriteNoteActivity.this.finish();
        }

        @Override
        public void cancel() {
            showKeyboard(false);
        }

        @Override
        public void insure(Dialog dialog, String name) {
            if (ConfigerUtils.isFail(name)) return;
            String saveCode = model.parentCode + "/" + name;
//            if (WritePadUtils.getInstance().isSavedWrite(saveCode, 1)) {
//                ToastUtils.getInstance().showToastShort("文件名已存在");
//                return;
//            }
            ((SaveDrawDialog) dialog).closeInput();
            dialog.cancel();
            try {
                Thread.sleep(100);
                model.name = name;
                model.saveCode = model.parentCode + "/" + name;
                dialogShowOrHide(true, "文件保存中...");
                saveIndex = 0;
                saveWriteNote(model);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    };
    private int saveIndex = 0;
    private String saveCode="";

    private void saveWriteNote(WritPadModel m) {
        WritPadModel model = new WritPadModel(m.name, m.saveCode, m.isFolder, m.parentCode, m._index, m.extend);
        if (saveIndex == 0 && !String.valueOf(click_item).equals(model.extend)) {
            noteViews.get(0).setCross(true);
//            write_back.saveWritePad();
        }
        saveCode=m.saveCode;
        model.extend = String.valueOf(click_item);
        noteViews.get(saveIndex).saveWritePad(model, new NoteSaveWriteListener() {
            @Override
            public void isSucess(boolean is, WritPadModel model) {
                if (isFinish) return;
                saveIndex++;
                if (saveIndex >= noteViews.size()) {
                    dialogShowOrHide(false, "");
                    clearAll();
                    WriteNoteActivity.this.finish();
                } else {
                    saveWriteNote(model);
                }
            }
        });
    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {
        dialogShowOrHide(false, "");
    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {
        if (saveIndex<noteViews.size()){
            //保存未完成点击home键
            WritePadUtils.getInstance().deleteAllSaveCode(saveCode);
        }
        if (drawDialog!=null&&drawDialog.isShowing()){
            drawDialog.dismiss();
        }
    }

    @Override
    public void onActivitySaveInstanceState(Bundle outState ) {
        outState.putSerializable("model", model);
        outState.putInt("index", index);
    }

    @Override
    public void onActivityRestoreInstanceState(Bundle savedInstanceState) {

    }

    private boolean isFinish = false;

    @Override
    public void onActivityDestroyed(Activity activity) {
        ActivityUtils.getInstance().ClearActivity(this);
        dialogShowOrHide(false, "");
        isFinish = true;
        clearAll();
        if (drawDialog!=null&&drawDialog.isShowing()){
            drawDialog.dismiss();
        }
    }

    private void clearAll() {
        for (int i = 0; i < noteViews.size(); i++) {
            noteViews.get(i).ClearLuch();
        }
    }

    /**
     * 获得虚拟保存saveCode路径
     *
     * @return
     */
    public String getTemporaryPath() {
        return ConfigInfor.temporaryPath + model.parentCode;
    }

    private int click_item = 0;
    PopupWindow popupWindow;
    private boolean isOther=false;

    private void showPopupWindow(View view) {
        isOther=false;
        //显示皮肤更换，屏蔽掉绘制
        getNoteView().setNowDraw(false);
//        MotionEvent.TOOL_TYPE_ERASER
        // 一个自定义的布局，作为显示的内容
        View contentView = LayoutInflater.from(this).inflate(
                R.layout.pop_skin_list, null);
        // 设置按钮的点击事件
        LinearLayout back_white = (LinearLayout) contentView.findViewById(R.id.back_white);
        LinearLayout back_corss = (LinearLayout) contentView.findViewById(R.id.back_corss);
        LinearLayout back_line = (LinearLayout) contentView.findViewById(R.id.back_line);

        ImageView show_index_three = (ImageView) contentView.findViewById(R.id.show_index_three);
        ImageView show_index_one = (ImageView) contentView.findViewById(R.id.show_index_one);
        ImageView show_index_two = (ImageView) contentView.findViewById(R.id.show_index_two);
        if (click_item == 2) {
            show_index_three.setVisibility(View.VISIBLE);
        } else if (click_item == 1) {
            show_index_two.setVisibility(View.VISIBLE);
        } else {
            show_index_one.setVisibility(View.VISIBLE);
        }

        popupWindow = new PopupWindow(contentView,
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);

        popupWindow.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!isOther)
                isOther=event.getAction()==2;

                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                   dismissPopWindow();
                    return true;
                }
                return false;
            }
        });
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                getHandler().sendEmptyMessageDelayed(6,100);
            }
        });

        back_white.setOnClickListener(clickListener);
        back_corss.setOnClickListener(clickListener);
        back_line.setOnClickListener(clickListener);
        // 设置好参数之后再show
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setOutsideTouchable(false);
        popupWindow.showAsDropDown(view);

    }
    private void dismissPopWindow(){
        if (popupWindow!=null){
            if (!popupWindow.isShowing()){
                popupWindow.showAsDropDown(skin);
            }
            popupWindow.dismiss();
            popupWindow=null;
            if (getNoteView()!=null)
            getNoteView().setNowDraw(true);
        }
    }

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.back_white://白色底
                    click_item = 0;
                    break;
                case R.id.back_corss://田字格
                    click_item = 1;
                    break;
                case R.id.back_line://线条
                    click_item = 2;
                    break;
                default:
                    break;
            }
            updataBack();
        }
    };

    /**
     * 更新底层画布
     */
    public void updataBack() {
        write_back.setDrawStyle(click_item);
       dismissPopWindow();
        getHandler().sendEmptyMessageDelayed(5,600);
    }


    @Override
    public void onBackPressed() {
        getNoteView().setleaveScribbleMode();
        if (model.id == -1) {
            SaveDrawDialog.getdialog(this, ConfigerUtils.hitnInput, saveDrawListener);
        } else {
            clearAll();
            WriteNoteActivity.this.finish();
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        APPLog.e(keyCode);
        if (keyCode == KeyEvent.KEYCODE_PAGE_UP) {
            //上一页
            onClick(last_page);
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_PAGE_DOWN) {
            //下一页
            onClick(next_page);
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }
    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        if (msg.what==1){

            write_back.setDrawStyle(0);
            getHandler().sendEmptyMessageDelayed(2,50);
        }else if (msg.what==2){
            updataBack();
        }else if (msg.what==3){
            addNoteWrite(true,4);
            penClick.onClick(pen);
        }else if (msg.what==4){
            getNoteView().setNowDraw(true);
        }else if (msg.what==5){
            if (getNoteView()!=null)
                getNoteView().setNowDraw(true);
            isCanAdd=true;
        }else if (msg.what==6){
            getNoteView().setleaveScribbleMode();
            getNoteView().setNowDraw(true);
        }
    }
    private long currentTime=0;

    private void refureshWindow(int time){
        isCanAdd=false;
        long  timel=System.currentTimeMillis();
        if (timel-currentTime<=1050)return;
        getNoteView().setNowDraw(false);
        currentTime=timel;
       getHandler().sendEmptyMessageDelayed(1,time);
    }

    @Override
    protected void onPause() {
        super.onPause();
        APPLog.e("onPause");
        if (getNoteView()!=null) {
            getNoteView().onPause();
            getNoteView().onstop();
        }
    }
    @Override
    protected void onStop() {
        super.onStop();
        APPLog.e("onStop");
        if (getNoteView()!=null){
            getNoteView().onstop();
        }
        dismissPopWindow();
        if (drawDialog!=null&&drawDialog.isShowing()){
            drawDialog.dismiss();
        }
        isCanAdd=true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (getNoteView()!=null) {
            getNoteView().onResume();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    @Override
    public void onInvallClick() {
        if (!isOther){
            dismissPopWindow();
        }
    }


    @Override
    public void onOverTime() {
        clear_screen.setText(clear_screen.getText().toString());
    }

}
