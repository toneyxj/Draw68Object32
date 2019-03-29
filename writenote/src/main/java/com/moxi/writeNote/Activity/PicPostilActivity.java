package com.moxi.writeNote.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.moxi.handwritinglibs.BaseSurfaceViewDraw;
import com.moxi.handwritinglibs.DrawView.MxImageView;
import com.moxi.handwritinglibs.utils.BrodcastUtils;
import com.moxi.writeNote.R;
import com.moxi.writeNote.WriteBaseActivity;
import com.moxi.writeNote.dialog.CustomPenActivity;
import com.moxi.writeNote.utils.BrushSettingUtils;
import com.mx.mxbase.interfaces.InsureOrQuitListener;
import com.mx.mxbase.interfaces.StopScreenListener;
import com.mx.mxbase.utils.StringUtils;
import com.mx.mxbase.utils.ToastUtils;
import com.mx.mxbase.view.WriteDrawLayout;

import java.io.File;
import java.io.FileOutputStream;

import butterknife.Bind;

/**
 * 图片批注处理
 */
public class PicPostilActivity extends WriteBaseActivity implements View.OnClickListener,
        RadioGroup.OnCheckedChangeListener {


    /**
     * 启动图片描图
     *
     * @param context     上下文
     * @param backImgPath 背景图路径
     * @param title       标题
     */
    public static void startPicPostil(Context context, String backImgPath, String title, boolean titleShow) {
        Intent intent = new Intent(context, PicPostilActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("backImgPath", backImgPath);
        bundle.putString("title", title);
        bundle.putBoolean("titleShow", titleShow);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    @Bind(R.id.quit_pic_postil)
    TextView quit_pic_postil;
    @Bind(R.id.title_postil)
    TextView title_postil;
    @Bind(R.id.setting)
    ImageButton setting;
    @Bind(R.id.rubber_)
     TextView rubber_;

    @Bind(R.id.back_img)
    MxImageView back_img;
    @Bind(R.id.write_view)
    BaseSurfaceViewDraw write_view;

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
    /**
     * 背景图片地址
     */
    private String backImgPath;
    private String title;
    private boolean statusShow;
    private boolean isClicksetingPen = true;
    /**
     * activity执行了onstop
     */
    private boolean isStop = false;
    private long clickTime = 0;
    private BrodcastUtils brodcastUtils;

    private Bitmap bitmap;


    @Override
    protected int getMainContentViewId() {
        return R.layout.activity_pic_postil;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

        if (savedInstanceState == null) {
            savedInstanceState = getIntent().getExtras();
        }
        if (savedInstanceState == null) finish();
        backImgPath = savedInstanceState.getString("backImgPath");
        title = savedInstanceState.getString("title");
        statusShow = savedInstanceState.getBoolean("titleShow", false);
        rubber_.setText("铅笔");
        rubber_.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                write_view.setCanDraw(false, 1);
                String value=rubber_.getText().toString();
                if (value.equals("铅笔")){
                    rubber_.setText("橡皮");
                }else {
                    rubber_.setText("铅笔");
                }
                write_view.setNibWipe(value.equals("铅笔"));

                write_view.setCanDraw(true, 1);
            }
        });


        if (StringUtils.isNull(backImgPath)) finish();


        title_postil.setText(title);

        initView();
    }

    private void setShowBitmap(Bitmap bitmap){
        if (isfinish) return;
                if (bitmap==null||bitmap.isRecycled()){
                    ToastUtils.getInstance().showToastShort("批注图片不存在!");
                    PicPostilActivity.this.finish();
                    return;
                }
                //获得处理后的背景图片
                int w = bitmap.getWidth();
                int h = bitmap.getHeight();
                Matrix matrix = null;
                if (h < w) {
//                    Bitmap newb = Bitmap.createBitmap(h, w, Bitmap.Config.ARGB_8888);// 创建一个新的和SRC长度宽度一样的位图
                    matrix = new Matrix();
                    matrix.postScale(1, -1);   //镜像垂直翻转
                    matrix.postScale(-1, 1);   //镜像水平翻转
                    matrix.postRotate(-90);  //旋转-90度

                    bitmap = Bitmap.createBitmap(bitmap, 0, 0,w, h, matrix, true);
                }
                    this.bitmap=bitmap;

                back_img.setBitmap(bitmap);
                setBackgroundStyle(bitmap);
    }

    private void setBackgroundStyle(final Bitmap bitmap) {
        if (write_view == null) return;
        if (write_view.getPenControl() == null || !write_view.isStart) {
            getHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    setBackgroundStyle(bitmap);
                }
            }, 200);
            return;
        } else {
            write_view.getPenControl().setBackgroundDraw(false, bitmap);
        }
    }
    private boolean openScreen=false;

    private void initView() {
        pen.setallValue(R.mipmap.pencil, false);
        rubber.setallValue(R.mipmap.rubber, false);
        quit_pic_postil.setOnClickListener(this);

        pen_group.setOnCheckedChangeListener(this);
        ((RadioButton) findViewById(R.id.pen6)).setOnClickListener(this);

        pen.setOnClickListener(penClick);
        rubber.setOnClickListener(penClick);

        pen.changeStatus(true);

        brodcastUtils=new BrodcastUtils(this, write_view, new StopScreenListener() {
            @Override
            public void openScreen() {
                openScreen=true;
            }
        });

        setting.setOnClickListener(this);
    }

    View.OnClickListener penClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            write_view.setCanDraw(false, 1);
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
            if (write_view.isNibWipe() == isRubber) return;
            if (write_view.setNibWipe(isRubber)) {
                pen.changeStatus(!isRubber);
                rubber.changeStatus(isRubber);
                setingPenIndex();
            }
            write_view.setCanDraw(true, 1);
        }
    };

    /**
     * 设置笔记大小
     *
     * @param group
     * @param checkedId
     */
    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
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

                break;
            default:
                break;
        }
        if (position != -1) {
            if (write_view.isNibWipe()) {
                int size = BrushSettingUtils.getInstance(PicPostilActivity.this).getRubberIndexSize(position);
                write_view.setClearLineWidth(size);
            } else {
                int size = BrushSettingUtils.getInstance(PicPostilActivity.this).getDrawLineIndexSize(position);
                write_view.setDrawLineWidth(size);
            }
        }
        write_view.setCanDraw(true, 2);
    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {
        acquireWakeLock();
        setingPenIndex();
        getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (windowFocus)
                    write_view.setCanDraw(true, 22);
            }
        }, 1000);
        isStop = false;
    }

    @Override
    public void onActivityPaused(Activity activity) {
        isStop = true;
        if (write_view != null)
            write_view.setCanDraw(false, 21);
        releaseWakeLock();
    }

    @Override
    public void onActivityStopped(Activity activity) {

    }


    @Override
    public void onActivitySaveInstanceState(Bundle outState) {
        outState.putString("backImgPath", backImgPath);
        outState.putString("title", title);
        outState.putBoolean("titleShow", statusShow);
    }

    @Override
    public void onActivityRestoreInstanceState(Bundle savedInstanceState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
//        if (!StringUtils.isNull(backImgPath))
//            LocationPhotoLoder.getInstance().clearCatch(backImgPath);
        StringUtils.recycleBitmap(bitmap);
        releaseWakeLock();
        brodcastUtils.destory();
    }

    @Override
    public void onClick(View v) {
        if (Math.abs(System.currentTimeMillis() - clickTime) < 1000) {
            return;
        }
        write_view.setleaveScribbleMode(false,113);

        clickTime = System.currentTimeMillis();
        switch (v.getId()) {
            case R.id.quit_pic_postil://返回
                write_view.setCanDraw(false, 6);
                insureDialog("标注保存", "请选择保存方式", "覆盖", "新建", "", new InsureOrQuitListener() {
                    @Override
                    public void isInsure(Object code, boolean is) {
                        saveFile(true, is);
                    }
                });
                break;
            case R.id.pen6:
                //跳转到设置界面
                if (isClicksetingPen) {
                    write_view.setCanDraw(false, 6);
                    CustomPenActivity.startCustomPen(this, !write_view.isNibWipe());
                    }
                break;
            case R.id.setting:
                write_view.setCanDraw(false, 6);
                CustomPenActivity.startCustomPen(this, !write_view.isNibWipe());
                isStartResume=true;
                break;
            default:
                break;
        }
    }
    private boolean isStartResume=false;
    @Override
    protected void onResume() {
        super.onResume();
        if (isStartResume){
            write_view.fullRefuresh();
            isStartResume=false;
        }else if(openScreen){
            getHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (isFinishing())return;
                    write_view.fullRefuresh();
                }
            },300);
        }
        openScreen=false;
    }
    private String savePath;

    /**
     * 保存文件
     *
     * @param finish    保存后是否退出
     * @param isReplace 是否替换以前文件
     */
    private void saveFile(final boolean finish, boolean isReplace) {
        savePath = backImgPath;
        if (!isReplace) {
            File file = new File(backImgPath);
            savePath = file.getParent() + "/" + System.currentTimeMillis() + ".png";
        }
        final Bitmap b = write_view.getBitmap();
        if (b == null || b.isRecycled()) {
            ToastUtils.getInstance().showToastShort("保存失败");
            return;
        }
        dialogShowOrHide(true, "保存中");
        new Thread(new Runnable() {
            @Override
            public void run() {
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(savePath);
                    if (null != fos) {
                        b.compress(Bitmap.CompressFormat.PNG, 100, fos);
                        fos.flush();
                        fos.close();
                    }
                    getHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            if (isfinish) return;

                            dialogShowOrHide(false, "");
                            File file = new File(savePath);
                            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file));
                            PicPostilActivity.this.sendBroadcast(intent);
                            if (finish) {
                                backActivity();
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_PAGE_UP || keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            write_view.onKeysetEnterScribble();
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_PAGE_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            write_view.onKeysetEnterScribble();
            return true;
        }

        return super.onKeyUp(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        write_view.setCanDraw(false, 6);
        insureDialog("退出提示", "请确认是否保存", "保存", "丢弃", "", new InsureOrQuitListener() {
            @Override
            public void isInsure(Object code, boolean is) {
                if (is) {
                    onClick(quit_pic_postil);
                } else {
                    backActivity();
                }
            }
        });
    }

    private void backActivity() {
        write_view.setleaveScribbleMode(false, 0);
        if (statusShow) {
            isfinish = true;
            getHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            }, 100);
        } else {
            finish();
        }
    }

    private void setingPenIndex() {
        isClicksetingPen = false;
        if ( write_view.getPenControl()==null){
            getHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    setingPenIndex();
                }
            },100);
            return;
        }
        if (write_view.isNibWipe()) {
            ((RadioButton) (pen_group.getChildAt(BrushSettingUtils.getInstance(PicPostilActivity.this).pitchOnRubberIndex()))).performClick();
        } else {
            ((RadioButton) (pen_group.getChildAt(BrushSettingUtils.getInstance(PicPostilActivity.this).pitchdrawLineIndex()))).performClick();
        }
        write_view.setClearLineWidth(BrushSettingUtils.getInstance(this).getRubberSize());
        write_view.setDrawLineWidth(BrushSettingUtils.getInstance(this).getDrawLineSize());
        isClicksetingPen = true;
    }


    private boolean windowFocus = false;

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (isfinish) return;
        windowFocus = hasFocus;
        if (hasFocus) {
            getHandler().removeCallbacksAndMessages(null);
//            Device.currentDevice.hideSystemStatusBar(this);
            write_view.setCanDraw(true, 23);
            if (bitmap==null||bitmap.isRecycled()){
                setShowBitmap(decodeSampledBitmapFromResource(backImgPath,write_view.getWidth(),write_view.getHeight(),false));
            }
        } else {
            write_view.setCanDraw(false, 24);
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

    /**
     * 根据计算的inSampleSize，得到压缩后图片
     *
     * @param pathName
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    private Bitmap decodeSampledBitmapFromResource(String pathName,
                                                   int reqWidth, int reqHeight, boolean isMax) {
        Bitmap bitmap=null;
        try {
            // 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(pathName, options);
            // 调用上面定义的方法计算inSampleSize�?
            options.inSampleSize = calculateInSampleSize(options, reqWidth,
                    reqHeight, isMax);
            // 使用获取到的inSampleSize值再次解析图�?
            options.inJustDecodeBounds = false;
            bitmap = BitmapFactory.decodeFile(pathName, options);
        }catch (Exception e){
        }
        return bitmap;
    }
    /**
     * 计算inSampleSize，用于压缩图�?
     *
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    private int calculateInSampleSize(BitmapFactory.Options options,
                                      int reqWidth, int reqHeight, boolean isMax) {
        // 源图片的宽度
        int width = options.outWidth;
        int height = options.outHeight;
        int inSampleSize = 1;
        if (isMax) {
            if (width > reqWidth && height > reqHeight) {
                // 计算出实际宽度和目标宽度的比
                int widthRatio = Math.round((float) width / (float) reqWidth);
                int heightRatio = Math.round((float) height / (float) reqHeight);
                inSampleSize = Math.max(widthRatio, heightRatio);
            }
        } else {
            if (width > reqWidth || height > reqHeight) {
                // 计算出实际宽度和目标宽度的比
                int widthRatio = Math.round((float) width / (float) reqWidth);
                int heightRatio = Math.round((float) height / (float) reqHeight);
                inSampleSize = Math.max(widthRatio, heightRatio);
            }
        }
        return inSampleSize;
    }
}
