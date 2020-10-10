package com.aspectgaming.net.game;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aspectgaming.net.game.data.MathParam;
import com.aspectgaming.net.game.data.MathParamsData;

/**
 * @author ligang.yao
 */
public class MathParamsParser {

    private static final Logger log = LoggerFactory.getLogger(MathParamsParser.class);

    private static final Map<String, Field> map = new HashMap<>();

    static {
        for (Field field : MathParamsData.class.getDeclaredFields()) {
            if (!Modifier.isTransient(field.getModifiers())) {
                map.put(field.getName(), field);
            }
        }
    }

    public static MathParamsData parse(List<MathParam> params) {
        MathParamsData gameData = new MathParamsData();
        if (params != null) {
            try {
                for (MathParam msg : params) {
                    String name = msg.Key;
                    String value = msg.Value;

                    Field field = map.get(name);

                    if (field == null) {
                        log.warn("Unknown math parameter: {} {}", name, value);
                        continue;
                    }

                    Class<?> type = field.getType();

                    if (type == Integer.TYPE) {
                        field.set(gameData, Integer.parseInt(value));
                    } else if (type == Long.TYPE) {
                        field.set(gameData, Long.parseLong(value));
                    } else if (type == Boolean.TYPE) {
                        field.set(gameData, value.equals("1") ? true : false);
                    } else if (type == Float.TYPE) {
                        field.set(gameData, Float.parseFloat(value));
                    } else if (type == Double.TYPE) {
                        field.set(gameData, Double.parseDouble(value));
                    } else if (type == int[].class) {
                        value = value.trim();
                        if (!value.isEmpty()) {
                            field.set(gameData, parseIntArray(value));
                        } else {
                            field.set(gameData, new int[0]);
                        }
                    } else if (type == byte[].class) {
                        value = value.trim();
                        if (!value.isEmpty()) {
                            field.set(gameData, parseByteArray(value));
                        } else {
                            field.set(gameData, new byte[0]);
                        }
                    } else if (type == String[].class) {
                        if (!value.isEmpty()) {
                            field.set(gameData, value.split(","));
                        } else {
                            field.set(gameData, new String[0]);
                        }
                    } else if (type == Integer.class) {
                        field.set(gameData, Integer.valueOf(value));
                    }
                }
            } catch (Exception e) {
                log.error("{}", e);
                System.exit(1);
            }
        }
        return gameData;
    }

    public static int[] parseIntArray(String value) {
        char[] chars = value.toCharArray();

        int count = 1;
        for (char c : chars) {
            if (c == ' ' || c == ',') {
                count++;
            }
        }

        int[] array = new int[count];

        int offset = 0;
        int idx = 0;

        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == ' ' || chars[i] == ',') {
                array[idx++] = parseInt(chars, offset, i);
                offset = i + 1;
            }
        }
        if (offset < chars.length) {
            array[idx++] = parseInt(chars, offset, chars.length);
        }
        return array;
    }

    public static byte[] parseByteArray(String value) {
        char[] chars = value.toCharArray();

        int count = 1;
        for (char c : chars) {
            if (c == ' ' || c == ',') {
                count++;
            }
        }

        byte[] array = new byte[count];

        int offset = 0;
        int idx = 0;

        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == ' ' || chars[i] == ',') {
                array[idx++] = (byte) parseInt(chars, offset, i);
                offset = i + 1;
            }
        }
        if (offset < chars.length) {
            array[idx++] = (byte) parseInt(chars, offset, chars.length);
        }
        return array;
    }

    public static int parseInt(char[] chars, int start, int stop) {
        int result = 0;
        boolean isMinus = false;

        while (start < stop) {
            char ch = chars[start++];

            if (ch == '-') {
                isMinus = true;
            } else {
                result = result * 10 + ch - '0';
            }
        }
        return isMinus ? -result : result;
    }
}
