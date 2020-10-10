package com.aspectgaming.common.loader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.aspectgaming.common.data.GameData;
import com.aspectgaming.common.util.AspectGamingUtil;

public class MessageLoader {

    private Map<String, Properties> map = new HashMap<>();

    private static final MessageLoader instance = new MessageLoader();

    public static MessageLoader getInstance() {
        return instance;
    }

    private MessageLoader() {
        File file = new File(AspectGamingUtil.WORKING_DIR + "/assets/Message/international/Message.properties");
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(file));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        map.put("international", properties);

        file = new File(AspectGamingUtil.WORKING_DIR + "/assets/Message/en-US/Message.properties");
        properties = new Properties();
        try {
            properties.load(new FileInputStream(file));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        map.put("en-US", properties);

        file = new File(AspectGamingUtil.WORKING_DIR + "/assets/Message/zh-CHT/Message.properties");
        properties = new Properties();
        try {
            properties.load(new FileInputStream(file));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        map.put("zh-CHT", properties);
    }

    public String getMessage(String key) {
        String message = map.get(GameData.getInstance().Context.Language).getProperty(key);
        if (message == null) {
            message = map.get("international").getProperty(key);
        }
        return message;
    }
}
