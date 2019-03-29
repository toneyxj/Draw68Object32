package com.moxi.writeNote.utils;

import android.os.AsyncTask;
import android.os.Environment;

import com.moxi.writeNote.listener.LoadingListener;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 文件获取
 * Created by xj on 2017/11/21.
 */

public class FileObtainAsy extends AsyncTask<String , Void, List<File>> {
    private LoadingListener listener;
    private File[] files;
    private boolean isDir;
    public static final  String E_DOWNLOAD_DIR= Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"DDBooks";

    /**
     * @param files  文件集合
     * @param isDir 是不是只获取dir
     * @param listener  监听
     */
    public FileObtainAsy(File[] files,boolean isDir, LoadingListener listener) {
        this.listener=listener;
        this.files=files;
        this.isDir=isDir;
    }

    @Override
    protected List<File>  doInBackground(String... arg0) {
        List<File> fls=new ArrayList<File>();
        if (files!=null){
            for (int i = 0; i < files.length; i++) {
                if (files[i].getName().substring(0, 1).equals(".")||files[i].getName().equals("com.onyx.android.data")) continue;
                if (files[i].getAbsolutePath().equals(E_DOWNLOAD_DIR)) continue;
                if (!files[i].canRead()) continue;
                if (isDir) {
                    if (files[i].isDirectory()){
                        fls.add(files[i]);
                    }
                }else {
                    fls.add(files[i]);
                }
            }
        }
        Collections.sort(fls, new NameComparator());
        Collections.sort(fls, new MdirOrFileComparator());

        return fls;
    }

    @Override
    protected void onPostExecute(List<File> fls) {
        if (listener!=null)listener.onLoadingSucess(fls);
    }
    /**
     * 按文件夹与文件排序
     */
    private static class MdirOrFileComparator implements Comparator<File> {

        @Override
        public int compare(File lhs, File rhs) {
            if (lhs.isFile() && rhs.isDirectory()) {
                return 1;
            } else if (lhs.isDirectory() && rhs.isFile()) {
                return -1;
            }
            return 0;
        }
    }
    /**
     * 初次顺序排序
     */
    private static class NameComparator implements Comparator<File> {
        @Override
        public int compare(File lhs, File rhs) {
            if (lhs == null && rhs == null) return 0;
            if (lhs == null || rhs == null) return lhs == null ? 1 : -1;

            String name1 = lhs.getName();
            String name2 = rhs.getName();
            if (name1 == null && name2 == null) {
                return 0;
            } else if (name1 == null && name2 != null) {
                return 1;
            } else if (name1 != null && name2 == null) {
                return -1;
            } else if (name1.equals("") && !name2.equals("")) {
                return 1;
            } else if (!name1.equals("") && name2.equals("")) {
                return -1;
            }
            List<String> list1 = getNumbers(name1);
            List<String> list2 = getNumbers(name2);
            if (list1.size() > 0 && list2.size() > 0) {
                String[] text1 = name1.split("\\d+");
                String[] text2 = name2.split("\\d+");
                if ((text1.length == 0 && text2.length == 0)
                        || (text1.length != 0 && text2.length == 0 && text1[0].equals(""))
                        || (text1.length == 0 && text2.length != 0 && text2[0].equals(""))) {
                    String num1 = list1.get(0);
                    String num2 = list2.get(0);
                    if (num1.length() != num2.length()) {
                        return num1.length() > num2.length() ? 1 : -1;
                    }

                    int type = name1.compareTo(name2);
                    if (type > 0) {
                        return 1;
                    } else if (type < 0) {
                        return -1;
                    }
                }

                int len = text1.length > text2.length ? text2.length : text1.length;

                for (int i = 0; i < len; i++) {
                    if (text1[i].equals(text2[i]) && (i < list1.size() && i < list2.size())) {
                        String num1 = list1.get(i);
                        String num2 = list2.get(i);
                        if (num1.length() != num2.length()) {
                            return num1.length() > num2.length() ? 1 : -1;
                        }
                        int type = name1.compareTo(name2);
                        if (type > 0) {
                            return 1;
                        } else if (type < 0) {
                            return -1;
                        }
                    }
                }
            }
            int type = name1.compareTo(name2);
            if (type > 0) {
                return 1;
            } else if (type < 0) {
                return -1;
            }
            return 0;
        }
    }

    public static List<String> getNumbers(String str) {
        List<String> list = new ArrayList<String>();
        Pattern p = Pattern.compile("\\d+");
        Matcher m = p.matcher(str);
        while (m.find()) {
            list.add(m.group());
        }
        return list;
    }
}

