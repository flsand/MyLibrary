//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.flysand.mylibrary.util;

public class ForbidFastClick {
    private static long lastClickTime;
    private static int timeOffset = 300;

    public ForbidFastClick() {
    }

    public static boolean isFastDoubleClick() {
        long var0 = System.currentTimeMillis();
        long var2 = var0 - lastClickTime;
        if(0L < var2 && var2 < (long)getTimeOffset()) {
            return true;
        } else {
            lastClickTime = var0;
            return false;
        }
    }

    public static int getTimeOffset() {
        return timeOffset;
    }

    public static void setTimeOffset(int var0) {
        timeOffset = var0;
    }
}
