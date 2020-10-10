package com.aspectgaming.common.configuration;

import javax.xml.bind.annotation.XmlAttribute;

public class SoundCfg {
    @XmlAttribute
    public String sPath;

    @XmlAttribute
    public float lowTime;

    @XmlAttribute
    public float hightTime;

    @XmlAttribute
    public float overlap;

    @XmlAttribute
    public String beforeSound;

    public float rollUpTime;

    @XmlAttribute
    public String animation;

    @XmlAttribute
    public int playCount;

    @XmlAttribute
    public float animOverlap;

    @XmlAttribute
    public float lastAnimOverlap;
}
