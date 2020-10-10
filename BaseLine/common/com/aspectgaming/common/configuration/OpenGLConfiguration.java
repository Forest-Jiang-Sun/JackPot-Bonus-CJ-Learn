package com.aspectgaming.common.configuration;

import javax.xml.bind.annotation.XmlAttribute;

/**
 * @author ligang.yao
 */
public class OpenGLConfiguration {

    @XmlAttribute
    public boolean useGL30 = false;

    @XmlAttribute
    public int samples = 0;

    @XmlAttribute
    public boolean pixelBufferObject;

}
