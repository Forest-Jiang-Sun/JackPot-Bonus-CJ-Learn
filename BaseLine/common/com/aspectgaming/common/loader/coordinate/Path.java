package com.aspectgaming.common.loader.coordinate;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public class Path {
	@XmlAttribute
	public String name;
	@XmlElement
	public Point[] point;
}
