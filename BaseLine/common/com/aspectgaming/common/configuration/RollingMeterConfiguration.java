package com.aspectgaming.common.configuration;

import javax.xml.bind.annotation.XmlAttribute;

public class RollingMeterConfiguration {

    @XmlAttribute
    public float rollingTime;

    @XmlAttribute
    public float rollingInterval;

    @XmlAttribute
    public int rollingRangeValue;
}
