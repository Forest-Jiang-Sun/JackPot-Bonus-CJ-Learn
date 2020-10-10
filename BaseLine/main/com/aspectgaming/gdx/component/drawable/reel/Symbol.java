package com.aspectgaming.gdx.component.drawable.reel;

import com.aspectgaming.common.configuration.SingleReelConfiguration;
import com.aspectgaming.common.data.GameData;
import com.aspectgaming.common.loader.CoordinateLoader;
import com.aspectgaming.common.loader.ImageLoader;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

/**
 * @author ligang.yao
 */
public class Symbol extends AbstractSymbol {
    public static final int BL = 0;
    public static final int D7 = 1;
    public static final int D3 = 2;
    public static final int D2 = 3;
    public static final int R7 = 4;
    public static final int FA = 5;
    public static final int FB = 6;
    public static final int FC = 7;
    public static final int BN = 8;

    public static int NUM_SYMBOLS = 9;

    public static int[] BIG_SYMBOLS = {};

    private boolean winShow;

    private final Sprite[] sprites;
    private final Vector2[] offsets;

    public Symbol(SingleReelConfiguration cfg) {
        super(cfg);

        sprites = new Sprite[NUM_SYMBOLS];
        offsets = new Vector2[NUM_SYMBOLS];

        for (int id = 0; id < sprites.length; id++) {
            sprites[id] = ImageLoader.getInstance().newSprite("Symbol/" + id);
            Vector2 offset = CoordinateLoader.getInstance().getOffset("Symbol" + id);
            if (offset != null) {
                offsets[id] = offset;
            } else {
                offsets[id] = new Vector2();
            }
        }
        setSprite(sprites[symbolIndex]);
    }

    @Override
    public void setWinShowMode(boolean val) {
        if (winShow != val) {
            winShow = val;
        }
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
        sprite.setY(y + offsets[symbolIndex].y);
    }

    private boolean isBigSymbol() {
        for (int bs : Symbol.BIG_SYMBOLS) {
            if (symbolIndex == bs) return true;
        }
        return false;
    }

    @Override
    public int compareTo(Object o) {
        Symbol s = (Symbol) o;

        if (this.isBigSymbol()) {
            if (!s.isBigSymbol()) return 1;
        } else {
            if (s.isBigSymbol()) return -1;
        }

        return Math.round(s.getY() - this.getY());
    }
}
