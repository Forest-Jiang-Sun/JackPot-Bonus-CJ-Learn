package com.aspectgaming.common.configuration;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

/**
 * @author ligang.yao
 */
public class MetersConfiguration {

    @XmlAttribute
    public float bgMusicVolWhileRolling = 0.3f;

    @XmlAttribute
    public float freeGameRollingSoundVol = 1f;

    @XmlElement
    public MeterConfiguration[] meter;

    public MeterConfiguration getMeter(String name) {
        for (MeterConfiguration mc : meter) {
            if (name.equals(mc.name)) {
                return mc;
            }
        }
        return null;
    }
}
