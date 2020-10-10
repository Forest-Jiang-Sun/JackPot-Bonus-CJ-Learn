package com.aspectgaming.common.configuration;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.aspectgaming.common.configuration.adapter.AlignAdapter;
import com.aspectgaming.common.configuration.adapter.ColorAdapter;
import com.badlogic.gdx.graphics.Color;

/**
 * @author ligang.yao
 */
public class MeterConfiguration {

    @XmlAttribute
    public String name;

    @XmlAttribute
    public String font;

    @XmlAttribute
    public String specialFont;

    @XmlJavaTypeAdapter(ColorAdapter.class)
    @XmlAttribute
    public Color color;

    @XmlJavaTypeAdapter(ColorAdapter.class)
    @XmlAttribute
    public Color specialColor;

    @XmlJavaTypeAdapter(AlignAdapter.class)
    @XmlAttribute
    public Integer align;

    @XmlAttribute
    public String mode;
}
