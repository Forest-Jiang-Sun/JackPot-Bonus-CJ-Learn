package com.aspectgaming.net;

import java.util.List;

public class ProtoUtil {

    public static int[] toIntArray(List<Integer> list) {
        if (list == null) return new int[0];

        int[] ret = new int[list.size()];

        int i = 0;
        for (Integer val : list) {
            ret[i++] = val;
        }
        return ret;
    }

    public static long[] toLongArray(List<Long> list) {
        if (list == null) return new long[0];

        long[] ret = new long[list.size()];

        int i = 0;
        for (Long val : list) {
            ret[i++] = val;
        }
        return ret;
    }

    public static String[] toStringArray(List<String> list) {
        if (list == null) return new String[0];

        return list.toArray(new String[list.size()]);
    }
}
