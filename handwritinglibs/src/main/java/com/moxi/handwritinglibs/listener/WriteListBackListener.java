package com.moxi.handwritinglibs.listener;

import com.moxi.handwritinglibs.db.WritPadModel;

import java.util.List;

/**
 * Created by xj on 2018/9/7.
 */

public interface WriteListBackListener {
    void onListBack(List<WritPadModel> list);
}
