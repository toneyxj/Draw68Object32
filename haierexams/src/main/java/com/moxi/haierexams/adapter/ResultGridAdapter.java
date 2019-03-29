package com.moxi.haierexams.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.moxi.haierexams.R;
import com.moxi.haierexams.cache.ACache;
import com.moxi.haierexams.model.ChoseExamsModel;
import com.moxi.haierexams.model.ExamsDetails;

import java.util.List;

/**
 * Created by Archer on 2016/11/25.
 */
public class ResultGridAdapter extends BaseAdapter {
    private List<ChoseExamsModel> listData;
    private Context context;

    public ResultGridAdapter(Context context, List<ChoseExamsModel> listData) {
        this.context = context;
        this.listData = listData;
    }

    @Override
    public int getCount() {
        return listData != null ? listData.size() : 0;
    }

    @Override
    public Object getItem(int i) {
        return listData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ResultViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ResultViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.mx_recycler_reply_result_item, null);
            viewHolder.tvIndex = (TextView) convertView.findViewById(R.id.tv_reply_result_index);
            viewHolder.imgIndex = (ImageView) convertView.findViewById(R.id.img_reply_result);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ResultViewHolder) convertView.getTag();
        }

        String resultKey = listData.get(position).getResultKey() + listData.get(position).getIndex();
        String tempResult = ACache.get(context).getAsString(resultKey);
        ExamsDetails examsDetails = listData.get(position).getExamsDetails();
        try {
            if (listData.get(position).getExamsDetails().getAnswer().equals(tempResult) || examsDetails.getResult().equals(examsDetails.answer)) {
                viewHolder.imgIndex.setBackgroundResource(R.drawable.moxi_shape_black_corner_35);
                viewHolder.tvIndex.setTextColor(context.getResources().getColor(R.color.colorWihte));
            } else {
                viewHolder.imgIndex.setBackgroundResource(R.drawable.moxi_shape_white_corner_35);
                viewHolder.tvIndex.setTextColor(context.getResources().getColor(R.color.colorBlack));
            }
        } catch (Exception e) {
            viewHolder.imgIndex.setBackgroundResource(R.drawable.moxi_shape_white_corner_35);
            viewHolder.tvIndex.setTextColor(context.getResources().getColor(R.color.colorBlack));
            e.printStackTrace();
        }
        viewHolder.tvIndex.setText((listData.get(position).getIndex() + 1) + "");
        return convertView;
    }

    class ResultViewHolder {
        private TextView tvIndex;
        private ImageView imgIndex;
    }
}
