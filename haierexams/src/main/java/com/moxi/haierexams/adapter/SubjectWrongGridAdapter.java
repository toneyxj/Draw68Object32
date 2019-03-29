package com.moxi.haierexams.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.moxi.haierexams.R;
import com.moxi.haierexams.activity.MXHistoryActivity;
import com.moxi.haierexams.model.WrongExamsModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhengdelong on 16/9/20.
 */

public class SubjectWrongGridAdapter extends BaseAdapter {

    private int max = -1;
    private List<WrongExamsModel> data = new ArrayList<WrongExamsModel>();
    private Context context;

    public SubjectWrongGridAdapter(Context context, List<WrongExamsModel> data) {
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
        if (view == null) {
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(context).inflate(R.layout.item_subject_wrong, null);
            viewHolder.num = (TextView) view.findViewById(R.id.num_wrong);
            viewHolder.title = (TextView) view.findViewById(R.id.subject_name);
            viewHolder.subject_icon = (ImageView) view.findViewById(R.id.subject_icon);
            viewHolder.top_subject = (ImageView) view.findViewById(R.id.top_subject);
            viewHolder.action_click = (RelativeLayout) view.findViewById(R.id.action_click);
            viewHolder.action_click.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClass(context, MXHistoryActivity.class);
                    intent.putExtra("sub_id", data.get(position).getSubjectId());
                    context.startActivity(intent);
                }
            });
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        setIcon(viewHolder.subject_icon, data.get(position).getSubjectName());
        viewHolder.title.setText(data.get(position).getSubjectName());
        viewHolder.num.setText(data.get(position).getCount() + "");
        return view;
    }

    static class ViewHolder {
        TextView title;
        TextView num;
        ImageView subject_icon;
        ImageView top_subject;
        RelativeLayout action_click;
    }

    private void setIcon(ImageView icon, String title) {
        if (title.contains("语文")) {
            icon.setImageResource(R.mipmap.yuwen_icon);
        } else if (title.contains("数学")) {
            icon.setImageResource(R.mipmap.shuxue_icon);
        } else if (title.contains("英语")) {
            icon.setImageResource(R.mipmap.yingyu_icon);
        } else if (title.contains("政治")) {
            icon.setImageResource(R.mipmap.zhengzhi_icon);
        } else if (title.contains("历史")) {
            icon.setImageResource(R.mipmap.lishi_icon);
        } else if (title.contains("物理")) {
            icon.setImageResource(R.mipmap.wuli_icon);
        } else if (title.contains("化学")) {
            icon.setImageResource(R.mipmap.huaxue_icon);
        } else if (title.contains("生物")) {
            icon.setImageResource(R.mipmap.shengwu_icon);
        } else if (title.contains("地理")) {
            icon.setImageResource(R.mipmap.dili_icon);
        }
    }
}
