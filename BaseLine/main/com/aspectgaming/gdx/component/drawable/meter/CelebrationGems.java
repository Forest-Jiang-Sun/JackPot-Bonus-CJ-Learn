package com.aspectgaming.gdx.component.drawable.meter;

import com.aspectgaming.common.actor.Animation;
import com.aspectgaming.common.actor.ShapeAnimation;
import com.aspectgaming.common.actor.SpineAnimation;
import com.badlogic.gdx.scenes.scene2d.Group;

public class CelebrationGems extends Group {
    private ShapeAnimation gem1;
    private ShapeAnimation gem2;
    //private Animation gem;
    public CelebrationGems() {
        gem1 = new ShapeAnimation("BigWin", "0001",  "5_stones","Background");
        gem2 = new ShapeAnimation("BigWin", "1",  "5_stones","Background");

        addActor(gem1);
        addActor(gem2);
        //gem = new Animation("BigWin/gem/");
        //gem.setPosition(-10, -8);
        //gem.setAutoVisible(true);
        //addActor(gem);
    }

    public void play() {
        gem1.play(false);
        gem2.play(false);
        //gem.stop();
        //gem.play();
    }

    public void stop() {
        gem1.stop();
        gem2.stop();
        //gem.stop();
    }
}

