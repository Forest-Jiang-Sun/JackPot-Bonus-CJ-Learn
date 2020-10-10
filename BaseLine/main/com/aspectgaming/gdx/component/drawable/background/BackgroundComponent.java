package com.aspectgaming.gdx.component.drawable.background;

import com.aspectgaming.common.actor.Animation;
import com.aspectgaming.common.actor.Image;
import com.aspectgaming.common.actor.Sound;
import com.aspectgaming.common.data.GameData;
import com.aspectgaming.common.data.GameMode;
import com.aspectgaming.common.event.GameModeChangeEvent;
import com.aspectgaming.common.event.game.GameResetEvent;
import com.aspectgaming.common.event.game.ReelStartSpinEvent;
import com.aspectgaming.common.event.game.ReelStoppedEvent;
import com.aspectgaming.common.event.machine.InTiltEvent;
import com.aspectgaming.common.event.machine.LanguageChangedEvent;
import com.aspectgaming.common.event.machine.OutTiltEvent;
import com.aspectgaming.common.loader.ImageLoader;
import com.aspectgaming.common.loader.SoundLoader;
import com.aspectgaming.gdx.component.drawable.DrawableComponent;
import com.aspectgaming.net.game.GameClient;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.AddAction;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

/**
 * show background about basegame and freegame with fade in or fade out.
 *
 * @author ligang.yao & johnny.shi
 */
public class BackgroundComponent extends DrawableComponent {

    private final Image imgBaseGame;
    private final Image imgbasegame_city;
    private Animation Lighting;

    public BackgroundComponent() {
        imgbasegame_city=ImageLoader.getInstance().load("Background/basegame_city", "Background");
        addActor(imgbasegame_city);

        Lighting=new Animation("Lighting/");
        Lighting.loop();

        addActor(Lighting);


        imgBaseGame = ImageLoader.getInstance().load("Background/basegame", "Background");
        addActor(imgBaseGame);


    }
    @Override
    protected void update(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            addAction(Actions.run(GameClient.getInstance().buttonPlay));
        }
    }
}
