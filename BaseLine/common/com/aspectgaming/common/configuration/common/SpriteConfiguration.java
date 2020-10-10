package com.aspectgaming.common.configuration.common;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

/**
 * @author ligang.yao
 */
@XmlAccessorType(XmlAccessType.NONE)
public class SpriteConfiguration {

    @XmlAttribute
    public String name;

    @XmlAttribute
    public String path;

    @XmlAttribute
    public String type;//image video animation ...

    @XmlAttribute
    public String mode;//Intro Outro *
    
    @XmlAttribute
    public String condition;//Bet Award Text Mask Message
    
    @XmlAttribute
    public int fps;//only work at Animation
    
    @XmlAttribute
    public float delay;//delay to play
    
    @XmlAttribute
    public String key;//HashMap<key, value>
    
    @XmlAttribute
    public boolean isMultilingual;
}
