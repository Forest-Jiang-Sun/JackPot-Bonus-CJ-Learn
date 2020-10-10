package com.aspectgaming.common.configuration;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;

public class ListenerConfiguration {
    @XmlElement
    public List<String> data;

    @XmlElement
    public List<String> action;
}
