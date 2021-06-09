package com.github.stazxr.quickscreen.util;

public class CommonUtil {
    public static void sleep(long mills) {
        try {
            Thread.sleep(mills);
        } catch (InterruptedException e) {
            e.printStackTrace(System.out);
        }
    }
}
