package com.aspectgaming.common.configuration;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.aspectgaming.common.configuration.adapter.ColorAdapter;
import com.badlogic.gdx.graphics.Color;

public class PayLineConfiguration {

    @XmlAttribute
    public String pipTextFont;

    @XmlJavaTypeAdapter(ColorAdapter.class)
    @XmlAttribute
    public Color pipTextColor;

    @XmlJavaTypeAdapter(ColorAdapter.class)
    @XmlAttribute
    public Color baseGameNoPayBoxColor = new Color(1, 1, 1, 0.4f);

    @XmlJavaTypeAdapter(ColorAdapter.class)
    @XmlAttribute
    public Color freeGameNoPayBoxColor = new Color(1, 1, 1, 0.4f);

    @XmlElement
    public LineConfiguration[] line;

    public LineConfiguration getLine(int index) {
        String name = index >= 0 ? Integer.toString(index) : "scatter";

        for (LineConfiguration configuration : line) {
            if (configuration.index.equals(name)) {
                return configuration;
            }
        }
        return null;
    }
}
