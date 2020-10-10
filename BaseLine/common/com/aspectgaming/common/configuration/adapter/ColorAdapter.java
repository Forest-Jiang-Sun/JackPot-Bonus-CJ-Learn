package com.aspectgaming.common.configuration.adapter;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.badlogic.gdx.graphics.Color;

/**
 * @author ligang.yao
 */
public class ColorAdapter extends XmlAdapter<String, Color> {

    @Override
    public Color unmarshal(String val) {
        if (val == null) return null;

        String temp = val.replace("#", "").trim();
        if (temp.isEmpty()) return null;

        float r = Integer.parseInt(temp.substring(0, 2), 16);
        float g = Integer.parseInt(temp.substring(2, 4), 16);
        float b = Integer.parseInt(temp.substring(4, 6), 16);
        float a = Integer.parseInt(temp.substring(6, 8), 16);

        return new Color(r / 255, g / 255, b / 255, a / 255);
    }

    @Override
    public String marshal(Color val) {
        return null;
    }
}
