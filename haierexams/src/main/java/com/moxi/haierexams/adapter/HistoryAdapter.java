package com.moxi.haierexams.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.moxi.haierexams.R;
import com.moxi.haierexams.model.HistoryModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhengdelong on 16/9/29.
 */

public class HistoryAdapter extends BaseAdapter{

    private List<HistoryModel> data = new ArrayList<HistoryModel>();
    private Context context;

    public HistoryAdapter(Context context, List<HistoryModel> data){
        this.context = context;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (view == null){
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(context).inflate(R.layout.mx_recycler_over_all_item,null);
            viewHolder.tv_recycler_over_all_exams_name = (TextView) view.findViewById(R.id.tv_recycler_over_all_exams_name);
            viewHolder.tv_recycler_over_all_exams_date = (TextView) view.findViewById(R.id.tv_recycler_over_all_exams_date);
            viewHolder.tv_recycler_over_all_exams_state = (TextView) view.findViewById(R.id.tv_recycler_over_all_exams_state);
            view.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)view.getTag();
        }

        viewHolder.tv_recycler_over_all_exams_name.setText(data.get(position).getTitle());
        viewHolder.tv_recycler_over_all_exams_date.setText(data.get(position).getCreatetime());
        viewHolder.tv_recycler_over_all_exams_state.setText(data.get(position).getType() + "");
        return view;
    }

    static class ViewHolder{
        TextView tv_recycler_over_all_exams_name;
        TextView tv_recycler_over_all_exams_date;
        TextView tv_recycler_over_all_exams_state;
    }

}
