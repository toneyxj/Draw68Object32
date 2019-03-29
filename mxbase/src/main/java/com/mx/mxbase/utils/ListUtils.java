package com.mx.mxbase.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Archer on 16/8/3.
 */
public class ListUtils {
    /**
     * list 分页
     *
     * @param list     list数据源
     * @param pageSize 页码大小
     * @param <T>
     * @return
     */
    public static <T> List<List<T>> splitList(List<T> list, int pageSize) {
        if (pageSize > 1) {
            int listSize = list.size();
            int page = (listSize + (pageSize - 1)) / pageSize;
            List<List<T>> listArray = new ArrayList<List<T>>();
            listArray.clear();
            for (int i = 0; i < page; i++) {
                List<T> subList = new ArrayList<T>();
                for (int j = 0; j < listSize; j++) {
                    int pageIndex = ((j + 1) + (pageSize - 1)) / pageSize;
                    if (pageIndex == (i + 1)) {
                        subList.add(list.get(j));
                    }
                    if ((j + 1) == ((j + 1) * pageSize)) {
                        break;
                    }
                }
                listArray.add(subList);
            }
            return listArray;
        } else {
            int listSize = list.size();
            int page = (listSize + (pageSize - 1)) / pageSize;
            List<List<T>> listArray = new ArrayList<List<T>>();
            listArray.clear();
            for (int i = 0; i < page; i++) {
                List<T> subList = new ArrayList<T>();
                for (int j = i; j < listSize; j++) {
                    subList.add(list.get(i));
                    break;
                }
                listArray.add(subList);
            }
            return listArray;
        }
    }
}
