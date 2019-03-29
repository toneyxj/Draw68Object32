package com.moxi.writeNote.settingPage;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.moxi.writeNote.R;


/**
 * Created by King on 2017/12/15.
 */

public class PassWordKeyboard extends RelativeLayout {
    private Context context;
    private GridView gvKeyboard;

    private String[] key;
    private OnClickKeyboardListener onClickKeyboardListener;

    public PassWordKeyboard(Context context) {
        this(context, null);
    }

    public PassWordKeyboard(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PassWordKeyboard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    /**
     * 初始化键盘的点击事件
     */
    private void initEvent() {
        gvKeyboard.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (onClickKeyboardListener != null && position >= 0) {
                    onClickKeyboardListener.onKeyClick(position, key[position]);
                }
            }
        });
    }

    /**
     * 初始化KeyboardView
     */
    private void initKeyboardView() {
        View view = View.inflate(context, R.layout.view_keyboard, this);
        gvKeyboard = (GridView) view.findViewById(R.id.gv_keyboard);
        gvKeyboard.setAdapter(keyboardAdapter);
        initEvent();
    }

    public interface OnClickKeyboardListener {
        void onKeyClick(int position, String value);
    }

    /**
     * 对外开放的方法
     *
     * @param onClickKeyboardListener
     */
    public void setOnClickKeyboardListener(OnClickKeyboardListener onClickKeyboardListener) {
        this.onClickKeyboardListener = onClickKeyboardListener;
    }

    /**
     * 设置键盘所显示的内容
     *
     * @param key
     */
    public void setKeyboardKeys(String[] key) {
        this.key = key;
        initKeyboardView();
    }

    private BaseAdapter keyboardAdapter = new BaseAdapter() {
        private static final int KEY_NINE = 9;

        @Override
        public int getCount() {
            return key.length;
        }

        @Override
        public Object getItem(int position) {
            return key[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public int getItemViewType(int position) {
            return (getItemId(position) == KEY_NINE) ? 2 : 1;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.item_grid_keyboard, parent, false);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            }
            viewHolder = (ViewHolder) convertView.getTag();
            viewHolder.tvKey.setText(key[position]);
            return convertView;
        }
    };

    /**
     * ViewHolder,view缓存
     */
    static class ViewHolder {
        private TextView tvKey;

        public ViewHolder(View view) {
            tvKey = (TextView) view.findViewById(R.id.tv_keyboard_keys);
            view.setTag(this);
        }
    }
}
