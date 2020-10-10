package com.aspectgaming.gdx.component.drawable.info;

import com.aspectgaming.common.loader.CoordinateLoader;
import com.aspectgaming.common.loader.FontLoader;
import com.aspectgaming.gdx.component.drawable.DrawableComponent;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.utils.Align;

public class InfoComponent extends DrawableComponent {

    private final Label info;
    private int x;
    private int y;

    public InfoComponent() {
        Rectangle bounds = CoordinateLoader.getInstance().getBound("Info");
        info = new Label("", new LabelStyle(FontLoader.getInstance().load("SystemMessageFont"), Color.WHITE));
        info.setAlignment(Align.right);
        info.setBounds(bounds.x, bounds.y, bounds.width, bounds.height);

        addActor(info);
    }

    @Override
    protected void update(float delta) {
        int xNew = Gdx.app.getInput().getX();
        int yNew = Gdx.app.getInput().getY();

        if (x != xNew || y != yNew) {
            x = xNew;
            y = yNew;

            info.setText(x + "," + y);

            // if 1680x945 is set in GraphicsUtil.java, then add following lines.
            // int xRelative = x * 1920 / 1680;
            // int yRelative = y * 1080 / 945;
            // info.setText(xRelative + "," + yRelative);
        }
    }
}
