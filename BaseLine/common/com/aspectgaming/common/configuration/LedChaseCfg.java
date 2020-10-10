package com.aspectgaming.common.configuration;

import javax.xml.bind.annotation.XmlAttribute;

public class LedChaseCfg {

    @XmlAttribute
    public String stringId;
    @XmlAttribute
    public int foregroundRed;
    @XmlAttribute
    public int foregroundGreen;
    @XmlAttribute
    public int foregroundBlue;
    @XmlAttribute
    public int backgroundRed;
    @XmlAttribute
    public int backgroundGreen;
    @XmlAttribute
    public int backgroundBlue;
    @XmlAttribute
    public int segmentSize;
    @XmlAttribute
    public int segmentIndexIncrement;
    @XmlAttribute
    public int activeTimeMs;
    @XmlAttribute
    public int delayTimeMs;


}
