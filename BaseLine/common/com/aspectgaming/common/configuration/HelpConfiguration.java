package com.aspectgaming.common.configuration;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.aspectgaming.common.configuration.adapter.IntArrayAdapter;

public class HelpConfiguration {

    @XmlAttribute
    public Integer gamble;

    @XmlAttribute
    public Integer progressive;
}
