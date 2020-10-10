package com.aspectgaming.common.configuration;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.aspectgaming.common.configuration.adapter.ColorAdapter;
import com.badlogic.gdx.graphics.Color;

public class LineConfiguration {

    @XmlAttribute
    public String index;

    @XmlJavaTypeAdapter(ColorAdapter.class)
    @XmlAttribute
    public Color color;

    public Color getColor() {
        return new Color(color);
    }
}
