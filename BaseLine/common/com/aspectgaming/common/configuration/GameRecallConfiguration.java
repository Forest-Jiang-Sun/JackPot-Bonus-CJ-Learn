package com.aspectgaming.common.configuration;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.aspectgaming.common.configuration.adapter.ColorAdapter;
import com.badlogic.gdx.graphics.Color;

/**
 * @author ligang.yao
 */
public class GameRecallConfiguration {

    @XmlJavaTypeAdapter(ColorAdapter.class)
    @XmlAttribute
    public Color creditColor = Color.WHITE;

    @XmlJavaTypeAdapter(ColorAdapter.class)
    @XmlAttribute
    public Color winColor = Color.WHITE;

    @XmlJavaTypeAdapter(ColorAdapter.class)
    @XmlAttribute
    public Color betColor = Color.WHITE;

    @XmlJavaTypeAdapter(ColorAdapter.class)
    @XmlAttribute
    public Color linesPlayedColor = Color.WHITE;

    @XmlJavaTypeAdapter(ColorAdapter.class)
    @XmlAttribute
    public Color betPerLineColor = Color.WHITE;

    @XmlJavaTypeAdapter(ColorAdapter.class)
    @XmlAttribute
    public Color freeGamesRemainingColor = Color.WHITE;
}
