package com.aspectgaming.common.configuration;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public class DisplayConfiguration {
	@XmlAttribute
	public int screenIndex;
	@XmlElement
	public int x;
	@XmlElement
	public int y;
	@XmlElement
	public int width;
	@XmlElement
	public int height;
	@XmlElement
	public boolean vSync;
	@XmlElement
	public int fps;
	@XmlElement
	public boolean visible;
	@XmlElement
	public boolean undecorated;
	@XmlElement
	public boolean forcedFullScreen;
    @XmlElement
	public boolean continuousRendering = true;
    @XmlElement
    public boolean cachingVideo = true;
}
