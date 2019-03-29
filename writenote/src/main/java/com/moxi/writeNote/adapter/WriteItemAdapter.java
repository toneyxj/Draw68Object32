package com.moxi.writeNote.adapter;

import android.content.Context;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.moxi.handwritinglibs.db.WritPadModel;
import com.moxi.handwritinglibs.utils.DbPhotoLoader;
import com.moxi.writeNote.R;
import com.mx.mxbase.adapter.BAdapter;
import com.mx.mxbase.base.MyApplication;
import com.mx.mxbase.utils.LocationImageLoader;
import com.mx.mxbase.utils.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.moxi.writeNote.R.id.show_backImage;

/**
 * Created by Administrator on 2016/8/2.
 */
public class WriteItemAdapter extends BAdapter<WritPadModel> {
    private List<WritPadModel> isSelectWritePads = new ArrayList<WritPadModel>();

    public List<WritPadModel> getIsSelectWritePads() {
        return isSelectWritePads;
    }

    public void setIsSelectWritePads(WritPadModel isSelectWritePad) {
        if (isSelectWritePads.contains(isSelectWritePad)) {
            removeSelect(isSelectWritePad);
        } else {
            this.isSelectWritePads.add(isSelectWritePad);
        }
    }

    public void removeSelect(WritPadModel isSelectWritePad) {
        this.isSelectWritePads.remove(isSelectWritePad);
    }

    private boolean showAelect = false;

    public void setShowAelect(boolean showAelect, GridView view) {
        this.showAelect = showAelect;
        this.isSelectWritePads.clear();
        if (view != null)
            updateAllSelect(view);
    }

    public boolean isShowAelect() {
        return showAelect;
    }

    public WriteItemAdapter(Context context, List<WritPadModel> list) {
        super(context, list);
    }

    @Override
    public int getContentView() {
        return R.layout.adapter_write_item;
    }

    @Override
    public void onInitView(View view, int position, boolean firstAdd) {
        ViewHolder holder;
        if (firstAdd) {
            holder = new ViewHolder();
            holder.all_layout = (RelativeLayout) view.findViewById(R.id.all_layout);
            holder.show_select = (ImageView) view.findViewById(R.id.show_select);
            holder.show_backImage = (ImageView) view.findViewById(show_backImage);
            holder.show_image = (ImageView) view.findViewById(R.id.show_image);
            holder.file_name = (TextView) view.findViewById(R.id.file_name);
            holder.file_create_date = (TextView) view.findViewById(R.id.file_create_date);

            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) holder.all_layout.getLayoutParams();
            params.width = MyApplication.ScreenWidth / 4;
            params.height = (MyApplication.ScreenHeight - 145) / 3;
            holder.all_layout.setLayoutParams(params);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        WritPadModel been = getItem(position);
        holder.show_image.setTag(been.saveCode);
        holder.show_backImage.setImageBitmap(null);
        if (been.getExtendModel()==null){
            holder.show_backImage.setTag("没有背景");
        }else {
            holder.show_backImage.setTag(been.getExtendModel().backgroundFilePath);
        }
        if (been.isFolder == -1) {
            holder.show_image.setScaleType(ImageView.ScaleType.CENTER);
            holder.show_image.setBackgroundResource(R.drawable.dotted_line_tray);
            holder.show_image.setImageResource(R.mipmap.write_add);
        } else if (been.isFolder == 1) {
            holder.show_image.setBackgroundResource(R.drawable.di_tray_bian_font_line1);
            holder.show_image.setScaleType(ImageView.ScaleType.FIT_XY);
            if (position == (getCount() - 1) || position == 0)
                holder.show_image.setImageBitmap(null);
            DbPhotoLoader.getInstance().loaderPhoto(been.saveCode, 0, holder.show_image);
//            LoadLocPhotoUtils.getPic(been.saveCode,0,holder.show_image);
            setBackImage(holder.show_backImage, been.getExtendModel().background, been.getExtendModel().backgroundFilePath);
        } else if (been.isFolder == 0) {
            holder.show_image.setBackgroundResource(R.color.colorWihte);
            holder.show_image.setScaleType(ImageView.ScaleType.FIT_CENTER);
            holder.show_image.setImageResource(R.mipmap.write_folder);
        }
        if (showAelect && !(been.isFolder == -1)) {
            holder.show_select.setVisibility(View.VISIBLE);
            if (isSelectWritePads.contains(been)) {
                holder.show_select.setImageResource(R.mipmap.have_select);
            } else {
                holder.show_select.setImageResource(R.mipmap.non_select);
            }
        } else {
            holder.show_select.setVisibility(View.INVISIBLE);
        }

//        holder.file_name.setText(been.name);
        elipseEnd(holder.file_name, been.name);
        if (been.isFolder != -1) {
            holder.file_create_date.setVisibility(View.VISIBLE);
            holder.file_create_date.setText(StringUtils.getStringDate(been.changeTime));
        } else {
            holder.file_create_date.setVisibility(View.INVISIBLE);
        }
    }

