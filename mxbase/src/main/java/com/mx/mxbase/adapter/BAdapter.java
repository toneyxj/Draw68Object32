package com.mx.mxbase.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * Created by Administrator on 2016/5/25.
 */
public abstract class BAdapter<t> extends BaseAdapter implements View.OnClickListener {
    private List<t> list;

    protected Context context;

    public BAdapter(Context context, List<t> list) {
        init(context, list);
    }

    private void init(Context context, List<t> list) {
        this.list = list;
        this.context = context;
    }

    public List<t> getList() {
        return list;
    }

    public void setList(List<t> list) {
        this.list = list;
    }

    public void clear() {
        this.list.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<t> list) {
        if (list != null) {
            this.list.addAll(list);
            notifyDataSetChanged();
        }
    }

    @Override
    public int getCount() {
        return list == null ? 0 : list.size();
    }

    @Override
    public t getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        boolean first = false;
        if (null == convertView) {
            convertView = inflate(getContentView());
            first = true;
        }
        onInitView(convertView, position, first);
        return convertView;
    }

    /**
     * 加载布局
     */
    private View inflate(int layoutResID) {
        LayoutInflater layoutInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(layoutResID, null);
        setItemHeight(view);
        return view;
    }

    /**
     * 设置Item单个显示高度
     * @param view
     */
    public void setItemHeight(View view){

    }

    public abstract int getContentView();

    /**
     *  构建具体显示数据
     * @param view  显示item
     * @param position 构建位置
     * @param firstAdd 是否是第一个item
     */
    public abstract void onInitView(View view, int position, boolean firstAdd);


    @Override
    public void onClick(View v) {
        click(v);
    }

    /**
     * 继承实现点击效果
     * @param v
     */
    public void click(View v) {

    }
}
