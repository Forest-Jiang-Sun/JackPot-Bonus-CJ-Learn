package com.aspectgaming.common.configuration;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import com.aspectgaming.common.configuration.common.SpriteConfiguration;

public class PaytableConfiguration {

    @XmlAttribute
    public float duration = 1.5f;

    @XmlAttribute
    public String type = "Flash";

    @XmlAttribute
    public float typeValue;

    @XmlAttribute
    public String indexFont = "PaytableIndexFont";

    @XmlAttribute
    public String valueFont = "PaytableFont";

    @XmlElement(name = "sprite")
    public SpriteConfiguration[] sprites;
    
    public SpriteConfiguration getSprite(String name) {
        if (name == null || sprites == null) return null;

        for (SpriteConfiguration sc : sprites) {
            if (sc.name.equals(name)) {
                return sc;
            }
        }
        return null;
    }
}