    private void elipseEnd(TextView textView, String value) {
        TextPaint paint = textView.getPaint();

        int bufferWidth = (int) paint.getTextSize() * 3;//缓冲区长度，空出两个字符的长度来给最后的省略号及图片
        // 计算出1行文字所能显示的长度
        int availableTextWidth =  MyApplication.ScreenWidth / 4 - bufferWidth;
        // 根据长度截取出剪裁后的文字
        String ellipsizeStr = (String) TextUtils.ellipsize(value, paint, availableTextWidth, TextUtils.TruncateAt.END);
        textView.setText(ellipsizeStr);
    }

    private void setBackImage(ImageView view, int style,String filepath) {
        if (style==-1&&(filepath==null||filepath.equals(""))){
            style=0;
        }
        if (!com.mx.mxbase.utils.StringUtils.isNull(filepath)&&!(new File(filepath)).exists()){
            style=0;
        }
        int resource = 0;
        switch (style) {
            case -1:
//                DbPhotoLoader.getInstance().locationPhoto(filepath,false, view, null);
                LocationImageLoader.getInstance().loadImage(filepath,view,null);
                break;
            case 1:
                resource = R.mipmap.image1;
                break;
            case 2:
                resource = R.mipmap.image2;
                break;
            case 3:
                resource = R.mipmap.image3;
                break;
            case 4:
                resource = R.mipmap.image4;
                break;
            case 5:
                resource = R.mipmap.image5;
                break;
            case 6:
                resource = R.mipmap.image6;
                break;
            case 7:
                resource = R.mipmap.image7;
                break;
            case 8:
                resource = R.mipmap.image8;
                break;
            case 9:
                resource = R.mipmap.image9;
                break;
            case 10:
                resource = R.mipmap.image10;
                break;
            case 11:
                resource = R.mipmap.image11;
                break;
            case 12:
                resource = R.mipmap.image12;
                break;
            case 13:
                resource = R.mipmap.image13;
                break;
            case 14:
                resource = R.mipmap.image14;
                break;
            case 15:
                resource = R.mipmap.image15;
                break;
            case 16:
                resource = R.mipmap.image16;
                break;
            default:
                break;
        }
        if (resource != 0)
            view.setImageResource(resource);
    }

    public class ViewHolder {
        RelativeLayout all_layout;
        ImageView show_select;
        ImageView show_backImage;
        ImageView show_image;
        TextView file_name;
        TextView file_create_date;
    }

    /**
     * 更新文件名
     *
     * @param position
     * @param listView
     */
    public void updateFileName(int position, GridView listView) {
        int visibleFirstPosi = listView.getFirstVisiblePosition();
        int visibleLastPosi = listView.getLastVisiblePosition();
        if (position >= visibleFirstPosi && position <= visibleLastPosi) {
            View view = listView.getChildAt(position - visibleFirstPosi);
            TextView file_name = (TextView) view.findViewById(R.id.file_name);
            file_name.setText(getList().get(position).name);
        }
    }

    /**
     * 更新选中
     *
     * @param position
     * @param listView
     */
    public void updateSelect(int position, GridView listView) {
        int visibleFirstPosi = listView.getFirstVisiblePosition();
        int visibleLastPosi = listView.getLastVisiblePosition();
        if (position >= visibleFirstPosi && position <= visibleLastPosi) {
            View view = listView.getChildAt(position - visibleFirstPosi);
            ImageView select_image = (ImageView) view.findViewById(R.id.show_select);
            boolean isTrue = isSelectWritePads.contains(getItem(position));
            if (isTrue) {
                select_image.setImageResource(R.mipmap.have_select);
            } else {
                select_image.setImageResource(R.mipmap.non_select);
            }
        }
    }

    /**
     * 更新所有选中
     */
    public void updateAllSelect(GridView listView) {
        int visibleFirstPosi = listView.getFirstVisiblePosition();
        int visibleLastPosi = listView.getLastVisiblePosition();
        for (int i = visibleFirstPosi; i <= visibleLastPosi; i++) {
            View view = listView.getChildAt(i);
            ImageView select_image = (ImageView) view.findViewById(R.id.show_select);
            if (getItem(i).id == -1) {
                select_image.setVisibility(View.INVISIBLE);
                continue;
            }
            select_image.setVisibility(showAelect ? View.VISIBLE : View.INVISIBLE);
            boolean isTrue = isSelectWritePads.contains(getItem(i));
            if (isTrue) {
                select_image.setImageResource(R.mipmap.have_select);
            } else {
                select_image.setImageResource(R.mipmap.non_select);
            }
        }
    }
}
