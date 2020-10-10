package com.aspectgaming.gdx.component.drawable.progressive;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aspectgaming.common.action.*;
import com.aspectgaming.common.actor.SpriteFont;
import com.aspectgaming.common.configuration.GameConfiguration;
import com.aspectgaming.common.data.GameData;
import com.aspectgaming.common.loader.CoordinateLoader;
import com.aspectgaming.common.loader.FontLoader;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasSprite;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;

/**
 * @author Johnson.Deng
 */
public class RollingMeter extends Actor {

    private final Logger log = LoggerFactory.getLogger(RollingMeter.class);

    private String fontName;
    private SpriteFont spFont;
    private SpriteFont nomalStringFont;
    private SpriteFont longStringFont;

    private List<Sprite> sprites = new ArrayList<>();
    private List<Sprite> sprites_down = new ArrayList<>();

    private boolean rollingAction = false;
    private boolean carryAction = false;
    private boolean playingAction = false;

    private long value = -1;
    private long valueStart = -1;
    private long valueEnd = -1;
    private long valueExt = -1;
    private long valueEndExt = -1;
    private long valueTarget = -1;
    private int valueExtPercent = 0;
    private String carryString;
    private String valueString;

    private float rollingTime;
    private float rollingInterval;
    private int rollingRangeValue;
    private float maxStringLen = 20;

    private Rectangle initArea = null;
    private Rectangle longSringArea = null;
    private Rectangle area = null;

    public RollingMeter(String font, Color color, String boundName) {
        fontName = font;
        value = 0;

        setTouchable(Touchable.disabled);

        spFont = FontLoader.getInstance().loadSpriteFont(font);
        nomalStringFont = spFont;

        rollingTime = GameConfiguration.getInstance().rollingMeter.rollingTime;
        rollingInterval = GameConfiguration.getInstance().rollingMeter.rollingInterval;
        rollingRangeValue = GameConfiguration.getInstance().rollingMeter.rollingRangeValue;

        setColor(color);

        initArea = CoordinateLoader.getInstance().getBound(boundName);

        setBounds(initArea.x, initArea.y, initArea.width, initArea.height);
        setViewArea(initArea);
    }

    @Override
    public void setBounds(float x, float y, float width, float height) {
        super.setBounds(x, y, width, height);

        layoutExt();
    }

    @Override
    public void setPosition(float x, float y) {
        super.setPosition(x, y);

        layoutExt();
    }

    @Override
    public void moveBy(float x, float y) {
        super.moveBy(x, y);

        for (Sprite sprite : sprites) {
            sprite.translate(x, y);
        }
        for (Sprite sprite : sprites_down) {
            sprite.translate(x, y);
        }

        Rectangle rect = new Rectangle(getX(), getY(), getWidth(), getHeight());
        setBounds(rect.x, rect.y, rect.width, rect.height);
        setViewArea(rect);
    }

    @Override
    public void setScaleX(float scaleX) {
        super.setScaleX(scaleX);
        for (Sprite sprite : sprites) {
            sprite.setScale(scaleX, sprite.getScaleY());
        }
        for (Sprite sprite : sprites_down) {
            sprite.setScale(scaleX, sprite.getScaleY());
        }

        updateViewArea();
    }

    @Override
    public void setScaleY(float scaleY) {
        super.setScaleY(scaleY);
        for (Sprite sprite : sprites) {
            sprite.setScale(sprite.getScaleX(), scaleY);
        }
        for (Sprite sprite : sprites_down) {
            sprite.setScale(sprite.getScaleX(), scaleY);
        }

        updateViewArea();
    }

    @Override
    public void setScale(float scale) {
        super.setScale(scale);
        for (Sprite sprite : sprites) {
            sprite.setScale(scale);
        }
        for (Sprite sprite : sprites_down) {
            sprite.setScale(scale);
        }

        updateViewArea();
    }

    @Override
    public void setScale(float scaleX, float scaleY) {
        super.setScale(scaleX, scaleY);
        for (Sprite sprite : sprites) {
            sprite.setScale(scaleX, scaleY);
        }
        for (Sprite sprite : sprites_down) {
            sprite.setScale(scaleX, scaleY);
        }

        updateViewArea();
    }

    @Override
    public void scaleBy(float scale) {
        super.scaleBy(scale);
        for (Sprite sprite : sprites) {
            sprite.scale(scale);
        }
        for (Sprite sprite : sprites_down) {
            sprite.scale(scale);
        }

        updateViewArea();
    }

