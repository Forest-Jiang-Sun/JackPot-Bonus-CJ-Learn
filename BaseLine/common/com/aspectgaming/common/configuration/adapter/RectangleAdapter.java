package com.aspectgaming.common.configuration.adapter;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.badlogic.gdx.math.Rectangle;

/**
 * @author ligang.yao
 */
public class RectangleAdapter extends XmlAdapter<String, Rectangle> {

    @Override
    public Rectangle unmarshal(String val) {
        if (val != null) {
            String[] strs = val.split(",");

            if (strs.length == 4) {
                float x = Float.parseFloat(strs[0].trim());
                float y = Float.parseFloat(strs[1].trim());
                float width = Float.parseFloat(strs[2].trim());
                float height = Float.parseFloat(strs[3].trim());

                return new Rectangle(x, y, width, height);
            } else if (strs.length == 2) {
                float width = Float.parseFloat(strs[0].trim());
                float height = Float.parseFloat(strs[1].trim());

                return new Rectangle(0, 0, width, height);
            }
        }
        return null;
    }

    @Override
    public String marshal(Rectangle val) {
        if (val != null) {
            String str = new String(val.x + "," + val.y + "," + val.width + "," + val.height);
            return str;
        }
        return null;
    }
}
