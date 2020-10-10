package com.aspectgaming.common.configuration;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public class ProgressiveReelConfiguration {
    @XmlAttribute
    public String defaultStops;

    @XmlAttribute
    public float spinDuration;

    @XmlAttribute
    public float reelInterval;

    @XmlAttribute
    public float scaleToDuration;

    @XmlAttribute
    public float delayEndIntro;

    @XmlAttribute
    public float delayStartNext;

    @XmlElement
    public SingleReelConfiguration progressiveSingleReel;
}
