package com.aspectgaming.common.configuration;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public class WinShowConfiguration {

    @XmlAttribute
    public boolean wildMultiplier = false;

    @XmlAttribute
    public float duration = 1.5f;

    @XmlAttribute
    public float landingDuration = 0; // 0 means no animation, just play scatter sound

    @XmlElement
    public SymbolConfiguration[] symbol;

    public SymbolConfiguration getSymbolConfiguration(int index) {
        for (SymbolConfiguration symbolConfiguration : symbol) {
            if (symbolConfiguration.index == index) {
                return symbolConfiguration;
            }
        }
        return null;
    }
}
