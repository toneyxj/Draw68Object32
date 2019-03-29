package com.moxi.handwritinglibs.listener;

/**
 * 导出文件监听
 * Created by 夏君 on 2017/2/28.
 */

public interface ExportListener {
    /**
     * 文件导出
     * @param is 是否导出成功
     */
    void onExport(boolean is);

    /**
     * 导出进度提示
     * @param hitn
     */
    void onExportHitn(String hitn);
}
