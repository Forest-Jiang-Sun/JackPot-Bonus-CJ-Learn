package com.aspectgaming.common.configuration;

import javax.xml.bind.annotation.XmlAttribute;

/**
 * @author ligang.yao
 */
public class ReelSoundConfiguration {

    @XmlAttribute
    public boolean scatterNotify = false;

    @XmlAttribute
    public boolean scatterLanding = true;
}
