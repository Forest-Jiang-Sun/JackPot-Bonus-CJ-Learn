package com.aspectgaming.gdx;

import java.io.IOException;

import com.aspectgaming.common.configuration.DisplayConfiguration;
import com.aspectgaming.common.configuration.GameConfiguration;
import com.aspectgaming.common.configuration.ResolutionConfiguration;
import com.aspectgaming.common.data.GameData;
import com.aspectgaming.common.loader.CoordinateLoader;
import com.aspectgaming.common.loader.LibraryLoader;
import com.aspectgaming.common.util.AspectGamingUtil;
import com.aspectgaming.net.game.GameClient;
import com.aspectgaming.util.GraphicsUtil;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

/**
 * @author ligang.yao & johnny.shi
 */
public class Main {

    public static void main(String... args) throws SecurityException, IOException {

        LibraryLoader.initPath();

        GameData.getInstance();
        GameData.Screen = args[0];

        // need to get resolution from platform, so must be connected at first
        GameClient.getInstance().connect();

        DisplayConfiguration game = GameConfiguration.getInstance().display;
        updateGameResolution(game);

        java.awt.Rectangle[] screens = GraphicsUtil.getScreenBounds();
        java.awt.Rectangle monitor = game.screenIndex >= screens.length ? screens[0] : screens[game.screenIndex];

        boolean isFullScreen = game.forcedFullScreen || (monitor.width == game.width && monitor.height == game.height);
        if (game.undecorated || isFullScreen) {
            // full screen
            System.setProperty("org.lwjgl.opengl.Window.undecorated", "true");
        }

        int width = monitor.width < game.width ? monitor.width : game.width;
        int height = monitor.height < game.height ? monitor.height : game.height;

        // keep width and height aspect ratio if not full screen
        if (!isFullScreen) {
            int x = width * game.height;
            int y = height * game.width;

            // if ratio changed
            if (x < y) {
                height = x / game.width;
            } else if (x > y) {
                width = y / game.height;
            }
        }

        LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
        cfg.title = GameData.Screen;
        cfg.x = monitor.x + game.x;
        cfg.y = monitor.y + game.y;
        cfg.resizable = false;
        cfg.vSyncEnabled = game.vSync;
        cfg.width = width;
        cfg.height = height;
        cfg.foregroundFPS = game.fps;
        cfg.backgroundFPS = game.fps;
        cfg.fullscreen = false;
        cfg.useGL30 = GameConfiguration.getInstance().openGL.useGL30;
        cfg.samples = GameConfiguration.getInstance().openGL.samples;

        cfg.addIcon(AspectGamingUtil.WORKING_DIR + "/assets/Icon/AspectGamingIcon16.png", Files.FileType.Internal);
        cfg.addIcon(AspectGamingUtil.WORKING_DIR + "/assets/Icon/AspectGamingIcon32.png", Files.FileType.Internal);
        cfg.addIcon(AspectGamingUtil.WORKING_DIR + "/assets/Icon/AspectGamingIcon48.png", Files.FileType.Internal);
        new LwjglApplication(new BaseLine(), cfg);
    }

    // set game resolution to the most suitable one
    private static void updateGameResolution(DisplayConfiguration game) {
        ResolutionConfiguration[] choices = CoordinateLoader.getInstance().resolution;
        // update screen resolutions on boot
        int[] resolutions = GameData.getInstance().Setting.ScreenResolution;
        if (resolutions != null) {
            game.width = resolutions[game.screenIndex * 2];
            game.height = resolutions[game.screenIndex * 2 + 1];
        }

        boolean match = false;
        for (ResolutionConfiguration rc : choices) {
            if (game.width == rc.width && game.height == rc.height) {
                match = true;
                break;
            }
        }
        if (!match) {
            for (ResolutionConfiguration rc : choices) {
                if (game.width >= game.height) {
                    if (rc.width >= rc.height) {
                        game.width = rc.width;
                        game.height = rc.height;
                        break;
                    }
                } else {
                    if (rc.width < rc.height) {
                        game.width = rc.width;
                        game.height = rc.height;
                        break;
                    }
                }
            }
        }
    }
}
