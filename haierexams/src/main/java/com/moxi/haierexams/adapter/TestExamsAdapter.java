package com.moxi.haierexams.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.moxi.haierexams.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by zhengdelong on 16/9/29.
 */

public class TestExamsAdapter extends BaseAdapter {

    private List<HashMap<String, Object>> data = new ArrayList<>();
    private Context context;

    public TestExamsAdapter(Context context, List<HashMap<String, Object>> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public int getCount() {
        if (data.size() > 4) {
            return 4;
        } else {
            return data.size();
        }
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
            view = LayoutInflater.from(context).inflate(R.layout.mx_recycler_test_exams_more_item, null);
            viewHolder.tv_exams_item_title = (TextView) view.findViewById(R.id.tv_exams_item_title);
            viewHolder.tv_exams_item_subject = (TextView) view.findViewById(R.id.tv_exams_item_subject);
            viewHolder.state_img = (ImageView) view.findViewById(R.id.state_img);
            viewHolder.tv_exams_item_point = (TextView) view.findViewById(R.id.tv_exams_item_point);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.tv_exams_item_title.setText(data.get(position).get("pap_name").toString());
        viewHolder.tv_exams_item_subject.setText(data.get(position).get("pap_sub_name").toString());
//        if (ACache.get(context).getAsString(data.get(position).getTitle() + data.get(position).getId()) == null) {
//            //未做
//            viewHolder.tv_exams_item_point.setVisibility(View.INVISIBLE);
//        } else {
//            //已做
//            viewHolder.tv_exams_item_point.setVisibility(View.VISIBLE);
//        }
//        viewHolder.tv_exams_item_point.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String fileUrl = Environment.getExternalStorageDirectory() + "/mx_exams/" +
//                        data.get(position).getTitle();
//                File file = new File(fileUrl + "/imgs");
//                if (file.exists() && file.isDirectory()) {
//                    File[] subFile = file.listFiles();
//                    int ssss = subFile.length;
//                    if (ssss > 0) {
//                        FileUtils.getInstance().openFile(context, subFile[1]);
//                    }
//                }
//            }
//        });
        return view;
    }

    static class ViewHolder {
        TextView tv_exams_item_title;
        TextView tv_exams_item_subject;
        TextView tv_exams_item_point;
        ImageView state_img;
    }
}
