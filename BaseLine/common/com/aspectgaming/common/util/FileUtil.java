package com.aspectgaming.common.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * @author ligang.yao
 */
public class FileUtil {

    private FileUtil() {}

    public static void listFiles(String path, List<String> list) {
        File file = new File(path);
        if (file.isDirectory()) {
            for (String s : file.list()) {
                listFiles(path + "/" + s, list);
            }
        } else {
            list.add(path);
        }
    }

    public static void listPaths(String path, List<Path> list) {
        Path dir = FileSystems.getDefault().getPath(path);
        listPaths(dir, list);
    }

    public static void listPaths(Path dir, List<Path> list) {
        try (DirectoryStream<Path> ds = Files.newDirectoryStream(dir)) {
            for (Path path : ds) {
                if (Files.isDirectory(path)) {
                    listPaths(path, list);
                } else {
                    list.add(path);
                }
            }
        } catch (IOException e) {}
    }

}
