package com.aspectgaming.common.loader.coordinate;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.aspectgaming.common.configuration.adapter.RectangleAdapter;
import com.badlogic.gdx.math.Rectangle;

public class Bound {
    @XmlAttribute
    public String name;

    @XmlJavaTypeAdapter(RectangleAdapter.class)
    @XmlValue
    public Rectangle value;
}
