package com.moxi.haierexams.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.moxi.haierexams.R;
import com.moxi.haierexams.model.OptionModel;
import com.mx.mxbase.interfaces.OnItemClickListener;

import java.util.List;

/**
 * Created by Archer on 16/8/10.
 */
public class OptionAdapter extends RecyclerView.Adapter {

    private Context context;
    private List<OptionModel> listOption;
    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public OptionAdapter(Context context, List<OptionModel> listOption) {
        this.context = context;
        this.listOption = listOption;
    }

    public void setData(List<OptionModel> listOption){
        this.listOption = listOption;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.mx_recycler_option_item, parent, false);
        return new OptionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (listOption.get(position).getOptionName().equals("")) {
            holder.itemView.setVisibility(View.GONE);
        } else {
            holder.itemView.setVisibility(View.VISIBLE);
            ((OptionViewHolder) holder).tvOptionName.setText(listOption.get(position).getOptionName());
            if (listOption.get(position).isChosen()) {
                ((OptionViewHolder) holder).tvOptionName.setBackgroundResource(R.drawable.moxi_shape_black_corner_5);
                ((OptionViewHolder) holder).tvOptionName.setTextColor(context.getResources().getColor(R.color.colorWihte));
            } else {
                ((OptionViewHolder) holder).tvOptionName.setBackgroundResource(R.drawable.moxi_shape_grayish_corner_5);
                ((OptionViewHolder) holder).tvOptionName.setTextColor(context.getResources().getColor(R.color.colorBlack));
            }
            if (onItemClickListener != null) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onItemClickListener.onItemClick(holder.itemView, position);
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return listOption == null ? 0 : listOption.size();
    }

    class OptionViewHolder extends RecyclerView.ViewHolder {
        TextView tvOptionName;

        public OptionViewHolder(View itemView) {
            super(itemView);
            tvOptionName = (TextView) itemView.findViewById(R.id.tv_option_name);
        }
    }
}
