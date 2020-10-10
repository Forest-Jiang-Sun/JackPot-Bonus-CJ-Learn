package com.aspectgaming.common.loader.coordinate;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.aspectgaming.common.configuration.adapter.VectorAdapter;
import com.badlogic.gdx.math.Vector2;

public class Offset {
    @XmlAttribute
    public String name;

    @XmlJavaTypeAdapter(VectorAdapter.class)
    @XmlValue
    public Vector2 value;
}
