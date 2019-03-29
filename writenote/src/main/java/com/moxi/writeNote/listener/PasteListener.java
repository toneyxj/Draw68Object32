package com.moxi.writeNote.listener;

/**
 * 文件粘贴监听
 * Created by 夏君 on 2017/2/28.
 */

public interface PasteListener  {
    /**
     * 粘贴
     * @param is 是否粘贴
     */
    void onPaste(boolean is,String hitn);
}
