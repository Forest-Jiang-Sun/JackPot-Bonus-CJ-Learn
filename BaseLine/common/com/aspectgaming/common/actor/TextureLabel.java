package com.aspectgaming.common.actor;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aspectgaming.common.loader.CoordinateLoader;
import com.aspectgaming.common.loader.FontLoader;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.utils.Align;

public class TextureLabel extends Actor {

    private String fontName;
    private SpriteFont spFont;
    private Label label;
    private int horizontalAlign;
    private int verticalAlign;
    private List<Sprite> sprites = new ArrayList<>();
    private String text = "";

    private int textWidth;
    private String hideText = null;

    private float spriteScale = 1.0f;

    public TextureLabel(String fontName, int horizontal, int vertical) {
        this.fontName = fontName;
        setTouchable(Touchable.disabled);

        BitmapFont font = FontLoader.getInstance().load(fontName);
        if (font != null) {
            label = new Label("", new LabelStyle(font, new Color(Color.WHITE)));
            label.setAlignment(horizontal, vertical);
        } else {
            spFont = FontLoader.getInstance().loadSpriteFont(fontName);
        }

        this.horizontalAlign = horizontal;
        this.verticalAlign = vertical;
    }

    public TextureLabel(String font, int horizontal, int vertical, String boundName) {
        this(font, horizontal, vertical);

        setBounds(CoordinateLoader.getInstance().getBound(boundName));
    }

    public TextureLabel(String font, Color color, int horizontal, int vertical, String boundName) {
        this(font, horizontal, vertical);

        setColor(color);
        setBounds(CoordinateLoader.getInstance().getBound(boundName));
    }
    
    public void setBounds(String boundName)
    {
        setBounds(CoordinateLoader.getInstance().getBound(boundName));
    }

    public void setBounds(Rectangle rectangle) {
        setBounds(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
    }

    @Override
    public void setBounds(float x, float y, float width, float height) {
        super.setBounds(x, y, width, height);

        if (label != null) {
            label.setBounds(x, y, width, height);
        } else {
            layout();
        }
    }

    @Override
    public void setPosition(float x, float y) {
        super.setPosition(x, y);

        if (label != null) {
            label.setPosition(x, y);
        } else {
            layout();
        }
    }

    @Override
    public void moveBy(float x, float y) {
        super.moveBy(x, y);

        if (label != null) {
            label.moveBy(x, y);
        } else {
            for (Sprite sprite : sprites) {
                sprite.translate(x, y);
            }
        }
    }

    @Override
    public Actor hit(float x, float y, boolean touchable) {
        if (getAlpha() < 0.01) return null;
        return super.hit(x, y, touchable);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        if (label != null) {
            label.setColor(getColor());
            label.draw(batch, parentAlpha);
        } else {
            parentAlpha *= getColor().a;

            for (Sprite sprite : sprites) {
                sprite.setColor(getColor());
                sprite.draw(batch, parentAlpha);
            }
        }
    }

    public void setMaxWidth(float spriteNum) {
        if (getText().length() > spriteNum) {
            float scale = (spriteNum + 1.2f)/getText().length();
            setSpriteScale(scale);
        }else {
            setSpriteScale(1.0f);
        }
    }

    public void setDenomMaxWidth() {
        if (getText().length() > 3) {
            float scale = 3.7f/getText().length();
            setSpriteScale(scale);
        }else {
            setSpriteScale(1.0f);
        }
    }

    private void setSpriteScale (float scaleXY) {
        for (Sprite sprite : sprites) {
            sprite.setScale(scaleXY);
        }

        spriteScale = scaleXY;
        layout();
    }

    private void layout() {
        int width = 0;
        switch (horizontalAlign) {
        case Align.left:
            for (Sprite sprite : sprites) {
                sprite.setX(getX() + width);
                width += sprite.getWidth() * spriteScale;
            }
            break;
        case Align.right:
            for (int i = sprites.size() - 1; i >= 0; i--) {
                Sprite sprite = sprites.get(i);
                width += sprite.getWidth() * spriteScale;
                sprite.setX(getX() + (getWidth() - width));
            }
            break;
        default:
            float offset = (getWidth() - textWidth*spriteScale) / 2;
            for (Sprite sprite : sprites) {
                sprite.setX(getX() + offset + width);
                width += sprite.getWidth() * spriteScale;
            }
            break;
        }

        switch (verticalAlign) {
        case Align.bottom:
            for (Sprite sprite : sprites) {
                sprite.setY(getY());
            }
            break;
        case Align.top:
            for (Sprite sprite : sprites) {
                sprite.setY(getY() + getHeight() - sprite.getHeight());
            }
            break;
        default:
            for (Sprite sprite : sprites) {
                sprite.setY(getY() + (getHeight() - sprite.getHeight()) / 2);
            }
            break;
        }
    }

    public void autoHide(String val) {
        hideText = val;
        setText(text);
    }

    public void setValue(long val) {
        setText(Long.toString(val));
    }

    public void setText(String val) {
        if (hideText != null && hideText.equals(val)) {
            val = "";
        }

        if (text.equals(val)) return;
        text = val;

        if (label != null) {
            label.setText(text);
        } else {
            this.sprites.clear();
            this.textWidth = 0;
            for (int i = 0; i < text.length(); i++) {
                char c = text.charAt(i);
                Sprite sprite = spFont.newSprite(c);
                if (sprite == null) {
                    Logger log = LoggerFactory.getLogger(TextureLabel.class);
                    log.error("Missing character {} in {} when using font {}", c, text, fontName);
                    System.exit(1);
                }
                this.sprites.add(sprite);
                sprite.setColor(getColor());
                this.textWidth += sprite.getWidth();
            }
            layout();
        }
    }

    public  void setForamtVal(long val){
        setFormatText(Long.toString(val));
    }

    public void setFormatText(String val){
        if (hideText != null && hideText.equals(val)) {
            val = "";
        }

        if (text.equals(val)) return;

        String tmpVal = "";
        int index = val.indexOf(".");
        int length = val.length();

        if(index != -1){
            for (int i = length - 1; i >= index; i--){
                tmpVal += val.charAt(i);
            }
            length = index;
        }

        int count = 0;
        for(int i = length - 1; i >= 0; i --) {
            count++;
            tmpVal += val.charAt(i);
            if (count % 3 == 0 && i != 0) {
                tmpVal += ',';
            }
        }

        val = "";
        for(int i = tmpVal.length() - 1; i >= 0; i--){
            val += tmpVal.charAt(i);
        }

        setText(val);
    }

    public String getText() {
        return text;
    }

    /*
    public int getTextWidth() {
        if (label != null) {
            return Math.round(label.getTextBounds().width);
        }
        return textWidth;
    }
    */

    public void setWrap(boolean wrap) {
        label.setWrap(wrap);
    }
}
