package com.aspectgaming.common.configuration;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.aspectgaming.common.configuration.adapter.IntArrayAdapter;

/**
 * @author ligang.yao
 */
@XmlAccessorType(XmlAccessType.NONE)
public class ReelFastSpinConfiguration {
    @XmlAttribute
    public boolean scatterTriggered;

    @XmlAttribute
    public int motionBlur;

    @XmlJavaTypeAdapter(IntArrayAdapter.class)
    @XmlAttribute
    public int[] spinCount;
}
