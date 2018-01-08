package com.example.appforsql.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangyue on 2016/7/13.
 */
public class ListUtils {
    public ListUtils() { throw new UnsupportedOperationException("cannot be instantiated");/* compiled code */ }

    public static boolean isEmpty(List list) {
        return list == null || list.size() == 0;
    }

    public static boolean notEmpty(List list) {
        return list != null && list.size() > 0;
    }
    /**
     * 将list数据转成string
     *
     * @param list
     * @return
     */
    public static String getStr(List<String> list) {
        String string = list.get(0);
        for (int i = 1; i < list.size(); i++) {
            string = string + "," + list.get(i);
        }
        return string;
    }
    /**
     * 将string数据转成list
     *
     * @param list
     * @return
     */
    public static List<String> getList(String list) {
        String[] arr = list.split(",");
        List<String> lists = new ArrayList<String>();
        for (String str : arr)
        {
            lists.add(str);
        }
        return lists;
    }
}
