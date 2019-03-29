package com.moxi.haierexams.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.moxi.haierexams.R;
import com.moxi.haierexams.model.CourseModel;
import com.mx.mxbase.interfaces.OnItemClickListener;

import java.util.List;

/**
 * Created by Archer on 16/8/9.
 */
public class TimeTableCourseAdapter extends RecyclerView.Adapter {
    private Context context;
    private List<CourseModel> listCourse;
    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public TimeTableCourseAdapter(Context context, List<CourseModel> listCourse) {
        this.context = context;
        this.listCourse = listCourse;
    }
    public void setData(List<CourseModel> listCourse){
        this.listCourse = listCourse;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.mx_recycler_time_table_course_item, parent, false);
        return new CourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        ((CourseViewHolder) holder).tvCourseName.setText(listCourse.get(position).getCourseName());
        if (listCourse.get(position).isChosen()) {
            ((CourseViewHolder) holder).imgCourse.setImageResource(listCourse.get(position).getCoursePressRes());
            ((CourseViewHolder) holder).imgCourse.setBackgroundResource(R.drawable.moxi_shape_black_left_corner_8);
            ((CourseViewHolder) holder).tvCourseName.setTextColor(context.getResources().getColor(R.color.colorWihte));
            ((CourseViewHolder) holder).tvCourseName.setBackgroundResource(R.drawable.moxi_shape_black_right_corner_8);
        } else {
            if (listCourse.size() < 10) {
                ((CourseViewHolder) holder).imgCourse.setImageResource(listCourse.get(position).getCourseRes());
                ((CourseViewHolder) holder).imgCourse.setBackgroundResource(R.color.colorWihte);
                ((CourseViewHolder) holder).tvCourseName.setTextColor(context.getResources().getColor(R.color.colorBlack));
                ((CourseViewHolder) holder).tvCourseName.setBackgroundResource(R.color.colorWihte);
            } else {
                ((CourseViewHolder) holder).imgCourse.setImageResource(listCourse.get(position).getCourseRes());
                ((CourseViewHolder) holder).imgCourse.setBackgroundResource(R.color.colorGrayish);
                ((CourseViewHolder) holder).tvCourseName.setTextColor(context.getResources().getColor(R.color.colorBlack));
                ((CourseViewHolder) holder).tvCourseName.setBackgroundResource(R.color.colorGrayish);
            }
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
        return listCourse != null ? listCourse.size() : 0;
    }

    class CourseViewHolder extends RecyclerView.ViewHolder {
        private TextView tvCourseName;
        private ImageView imgCourse;

        public CourseViewHolder(View itemView) {
            super(itemView);
            imgCourse = (ImageView) itemView.findViewById(R.id.img_recycler_item_time_table_course);
            tvCourseName = (TextView) itemView.findViewById(R.id.tv_recycler_item_time_table_course);
        }
    }
}
