package com.aspectgaming.common.configuration;

import javax.xml.bind.annotation.XmlElement;

import com.aspectgaming.common.configuration.common.SpriteConfiguration;

public class AnimationConfiguration {
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
