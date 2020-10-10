package com.aspectgaming.log;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ligang.yao
 */
public class Debug {

    private static final String INDENT = "    ";
    private static final Logger log = LoggerFactory.getLogger(Debug.class);

    private static void decode(final StringBuilder sb, final String indent, final Object obj) {
        String bodyIndent = INDENT + indent;

        if (obj != null) {
            Class<?> claz = obj.getClass();

            sb.append(claz.getName());
            sb.append('\n');
            sb.append(indent).append("{");
            sb.append('\n');

            while (claz != null) {
                Field[] fields = claz.getDeclaredFields();

                for (Field field : fields) {
                    if (!Modifier.isStatic(field.getModifiers()) && !Modifier.isTransient(field.getModifiers())) {
                        field.setAccessible(true);
                        sb.append(bodyIndent);
                        sb.append(field.getName());
                        sb.append(": ");
                        try {
                            Object data = field.get(obj);
                            decodeField(sb, bodyIndent, data);
                        } catch (Exception e) {
                            log.warn("{}", e);
                            sb.append("error");
                        }
                        sb.append('\n');
                    }
                }

                claz = claz.getSuperclass();
            }
            sb.append(indent);
            sb.append("}");

        } else {
            sb.append("null");
        }
    }

    private static void decodeArray(final StringBuilder sb, final String indent, final Object[] obj) {
        String bodyIndent = INDENT + indent;

        sb.append(obj.getClass().getName());
        sb.append(" (size: ").append(Integer.toString(obj.length)).append(')');
        sb.append('\n').append(indent).append('{').append('\n');
        for (Object data : obj) {
            sb.append(bodyIndent);
            decodeField(sb, bodyIndent, data);
            sb.append(',');
            sb.append('\n');
        }
        sb.append(indent).append('}');

    }

    private static void decodeArray(final StringBuilder sb, final String indent, final int[] obj) {
        String bodyIndent = INDENT + indent;

        sb.append(obj.getClass().getName());
        sb.append(" (size: ").append(Integer.toString(obj.length)).append(')');
        sb.append('\n').append(indent).append('{').append('\n');
        for (Object data : obj) {
            sb.append(bodyIndent);
            decodeField(sb, bodyIndent, data);
            sb.append(',');
            sb.append('\n');
        }
        sb.append(indent).append('}');

    }

    private static void decodeArray(final StringBuilder sb, final String indent, final long[] obj) {
        String bodyIndent = INDENT + indent;

        sb.append(obj.getClass().getName());
        sb.append(" (size: ").append(Integer.toString(obj.length)).append(')');
        sb.append('\n').append(indent).append('{').append('\n');
        for (Object data : obj) {
            sb.append(bodyIndent);
            decodeField(sb, bodyIndent, data);
            sb.append(',');
            sb.append('\n');
        }
        sb.append(indent).append('}');

    }

    private static void decodeMap(final StringBuilder sb, final String indent, final Map<?, ?> map) {
        String bodyIndent = INDENT + indent;

        sb.append(map.getClass().getName());
        sb.append('\n');
        sb.append(indent).append('{').append('\n');
        for (Object key : map.keySet()) {
            sb.append(bodyIndent);
            sb.append(key);
            sb.append(": ");
            try {
                Object data = map.get(key);
                decodeField(sb, bodyIndent, data);
            } catch (Exception e) {
                log.warn("{}", e);
                sb.append("error");
            }
            sb.append('\n');
        }
        sb.append(indent).append('}');
    }

    private static void decodeField(final StringBuilder sb, String indent, Object data) {
        if (data == null) {
            sb.append("null");

        } else if (data instanceof String) {
            sb.append('"').append(data).append('"');

        } else if (data instanceof Object[]) {
            decodeArray(sb, indent, (Object[]) data);

        } else if (data instanceof int[]) {
            decodeArray(sb, indent, (int[]) data);

        } else if (data instanceof long[]) {
            decodeArray(sb, indent, (long[]) data);

        } else if (data instanceof Map) {
            decodeMap(sb, indent, (Map<?, ?>) data);

        } else if (!data.getClass().getName().startsWith("java")) {
            decode(sb, indent, data);

        } else {
            sb.append(data);
        }
    }

    public static void decode(final Object obj) {
        StringBuilder sb = new StringBuilder();
        decode(sb, "", obj);
        System.out.println(sb.toString());
    }
    
    public static void mark(long index) {
        System.out.println("########################" + index);
    }
    
    public static void mark(String val) {
        System.out.println("########################" + val);
    }
}
