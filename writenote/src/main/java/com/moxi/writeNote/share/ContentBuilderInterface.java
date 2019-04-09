package com.moxi.writeNote.share;

import java.util.List;

/**
 * 文字内容生成器
 * Created by Administrator on 2019/3/1.
 */
public interface ContentBuilderInterface<Content> {
    /**
     * 保存笔记记录到文件-防止bundle过大而出现报错问题
     * @param list 数据列表
     * @param bj 其余数据指引类
     * @return 保存笔记记录到文件路径
     */
    void getContent(List<Content> list, Object bj, ShareCallBack onShare);
}
