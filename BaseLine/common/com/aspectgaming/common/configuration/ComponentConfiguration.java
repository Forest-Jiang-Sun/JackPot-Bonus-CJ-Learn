package com.aspectgaming.common.configuration;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

public class ComponentConfiguration {
	@XmlAttribute
	public boolean debug;
	@XmlValue
	public String value;
}
