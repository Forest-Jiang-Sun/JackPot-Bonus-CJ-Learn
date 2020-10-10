package com.aspectgaming.common.configuration;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.aspectgaming.common.configuration.adapter.IntArrayAdapter;

/**
 * @author ligang.yao
 */
public class ReelStripConfiguration {
    @XmlAttribute
    public String type;

    @XmlAttribute
    public String rtp;

    @XmlAttribute
    public int selection;

    @XmlJavaTypeAdapter(IntArrayAdapter.class)
    @XmlValue
    public int[] value;
}
