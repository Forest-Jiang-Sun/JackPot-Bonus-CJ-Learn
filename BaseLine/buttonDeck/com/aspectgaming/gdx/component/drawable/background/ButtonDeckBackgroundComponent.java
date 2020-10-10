package com.aspectgaming.gdx.component.drawable.background;

import com.aspectgaming.common.actor.Animation;
import com.aspectgaming.common.actor.Image;
import com.aspectgaming.common.data.GameData;
import com.aspectgaming.common.data.GameMode;
import com.aspectgaming.common.data.State;
import com.aspectgaming.common.event.GameModeChangeEvent;
import com.aspectgaming.common.event.game.GameResetEvent;
import com.aspectgaming.common.event.machine.LanguageChangedEvent;
import com.aspectgaming.common.loader.CoordinateLoader;
import com.aspectgaming.common.loader.ImageLoader;
import com.aspectgaming.gdx.component.drawable.DrawableComponent;
import com.badlogic.gdx.math.Vector2;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeOut;

public class ButtonDeckBackgroundComponent extends DrawableComponent {
    //成员变量背景图片
    private final Image Bg;

    //创建ImageLoader实例 调用其方法加载指定的图片作为背景
    public ButtonDeckBackgroundComponent() {
        Bg = ImageLoader.getInstance().load("Background/PanelBG", "Background");

        //Actor模式？？
        addActor(Bg);

    }
}