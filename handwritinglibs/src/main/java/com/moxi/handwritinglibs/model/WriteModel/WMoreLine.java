package com.moxi.handwritinglibs.model.WriteModel;

import java.util.ArrayList;
import java.util.List;

/**
 * 多条线组合成一个绘制数据包
 * Created by xj on 2018/7/16.
 */

public class WMoreLine {
    /**
     * 多条线组合成一个数据包
     */
    public List<WLine> MoreLines;
    /**
     * 绘制线0；删除线1
     */
    public int status=0;

    /**
     * 获得当前数据状态，如果是绘制线返回true，否则代表是删除线
     * @return
     */
    public boolean isLineStatus(){
        return status==0;
    }
    /**
     * 获得当前数据状态，如果是绘制线返回true，否则代表是删除线
     * @return
     */
    public boolean ChangeLineStatus(){
        if (status==0){
            status=1;
        }else {
            status=0;
        }
        return status==0;
    }

    public void setMoreLines(List<WLine> moreLines) {
        MoreLines = moreLines;
    }

    public WMoreLine(List<WLine> moreLines, int status) {
        MoreLines = moreLines;
        this.status = status;
    }

    public List<WLine> getMoreLines() {
        if (MoreLines==null)MoreLines=new ArrayList<WLine>();
        return MoreLines;
    }

    /**
     * 默认绘制线
     * @param wl
     */
    public WMoreLine(WLine wl) {
        MoreLines=new ArrayList<WLine>();
        MoreLines.add(wl);
    }
    public WMoreLine(WLine wl, int status) {
        MoreLines=new ArrayList<WLine>();
        MoreLines.add(wl);
        this.status = status;
    }

    public void isNull(){
        for (int i = 0; i < MoreLines.size(); i++) {
            MoreLines.get(i).isNull();
        }

    }

    @Override
    public String toString() {
        return "WMoreLine{" +
                "MoreLines=" + MoreLines +
                ", status=" + status +
                '}';
    }
    public int getSize(){
        int size=12;
        if (MoreLines!=null){
            for (int i = 0; i < MoreLines.size(); i++) {
                size += MoreLines.get(i).getSize();
            }
        }
        return size;
    }
}