    public void setLongStringInfo(float len, String font, String boundName) {
        maxStringLen = len;
        longStringFont = FontLoader.getInstance().loadSpriteFont(font);
        longSringArea = CoordinateLoader.getInstance().getBound(boundName);
    }

    private void updateViewArea() {
        Rectangle rect = area;
        rect.x = area.x + rect.getWidth()*(1-getScaleX())/2;
        rect.y = area.y + rect.getHeight()*(1-getScaleY())/2;
        rect.width = area.width;
        rect.height = area.height;
        setViewArea(rect);
    }

    @Override
    public Actor hit(float x, float y, boolean touchable) {
        if (getAlpha() < 0.01) return null;
        return super.hit(x, y, touchable);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (area != null) {
            for (Sprite sprite : sprites) {
                drawSprite(sprite, batch, parentAlpha);
            }
            for (Sprite sprite1 : sprites_down) {
                drawSprite(sprite1, batch, parentAlpha);
            }

        } else {
            super.draw(batch, parentAlpha);

            float alpha = getColor().a;
            getColor().a *= parentAlpha;

            for (Sprite sprite : sprites) {
                sprite.setColor(getColor());
                sprite.draw(batch);
            }
            for (Sprite sprite1 : sprites_down) {
                sprite1.setColor(getColor());
                sprite1.draw(batch);
            }
            getColor().a = alpha;
        }

    }

    @Override
    public void clearActions () {
    }

    private void drawSprite(Sprite sprite, Batch batch, float parentAlpha) {
        float spX = sprite.getX();
        float spY = sprite.getY();
        float spW = sprite.getRegionWidth();
        float spH = sprite.getRegionHeight();

        if (sprite instanceof AtlasSprite) {
            AtlasRegion ar = ((AtlasSprite) sprite).getAtlasRegion();
            spX += ar.offsetX;
            spY += ar.offsetY;
        }

        float x = Math.max(area.x, spX);
        float y = Math.max(area.y, spY);

        float width = Math.min(area.x + area.width, spX + spW) - x;
        float height = Math.min(area.y + area.height, spY + spH) - y;

        if (width > 0 && height > 0) {
            Color oldColor = batch.getColor();

            Color color = getColor();
            float oldAlpha = color.a;
            color.a *= parentAlpha;
            batch.setColor(color);

            float deltaX = x - spX;
            float deltaYTop = spY + spH - y - height;
            int srcX = Math.round(sprite.getRegionX() + deltaX);
            int srcY = Math.round(sprite.getRegionY() + deltaYTop );

            batch.draw(sprite.getTexture(), x, y, width*sprite.getScaleX(), height*sprite.getScaleY() , srcX, srcY, Math.round(width), Math.round(height), false, false);

            color.a = oldAlpha;

            batch.setColor(oldColor);
        }
    }

