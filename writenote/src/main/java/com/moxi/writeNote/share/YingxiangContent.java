package com.moxi.writeNote.share;

import com.mx.mxbase.utils.FileUtils;
import com.mx.mxbase.utils.StringUtils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 印象笔记装换
 * Created by Administrator on 2019/3/1.
 */
public class YingxiangContent implements ContentBuilderInterface<String> {
    /**
     * 保存笔记记录到文件-防止bundle过大而出现报错问题
     * @param list 数据列表
     * @param bj
     * @return
     */
    @Override
    public void getContent(final List<String> list, final Object bj, final ShareCallBack onShare) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean is=false;
                String wj = null;
                StringBuilder builder=new StringBuilder();
                for (String wrapper : list) {
                    builder.append(ContentUtils.getSingContent(wrapper));
                }
                builder.append(ContentUtils.getMaxLine());
                builder.append(ContentUtils.getBottomSource("手写备忘录"));

                //保存临时文件文件到本地sd卡
                try {
                     wj= StringUtils.getSDPath()+"wssclswj";
                    is= FileUtils.getInstance().writeFile(wj,builder.toString());
                } catch (IOException e) {
                    is=false;
                    e.printStackTrace();
                }
                if(onShare!=null)onShare.shareSavePath(is,wj);
                
            }
        }).start();

    }

    /**
     * 把long 转换成 日期 再转换成String类型
     */
    public String transferLongToDate( Long millSec) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(millSec);
        return sdf.format(date);
    }

}
