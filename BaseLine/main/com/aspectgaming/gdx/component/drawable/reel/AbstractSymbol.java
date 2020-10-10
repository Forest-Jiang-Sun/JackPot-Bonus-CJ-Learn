package com.aspectgaming.gdx.component.drawable.reel;

import com.aspectgaming.common.actor.Image;
import com.aspectgaming.common.configuration.SingleReelConfiguration;

/**
 * @author ligang.yao
 */
public abstract class AbstractSymbol extends Image implements Comparable {

    private final int reelId;

    public int stopIndex;
    public int symbolIndex;
    public int reelIndex;

    private float offsetX;
    private float offsetY;

    public AbstractSymbol(SingleReelConfiguration cfg) {
        reelId = cfg.index;
    }

    public abstract void setWinShowMode(boolean val);

    public abstract void setSymbol(int val);

    public abstract void onLanguageChanged();

    public abstract void onGameModeChanged();

    public int getReelId() {
        return reelId;
    }

    public void setOffset(float x, float y) {
        offsetX = x;
        offsetY = y;
    }

    public float getScreenX() {
        return offsetX + getX();
    }

    public float getScreenY() {
        return offsetY + getY();
    }

    public void setPositionId(int positionId) {
        this.stopIndex = positionId;
    }

    public int getPositionId() {
        return this.stopIndex;
    }

    public void stopWinShow() {
        this.clearActions();
        this.setVisible(true);
        this.setAlpha(1);
    }

    @Override
    public int compareTo(Object o) {
        return 0;
    }
}
