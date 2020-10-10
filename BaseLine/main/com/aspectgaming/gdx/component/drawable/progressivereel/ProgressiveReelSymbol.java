package com.aspectgaming.gdx.component.drawable.progressivereel;

import com.aspectgaming.common.configuration.SingleReelConfiguration;
import com.aspectgaming.common.loader.ImageLoader;
import com.aspectgaming.gdx.component.drawable.reel.AbstractSymbol;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class ProgressiveReelSymbol extends AbstractSymbol {
    public static int PRO_NOTHING   = 0;
    public static int PRO_GRAND     = 1;
    public static int PRO_MAJOR     = 2;
    public static int PRO_MINOR     = 3;
    public static int PRO_MINI      = 4;

    public static int PRO_NUM_SYMBOLS = 5;

    private final Sprite[] sprites;

    public ProgressiveReelSymbol(SingleReelConfiguration cfg) {
        super(cfg);

        sprites = new Sprite[PRO_NUM_SYMBOLS];


        for (int id = 1; id < sprites.length; id++) {
            sprites[id] = ImageLoader.getInstance().newSprite("Symbol/" + (id+12));
        }
        setSprite(sprites[symbolIndex]);
    }

    //@Override
    public void setWinShowMode(boolean val) {
    }

    @Override
    public void setSymbol(int val) {
        if (symbolIndex != val) {
            symbolIndex = val;
            setSprite(sprites[val]);
        }
    }

    @Override
    public void onLanguageChanged() {
        for (int id = 1; id < sprites.length; id++) {
            sprites[id] = ImageLoader.getInstance().newSprite("Symbol/" + (id+12));
        }
        setSprite(sprites[symbolIndex]);
        setY(getY());
    }

    @Override
    public void onGameModeChanged() {
    }

    public void adjustPosition() {
        this.setX(Math.round(this.getX()));
        this.setY(Math.round(this.getY()));
    }

    @Override
    public void setY(float y) {
        super.setY(y);
        sprite.setY(y);
    }

    @Override
    public int compareTo(Object o) {
        return 0;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        sprites[symbolIndex].draw(batch, parentAlpha);
    }
}
