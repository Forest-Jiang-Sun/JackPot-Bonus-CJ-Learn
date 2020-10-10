package com.aspectgaming.gdx.component.drawable.fps;

import com.aspectgaming.common.loader.CoordinateLoader;
import com.aspectgaming.common.loader.FontLoader;
import com.aspectgaming.gdx.component.drawable.DrawableComponent;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.utils.Align;

public class FPSComponent extends DrawableComponent {

    private final Label fpsLabel;
    private int fps;

    public FPSComponent() {
        Rectangle bounds = CoordinateLoader.getInstance().getBound("FPS");
        fpsLabel = new Label(" ", new LabelStyle(FontLoader.getInstance().load("SystemMessageFont"), Color.WHITE));
        fpsLabel.setAlignment(Align.left);
        fpsLabel.setBounds(bounds.x, bounds.y, bounds.width, bounds.height);
        addActor(this.fpsLabel);
    }

    @Override
    protected void update(float delta) {
        if (fps != Gdx.graphics.getFramesPerSecond()) {
            fps = Gdx.graphics.getFramesPerSecond();
            fpsLabel.setText("FPS:" + fps);
        }
    }
}
