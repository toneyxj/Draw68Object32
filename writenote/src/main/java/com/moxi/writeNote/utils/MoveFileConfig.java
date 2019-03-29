package com.moxi.writeNote.utils;

import com.moxi.writeNote.Model.SimpleWriteModel;

import java.util.List;

/**
 * 文件移动保存
 * Created by 夏君 on 2017/2/28.
 */

public class MoveFileConfig {
    // 初始化类实列
    private static MoveFileConfig instatnce = null;

    /**
     * 获得软键盘弹出类实列
     *
     * @return 返回初始化实列
     */
    public static MoveFileConfig getInstance() {
        if (instatnce == null) {
            synchronized (MoveFileConfig.class) {
                if (instatnce == null) {
                    instatnce = new MoveFileConfig();
                }
            }
        }
        return instatnce;
    }
    /**
     * 是否拥有事件
     */
    public boolean isHaveEvent = false;
    /**
     * 事件操作文件集合
     */
    private List<SimpleWriteModel> simpleWriteModels;
    private boolean isMove=false;

    /**
     * 初始化数据
     *
     * @param simpleWriteModels 操作文件集合
     */
    public void init( List<SimpleWriteModel> simpleWriteModels,boolean isMove) {
        this.isHaveEvent = true;
        this.simpleWriteModels = simpleWriteModels;
        this.isMove=isMove;
    }

    /**
     * 清除操作
     */
    public void ClearMove() {
        this.isHaveEvent = false;
        if (simpleWriteModels!=null){
            simpleWriteModels.clear();
        }
        isMove=false;
        this.simpleWriteModels = null;
    }

    public List<SimpleWriteModel> getSimpleWriteModels() {
        return simpleWriteModels;
    }
    public String getParentCode(){
        if (simpleWriteModels==null||simpleWriteModels.size()==0)return null;
        return simpleWriteModels.get(0).parentCode;
    }

    /**
     * 是否正在移动
     * @return
     */
    public boolean isMove() {
        return isMove;
    }

}
