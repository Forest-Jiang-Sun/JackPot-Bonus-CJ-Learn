package com.aspectgaming.gdx.component.drawable.meter;


import com.aspectgaming.common.actor.Animation;
import com.aspectgaming.common.actor.ShapeAnimation;
import com.aspectgaming.common.actor.SpineAnimation;
import com.badlogic.gdx.scenes.scene2d.Group;

public class CelebrationCoins extends Group {
    private ShapeAnimation coin1;
    private ShapeAnimation coin2;
    //private Animation coin;

    public CelebrationCoins() {
        coin1 = new ShapeAnimation("BigWin", "0001",  "5_gold","Background");
        coin2 = new ShapeAnimation("BigWin", "1",  "5_gold","Background");

        addActor(coin1);
        addActor(coin2);
        //coin = new Animation("BigWin/coin/");
        //setPosition(-10, -8);
        //coin.setAutoVisible(true);
        //addActor(coin);
    }

    public void play() {
        coin1.play(false);
        coin2.play(false);
        //coin.stop();
        //coin.play();
    }

    public void stop() {
        coin1.stop();
        coin2.stop();
        //coin.stop();
    }
}
