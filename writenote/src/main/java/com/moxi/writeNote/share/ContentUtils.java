package com.moxi.writeNote.share;

/**
 * Created by Administrator on 2019/3/1.
 */

public class ContentUtils {
    public static  final String NoteShareDescribe="topsir电子书 手写备忘录";
    /**
     * 获得章节标题栏
     * @param title
     * @return
     */
    public static String  getChapter(String title){
        return"<h1 style=\"font-weight: bold; font-size: 1.58em; margin-top: 2.4em;font-weight: bold; font-size: 1.58em; margin-top: 2.4em;\">"+title+"</h1>";
    }

    /**
     * 获得标记时间
     * @param time
     * @return
     */
    public static String getSingtime(String time){
        return "<p style=\"font-size: 0.82em; color: #888888; border-left: solid 0.42em #e04149; padding-left: 0.5em; margin-top: 2.6em;font-size: 0.82em; color: #888888; border-left: solid 0.42em #e04149; padding-left: 0.5em; margin-top: 2.6em;\">"+time+"</p>";
    }

    /**
     * 获得标记内容显示格式
     * @param sing
     * @return
     */
    public static String getSingContent(String sing){
        return "<p style=\"font-size: 1.1em;font-size: 1.1em;\">"+sing+"</p>";
    }

    /**
     * 获得输入笔记显示
     * @param note
     * @return
     */
    public static String getWriteNote(String note){
        return "<p style=\"color: #555555; font-size: 0.96em;color: #555555; font-size: 0.96em;\"><span style=\"color: #888888;color: #888888;\">注</span><span style=\"color: #a9a9a9;color: #a9a9a9;\"> | </span>"+note+"</p>";
    }

    /**
     * 获得分割线
     * @return
     */
    public static String getMaxLine(){
        return "<hr style=\"margin-top: 3.3em; border-style: solid none none none; border-top: solid 2px #dddddd;margin-top: 3.3em; border-style: solid none none none; border-top: solid 2px #dddddd;\"/>";
    }
    public static String getMinLine(){
        return "<hr style=\"border-style: dotted none none none; border-top: dotted 1px #d3d3d3;border-style: dotted none none none; border-top: dotted 1px #d3d3d3;\"/>";
    }

    /**
     * 获得来源标记
     * @return
     */
    public static String getBottomSource(String appname){
        return "<p style=\"margin-top: 1.25em; color: #666666; font-size: 0.82em;margin-top: 1.25em; color: #666666; font-size: 0.82em;\">Topsir电子书笔记 来自"+appname+" </p>";
    }
}
