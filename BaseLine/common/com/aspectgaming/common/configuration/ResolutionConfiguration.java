package com.aspectgaming.common.configuration;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import com.aspectgaming.common.configuration.common.SpriteConfiguration;
import com.aspectgaming.common.loader.coordinate.Bound;
import com.aspectgaming.common.loader.coordinate.Offset;
import com.aspectgaming.common.loader.coordinate.Path;
import com.aspectgaming.common.loader.coordinate.Point;

public class ResolutionConfiguration {

    @XmlAttribute
    public int width;

    @XmlAttribute
    public int height;
    
    @XmlAttribute
    public String video;

    @XmlElement
    public ComponentsConfiguration components;

    @XmlElement
    public BackgroundConfiguration background;

    @XmlElement
    public PaytableConfiguration paytable;

    @XmlElement
    public AnimationConfiguration animation;

    @XmlElement
    public AnimationConfiguration bonus;

    @XmlElement
    public AnimationConfiguration introOutro;

    @XmlElement
    public SpriteConfiguration attractMovie;

    @XmlElement
    public List<Point> point;

    @XmlElement
    public List<Bound> bound;

    @XmlElement
    public Path[] path;

    @XmlElement
    public Offset[] offset;

}
