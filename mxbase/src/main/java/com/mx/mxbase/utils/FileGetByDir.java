package com.mx.mxbase.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Archer on 16/8/4.
 */
public class FileGetByDir {

    public static List<File> listFiles = new ArrayList<File>();

    public static List<File> getFileDir(String filePath) {
        if (listFiles.size() > 0) {
            listFiles.clear();
        }
        try {
            File f = new File(filePath);
            File[] files = f.listFiles();// 列出所有文件
            // 如果不是根目录,则列出返回根目录和上一目录选项
            if (files != null) {
                int count = files.length;// 文件个数
                for (int i = 0; i < count; i++) {
                    File file = files[i];
                    if (!file.isDirectory()) {
                        listFiles.add(file);
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return listFiles;
    }
}
