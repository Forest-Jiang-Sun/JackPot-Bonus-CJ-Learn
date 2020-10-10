package com.aspectgaming.tools;

import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.tools.texturepacker.TexturePacker.Settings;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author ligang.yao
 */
public class TexturePacker {

    private static final String I18N = "international";
    private static final String EN = "en-US";
    private static final String ZH = "zh-CHT";

    private final Settings settings;
    private String srcDir;
    private String desDir;

    private TexturePacker(String[] args) {
        srcDir = args[0] + "/";
        desDir = args[1] + "/";
        String params = args[2];

        settings = new Settings();
        settings.maxWidth = 4096;
        settings.maxHeight = 4096;
        settings.combineSubdirectories = true;
        settings.useIndexes = false;
        settings.limitMemory = true;
        settings.ignoreBlankImages = false;
        settings.alias = false;

        if (params.contains("LinearTexture")) {
            settings.filterMin = TextureFilter.Linear;
            settings.filterMag = TextureFilter.Linear;
        }

        if (params.contains("StripWhiteSpace")) {
            settings.stripWhitespaceX = true;
            settings.stripWhitespaceY = true;
        }
        if (params.contains("Pot")) {
            settings.pot = true;
        }
        if (params.contains("Fast")) {
            settings.fast = true;
        }
    }

    private void startPack() {
        pack(srcDir, desDir);
    }

    private void pack(String srcDir, String desDir) {
        ExecutorService exec = Executors.newFixedThreadPool(3);

        File file = new File(srcDir + EN);
        if (file.exists()) {
            exec.execute(() -> {
                try {
                    com.badlogic.gdx.tools.texturepacker.TexturePacker.process(settings, srcDir + EN, desDir + EN, EN);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            });
        }

        file = new File(srcDir + ZH);
        if (file.exists()) {
            exec.execute(() -> {
                try {
                    com.badlogic.gdx.tools.texturepacker.TexturePacker.process(settings, srcDir + ZH, desDir + ZH, ZH);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            });
        }

        file = new File(srcDir + I18N);
        if (file.exists()) {
            exec.execute(() -> {
                try {
                    com.badlogic.gdx.tools.texturepacker.TexturePacker.process(settings, srcDir + I18N, desDir + I18N, I18N);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            });
        }

        // wait all threads completed
        exec.shutdown();
        try {
            exec.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            System.out.println("Packing interrupted:" + srcDir);
        }

        // do not use logger since information is print during compiling
        System.out.println("Packed :" + srcDir);
    }

    public static void main(String[] args) {
        new TexturePacker(args).startPack();
    }

}
