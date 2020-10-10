package com.aspectgaming.util;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.util.Arrays;
import java.util.Comparator;

/**
 * @author ligang.yao
 */
public class GraphicsUtil {

    public static Rectangle[] getScreenBounds() {
        GraphicsDevice[] gs = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
        Rectangle[] sizes = new Rectangle[gs.length];
        for (int i = 0; i < gs.length; i++) {
            sizes[i] = new Rectangle(gs[i].getConfigurations()[0].getBounds());
        }

        // sort the screens from left to right and bottom to up
        Arrays.sort(sizes, new RectangleComparator());

        // if need to show smaller game screen on PC, add following lines
        // sizes[0].width = 1680;
        // sizes[0].height = 945;

        return sizes;
    }

    public static Rectangle getMainScreenBounds() {
        GraphicsDevice[] gs = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
        return new Rectangle(gs[0].getConfigurations()[0].getBounds());
    }

    private static class RectangleComparator implements Comparator<Rectangle> {
        @Override
        public int compare(Rectangle o1, Rectangle o2) {
            int ret = o1.x - o2.x;
            if (ret != 0) return ret;

            return o2.y - o1.y;
        }
    }

    private GraphicsUtil() {
    }
}
