package com.moxi.writeNote.Activity.utils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.moxi.writeNote.R;
import com.moxi.writeNote.WriteBaseActivity;
import com.moxi.writeNote.view.CorpImageView;
import com.mx.mxbase.interfaces.Sucess;
import com.mx.mxbase.utils.LocationPhotoLoder;
import com.mx.mxbase.utils.ScreenShot;
import com.mx.mxbase.utils.StringUtils;
import com.mx.mxbase.utils.WindowsUtils;

import java.io.File;

import butterknife.Bind;

/**
 * 图像裁剪操作界面
 */
public class PhotoCutActivity extends WriteBaseActivity implements View.OnClickListener {
    /**
     * 启动图片截取activity
     *
     * @param activity    文件截取操作器
     * @param filePath    文件路径
     * @param requestCode 请求返回标识
     */
    public static void startACutctivity(Activity activity, String filePath, int requestCode) {
        Intent intent = new Intent(activity, PhotoCutActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("filePath", filePath);
        intent.putExtras(bundle);
        activity.startActivityForResult(intent, requestCode);
    }

    @Bind(R.id.get_image)
    RelativeLayout get_image;
    @Bind(R.id.cut_photo)
    CorpImageView cut_photo;
    @Bind(R.id.cancle)
    Button cancle;
    @Bind(R.id.restore)
    Button restore;
    @Bind(R.id.result)
    Button result;
    //50重启，54启动唤醒，57启动唤醒，5:00唤醒
    private ScreenShot screenShot;//屏幕截图
    @Override
    protected int getMainContentViewId() {
        return R.layout.activity_photo_cut;
    }

    private String filePath = "";

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            filePath = savedInstanceState.getString("filePath");
        } else {
            filePath = getIntent().getExtras().getString("filePath");
        }
        LocationPhotoLoder.getInstance().loadImage(filePath, new Sucess() {
            @Override
            public void setSucess(Bitmap bitmap, boolean isS) {
                if (isfinish) return;
                //获得处理后的背景图片
                cut_photo.setImageBitmap(bitmap);
            }
        }, (int) (WindowsUtils.WritedrawWidth * 1.3), (int) (WindowsUtils.WritedrawHeight * 1.3), true);


        cancle.setOnClickListener(this);
        restore.setOnClickListener(this);
        result.setOnClickListener(this);

        screenShot=new ScreenShot(null, new ScreenShot.ScreenShotListener() {
            @Override
            public void onShotSucess(String filePath) {
                //保存图片完成
                Intent intent=new Intent();
                intent.putExtra("filePath",filePath);
                setResult(RESULT_OK,intent);
                onBackPressed();
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancle:
                onBackPressed();
                break;
            case R.id.restore:
                cut_photo.resetImage();
                break;
            case R.id.result:
                //获取图片保存到sd卡里面
                String filePath= StringUtils.getWriteNotePhotoPath();
                if (StringUtils.isNull(filePath))return;

                File saveFile=new File(filePath,String.valueOf(System.currentTimeMillis())+StringUtils.WRITENOTE_BACKGROUNG_END);
                screenShot.savePic(getBitmap(),saveFile.getAbsolutePath(),true);
                break;
            default:
                break;
        }
    }
    /**
     * 获取Layout截图
     *
     * @return 所需区域的截图
     */
    private Bitmap getBitmap() {
        get_image.setDrawingCacheEnabled(true);
        get_image.buildDrawingCache();  //启用DrawingCache并创建位图
        Bitmap bitmap = Bitmap.createBitmap(get_image.getDrawingCache()); //创建一个DrawingCache的拷贝，因为DrawingCache得到的位图在禁用后会被回收
        get_image.setDrawingCacheEnabled(false);  //禁用DrawingCahce否则会影响性能
        return bitmap;
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
        outState.putString("filePath", filePath);
    }

    @Override
    public void onActivityRestoreInstanceState(Bundle savedInstanceState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }
}
