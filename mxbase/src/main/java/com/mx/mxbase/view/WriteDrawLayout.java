package com.mx.mxbase.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.mx.mxbase.R;


/**
 * Created by Administrator on 2016/8/4.
 */
public class WriteDrawLayout extends RelativeLayout {
    private ImageView is_select;
    private ImageView show_image;
    /**
     * 正时的image
     */
    private int image;
    /**
     * 当前状态
     */
    private boolean currentStatus = false;

    public void setCurrentStatus(boolean currentStatus) {
        this.currentStatus = currentStatus;
        changeStatus(currentStatus);
    }
    public void setChange(){
        currentStatus=!currentStatus;
        changeStatus(currentStatus);
    }

    public void setImage(int image) {
        this.image = image;
        if (show_image!=null){
            show_image.setImageResource(image);
        }
    }

    /**
     * 设置所有初始化需要的值
     * @param currentStatus 当前显示状态
     */
    public void setallValue(int image, boolean currentStatus) {
        this.currentStatus = currentStatus;
        this.image=image;

        if (show_image!=null){
            show_image.setImageResource(image);
        }
        changeStatus(currentStatus);
    }

    public WriteDrawLayout(Context context) {
        super(context);
        init(context);
    }

    public WriteDrawLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public WriteDrawLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_select_draw_type,
                this);
        is_select = (ImageView) view.findViewById(R.id.is_select);
        show_image = (ImageView) view.findViewById(R.id.show_image);

        if (image!=0){
            show_image.setImageResource(image);
        }
    }

    /**
     * 开始改变
     */
    public void changeStatus(boolean is){
        if (is) {
            is_select.setVisibility(View.VISIBLE);
        }else{
            is_select.setVisibility(INVISIBLE);
        }
    }


}
