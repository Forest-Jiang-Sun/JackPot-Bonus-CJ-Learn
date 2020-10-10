package com.LT.math.JackpotBonus;

import com.aspectgaming.math.WagerCategory;
import com.aspectgaming.math.progressive.ProgressiveLevel;

public class JackpotBonusWagerCategory implements WagerCategory{
    private final String name;

    float rtpBaseGame;
    float rtpFreeGame;
    //float rtpBase;
    float rtpMinBase;
    float rtpMaxBase;
    float rtpLP;
    float rtpSAP;
    float rtpBonus;
    private final boolean isProgressive;
    private ProgressiveLevel[] progressiveLevels;

    public JackpotBonusWagerCategory(String name){
        this.name=name;
        this.isProgressive=false;
    }

    public JackpotBonusWagerCategory(String name,boolean isProgressive)
    {
        this.name=name;
        this.isProgressive=isProgressive;
    }

    public void setProgressiveLevels(ProgressiveLevel[] progressiveLevels) {
        this.progressiveLevels = progressiveLevels;
    }

    public ProgressiveLevel[] getProgressiveLevels() {
        return this.progressiveLevels;
    }


    @Override
    public String getName() { return name;}

//    Removed getBaseRTP() method
//    @Override
//    public float getBaseRTP() {
//        return rtpBase;
//    }

    @Override
    public float getBaseMinRTP() {
        return rtpMinBase;
    }

    @Override
    public float getBaseMaxRTP() {
        return rtpMaxBase;
    }

    @Override
    public float getBaseGameRTP() {
        return rtpBaseGame;
    }

    @Override
    public float getFreeGameRTP() {
        return rtpFreeGame;
    }

    @Override
    public float getBonusGameRTP() {
        return rtpBonus;
    }

    @Override
    public float getStandAloneProgressiveRTP() {
        return rtpSAP;
    }

    @Override
    public float getLinkedProgressiveRTP() {
        return rtpLP;
    }

    @Override
    public boolean isProgressive() {
        return isProgressive;
    }
}