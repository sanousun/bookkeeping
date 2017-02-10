package com.sz.bookkeeping.util;

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
     * 将字符串转换为布尔值
     *
     * @param str      目标字符串
     * @param fallback 默认值
     * @return 布尔值
     */
    public static boolean parseBoolean(String str, boolean fallback) {
        if (str == null) {
            return fallback;
        }
        return Boolean.parseBoolean(str);
    }

    /**
     * 将字符串转换为字节
     *
     * @param str      目标字符串
     * @param fallback 默认值
     * @return 字节
     */
    public static byte parseByte(String str, byte fallback) {
        try {
            return Byte.parseByte(str);
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    /**
     * 将字符串转换为Short
     *
     * @param str      目标字符串
     * @param fallback 默认值
     * @return Short型数值
     */
    public static short parseShort(String str, short fallback) {
        try {
            return Short.parseShort(str);
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    /**
     * 将字符串转换为Int
     *
     * @param str      目标字符串
     * @param fallback 默认值
     * @return Int型数值
     */
    public static int parseInt(String str, int fallback) {
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    /**
     * 将字符串转换为Long
     *
     * @param str      目标字符串
     * @param fallback 默认值
     * @return Long型数值
     */
    public static long parseLong(String str, long fallback) {
        try {
            return Long.parseLong(str);
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    /**
     * 将字符串转换为Float
     *
     * @param str      目标字符串
     * @param fallback 默认值
     * @return Float型数值
     */
    public static float parseFloat(String str, float fallback) {
        try {
            return Float.parseFloat(str);
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    /**
     * 将字符串转换为Double
     *
     * @param str      目标字符串
     * @param fallback 默认值
     * @return Double型数值
     */
    public static double parseDouble(String str, double fallback) {
        try {
            return Double.parseDouble(str);
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    /**
     * 取字符串的第一个字符
     *
     * @param str      目标字符串
     * @param fallback 默认值
     * @return char型数值
     */
    public static char parseChat(String str, char fallback) {
        if (str == null || str.isEmpty()) {
            return fallback;
        }
        return str.charAt(0);
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