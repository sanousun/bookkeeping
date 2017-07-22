package com.sz.calendar.util;

/**
 * Created with Android Studio.
 * User: dashu
 * Date: 2017/2/9
 * Time: 下午8:03
 * Desc: 字符串相关的工具类
 */

@SuppressWarnings("unused")
public class StringUtils {
    /**
     * 不支持实例
     */
    private StringUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * 对字符串进行空判断
     *
     * @param str 目标字符串
     * @return 布尔值
     */
    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

    /**
     * 对字符串进行非空判断
     *
     * @param str 目标字符串
     * @return 布尔值
     */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }
}