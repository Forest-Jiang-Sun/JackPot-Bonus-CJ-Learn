package com.badlogic.gdx.backends.lwjgl;

import com.aspectgaming.common.loader.LibraryLoader;

public final class LwjglNativesLoader {

    static {
        System.setProperty("org.lwjgl.input.Mouse.allowNegativeMouseCoords", "true");
    }

    static public void load() {
        LibraryLoader.load("gdx");
        System.setProperty("org.lwjgl.librarypath", LibraryLoader.DIR);
    }
}
