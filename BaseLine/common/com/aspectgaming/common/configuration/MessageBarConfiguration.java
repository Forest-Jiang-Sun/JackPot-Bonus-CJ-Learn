package com.aspectgaming.common.configuration;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.aspectgaming.common.configuration.adapter.ColorAdapter;
import com.badlogic.gdx.graphics.Color;

/**
 * @author ligang.yao
 */
public class MessageBarConfiguration {
    @XmlAttribute
    public String font;
    
    @XmlJavaTypeAdapter(ColorAdapter.class)
    @XmlAttribute
    public Color color = Color.WHITE;

    @XmlAttribute
    public boolean visibleInFreeGames;
}
