package com.aspectgaming.common.loader;

import java.io.File;
import java.lang.reflect.Field;
import java.util.HashSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aspectgaming.common.util.AspectGamingUtil;

/**
 * @author ligang.yao
 */
public class LibraryLoader {

    public static final boolean JAVA_64BIT = System.getProperty("os.arch").equals("amd64");
    public static final String DIR = AspectGamingUtil.WORKING_DIR + "/assets/DLL/" + (JAVA_64BIT ? "Win64/" : "Win32/");

    private static final HashSet<String> libs = new HashSet<>();
    private static final Logger log = LoggerFactory.getLogger(LibraryLoader.class);

    private LibraryLoader() {}

    public static void initPath() {
        String path = System.getProperty("java.library.path");
        System.setProperty("sun.boot.library.path", System.getProperty("sun.boot.library.path") + ";" + path);

        try {
            Field fieldSysPath = ClassLoader.class.getDeclaredField("sys_paths");
            fieldSysPath.setAccessible(true);
            fieldSysPath.set(null, null);
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
            log.error("Failed to set library path");
            throw new RuntimeException("Failed to set library path");
        }
    }

    public static synchronized void load(String libraryName) {
        if (!libs.contains(libraryName)) {
            File file = new File(DIR + libraryName + ".dll");
            log.info("Loading library: {}", file);
            try {
                Runtime.getRuntime().load(file.getAbsolutePath());
            } catch (Throwable e) {
                throw new RuntimeException("Failed to load: " + file, e);
            }
            libs.add(libraryName);
        }
    }
}
