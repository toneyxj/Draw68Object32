package com.moxi.writeNote.popwin;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.moxi.writeNote.R;

/**
 * 文件排序pop
 * Created by xj on 2017/9/25.
 */

public class SortStylePopwin implements View.OnClickListener{
    private SelectSortListener listener;
    private PopupWindow popSort;
    private View view;
    private int sortStyle;
    private Context context;

    public SortStylePopwin(Context context,int sortStyle,SelectSortListener listener) {
        this.context = context;
        this.sortStyle=sortStyle;
        this.listener=listener;
    }

    public void popWindow(View view) {
        if (view==null)return;
        this.view=view;
        View contentView = LayoutInflater.from(context).inflate(
                R.layout.pop_file_manager_sort_layout, null);
        // 设置按钮的点击事件
        LinearLayout sort_name = (LinearLayout) contentView.findViewById(R.id.sort_name);
//        LinearLayout sort_big = (LinearLayout) contentView.findViewById(R.id.sort_big);
        LinearLayout sort_create_time = (LinearLayout) contentView.findViewById(R.id.sort_create_time);
//        LinearLayout sort_type = (LinearLayout) contentView.findViewById(R.id.sort_type);

        ImageView show_index_one = (ImageView) contentView.findViewById(R.id.show_index_one);
//        ImageView show_index_two = (ImageView) contentView.findViewById(R.id.show_index_two);
        ImageView show_index_three = (ImageView) contentView.findViewById(R.id.show_index_three);
//        ImageView show_index_four = (ImageView) contentView.findViewById(R.id.show_index_four);
        if (sortStyle == 1) {
            show_index_one.setVisibility(View.VISIBLE);
        }else{
            show_index_three.setVisibility(View.VISIBLE);
        }

        popSort = new PopupWindow(contentView,
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);

        popSort.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    popSort.dismiss();
                    popSort=null;
                    return true;
                }
                return false;
            }
        });

        sort_name.setOnClickListener(this);
        sort_create_time.setOnClickListener(this);
        // 设置好参数之后再show
        popSort.setBackgroundDrawable(new BitmapDrawable());
        popSort.showAsDropDown(view,-10,5);
    }

    public void dismissSort(){
        if (popSort!=null) {
            if (!popSort.isShowing()){
                if (view!=null){
                    popSort.showAsDropDown(view);
                }
                popSort.dismiss();
            }else {
                popSort.dismiss();
            }
            popSort = null;
        }
    }

    @Override
    public void onClick(View v) {
        dismissSort();
        switch (v.getId()) {
            case R.id.sort_name://按名称排序
                if (sortStyle==1)return;
                sortStyle=1;
                break;
            case R.id.sort_create_time://按创建时间排序
                if (sortStyle==0)return;
                sortStyle=0;
                break;
            default:
                break;
        }
        listener.onUpdate(sortStyle);
    }

    public interface SelectSortListener{
        void onUpdate(int style);
    }
}