    private void layoutExt() {
        int width = 0;
        float offset = 0;
        float allwidth = 0;
        if(sprites.size()>sprites_down.size()){
            for (int i = sprites.size() - 1; i >= 0; i--) {
                Sprite sprite = sprites.get(i);
                allwidth += sprite.getWidth()*sprite.getScaleX();
                // sprite.setX((getX() + getWidth())/2+sprites.get(0).getWidth()*sprites.size()/2 - width);
            }
        }else{
            for (int i = sprites_down.size() - 1; i >= 0; i--) {
                Sprite sprite = sprites_down.get(i);
                allwidth += sprite.getWidth()*sprite.getScaleX();
                // sprite.setX((getX() + getWidth())/2+sprites.get(0).getWidth()*sprites.size()/2 - width);
            }
        }
        
        for (int i = sprites.size() - 1; i >= 0; i--) {
            Sprite sprite = sprites.get(i);
            width += sprite.getWidth()*sprite.getScaleX();
            //sprite.setX(getX() + (1.0f-sprite.getScaleX())*getWidth()/2.0f + getWidth()*sprite.getScaleX() / 2 + allwidth / 2 - width);
            sprite.setX(getX() + (1.0f-getScaleX())*getWidth()/2.0f + getWidth()*getScaleX() / 2 + allwidth / 2 - width);
            //sprite.setX(getX() + (1.0f-sprite.getScaleX())*getWidth()/2.0f + getWidth()*sprite.getScaleX()  - width);
        }
        width = 0;
        for (int i = sprites_down.size() - 1; i >= 0; i--) {
            Sprite sprite = sprites_down.get(i);
            width += sprite.getWidth()*sprite.getScaleX();
            //sprite.setX(getX() + (1.0f-sprite.getScaleX())*getWidth()/2.0f + getWidth()*sprite.getScaleX() / 2 + allwidth / 2 - width);
            sprite.setX(getX() + (1.0f-getScaleX())*getWidth()/2.0f + getWidth()*getScaleX() / 2 + allwidth / 2 - width);
            //sprite.setX(getX() + (1.0f-sprite.getScaleX())*getWidth()/2.0f + getWidth()*sprite.getScaleX() - width);
        }

        int count = 0;
        for (int i = sprites.size() - 1; i >= 0; i--) {
            Sprite sprite = sprites.get(i);
            int pos = sprites_down.size() - count - 1;

            if (sprites_down.size() - count > 0 && carryString.length() - count > 0) {
                Sprite sprite1 = sprites_down.get(pos);
                if ((carryAction) && (carryString.charAt(pos) == '.' || carryString.charAt(pos) == ',' || carryString.charAt(pos) == valueString.charAt(i))) {
                    offset = 0;
                } else {
                    offset = valueExtPercent * sprite.getHeight()* sprite.getScaleY() / 100;
                }

                sprite.setY(getY() +(1.0f-getScaleY())*getHeight()/2.0f+ (getHeight()* getScaleY()- sprite.getHeight() * sprite.getScaleY() )/ 2 + offset);
                sprite1.setY(getY() + (1.0f-getScaleY())*getHeight()/2.0f + (getHeight()* getScaleY() - sprite1.getHeight() * sprite1.getScaleY()) / 2 - sprite1.getHeight() *sprite1.getScaleY() + offset);
            } else {
                sprite.setY(getY() + (1.0f-getScaleY())*getHeight()/2.0f + (getHeight()* getScaleY() - sprite.getHeight()* sprite.getScaleY()) / 2);
            }
            count++;

        }
        if (sprites_down.size() > sprites.size() && carryAction) {
            for (int i = 0; i < sprites_down.size() - sprites.size(); i++) {
                Sprite sprite1 = sprites_down.get(i);
                offset = valueExtPercent * sprite1.getHeight()*sprite1.getScaleY() / 100f;
                sprite1.setY(getY() + (1.0f-getScaleY())*getHeight()/2.0f + (getHeight()*getScaleY() - sprite1.getHeight() * sprite1.getScaleY()) / 2 - sprite1.getHeight()*sprite1.getScaleY() + offset);
            }
        }
    }

    private final LongIntAction frameAction = new LongIntAction() {
        @Override
        protected void update(float percent) {
            super.update(percent);
            if (rollingAction) {
                long valueCur = getValue();
                if (valueExt != valueCur) {// Start rolling
                    valueExtPercent = (int) (valueCur % 100);
                    // judge whether it will use carry.

                    valueExt = valueCur;
                    value = valueExt / 100;
                    carryAction = checkCarry(valueCur);
                    valueString = GameData.Currency.format(valueCur / 100);

                    if (carryAction) {
                        setTextExtCarry();
                    } else {
                        setTextExt();
                    }
                }

            } else {
                long valueCur = getValue();
                if (value != valueCur) {
                    value = valueCur;
                    carryAction = true;
                    valueExtPercent = (int) ((valueCur - valueStart) * 100 / (valueEnd - valueStart));
                    carryString = GameData.Currency.format(valueEnd);
                    valueString = GameData.Currency.format(valueStart);
                    setTextExtCarry();
                }
            }
        }
    };

    private boolean checkCarry(long value) {
        boolean result = false;
        boolean head = true;
        carryString = "";
        long valueOrg = value / 100;

        while (valueOrg != 0) {
            if (valueOrg % 10 == 9) {
                result = true;
                valueOrg = valueOrg / 10;
                carryString = "0" + carryString;
            } else {
                carryString = String.valueOf(valueOrg % 10 + 1) + carryString;
                head = false;
                break;
            }
        }
        if (head) {
            carryString = 1 + carryString;
        }

        if (result) {
            if (carryString.length() > String.valueOf(value / 100).length()) {
                if (carryString.length() > 3) {
                    carryString = GameData.Currency.format(Long.valueOf(carryString));
                } else if (carryString.length() == 3) {
                    carryString = GameData.Currency.format(Long.valueOf(carryString));
                    carryString = carryString.substring(1, carryString.length());
                } else {
                    carryString = GameData.Currency.format(Long.valueOf(carryString));
                    carryString = carryString.substring(3, carryString.length());
                }
            } else {
                if (carryString.length() == 2) {
                    carryString = GameData.Currency.format(Long.valueOf(carryString));
                    carryString = carryString.substring(3, carryString.length());
                } else {
                    carryString = GameData.Currency.format(Long.valueOf(carryString));
                    carryString = carryString.substring(1, carryString.length());
                }
            }
        }

        return result;
    }

