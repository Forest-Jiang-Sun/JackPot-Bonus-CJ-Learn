package com.aspectgaming.gdx.component.drawable.progressive;

import com.aspectgaming.common.actor.Animation;
import com.aspectgaming.common.actor.SpineAnimation;
import com.aspectgaming.common.data.GameData;
import com.aspectgaming.common.data.State;
import com.aspectgaming.common.event.game.GameStateChangedEvent;
import com.aspectgaming.common.event.game.ProgressiveSingleReelResultsStartEvent;
import com.aspectgaming.gdx.component.drawable.DrawableComponent;
import com.aspectgaming.net.game.data.SettingData;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

import java.util.ArrayList;
import java.util.List;

import static com.aspectgaming.common.data.GameConst.getSelectionPositions;

public class ProgressiveTopCoin extends DrawableComponent {
    private final List<Animation> coins = new ArrayList<>();
    //private Animation coinHitTop;
    private SpineAnimation coinHitTop;

    public ProgressiveTopCoin(){
        for (int i=1; i<=20; ++i) {
            Animation coin = new Animation("Progressive/CoinTop" + i + "/");
            coin.setPosition(0, 0);
            coin.setAutoVisible(true);
            addActor(coin);
            coins.add(coin);
        }

        /*
        coinHitTop = new Animation("Progressive/CoinHitTop/");//VideoLoader.Instance.load("Progressive/CoinHitTop"); //
        coinHitTop.setPosition(0, 0);
        coinHitTop.setAutoVisible(true);
        addActor(coinHitTop);
        */
        //coinHitTop = new SpineAnimation("CoinHitTop", "sparkle","animation", "CoinHitTop");
        //addActor(coinHitTop);

        registerEvent(new ProgressiveSingleReelResultsStartEvent() {
            @Override
            public void execute(Object... obj) {
                int line = (int)obj[0];
                playProgressiveCoins(line);
            }
        });

        registerEvent(new GameStateChangedEvent() {
            @Override
            public void execute(Object... obj) {
                switch (GameData.getPrevious().Context.GameState) {
                    case State.ProgressiveResults:
                        reset();
                        break;
                    default:
                        break;
                    }
            }
        });
    }

    private void playProgressiveCoins(int line) {
        SettingData cfg = GameData.getInstance().Setting;
        int[][] lintPositions = getSelectionPositions(cfg.MaxSelections, "");
        if (line >= 0 && line < cfg.MaxSelections) {
            for (int i = 0; i < 5; i++) {
                int row = lintPositions[line][i] / 5;
                int col = lintPositions[line][i] % 5;
                Animation coin = coins.get(col*4 + row);
                coin.play();
            }
        }

        addAction(Actions.delay(1.2f, Actions.run(()->coinHitTop.play(false))));
    }

    private void reset() {
        clearActions();
        for (int i=0; i<coins.size(); ++i) {
            coins.get(i).stop();
        }
        coinHitTop.stop();
    }
}