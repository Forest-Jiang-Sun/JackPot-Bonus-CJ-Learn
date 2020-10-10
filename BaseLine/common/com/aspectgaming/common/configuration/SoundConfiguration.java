package com.aspectgaming.common.configuration;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.aspectgaming.common.configuration.adapter.StringArrayAdapter;

/**
 * @author ligang.yao
 */
public class SoundConfiguration {
    @XmlAttribute
    public float lowMultiple;

    @XmlAttribute
    public float highMultiple;

    @XmlAttribute
    public int type;

    @XmlElement
    public SoundCfg[] soundInfo;
}
