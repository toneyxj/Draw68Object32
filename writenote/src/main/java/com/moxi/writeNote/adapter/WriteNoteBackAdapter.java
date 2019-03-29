package com.moxi.writeNote.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.moxi.writeNote.R;
import com.mx.mxbase.adapter.BAdapter;

import java.util.List;

/**
 * 手写背景adapter
 * Created by xj on 2017/6/22.
 */

public class WriteNoteBackAdapter extends BAdapter<Integer> {
    private int index=0;

    public WriteNoteBackAdapter(Context context, List<Integer> list,int index) {
        super(context, list);
        this.index=index;
    }

    @Override
    public int getContentView() {
        return R.layout.adapter_write_note_back;
    }

    @Override
    public void onInitView(View view, int position, boolean firstAdd) {
        ViewHolder holder;
        if (firstAdd) {
            holder = new ViewHolder();
            holder.back_photo = (ImageView) view.findViewById(R.id.back_photo);
            holder.is_select = (ImageView) view.findViewById(R.id.is_select);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        int been = getItem(position);
        holder.back_photo.setImageResource(been);
        if (index==-1){
            holder.is_select.setVisibility(18 == position ? View.VISIBLE : View.INVISIBLE);
        }else {
            holder.is_select.setVisibility(index == position ? View.VISIBLE : View.INVISIBLE);
        }
    }

    public class ViewHolder {
        ImageView back_photo;
        ImageView is_select;
    }
}
