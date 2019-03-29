package com.moxi.writeNote.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by xj on 2017/6/23.
 */

public class BrushSettingUtils {

    private static BrushSettingUtils instatnce = null;
    /**
     * 小型数据库读取
     */
    private   SharedPreferences preferences;
    /**
     * 小型数据库写入
     */
    private   SharedPreferences.Editor editor;
    /**
     * 获得软键盘弹出类实列
     *
     * @return 返回初始化实列
     */
    public static BrushSettingUtils getInstance(Context context) {
        if (instatnce == null) {
            synchronized (BrushSettingUtils.class) {
                if (instatnce == null) {
                    instatnce = new BrushSettingUtils();
                    instatnce.initData(context);
                }
            }
        }
        return instatnce;
    }
    private List<Integer> clears=new ArrayList<Integer>();
    private List<Integer> lines=new ArrayList<Integer>();
    private void initData(Context context){
        // 初始化小型数据库的读写
        preferences = context.getSharedPreferences("brush", MODE_PRIVATE);
        editor = preferences.edit();

        clears.add(5);
        clears.add(10);
        clears.add(15);
        clears.add(20);
        clears.add(15);
        clears.add(30);

        lines.add(5);
        lines.add(7);
        lines.add(9);
        lines.add(11);
        lines.add(13);
        lines.add(15);
    }

    public int getRubberIndexSize(int index){
        int size=clears.get(index);
        setRubberSize(size);
        return size;
    }
    public int getDrawLineIndexSize(int index){
        int size=lines.get(index);
        setDrawLineSize(size);
        return size;
    }

    /**
     * 获得橡皮差宽度
     * @return
     */
    public int getRubberSize(){
        return preferences.getInt("rubberSize",11);
    }
    public void setRubberSize(int size){
        editor.putInt("rubberSize",size);
        editor.commit();
    }

    /**
     * 获得橡皮差选中点index
     * @return
     */
    public int pitchOnRubberIndex(){
        int size=getRubberSize();
        int index=6;//设置的宽度
        if (size == clears.get(0)) {
            index = 0;
        } else if (size == clears.get(1)) {
            index = 1;
        } else if (size == clears.get(2)) {
            index = 2;
        } else if (size == clears.get(3)) {
            index = 3;
        } else if (size == clears.get(4)) {
            index = 4;
        } else if (size == clears.get(5)) {
            index = 5;
        }
        return index;
    }
    /**
     * 获得绘制线差宽度
     * @return
     */
    public int getDrawLineSize(){
        return preferences.getInt("drawLine",2);
    }
    public void setDrawLineSize(int size){
        editor.putInt("drawLine",size);
        editor.commit();
    }

    /**
     * 获得绘制线选中点index
     * @return
     */
    public int pitchdrawLineIndex(){
        int size=getDrawLineSize();
        int index=6;//设置的宽度
        if (size == lines.get(0)) {
            index = 0;
        } else if (size == lines.get(1)) {
            index = 1;
        } else if (size == lines.get(2)) {
            index = 2;
        } else if (size == lines.get(3)) {
            index = 3;
        } else if (size == lines.get(4)) {
            index = 4;
        } else if (size == lines.get(5)) {
            index = 5;
        }
        return index;
    }

}
