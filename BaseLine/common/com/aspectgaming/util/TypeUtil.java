package com.aspectgaming.util;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public final class TypeUtil {

    private static final Charset UTF8_CHARSET = Charset.forName("UTF-8");

    public static int[] copyOf(int[] data) {
        return data != null ? data.clone() : null;
    }

    public static byte[] getBytes(byte[] data) {
        return (data != null) ? data.clone() : null;
    }

    public static byte[] getBytes(int[] data) {
        if (data == null) {
            return null;
        } else {
            ByteBuffer byteBuffer = ByteBuffer.allocate(data.length * 4);
            IntBuffer buf = byteBuffer.asIntBuffer();
            buf.put(data);
            return byteBuffer.array();
        }
    }

    public static byte[] getBytes(long[] data) {
        if (data == null) {
            return null;
        } else {
            ByteBuffer byteBuffer = ByteBuffer.allocate(data.length * 8);
            LongBuffer buf = byteBuffer.asLongBuffer();
            buf.put(data);
            return byteBuffer.array();
        }
    }

    public static byte[] getBytes(String data) {
        return (data != null) ? data.getBytes(UTF8_CHARSET) : null;
    }

    public static String getString(byte[] data) {
        return (data != null) ? new String(data, UTF8_CHARSET) : null;
    }

    public static byte[] getBytes(BigDecimal data) {
        return (data != null) ? data.toString().getBytes() : null;
    }

    public static int[] getInts(byte[] data) {
        if (data == null) {
            return null;
        } else {
            ByteBuffer byteBuffer = ByteBuffer.wrap(data);
            IntBuffer buf = byteBuffer.asIntBuffer();
            int[] ret = new int[data.length / 4];
            buf.get(ret);
            return ret;
        }
    }

    public static long[] getLongs(byte[] data) {
        if (data == null) {
            return null;
        } else {
            ByteBuffer byteBuffer = ByteBuffer.wrap(data);
            LongBuffer buf = byteBuffer.asLongBuffer();
            long[] ret = new long[data.length / 8];
            buf.get(ret);
            return ret;
        }
    }

    public static BigDecimal getBigDecimal(byte[] data) {
        return (data != null) ? new BigDecimal(new String(data)) : null;
    }

    public static void intToBytes(byte[] data, int srcData, int index) {
        for (int i = 0; i < 4; i++) {
            data[index + i] = (byte) (srcData >> i * 8);
        }
    }

    public static int bytesToInt(byte[] data, int index) {
        int h1 = data[index + 3] << 24;
        int h2 = (data[index + 2] << 16) & 0xff0000;
        int h3 = (data[index + 1] << 8) & 0xff00;
        int h4 = 0xff & data[index];
        return h1 | h2 | h3 | h4;
    }

    public static String toHex(byte[] data) {
        if (data == null) {
            return "";
        }

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < data.length; i++) {
            if (i != 0) {
                sb.append(" ");
            }
            String val = Integer.toHexString(data[i] & 0xff);
            if (val.length() < 2) {
                sb.append("0");
            }
            sb.append(val);
        }

        return sb.toString();
    }

    public static String toHexString(int val) {
        String hex = Integer.toHexString(val);
        if (hex.length() % 2 == 1) {
            return "0x0" + hex;
        } else {
            return "0x" + hex;
        }
    }

    public static String currencyOf(String currency, Long val) {
        return currencyOf(currency, val, 2);
    }

    public static String currencyOf(String currency, Long val, int scale) {
        if (val == null) {
            val = 0L;
        }

        StringBuilder sb = new StringBuilder();
        sb.append(currency);

        String ret = val.toString();

        if (ret.length() <= scale) {
            sb.append("0.");
            int count = scale - ret.length();

            while (count-- > 0) {
                sb.append("0");
            }
            sb.append(ret);
        } else {
            sb.append(ret);

            int index = sb.length() - scale - 2;
            int count = index / 3;
            int start = index % 3 + 2;

            for (int i = 0; i < count; i++) {
                sb.insert(start, ",");
                start += 4;
            }

            if (scale != 0) {
                sb.insert(sb.length() - scale, ".");
            }
        }

        return sb.toString();
    }

    public static String toPercent(long val, int precision) {
        StringBuilder sb = new StringBuilder();

        precision = precision - 2;

        String value = Long.toString(val);

        if (precision == 0) {
            sb.append(value);
        } else {
            if (value.length() <= precision) {
                sb.append("0.");
                int count = precision - value.length();
                for (int i = 0; i < count; i++) {
                    sb.append('0');
                }
                sb.append(value);
            } else {
                int idx = value.length() - precision;
                sb.append(value);
                sb.insert(idx, '.');
            }
        }
        sb.append('%');
        return sb.toString();
    }

    public static String stringOf(final byte[] val) {
        if (val == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder(val.length);
        for (int i = 0; i < val.length; i++) {
            sb.append((char) val[i]);
        }
        return sb.toString();
    }

    public static byte[] bytesOf(final String val) {
        byte[] data = new byte[val.length()];

        for (int i = 0; i < val.length(); i++) {
            data[i] = (byte) val.charAt(i);
        }
        return data;
    }

    public static String shortStringOf(long val) {
        if (val >= 1000000) {
            return String.valueOf(val / 1000000) + "M";
        } else if (val >= 1000) {
            return String.valueOf(val / 1000) + "K";
        } else {
            return String.valueOf(val);
        }
    }

    public static String integerTo8421(int num) {
        StringBuilder sb = new StringBuilder();
        List<Integer> results = new ArrayList<Integer>();
        int quotient = num;
        int remainder = 0;
        for (; quotient > 0;) {
            remainder = quotient % 2;
            quotient = quotient / 2;
            results.add(remainder);
        }
        for (int i = results.size() - 1; i >= 0; i--) {
            if (results.get(i) == 1) {
                sb.insert(0, i);
                sb.insert(0, ",");
            }
        }
        sb.delete(0, 1);
        return sb.toString();
    }

    public static int findMax(final int[] data) {
        int max = data[0];
        for (int i = 1; i < data.length; i++) {
            if (max < data[i]) {
                max = data[i];
            }
        }
        return max;
    }

    public static List<String> stringToList(String s, String splitString) {
        List<String> list = new LinkedList<String>();
        if (s.length() > 0) {
            String[] split = s.split(splitString);

            for (int i = 0; i < split.length; i++) {
                list.add(split[i]);
            }
        }
        return list;
    }

    public static String splitCamelCase(String s) {
        return s.replace(" ", "").replaceAll("([A-Z])", " $1").trim();
    }

    public static String toCamelCase(String s) {
        char[] str = s.toCharArray();

        boolean isFirst = true;
        for (int i = 0; i < str.length; i++) {
            if (isFirst) {
                str[i] = Character.toUpperCase(str[i]);
            } else {
                str[i] = Character.toLowerCase(str[i]);
            }

            isFirst = (str[i] == ' ');
        }
        return new String(str);
    }

    public static <E extends Enum<E>> String formatEnumName(E val) {
        return toCamelCase(val.name().replace("_", " "));
    }

    public static String listToString(List<?> list, String splitString) {
        StringBuilder builder = new StringBuilder();
        if (list.size() > 0) {
            for (Object s : list) {
                builder.append(s);
                builder.append(splitString);
            }
            builder.deleteCharAt(builder.length() - splitString.length());
        }
        return builder.toString();
    }

    public static String stopsToString(int[] stops) {
        if (stops == null) {
            return "0";
        }
        return CommonUtil.arrayToString(stops);
    }

    public static int[] stringToArray(String str, int maxSize) {
        int[] val = CommonUtil.stringToArray(str);

        if (val.length > maxSize) {
            return Arrays.copyOf(val, maxSize);
        } else {
            return val;
        }
    }

    private TypeUtil() {}
}
