package com.mx.mxbase.interfaces;

import android.view.View;

/**
 * 点击回调接口
 * Created by Archer on 16/8/1.
 */
public interface OnItemClickListener {

    void onItemClick(View view, int position);

    void onItemLongClick(View view, int position);
}
