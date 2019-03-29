package com.moxi.writeNote.listener;

import java.io.File;
import java.util.List;

/**
 * 数据加载监听
 * Created by xj on 2017/11/21.
 */

public interface LoadingListener {
    void onLoadingSucess(List<File> files);
}
