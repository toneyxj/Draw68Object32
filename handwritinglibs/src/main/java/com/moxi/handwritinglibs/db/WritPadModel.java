package com.moxi.handwritinglibs.db;

import com.moxi.handwritinglibs.model.ExtendModel;
import com.moxi.handwritinglibs.utils.DataUtils;

import org.litepal.annotation.Column;
import org.litepal.crud.DataSupport;

import java.io.Serializable;

/**
 * 手写板数据保存类
 * Created by 夏君 on 2017/2/9.
 */
public class WritPadModel extends DataSupport implements Serializable{
    @Column(unique = true)
    public long id;
    /**
     * 手写备忘文件名称
     */
    public String name="save";
    /**
     * 保存的唯一标示
     */
    public String saveCode;
    /**
     * 是否是文件夹,0为文件夹，1为文件,-1新建文件
     */
    public int isFolder=1;
    /**
     * 父分类标示
     */
    public String parentCode="-1";
    /**
     * 当前数据分页索引保存
     */
    public int _index=0;
    /**
     * 图片内容
     */
    public String imageContent;
    /**
     * 扩展字段
     */
    public String extend="0";
    /**
     * 文件修改时间
     */
    public Long changeTime;
    /**
     * 手写页面别名
     */
    public String pageName="";
    /**
     * 获取手写单个界面名称
     * @return
     */
    public String getPageName(){
        if (isFolder==1){
            if (null!=pageName&&!pageName.isEmpty()){
                return pageName;
            }else {
                return "添加手写页标识";
            }
        }
        return name;
    }

    /**
     * 普通绘制文件保存
     * @param name 绘制图片取名，也可以填""
     * @param saveCode 保存的唯一标示，依据此进行数据的查询
     * @param imageContent 图片信息装换成String，后保存
     * @param changeTime ，文件改变时或创建时间
     */
    public WritPadModel( String name, String saveCode, String imageContent, Long changeTime) {
        this.name = name;
        this.saveCode = saveCode;
        this.imageContent = imageContent;
        this.changeTime = changeTime;
    }

    /**
     * 专业手写板绘制构造方法
     * @param name 手写输入的名称
     * @param saveCode 保存的唯一标示，依据此进行数据的查询
     * @param isFolder 是否是文件夹
     * @param parentCode 父文件夹路径信息
     * @param index 保存可能一个文件多张手写纸，保存手写文件索引值0开始
     * @param extend 扩展字段
     * @param imageContent 图片信息装换成String，后保存
     * @param changeTime 文件改变时或创建时间
     */
    public WritPadModel(long id,String name, String saveCode, int isFolder, String parentCode,
                        int index,String extend, String  imageContent, Long changeTime,String pageName) {
        this.id=id;
        this.name = name;
        this.saveCode = saveCode;
        this.isFolder = isFolder;
        this.parentCode = parentCode;
        this._index = index;
        this.extend=extend;
        this.imageContent = imageContent;
        this.changeTime = changeTime;
        this.pageName=pageName;
    }
    /**
     * 专业手写板绘制构造方法
     * @param name 手写输入的名称
     * @param saveCode 保存的唯一标示，依据此进行数据的查询
     * @param isFolder 是否是文件夹
     * @param parentCode 父文件夹路径信息
     * @param index 保存可能一个文件多张手写纸，保存手写文件索引值0开始
     * @param extend 扩展字段
     * @param imageContent 图片信息装换成String，后保存
     */
    public WritPadModel(String name, String saveCode, int isFolder, String parentCode,
                        int index,String extend, String imageContent) {
        this.name = name;
        this.saveCode = saveCode;
        this.isFolder = isFolder;
        this.parentCode = parentCode;
        this._index = index;
        this.extend=extend;
        this.imageContent = imageContent;
        this.changeTime = System.currentTimeMillis();
    }
    /**
     * 专业手写板绘制构造方法
     * @param name 手写输入的名称
     * @param saveCode 保存的唯一标示，依据此进行数据的查询
     * @param isFolder 是否是文件夹
     * @param parentCode 父文件夹路径信息
     * @param index 保存可能一个文件多张手写纸，保存手写文件索引值0开始
     * @param extend 扩展字段
     */
    public WritPadModel(String name, String saveCode, int isFolder, String parentCode,
                        int index,String extend) {
        this.name = name;
        this.saveCode = saveCode;
        this.isFolder = isFolder;
        this.parentCode = parentCode;
        this._index = index;
        this.extend=extend;
        this.changeTime = System.currentTimeMillis();
    }

    /**
     * 设置扩展字段
     * @param model
     */
    public void setExtend(ExtendModel model) {
        this.extend = DataUtils.getExtendStr(model);
    }

    /**
     * 获得笔记扩展字段
     * @return 返回扩展字段
     */
    public ExtendModel getExtendModel(){
        return DataUtils.getExtendModel(extend);
    }

    @Override
    public String toString() {
        return "WritPadModel{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", saveCode='" + saveCode + '\'' +
                ", isFolder=" + isFolder +
                ", parentCode='" + parentCode + '\'' +
                ", _index=" + _index +
                ", imageContent='" + imageContent + '\'' +
                ", extend='" + extend + '\'' +
                ", changeTime=" + changeTime +
                '}';
    }
}