    public void setValue(long val, boolean snap) {
        if (valueEnd == val) return;
        valueTarget = val;

        if (!playingAction) {
            startRollingAction(snap);
        }

    }

    private Runnable calibrate = new Runnable() {
        @Override
        public void run() {
            startRollingAction(false);
        }
    };

    private void startRollingAction(boolean snap) {
        valueEnd = valueTarget;
        float multiplier = 1;
        if (value >= valueEnd || snap) {
            value = valueEnd;
            carryString = "";
            valueString = GameData.Currency.format(value);
            rollingAction = false;
            carryAction = false;
            playingAction = false;
            setTextExt();
        } else {
            long valueIncrease = valueEnd - value;
            if (valueIncrease <= rollingRangeValue) {

                Interpolation intpl = null;
                if (valueIncrease <= 5) {
                    multiplier = 1f;
                } else if (valueIncrease > 5 && valueIncrease <= 10) {
                    multiplier = 0.5f;
                    intpl = Interpolation.pow2Out;
                } else if (valueIncrease <= 20 && valueIncrease > 10) {
                    multiplier = 0.2f;
                    intpl = Interpolation.pow2Out;
                } else {
                    multiplier = 0.1f;
                    intpl = Interpolation.pow2Out;
                }
                // use the rolling action
                valueExt = value * 100; // use the Extension value to support the percentage.
                valueEndExt = valueEnd * 100;
                rollingAction = true;
                carryAction = false;
                removeAction(frameAction);
                frameAction.reset();
                frameAction.setStart(valueExt);
                frameAction.setEnd(valueEndExt);
                frameAction.setDuration(rollingInterval * valueIncrease * multiplier);
                frameAction.setInterpolation(intpl);
                addAction(sequence(frameAction, run(calibrate)));
            } else {
                // use the refresh action
                valueStart = value;
                rollingAction = false;
                carryAction = true;
                removeAction(frameAction);
                frameAction.reset();
                frameAction.setStart(value);
                frameAction.setEnd(valueEnd);
                if (value == 0){
                    value = valueEnd;
                    valueString = GameData.Currency.format(valueEnd);
                    setTextExt();
                    playingAction = false;
                    return;
                }else {
                    frameAction.setDuration(rollingTime);
                    addAction(sequence(frameAction, run(calibrate)));
                }
            }
            playingAction = true;
        }
    }

    public void setViewArea(Rectangle val) {
        area = val;
        layoutExt();
    }

    private void setTextExt() {
        String text = valueString;
        if (text.length() > maxStringLen) {
            //setViewArea(longSringArea);
            area = longSringArea;
            spFont = longStringFont;
        }else {
            //setViewArea(initArea);
            area = initArea;
            spFont = nomalStringFont;
        }

        sprites.clear();
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            Sprite s = newChar(c);
            s.setScale(getScaleX(), getScaleY());
            sprites.add(s);
        }

        sprites_down.clear();
        if (rollingAction) {
            char c = text.charAt(text.length() - 1);
            if (c == '9') {
                c = '0';
            } else {
                c++;
            }
            Sprite s = newChar(c);
            s.setScale(getScaleX(), getScaleY());
            sprites_down.add(s);
        }

        layoutExt();
    }

    private void setTextExtCarry() {
        String text = valueString;
        if (text.length() > maxStringLen) {
            //setViewArea(longSringArea);
            area = longSringArea;
            spFont = longStringFont;
        }else {
            //setViewArea(initArea);
            area = initArea;
            spFont = nomalStringFont;
        }
        sprites.clear();
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            Sprite s = newChar(c);
            s.setScale(getScaleX(), getScaleY());
            sprites.add(s);
        }

        sprites_down.clear();
        for (int i = 0; i < carryString.length(); i++) {
            char c = carryString.charAt(i);
            Sprite s = newChar(c);
            s.setScale(getScaleX(), getScaleY());
            sprites_down.add(s);
        }

        layoutExt();
    }

    private Sprite newChar(char c) {
        Sprite sp = spFont.newSprite(c);
        if (sp == null) {
            log.error("Missing character: {} in font: {}", c, fontName);
            System.exit(1);
        }
        sp.setColor(getColor());
        return sp;
    }
}
