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
public class PeriodAdapter extends RecyclerView.Adapter {

    private Context context;
    private List<OptionModel> listOption;
    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public PeriodAdapter(Context context, List<OptionModel> listOption) {
        this.context = context;
        this.listOption = listOption;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.mx_recycler_period_item, parent, false);
        return new PeriodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        ((PeriodViewHolder) holder).tvPeriod.setText(listOption.get(position).getOptionName());
        if (listOption.get(position).isChosen()) {
            ((PeriodViewHolder) holder).tvPeriod.setTextColor(context.getResources().getColor(R.color.colorWihte));
            ((PeriodViewHolder) holder).tvPeriod.setBackgroundResource(R.drawable.moxi_shape_black_corner);
        } else {
            ((PeriodViewHolder) holder).tvPeriod.setTextColor(context.getResources().getColor(R.color.colorBlack));
            ((PeriodViewHolder) holder).tvPeriod.setBackgroundResource(R.drawable.moxi_shape_grayish_corner);
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

    @Override
    public int getItemCount() {
        return listOption == null ? 0 : listOption.size();
    }

    class PeriodViewHolder extends RecyclerView.ViewHolder {
        TextView tvPeriod;

        public PeriodViewHolder(View itemView) {
            super(itemView);
            tvPeriod = (TextView) itemView.findViewById(R.id.tv_recycler_item_period);
        }
    }
}
