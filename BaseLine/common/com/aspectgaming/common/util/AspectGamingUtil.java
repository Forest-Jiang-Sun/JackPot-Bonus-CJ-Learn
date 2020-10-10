package com.aspectgaming.common.util;

import com.aspectgaming.common.configuration.GameConfiguration;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;

/**
 * @author johnny.shi & ligang.yao
 */

//
public class AspectGamingUtil {

    public static final String WORKING_DIR = getWorkingDirectory();


    private static String getWorkingDirectory() {
        String path = System.getProperty("user.dir");

        if (!path.endsWith("share")) {
            // if running from eclipse
            path = path + "/build/share";
        }

        return path;
    }

    public static Vector2 changeToBottomLeft(Actor actor, Vector2 point) {
        Vector2 vector2 = new Vector2(point);
        if (Widget.class.isAssignableFrom(actor.getClass())) {
            vector2.y = GameConfiguration.getInstance().display.height - (vector2.y + ((Widget) actor).getPrefHeight());
        } else {
            vector2.y = GameConfiguration.getInstance().display.height - (vector2.y + actor.getHeight());
        }
        return vector2;
    }
}
