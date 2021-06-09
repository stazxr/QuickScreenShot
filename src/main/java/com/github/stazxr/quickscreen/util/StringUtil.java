package com.github.stazxr.quickscreen.util;

public class StringUtil {
    /**
     * 判断字符串是否为空
     *
     * @param str 字符串
     * @return boolean
     */
    public static boolean isNullOrTrimmedEmpty(String str) {
        return str == null || str.trim().length() == 0;
    }
}
