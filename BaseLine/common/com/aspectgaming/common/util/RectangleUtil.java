package com.aspectgaming.common.util;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.math.Rectangle;

/**
 * @author ligang.yao
 */
public class RectangleUtil {

    public static List<Rectangle> cutHoles(Rectangle area, Rectangle... holes) {
        List<Rectangle> rects = new ArrayList<>();

        rects.add(new Rectangle(area));

        for (Rectangle hole : holes) {
            rects = cut(rects, hole);
        }

        return rects;
    }

    private static List<Rectangle> cut(List<Rectangle> rects, Rectangle hole) {
        List<Rectangle> ret = new ArrayList<>();

        for (Rectangle rect : rects) {
            cut(ret, rect, hole);
        }

        return ret;
    }

    private static void cut(List<Rectangle> list, Rectangle rect, Rectangle hole) {
        // left
        if (rect.x < hole.x) {
            float rectRight = rect.x + rect.width;
            if (rectRight <= hole.x) {
                list.add(rect);
                return;
            }
            list.add(new Rectangle(rect.x, rect.y, (hole.x - rect.x), rect.height));
            rect.width = rectRight - hole.x;
            rect.x = hole.x;
        }

        // right
        float rectRight = rect.x + rect.width;
        float holeRight = hole.x + hole.width;
        if (rectRight > holeRight) {
            if (rect.x >= holeRight) {
                list.add(rect);
                return;
            }
            list.add(new Rectangle(holeRight, rect.y, (rectRight - holeRight), rect.height));
            rect.width = holeRight - rect.x;
        }

        // top
        if (rect.y < hole.y) {
            float rectBottom = rect.y + rect.height;
            if (rectBottom <= hole.y) {
                list.add(rect);
                return;
            }
            list.add(new Rectangle(rect.x, rect.y, rect.width, (hole.y - rect.y)));
            rect.height = rectBottom - hole.y;
            rect.y = hole.y;
        }

        // bottom
        float rectBottom = rect.y + rect.height;
        float holeBottom = hole.y + hole.height;
        if (rectBottom > holeBottom) {
            if (rect.y >= holeBottom) {
                list.add(rect);
                return;
            }
            rect.height = rectBottom - holeBottom;
            rect.y = holeBottom;
            list.add(rect);
        }
    }
}
