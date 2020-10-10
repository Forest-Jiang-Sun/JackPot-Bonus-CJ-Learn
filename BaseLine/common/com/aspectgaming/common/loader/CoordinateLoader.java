package com.aspectgaming.common.loader;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.aspectgaming.common.configuration.DisplayConfiguration;
import com.aspectgaming.common.configuration.GameConfiguration;
import com.aspectgaming.common.configuration.ResolutionConfiguration;
import com.aspectgaming.common.data.GameData;
import com.aspectgaming.common.loader.coordinate.Bound;
import com.aspectgaming.common.loader.coordinate.Offset;
import com.aspectgaming.common.loader.coordinate.Path;
import com.aspectgaming.common.loader.coordinate.Point;
import com.aspectgaming.common.util.AspectGamingUtil;
import com.aspectgaming.common.util.XMLUtil;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;

@XmlRootElement
public class CoordinateLoader {

    @XmlElement
    public ResolutionConfiguration[] resolution;

    private static final CoordinateLoader instance = load();

    public static CoordinateLoader load() {
        String file = AspectGamingUtil.WORKING_DIR + "/" + GameData.Screen + "Coordinate.xml";
        return XMLUtil.unmarshal(file, CoordinateLoader.class);
    }

    public static CoordinateLoader getInstance() {
        return instance;
    }

    private ResolutionConfiguration currentResolution() {
        DisplayConfiguration display = GameConfiguration.getInstance().display;
        for (ResolutionConfiguration rc : resolution) {
            if (display.width == rc.width && display.height == rc.height) return rc;
        }
        return null;
    }

    public Vector2 getCoordinate(Actor actor, String name) {
        for (Point point : currentResolution().point) {
            if (point.name.equals(name)) {
                return AspectGamingUtil.changeToBottomLeft(actor, point.value);
            }
        }

        if (GameData.getInstance().Context.Language.equals("zh-CHT")) {
            name += "CN";
        } else {
            name += "EN";
        }

        for (Point point : currentResolution().point) {
            if (point.name.equals(name)) {
                return AspectGamingUtil.changeToBottomLeft(actor, point.value);
            }
        }

        return null;
    }

    public Vector2 getPos(String name) {
        for (Point point : currentResolution().point) {
            if (point.name.equals(name)) {
                return point.value;
            }
        }
        return null;
    }

    public Vector2[] getPath(Actor actor, String name) {
        for (Path path : currentResolution().path) {
            if (path.name.equals(name)) {
                Vector2[] result = new Vector2[path.point.length];
                for (int i = 0; i < result.length; i++) {
                    result[i] = AspectGamingUtil.changeToBottomLeft(actor, path.point[i].value);
                }
                return result;
            }
        }

        return null;
    }

    public Vector2 getOffset(String name) {
        for (Offset offset : currentResolution().offset) {
            if (offset.name.equals(name)) {
                return offset.value;
            }
        }
        return null;
    }

    public Rectangle getBound(String name) {
        for (Bound bound : currentResolution().bound) {
            if (bound.name.equals(name)) {
                Rectangle result = new Rectangle(bound.value);
                result.y = GameConfiguration.getInstance().display.height - (result.y + result.height);
                return result;
            }
        }
        return null;
    }
}
