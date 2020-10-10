package com.aspectgaming.gdx.component.drawable.progressivereel;

import com.aspectgaming.common.actor.Animation;
import com.aspectgaming.common.actor.Sound;
import com.aspectgaming.common.configuration.GameConfiguration;
import com.aspectgaming.common.data.Content;
import com.aspectgaming.common.data.GameData;
import com.aspectgaming.common.data.State;
import com.aspectgaming.common.event.game.GameStateChangedEvent;
import com.aspectgaming.common.event.game.ProgressiveSingleReelResultsStartEvent;
import com.aspectgaming.common.loader.SoundLoader;
import com.aspectgaming.common.loader.VideoLoader;
import com.aspectgaming.common.video.Video;
import com.aspectgaming.gdx.component.drawable.DrawableComponent;
import com.aspectgaming.gdx.component.drawable.reel.ReelComponent;
import com.aspectgaming.gdx.component.drawable.reel.Symbol;
import com.aspectgaming.net.game.data.SettingData;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

import java.util.ArrayList;
import java.util.List;

import static com.aspectgaming.common.data.GameConst.getSelectionPositions;

public class ProgressiveCoins extends DrawableComponent {
//    private final List<Animation> coins = new ArrayList<>();
//    private final List<Animation> symbolHighlight = new ArrayList<>();
//    //private Video coinHitBottom ;
//    private Sound gong2;
//    private int numReels;
//    private final Sound sndGong;

    public ProgressiveCoins() {
//        numReels = GameConfiguration.getInstance().reel.reels.length;
//        gong2 = SoundLoader.getInstance().get("progressive/Gong2");
//        sndGong = SoundLoader.getInstance().get("progressive/Gong");
//
//        for (int j=0; j<5; ++j) {
//            Animation highlight = new Animation("SymbolHighlight/SparkleCoin/");
//            highlight.setAutoVisible(true);
//            addActor(highlight);
//            symbolHighlight.add(highlight);
//        }
//
//        for (int i=1; i<=20; ++i) {
//            Animation coin = new Animation("Progressive/CoinBottom" + i + "/");
//            coin.setPosition(0, 0);
//            coin.setAutoVisible(true);
//            addActor(coin);
//            coins.add(coin);
//        }
//
//        registerEvent(new ProgressiveSingleReelResultsStartEvent() {
//            @Override
//            public void execute(Object... obj) {
//                boolean isDim = (boolean)obj[0];
//                boolean isSkip = (boolean)obj[1];
//                int line = (int)obj[2];
//                if (isDim && !isSkip) {
//                    playProgressiveCoins(line);
//                }
//            }
//        });
//
//        registerEvent(new GameStateChangedEvent() {
//            @Override
//            public void execute(Object... obj) {
//                switch (GameData.getPrevious().Context.GameState) {
//                    case State.ProgressiveResults:
//                        reset();
//                        break;
//                    default:
//                        break;
//                }
//            }
//        });
    }

//    private void reset() {
//        clearActions();
//        for (int i=0; i<coins.size(); ++i) {
//            coins.get(i).stop();
//        }
//
//        for (int i=0; i<symbolHighlight.size(); ++i) {
//            symbolHighlight.get(i).stop();
//        }
//
//        sndGong.stop();
//        //coinHitBottom.stop();
//        gong2.stop();
//    }

//    private void playProgressiveCoins(int line) {
//        SettingData cfg = GameData.getInstance().Setting;
//        int[][] lintPositions = getSelectionPositions(cfg.MaxSelections, "");
//        ReelComponent reels = ((ReelComponent) Content.getInstance().getComponent(Content.REELCOMPONENT));
//        if (line >= 0 && line < cfg.MaxSelections) {
//            for (int i = 0; i < numReels; i++) {
//                Symbol symbol = reels.getSymbol(lintPositions[line][i]);
//                Animation highlight = symbolHighlight.get(i);
//                highlight.setPosition(symbol.getScreenX() - 84.5f, symbol.getScreenY() - 47);
//                highlight.play();
//
//                int row = lintPositions[line][i] / numReels;
//                int col = lintPositions[line][i] % numReels;
//                Animation coin = coins.get(col*4 + row);
//                coin.play();
//            }
//        }
//
//        if (sndGong != null) {
//            sndGong.play();
//        }
//    }
}
