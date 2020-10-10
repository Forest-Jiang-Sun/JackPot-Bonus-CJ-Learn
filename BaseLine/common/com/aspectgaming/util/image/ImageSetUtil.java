package com.aspectgaming.util.image;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.aspectgaming.util.FileUtil;

public class ImageSetUtil {

    private static final int CPU_THREADS = Runtime.getRuntime().availableProcessors();

    public static void resize(String srcDir, String outDir, int width, int height) {
        List<String> files = FileUtil.listPngFiles(srcDir);
        new File(outDir).mkdirs();

        ExecutorService exec = Executors.newFixedThreadPool(CPU_THREADS);

        for (String file : files) {
            exec.execute(() -> {
                BufferedImage img = ImageUtil.read(file);
                BufferedImage imgNew = ImageUtil.resize(img, width, height);
                if (imgNew != null && imgNew != img) {
                    String fileNew = srcDir == outDir ? file : outDir + file.substring(srcDir.length());
                    ImageUtil.save(imgNew, fileNew);
                    System.out.println("Resized: " + file + " -> " + fileNew);
                }
            });
        }

        // wait all threads completed
        exec.shutdown();
        try {
            exec.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {}
    }

    /**
     * @param srcDir
     * @param outDir
     * @param scaleX
     * @param scaleY
     * @param isEvenSize
     *            Some video formats must use even width and height
     */
    public static void resize(String srcDir, String outDir, double scaleX, double scaleY, boolean isEvenSize) {
        List<String> files = FileUtil.listPngFiles(srcDir);
        new File(outDir).mkdirs();

        ExecutorService exec = Executors.newFixedThreadPool(CPU_THREADS);

        for (String file : files) {
            exec.execute(() -> {
                BufferedImage img = ImageUtil.read(file);

                int width;
                int height;

                if (isEvenSize) {
                    width = (int) Math.floor((img.getWidth() * scaleX + 1) / 2) * 2; // find nearest even number
                    height = (int) Math.floor((img.getHeight() * scaleY + 1) / 2) * 2; // find nearest even number;
                } else {
                    width = (int) Math.round(img.getWidth() * scaleX);
                    height = (int) Math.round(img.getHeight() * scaleY);
                }

                BufferedImage imgNew = ImageUtil.resize(img, width, height);
                if (imgNew != null && imgNew != img) {
                    String fileNew = srcDir == outDir ? file : outDir + file.substring(srcDir.length());
                    ImageUtil.save(imgNew, fileNew);
                    System.out.println("Resized: " + file + " -> " + fileNew);
                }
            });
        }

        // wait all threads completed
        exec.shutdown();
        try {
            exec.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {}
    }

    public static void crop(String srcDir, String outDir, int x, int y, int width, int height) {
        List<String> files = FileUtil.listPngFiles(srcDir);
        new File(outDir).mkdirs();

        ExecutorService exec = Executors.newFixedThreadPool(CPU_THREADS);

        for (String file : files) {
            exec.execute(() -> {
                BufferedImage img = ImageUtil.read(file);
                BufferedImage imgNew = ImageUtil.crop(img, x, y, width, height);
                if (imgNew != null && imgNew != img) {
                    String fileNew = srcDir == outDir ? file : outDir + file.substring(srcDir.length());
                    ImageUtil.save(imgNew, fileNew);
                    System.out.println("Cropped: " + file + " -> " + fileNew);
                }
            });
        }

        // wait all threads completed
        exec.shutdown();
        try {
            exec.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {}
    }

    public static void split(String srcDir, String outDir, int num, int width, int height, int space) {
        List<String> files = FileUtil.listPngFiles(srcDir);

        for (int i = 1; i <= num; i++) {
            new File(outDir + "\\" + i).mkdirs();
        }

        ExecutorService exec = Executors.newFixedThreadPool(CPU_THREADS);

        for (String file : files) {
            exec.execute(() -> {
                BufferedImage img = ImageUtil.read(file);

                for (int i = 0; i < num; i++) {
                    int x = i * (width + space);
                    int y = 0;
                    BufferedImage imgNew = ImageUtil.crop(img, x, y, width, height);

                    if (imgNew != null) {
                        String fileNew = outDir + "\\" + (i + 1) + file.substring(srcDir.length());
                        ImageUtil.save(imgNew, fileNew);
                        System.out.println("Splitted: " + file + " -> " + fileNew);
                    }
                }
            });
        }

        // wait all threads completed
        exec.shutdown();
        try {
            exec.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {}
    }

    public static Rectangle trimMargin(String dir, int flags, int marginColor, int colorMask) {
        List<String> files = FileUtil.listPngFiles(dir);
        Rectangle area = new Rectangle(Integer.MAX_VALUE, Integer.MAX_VALUE);

        for (String file : files) {
            Rectangle rect = ImageUtil.trimMargin(file, flags, marginColor, colorMask);
            area = area.intersection(rect);
        }

        System.out.println("Area without margin: " + dir + ": " + area.x + "," + area.y + "," + area.width + "," + area.height);

        return area;
    }

    public static void renamePngsForVideo(String dir) {
        System.out.println("Renaming dir: " + dir);

        List<String> files = FileUtil.listPngFiles(dir);

        sort(files);

        int index = files.size();

        for (String path : files) {
            File fileOld = new File(path);

            String pathNew = dir + "\\" + String.format("%05d.png", index);
            File fileNew = new File(pathNew);

            if (!path.equals(pathNew)) {
                System.out.println(path + " -> " + pathNew);
                fileOld.renameTo(fileNew);
            }

            index--;
        }
    }

    private static void sort(List<String> files) {
        Collections.sort(files, (f1, f2) -> {
            try {
                int i1 = parse(f1);
                int i2 = parse(f2);
                return i2 - i1;
            } catch (NumberFormatException e) {
                throw new AssertionError(e);
            }
        });
    }

    private static int parse(String file) {
        int del = file.lastIndexOf('\\');
        String name = file.substring(del);
        name = name.replaceAll("[^\\d]", "");
        return Integer.parseInt(name);
    }
}
