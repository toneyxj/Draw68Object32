package com.moxi.handwritinglibs.model.WriteModel;

import com.moxi.handwritinglibs.writeUtils.PenUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 一页手写信息的数据集合
 * Created by xj on 2018/7/16.
 */

public class WritePageData {
    /**
     * 缓冲线段 处理数据的返回和退出默认设置30步
     */
    public List<WMoreLine> drawMiddleLines;
    /**
     * 数据读取和保存最后实现的类；
     */
    public List<WLine> mainLines;
    public void nullInit(){
        if(drawMiddleLines==null)drawMiddleLines=new ArrayList<WMoreLine>();
        if(mainLines==null)mainLines=new ArrayList<WLine>();
    }

    public String getSaveDate(){
        for (int i = 0; i < drawMiddleLines.size(); i++) {
            drawMiddleLines.get(i).isNull();
        }
        for (int i = 0; i < mainLines.size(); i++) {
            mainLines.get(i).isNull();
        }
        synchronized (this) {
            return PenUtils.getWritePageDate(this);
        }
    }

    public void isNull(){
        nullInit();
        for (int i = 0; i < drawMiddleLines.size(); i++) {
            drawMiddleLines.get(i).isNull();
        }
        for (int i = 0; i < mainLines.size(); i++) {
            mainLines.get(i).isNull();
        }
    }
    public int getSize(){
        int size=8;
        if (drawMiddleLines!=null){
            for (int i = 0; i < drawMiddleLines.size(); i++) {
                size += drawMiddleLines.get(i).getSize();
            }
        }
        if (mainLines!=null){
            for (int i = 0; i < mainLines.size(); i++) {
                size += mainLines.get(i).getSize();
            }
        }
        return size;
    }
    public boolean dataNull(){
        nullInit();
        return drawMiddleLines.size()==0&&mainLines.size()==0;
    }
}
