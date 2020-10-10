package com.aspectgaming.util;

/**
 * This class is used by math implementations. Must not rename or remove methods.
 * Only methods will be used by multiple maths should be added here.
 * 
 * @author daniel.huang & ligang.yao
 */
public final class CommonUtil {

    public static int[] getSetBitPositions(int mask) {
        int count = Integer.bitCount(mask);
        int[] ret = new int[count];
        int idx = 0;

        for (int i = 0; i < Integer.SIZE; i++) {
            if (isBitSet(mask, i)) {
                ret[idx++] = i;
                if (idx == count) {
                    break;
                }
            }
        }
        return ret;
    }

    public static boolean isBitSet(int x, int position) {
        return ((x >> position) & 1) == 1;
    }

    public static String convertToCommaSeparatedString(int[] val) {
        if (val == null) return null;

        return arrayToString(val);
    }

    public static String arrayToString(int[] val) {
        if (val == null) return "";

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < val.length; i++) {
            if (i > 0) {
                sb.append(',');
            }
            sb.append(val[i]);
        }
        return sb.toString();
    }

    public static String arrayToString(long[] val) {
        if (val == null) return "";

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < val.length; i++) {
            if (i > 0) {
                sb.append(',');
            }
            sb.append(val[i]);
        }
        return sb.toString();
    }

    public static int[] stringToArray(String str) {
        String[] split = str.split(",");
        int[] stops = new int[split.length];

        for (int i = 0; i < split.length; i++) {
            stops[i] = Integer.parseInt(split[i]);
        }
        return stops;
    }

    public static long[] stringToLongArray(String val) {
        String[] split = val.split(",");
        long[] stops = new long[split.length];

        for (int i = 0; i < split.length; i++) {
            stops[i] = Long.parseLong(split[i]);
        }
        return stops;
    }

    private CommonUtil() {}
}
