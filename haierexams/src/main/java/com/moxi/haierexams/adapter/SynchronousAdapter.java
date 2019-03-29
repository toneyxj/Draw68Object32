package com.moxi.haierexams.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.moxi.haierexams.R;
import com.moxi.haierexams.db.SQLUtil;
import com.moxi.haierexams.model.OptionModel;
import com.moxi.haierexams.model.SynchronousModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhengdelong on 16/9/30.
 */

public class SynchronousAdapter extends BaseAdapter {

    private List<SynchronousModel> data = new ArrayList<SynchronousModel>();
    private Context context;
    private LinearLayout content_rel = null;
    private ImageView jj_img = null;
    private String parentId;

    private View.OnClickListener onClickListener;

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public SynchronousAdapter(Context context, List<SynchronousModel> data, String parentId) {
        this.context = context;
        this.data = data;
        this.parentId = parentId;
    }

    public void setData(List<SynchronousModel> data) {
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
        final ViewHolder viewHolder;
        if (view == null) {
            viewHolder = new ViewHolder();

            view = LayoutInflater.from(context).inflate(R.layout.item_synchrous, null);
            viewHolder.title_rel = (RelativeLayout) view.findViewById(R.id.title_rel);
            viewHolder.content_rel = (LinearLayout) view.findViewById(R.id.content_rel);
            viewHolder.jiajian_img = (ImageView) view.findViewById(R.id.jiajian_img);
            viewHolder.zhangjie_text = (TextView) view.findViewById(R.id.zhangjie_text);
            viewHolder.content_zhangjie = (TextView) view.findViewById(R.id.content_zhangjie);

            viewHolder.title_rel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (content_rel != null) {
                        if (content_rel != viewHolder.content_rel) {
                            content_rel.setVisibility(View.GONE);
                        }
                    }
                    if (jj_img != null) {
                        jj_img.setImageResource(R.mipmap.zhankai_icon);
                    }
                    jj_img = viewHolder.jiajian_img;
                    content_rel = viewHolder.content_rel;
                    if (viewHolder.content_rel.getVisibility() == View.GONE) {
                        viewHolder.jiajian_img.setImageResource(R.mipmap.zhedie_icon);
                        if (viewHolder.content_rel.getChildCount() > 0) {
                            if (viewHolder.content_rel.getVisibility() == View.GONE) {
                                viewHolder.content_rel.setVisibility(View.VISIBLE);
                                jj_img.setImageResource(R.mipmap.zhedie_icon);
                            } else {
                                viewHolder.content_rel.setVisibility(View.GONE);
                                jj_img.setImageResource(R.mipmap.zhankai_icon);
                            }
                            return;
                        } else {
                            viewHolder.content_rel.setVisibility(View.VISIBLE);
                        }
                        int id = data.get(position).getId();
                        int tempid = data.get(position).getParentId();
                        List<OptionModel> itemData = SQLUtil.getInstance(context).getCourseChapterChildFromDb(tempid + "", id + "");
                        for (int i = 0; i < itemData.size(); i++) {
                            final View view1 = LayoutInflater.from(context).inflate(R.layout.item_sync, null);
                            SpannableStringBuilder builder = new SpannableStringBuilder(itemData.get(i).getOptionName() + "我");
                            TextView textView = (TextView) view1.findViewById(R.id.yiu);
                            ImageView img = (ImageView) view1.findViewById(R.id.edit_img);
                            ForegroundColorSpan whiteSpan = new ForegroundColorSpan(Color.WHITE);
                            int temp = (itemData.get(i).getOptionName() + "我").length();
                            builder.setSpan(whiteSpan, temp - 1, temp, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            textView.setText(builder);
                            img.setImageResource(R.mipmap.edit_icon);
                            viewHolder.content_rel.addView(view1);
                            view1.setTag(itemData.get(i));
                            view1.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    onClickListener.onClick(view1);
                                }
                            });
                        }

                    } else {
                        viewHolder.content_rel.setVisibility(View.GONE);
                        viewHolder.jiajian_img.setImageResource(R.mipmap.zhankai_icon);
                    }
                }
            });
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.zhangjie_text.setText(data.get(position).getTitle());
        return view;
    }

    static class ViewHolder {
        RelativeLayout title_rel;
        ImageView jiajian_img;
        TextView zhangjie_text;
        TextView content_zhangjie;
        LinearLayout content_rel;
    }
}
