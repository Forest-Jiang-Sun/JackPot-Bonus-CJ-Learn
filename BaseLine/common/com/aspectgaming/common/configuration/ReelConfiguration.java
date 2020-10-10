package com.aspectgaming.common.configuration;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

/**
 * @author ligang.yao
 */
public class ReelConfiguration {
    @XmlAttribute
    public String defaultStops;

    @XmlAttribute
    public String defaultFreeGameStops;

    @XmlAttribute
    public int motionBlur;

    @XmlAttribute
    public boolean scatterAdjacent = false;

    @XmlAttribute
    public float spinDuration;

    @XmlAttribute
    public float reelInterval;

    @XmlAttribute
    public boolean manualStop = true; // whether the reel spinning can be stopped by play button in base games

    @XmlAttribute
    public boolean manualStopInFreeGames = true; // whether the reel spinning can be stopped by play button in free games

    @XmlElement
    public ReelSoundConfiguration sound = new ReelSoundConfiguration();

    @XmlElement
    public ReelFastSpinConfiguration fastSpin = new ReelFastSpinConfiguration();

    @XmlElement(name = "singleReel")
    public SingleReelConfiguration[] reels;

    @XmlElement
    public SingleReelConfiguration bonusReel;

    public SingleReelConfiguration getSingleReel(int index) {
        for (SingleReelConfiguration singleReel : reels) {
            if (singleReel.index == index) {
                return singleReel;
            }
        }
        return null;
    }
}
