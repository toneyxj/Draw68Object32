package com.mx.mxbase.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by xj on 2018/1/2.
 */

public class RegHtml {

    private static final String regEx_script = "<script[^>]*?>[\\s\\S]*?<\\/script>"; // 定义script的正则表达式
    private static final String regEx_style = "<style[^>]*?>[\\s\\S]*?<\\/style>"; // 定义style的正则表达式
    private static final String regEx_html = "<[^>]+>"; // 定义HTML标签的正则表达式
    private static final String regEx_space = "\\s*|\t|\r|\n";// 定义空格回车换行符
    private static final String regEx_w = "<w[^>]*?>[\\s\\S]*?<\\/w[^>]*?>";//定义所有w标签
    private static final String img = "<img.*?>";//定义所有w标签<(img|IMG)[^\\<\\>]*>


    /**
     * @param htmlStr
     * @return 删除Html标签
     * @author LongJin
     */
    public static String delHTMLTag(String htmlStr, String tag) {
        if (tag.equals("w")) {
            Pattern p_w = Pattern.compile(regEx_w, Pattern.CASE_INSENSITIVE);
            Matcher m_w = p_w.matcher(htmlStr);
            htmlStr = m_w.replaceAll(""); // 过滤script标签
        }
        if (tag.equals("img")) {
            Pattern pattern = Pattern.compile(img);
            Matcher matcher = pattern.matcher(htmlStr);//sendString为网页源码
            htmlStr = matcher.replaceAll("");
        }
        if (tag.equals("script")) {
            Pattern p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);
            Matcher m_script = p_script.matcher(htmlStr);
            htmlStr = m_script.replaceAll(""); // 过滤script标签
        }
        if (tag.equals("style")) {
            Pattern p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);
            Matcher m_style = p_style.matcher(htmlStr);
            htmlStr = m_style.replaceAll(""); // 过滤style标签
        }
        if (tag.equals("html")) {
            Pattern p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
            Matcher m_html = p_html.matcher(htmlStr);
            htmlStr = m_html.replaceAll(""); // 过滤html标签
        }
        if (tag.equals("regEx_space")) {
            Pattern p_space = Pattern.compile(regEx_space, Pattern.CASE_INSENSITIVE);
            Matcher m_space = p_space.matcher(htmlStr);
            htmlStr = m_space.replaceAll(""); // 过滤空格回车标签
        }
        if (tag.equals(" ")) {
            htmlStr = htmlStr.replaceAll(" ", ""); //过滤
        }
        return htmlStr.trim(); // 返回文本字符串
    }
}
