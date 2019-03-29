package com.moxi.writeNote.adapter;

import android.content.Context;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.moxi.writeNote.R;
import com.mx.mxbase.adapter.BAdapter;
import com.mx.mxbase.constant.PhotoConfig;
import com.mx.mxbase.utils.StringUtils;

import java.io.File;
import java.util.List;

/**
 * Created by Administrator on 2016/8/29.
 */
public class FileAdapter extends BAdapter<File> {
    private int height;
    private int width;

    public FileAdapter(Context context, List<File> list, int height, int width) {
        super(context, list);
        this.height = height;
        this.width = width;
    }

    @Override
    public int getContentView() {
        return R.layout.adapter_file;
    }

    @Override
    public void onInitView(View view, int position, boolean firstAdd) {
        ViewHolder holder;
        if (firstAdd) {
            holder = new ViewHolder();
            holder.all_layout = (LinearLayout) view.findViewById(R.id.all_layout);
            holder.show_image = (ImageView) view.findViewById(R.id.show_image);
            holder.file_name = (TextView) view.findViewById(R.id.file_name);

            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) holder.all_layout.getLayoutParams();
            params.height = height;
            holder.all_layout.setLayoutParams(params);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        File been = getItem(position);

        holder.show_image.setImageResource(ShowDrawableId(been));

        elipseEnd(holder.file_name, been.getName());
    }

    /**
     * 获得文件显示图片
     *
     * @return
     */
    public int ShowDrawableId(File file) {
        if (file.isDirectory()) {
            return R.mipmap.filoder;
        } else {
            String name=file.getName();
            if (name.contains(".")) {
                String prefix = name.substring(name.lastIndexOf(".") + 1);
                prefix = prefix.toLowerCase();
                if (prefix.equals("jpg") || prefix.equals("png") || prefix.equals("jpeg")) {
                    return R.mipmap.image_jpg;
                } else if (PhotoConfig.getAllFileType().contains(prefix)) {
                    return PhotoConfig.getSources(name);
                } else if (file.getAbsolutePath().equals(StringUtils.getSDCardPath() + "/update.zip")) {
                    return R.mipmap.update_file;
                } else {
                    //未知
                    return R.mipmap.unknown;
                }
            } else {
                //未知
                return R.mipmap.unknown;
            }

        }
    }

    private void elipseEnd(TextView textView, String value) {
        TextPaint paint = textView.getPaint();

        int bufferWidth = (int) paint.getTextSize() * 3;//缓冲区长度，空出两个字符的长度来给最后的省略号及图片
        // 计算出1行文字所能显示的长度
        int availableTextWidth;
        availableTextWidth = width * 2 - bufferWidth;
        // 根据长度截取出剪裁后的文字
        String ellipsizeStr = (String) TextUtils.ellipsize(value, paint, availableTextWidth, TextUtils.TruncateAt.END);
        textView.setText(ellipsizeStr);
    }

    public class ViewHolder {
        LinearLayout all_layout;
        ImageView show_image;
        TextView file_name;
    }
}
