package com.aspectgaming.util;

/**
 * @author ligang.yao
 */
public class StringUtil {

    public static String replace(String str, char character, int number) {
        int idx = str.indexOf(character);
        if (idx < 0) return str;
        return str.substring(0, idx) + number + str.substring(idx + 1);
    }

    private StringUtil() {}
}
