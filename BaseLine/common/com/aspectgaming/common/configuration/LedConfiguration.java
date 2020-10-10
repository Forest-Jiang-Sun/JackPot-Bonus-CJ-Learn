package com.aspectgaming.common.configuration;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public class LedConfiguration {
    @XmlElement
    public int base;
    @XmlElement
    public int logo;
    @XmlElement
    public int buttonPanel;
    @XmlElement
    public int body;
    @XmlElement
    public int lowerMonitor;
    @XmlElement
    public int upperMonitor;
    @XmlElement
    public int buttonLightRing;
    @XmlElement
    public LedIdleCfg idle;
    @XmlElement
    public LedFreeGamesCfg freeGames;
    @XmlElement
    public LedDuringSpinCfg duringSpin;
    @XmlElement
    public LedDuringWinCelebrationsCfg duringWinCelebrations;

}
