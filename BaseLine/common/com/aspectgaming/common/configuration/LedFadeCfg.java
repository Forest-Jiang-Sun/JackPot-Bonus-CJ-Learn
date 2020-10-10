package com.aspectgaming.common.configuration;

import javax.xml.bind.annotation.XmlAttribute;

public class LedFadeCfg {

    @XmlAttribute
     public String stringId;
     @XmlAttribute
     public int redA;
     @XmlAttribute
     public int greenA;
     @XmlAttribute
     public int blueA;
     @XmlAttribute
     public int redB;
     @XmlAttribute
     public int greenB;
     @XmlAttribute
     public int blueB;
     @XmlAttribute
     public int fadeTimeMs;
     @XmlAttribute
     public int delayTimeMs;
     @XmlAttribute
     public boolean reverse;

}
