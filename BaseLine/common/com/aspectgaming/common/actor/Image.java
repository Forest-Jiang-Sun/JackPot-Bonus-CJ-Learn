package com.aspectgaming.common.actor;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasSprite;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;

/**
 * The image actor
 * 
 * @author johnny.shi
 *
 */
public class Image extends Actor {

    private String path;
    protected Sprite sprite;
    private Rectangle area;

    private boolean flipX;
    private boolean flipY;

    public Image() {
        setTouchable(Touchable.disabled);
    }
    /**
     * Set the image sprite
     * 
     * @param val
     *            Sprite
     */
    public void setSprite(Sprite val) {
        if (sprite != val) {
            setWidth(val.getWidth());
            setHeight(val.getHeight());

            val.setPosition(getX(), getY());
            val.setRotation(getRotation());
            val.setScale(getScaleX(), getScaleY());
            val.setFlip(flipX, flipY);

            if (sprite != null) {
                val.setOrigin(getOriginX(), getOriginY());
            } else {
                super.setOrigin(val.getOriginX(), val.getOriginY());
            }
            sprite = val;
        }
    }
    /**
     * Get the image sprite
     * 
     * @return image sprite
     */
    public Sprite getSprite() {
        return sprite;
    }

    public void setPath(String val) {
        path = val;
    }

    public String getPath() {
        return path;
    }

    public void setViewArea(Rectangle val) {
        area = val;
    }

    @Override
    public void setX(float x) {
        super.setX(x);
        sprite.setX(x);
    }

    @Override
    public void setY(float y) {
        super.setY(y);
        sprite.setY(y);
    }

    @Override
    public void setPosition(float x, float y) {
        super.setPosition(x, y);
        sprite.setPosition(x, y);
    }

    @Override
    public void moveBy(float x, float y) {
        super.moveBy(x, y);
        sprite.translate(x, y);
    }

    @Override
    public void setRotation(float degrees) {
        super.setRotation(degrees);
        sprite.setRotation(degrees);
    }

    @Override
    public void setOrigin(float originX, float originY) {
        super.setOrigin(originX, originY);
        sprite.setOrigin(originX, originY);
    }

    @Override
    public void rotateBy(float amountInDegrees) {
        super.rotateBy(amountInDegrees);
        sprite.rotate(amountInDegrees);
    }

    @Override
    public void setScaleX(float scaleX) {
        super.setScaleX(scaleX);
        sprite.setScale(scaleX, sprite.getScaleY());
    }

    @Override
    public void setScaleY(float scaleY) {
        super.setScaleY(scaleY);
        sprite.setScale(sprite.getScaleX(), scaleY);
    }

    @Override
    public void setScale(float scale) {
        super.setScale(scale);
        sprite.setScale(scale);
    }

    @Override
    public void setScale(float scaleX, float scaleY) {
        super.setScale(scaleX, scaleY);
        sprite.setScale(scaleX, scaleY);
    }

    @Override
    public void scaleBy(float scale) {
        super.scaleBy(scale);
        sprite.scale(scale);
    }

    public void setFlip(boolean x, boolean y) {
        sprite.setFlip(x, y);
        flipX = x;
        flipY = y;
    }

    @Override
    public void setBounds(float x, float y, float width, float height) {
        super.setBounds(x, y, width, height);
        sprite.setBounds(x, y, width, height);
    }

    @Override
    public Actor hit(float x, float y, boolean touchable) {
        if (getAlpha() < 0.01) return null;
        return super.hit(x, y, touchable);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (area != null) {
            // TODO: need to support scale and rotate

            float spX = getX();
            float spY = getY();
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
                int srcY = Math.round(sprite.getRegionY() + deltaYTop);

                batch.draw(sprite.getTexture(), x, y, width, height, srcX, srcY, Math.round(width), Math.round(height), false, false);

                color.a = oldAlpha;

                batch.setColor(oldColor);
            }

        } else {
            sprite.setColor(getColor());
            sprite.draw(batch, parentAlpha);
        }
    }

}
