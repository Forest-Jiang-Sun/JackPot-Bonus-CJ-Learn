package com.aspectgaming.common.configuration;

import javax.xml.bind.annotation.XmlAttribute;

public class SymbolConfiguration {
    @XmlAttribute
    public int index;

    @XmlAttribute
    public String type;

    @XmlAttribute
    public float typeValue;

    @XmlAttribute
    public String sound;

    @XmlAttribute
    public Float duration;

    @XmlAttribute
    public int multi = -1;

    @XmlAttribute
    public boolean stopAnim;
    
    @XmlAttribute
    public String landingPath;

    @XmlAttribute
    public String landingSound;
}
