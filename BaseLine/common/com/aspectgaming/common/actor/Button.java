package com.aspectgaming.common.actor;

import java.util.Arrays;

import com.aspectgaming.common.loader.ImageLoader;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

/**
 * The Button Actor
 * There are 5 state in the button. up, down, disable, checked, disabled checked.
 * If checked and disabled checked image is null, the button is a push button, or the button is a check button.
 * 
 * @author johnny.shi
 *
 */
public class Button extends Actor {

    private static final int UP = 0;
    private static final int DOWN = 1;
    private static final int DISABLED = 2;
    private static final int CHECKED = 3;
    private static final int DISABLED_CHECKED = 4;
    private static final int UP_SWAP = 5;
    private static final int NUM_SKINS = 6;

    private boolean isChecked, isDisabled, isHightlight;
    private int flag;

    private volatile String[] paths = new String[NUM_SKINS];
    private Sprite[] sprites = new Sprite[NUM_SKINS];
    private ClickListener clickListener;

    private Runnable onClicked = null;
    private boolean isTouchAreaSet = false;
    private float touchX = 0;
    private float touchY = 0;
    private float touchWidth = 0;
    private float touchHeigh = 0;
    private boolean isShowSwap = false;
    /**
     * 
     * @param skin
     *            the skin path, up state file should named to *_up.png, down state file should named to *_down.png, disable state file should named to
     *            *_disable.png, checked state file should named to *_checked.png, disable checked state file should named to *_disable_checked.png
     */
    public Button(String skin) {
        updateSkin(skin);
        init();
    }
    /**
     * 
     * @param images
     *            the skin path, the path and name up to you.
     */
    public Button(String... images) {
        paths = Arrays.copyOf(images, NUM_SKINS);
        init();
    }

    private void init() {
        updateLanguage();

        isHightlight = false;
        setBounds(0, 0, sprites[UP].getWidth(), sprites[UP].getHeight());

        addListener(clickListener = new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (onClicked != null) onClicked.run();
            }
        });
    }

    public void updateSkin(String skin) {
        paths[UP] = skin + "up";
        paths[DOWN] = skin + "down";
        paths[DISABLED] = skin + "disable";
        paths[CHECKED] = skin + "checked";
        paths[DISABLED_CHECKED] = skin + "disable_checked";
        paths[UP_SWAP] = skin + "swap";
        updateLanguage();
    }

    public void showSwap(boolean show) {
        isShowSwap = show;
    }

    public void updateSkin(String... images) {
        paths = Arrays.copyOf(images, NUM_SKINS);

        updateLanguage();
    }

    public void updateLanguage() {
        for (int i = 0; i < NUM_SKINS; i++) {
            if (paths[i] != null) {
                sprites[i] = ImageLoader.getInstance().newSprite(paths[i]);

                if (sprites[i] == null) {
                    paths[i] = null;
                }
            }
        }
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int val) {
        flag = val;
    }

    public void setOnClicked(Runnable val) {
        onClicked = val;
    }

    public boolean isDisabled() {
        return isDisabled;
    }

    public void setDisabled(boolean val) {
        isDisabled = val;
        setTouchable(isDisabled ? Touchable.disabled : Touchable.enabled);
    }

    public void setHightlight(boolean val) {
        isHightlight = val;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean val) {
        isChecked = val;
    }

    public void setTouchArea(float x, float y, float width, float height) {
        isTouchAreaSet = true;
        touchX = x;
        touchY = y;
        touchWidth = width;
        touchHeigh = height;
    }

    public void setTouchArea(Rectangle area) {
        isTouchAreaSet = true;
        touchX = area.x;
        touchY = area.y;
        touchWidth = area.width;
        touchHeigh = area.height;
    }

    @Override
    public Actor hit(float x, float y, boolean touchable) {
        if (getAlpha() < 0.01) return null;
        if (isTouchAreaSet) {
            if (touchable && !isTouchable()) return null;
            float startX = touchX - this.getX();
            float startY = touchY - getY();
            return x >= startX && x < touchWidth + startX && y >= startY && y < touchHeigh + startY ? this : null;
        } else {
            return super.hit(x, y, touchable);
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        Sprite sp = null;

        if (isChecked) {
            if (isDisabled) {
                if (isHightlight) {
                    sp = sprites[CHECKED];
                }else {
                    sp = sprites[DISABLED_CHECKED];
                }
            }
            if (!isDisabled || sp == null) {
                sp = sprites[CHECKED];
            }
        } else {
            if (isDisabled) {
                sp = sprites[DISABLED];
            } else if (clickListener.isPressed()) {
                sp = sprites[DOWN];
            }
        }

        if (sp == null) {
            if (isShowSwap && sprites[UP_SWAP] != null) {
                sp = sprites[UP_SWAP];
            }else {
                sp = sprites[UP];
            }
        }

        sp.setX(getX());
        sp.setY(getY());

        sp.setColor(getColor());
        sp.draw(batch, parentAlpha);

//      batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
//      background.draw(batch, getX(), getY(), getWidth(), getHeight());

    }
}
