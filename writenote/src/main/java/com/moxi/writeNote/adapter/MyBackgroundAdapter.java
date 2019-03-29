package com.moxi.writeNote.adapter;

import android.content.Context;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.moxi.writeNote.R;
import com.mx.mxbase.adapter.BAdapter;
import com.mx.mxbase.base.MyApplication;
import com.mx.mxbase.utils.LocationImageLoader;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.moxi.writeNote.R.id.show_backImage;

/**
 * Created by xj on 2018/4/12.
 */

public class MyBackgroundAdapter extends BAdapter<String> {
    //保存选择的数据
    private Set<String> sets= new HashSet<String>();
    private boolean isSelect=false;

    public MyBackgroundAdapter(Context context, List<String> list) {
        super(context, list);
    }

    public void startSet(boolean is,GridView listView){
        this.isSelect=is;
        sets.clear();
        if (listView!=null){
           updateAllSelect(listView);
        }
    }

    public boolean isSelect() {
        return isSelect;
    }

    /**
     * 获得保存的数据
     * @return
     */
    public Set<String> getSets() {
        return sets;
    }

    @Override
    public int getContentView() {
        return R.layout.adapter_my_background;
    }

    @Override
    public void onInitView(View view, int position, boolean firstAdd) {
        ViewHolder holder;
        if (firstAdd) {
            holder = new ViewHolder();
            holder.all_layout = (RelativeLayout) view.findViewById(R.id.all_layout);
            holder.show_select = (ImageView) view.findViewById(R.id.show_select);
            holder.show_backImage = (ImageView) view.findViewById(show_backImage);

            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) holder.all_layout.getLayoutParams();
            params.width = MyApplication.ScreenWidth / 4;
            params.height = (MyApplication.ScreenHeight-145) / 4;
            holder.all_layout.setLayoutParams(params);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        holder.show_backImage.setImageBitmap(null);
//        DbPhotoLoader.getInstance().locationPhoto(getItem(position),false,holder.show_backImage,null);
        LocationImageLoader.getInstance().loadImage(getItem(position),holder.show_backImage,null);

        if (isSelect ) {
            holder.show_select.setVisibility(View.VISIBLE);
            if (sets.contains(getItem(position))) {
                holder.show_select.setImageResource(R.mipmap.have_select);
            } else {
                holder.show_select.setImageResource(R.mipmap.non_select);
            }
        } else {
            holder.show_select.setVisibility(View.INVISIBLE);
        }
    }
    public class ViewHolder {
        RelativeLayout all_layout;
        ImageView show_backImage;
        ImageView show_select;
    }
    /**
     * 更新选中
     *
     * @param position
     * @param listView
     */
    public void updateSelect(int position, GridView listView) {
        if (sets.contains(getItem(position))){
            sets.remove(getItem(position));
        }else {
            sets.add(getItem(position));
        }
        int visibleFirstPosi = listView.getFirstVisiblePosition();
        int visibleLastPosi = listView.getLastVisiblePosition();
        if (position >= visibleFirstPosi && position <= visibleLastPosi) {
            View view = listView.getChildAt(position - visibleFirstPosi);
            ImageView select_image = (ImageView) view.findViewById(R.id.show_select);
            boolean isTrue = sets.contains(getItem(position));
            if (isTrue) {
                select_image.setImageResource(R.mipmap.have_select);
            } else {
                select_image.setImageResource(R.mipmap.non_select);
            }
        }
    }
    /**
     * 更新所有选中
     */
    public void updateAllSelect(GridView listView) {
        int visibleFirstPosi = listView.getFirstVisiblePosition();
        int visibleLastPosi = listView.getLastVisiblePosition();
        for (int i = visibleFirstPosi; i <= visibleLastPosi; i++) {
            View view = listView.getChildAt(i);
            ImageView select_image = (ImageView) view.findViewById(R.id.show_select);

            select_image.setVisibility(isSelect ? View.VISIBLE : View.INVISIBLE);
            boolean isTrue = sets.contains(getItem(i));
            if (isTrue) {
                select_image.setImageResource(R.mipmap.have_select);
            } else {
                select_image.setImageResource(R.mipmap.non_select);
            }
        }
    }
}
