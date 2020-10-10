package com.aspectgaming.common.configuration;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import com.aspectgaming.common.data.GameData;
import com.aspectgaming.net.game.data.ReelStripData;

public class SingleReelConfiguration {
    @XmlAttribute
    public int index;
    @XmlElement
    public float width;
    @XmlElement
    public float symbolHeight;
    @XmlElement
    public float symbolInterval;
    @XmlElement
    public int spinCount;
    @XmlElement
    public float spinSpeed;
    @XmlElement
    public float sinkPercent;
    @XmlElement
    public float sinkSpeed; // in fact, it is the speed of unsink. sink speed will use spin speed
    @XmlElement
    public ReelStripConfiguration[] reelStrip;

    private int[] getReelStripFromConfig(String type, int selection, String rtp) {
        for (ReelStripConfiguration rsc : reelStrip) {
            if (rsc.selection == selection) {
                if (rsc.rtp.equals(rtp)) {
                    if (rsc.type == null || rsc.type.equals(type)) {
                        return rsc.value;
                    }
                }
            }
        }
        for (ReelStripConfiguration rsc : reelStrip) {
            if (rsc.selection == 0) {
                if (rsc.rtp.equals(rtp)) {
                    if (rsc.type == null || rsc.type.equals(type)) {
                        return rsc.value;
                    }
                }
            }
        }
        return new int[0];
    }

    public int[] getReeStrip(String type, int selection, String rtp) {
        // support old maths which do not sending reel strips

        if (type.equals("progressive")) {
            return getReelStripFromConfig(type, selection, rtp);
        }

        if (GameData.getInstance().ReelStrips == null) {
            return getReelStripFromConfig(type, selection, rtp);
        }

        for (ReelStripData rsc : GameData.getInstance().ReelStrips.ReelStrips) {
            if (rsc.ReelIndex == index) {
                if (rsc.Selection == selection) {
                    if (rsc.Paytable.equals(rtp)) {
                        if (rsc.Type == null || rsc.Type.equals(type)) {
                            return rsc.Symbols;
                        }
                    }
                }
            }
        }

        for (ReelStripData rsc : GameData.getInstance().ReelStrips.ReelStrips) {
            if (rsc.ReelIndex == index) {
                if (rsc.Selection == 0) {
                    if (rsc.Paytable.equals(rtp)) {
                        if (rsc.Type == null || rsc.Type.equals(type)) {
                            return rsc.Symbols;
                        }
                    }
                }
            }
        }
        return new int[0];
    }
}
