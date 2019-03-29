package com.moxi.writeNote.sortUtils;

import com.moxi.handwritinglibs.db.WritPadModel;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by xj on 2017/9/25.
 */

public class SortName implements Comparator<WritPadModel> {
    /**
     * 初次顺序排序
     */
    @Override
    public int compare(WritPadModel lhs, WritPadModel rhs) {
        if (lhs == null && rhs == null) return 0;
        if (lhs == null || rhs == null) return lhs == null ? 1 : -1;

        if (lhs.isFolder==-1){
            return -1;
        }else if (rhs.isFolder==-1){
            return 1;
        }
        if (lhs.isFolder==0||rhs.isFolder==0){
            if (lhs.isFolder==0&&rhs.isFolder==1)return -1;
            if (lhs.isFolder==1&&rhs.isFolder==0)return 1;
        }
        String name1 = lhs.name;
        String name2 = rhs.name;
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

    private List<String> getNumbers(String str) {
        List<String> list = new ArrayList<String>();
        Pattern p = Pattern.compile("\\d+");
        Matcher m = p.matcher(str);
        while (m.find()) {
            list.add(m.group());
        }
        return list;
    }
}
