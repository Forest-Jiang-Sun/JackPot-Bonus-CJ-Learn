package com.aspectgaming.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ligang.yao
 */
public final class FileUtil {

    private FileUtil() {}

    public static List<String> listPngDirs(String dir) {
        List<String> list = new ArrayList<>();
        listPngDirs(list, dir);
        return list;
    }

    private static void listPngDirs(List<String> list, String dir) {
        File file = new File(dir);

        if (file.isDirectory()) {
            String[] files = file.list();

            if (files == null || files.length == 0) return; // ignore empty folders

            boolean hasPng = false;

            for (String s : files) {
                if (s.toLowerCase().endsWith(".png")) {
                    hasPng = true;
                } else {
                    listPngDirs(list, dir + "\\" + s);
                }
            }

            if (hasPng) {
                list.add(dir);
            }
        }
    }

    public static List<String> listPngFiles(String dir) {
        List<String> files = new ArrayList<>();
        try (DirectoryStream<Path> paths = Files.newDirectoryStream(Paths.get(dir))) {
            for (Path path : paths) {
                String file = path.toString();
                if (file.toLowerCase().endsWith(".png")) {
                    files.add(file);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return files;
    }

    public static List<String> listSubDirs(String dir) {
        List<String> files = new ArrayList<>();
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(dir))) {
            for (Path path : directoryStream) {
                if (path.toFile().isDirectory()) {
                    files.add(path.toString());
                }
            }
        } catch (IOException ex) {}
        return files;
    }

    public static List<String> listFiles(String path) {
        List<String> files = new ArrayList<>();
        listFiles(files, path);
        return files;
    }

    private static void listFiles(List<String> list, String path) {
        File file = new File(path);
        if (file.isDirectory()) {
            for (String s : file.list()) {
                listFiles(list, path + "\\" + s);
            }
        } else {
            list.add(path);
        }
    }

    public static String changeFileExtension(String file, String ext) {
        int index = file.lastIndexOf('.');
        if (index <= 0 || index == file.length() - 1) return file;
        return file.substring(0, index + 1) + ext;
    }

    public static String readUtf8File(final File file) {
        try {
            return new String(FileUtil.readFile(file), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static List<String> readLinesFromFile(final String file) {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
            List<String> lines = new ArrayList<>();

            while (true) {
                String line = br.readLine();
                if (line == null) break;
                lines.add(line);
            }

            return lines;
        } catch (Throwable e) {
            throw new RuntimeException("Could not read file " + file);
        }
    }

    public static void writeLinesToFile(final String file, List<String> lines) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(new File(file)))) {
            boolean first = true;
            for (String line : lines) {
                if (first) {
                    bw.write(line);
                } else {
                    bw.write("\r\n" + line);
                }
                first = false;
            }
        } catch (Throwable e) {
            throw new RuntimeException("Could not read file " + file);
        }
    }

    public static byte[] readFile(final String file) {
        return readFile(new File(file));
    }

    public static byte[] readFile(final File file) {
        byte[] bytes = new byte[0];
        if (file == null || !file.isFile()) {
            return bytes;
        }

        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            long length = file.length();
            if (length > Integer.MAX_VALUE) {
                System.err.println("File length is too large");
                return bytes;
            }
            bytes = new byte[(int) length];
            int offset = 0;
            while (offset < bytes.length) {
                int numRead = fis.read(bytes, offset, bytes.length - offset);
                if (numRead < 0) {
                    System.err.println("Could not completely read file");
                    break;
                }
                offset += numRead;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bytes;
    }

    public static void saveFile(final String file, final byte[] bytes) {
        saveFile(new File(file), bytes);
    }

    public static void saveFile(final File file, final byte[] bytes) {
        FileOutputStream fos = null;
        try {
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
            fos = new FileOutputStream(file);
            fos.write(bytes);
            fos.flush();
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void saveTextFile(final String fileName, final String data) {
        File file = new File(fileName);

        FileWriter fstream = null;
        BufferedWriter out = null;

        try {
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
            fstream = new FileWriter(file);
            out = new BufferedWriter(fstream);
            out.write(data);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (fstream != null) {
                    fstream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void createDirForFile(String file) {
        File f = new File(file);
        if (f.exists()) return;
        
        int p = file.lastIndexOf('/');
        if (p < 0) {
            p = file.lastIndexOf('\\');
        }
        if (p > 0) {
            String dir = file.substring(0, p);
            new File(dir).mkdirs();
        }
    }
}
