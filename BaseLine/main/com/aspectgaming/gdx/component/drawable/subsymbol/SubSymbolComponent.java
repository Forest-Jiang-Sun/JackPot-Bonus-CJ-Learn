package com.aspectgaming.gdx.component.drawable.subsymbol;

import com.aspectgaming.common.actor.Animation;
import com.aspectgaming.common.actor.Image;
import com.aspectgaming.common.configuration.GameConfiguration;
import com.aspectgaming.common.data.GameConst;
import com.aspectgaming.common.data.GameData;
import com.aspectgaming.common.data.State;
import com.aspectgaming.common.event.GameModeChangeEvent;
import com.aspectgaming.common.event.freegame.InFreeGameIntroEvent;
import com.aspectgaming.common.event.freegame.InFreeGameOutroEvent;
import com.aspectgaming.common.event.freegame.OutFreeGameIntroEvent;
import com.aspectgaming.common.event.freegame.OutFreeGameOutroEvent;
import com.aspectgaming.common.event.game.*;
import com.aspectgaming.common.event.machine.InTiltEvent;
import com.aspectgaming.common.event.machine.LanguageChangedEvent;
import com.aspectgaming.common.event.machine.OutTiltEvent;
import com.aspectgaming.common.event.progressive.ProgressiveSkipEndEvent;
import com.aspectgaming.common.loader.CoordinateLoader;
import com.aspectgaming.common.loader.ImageLoader;
import com.aspectgaming.gdx.component.drawable.DrawableComponent;
import com.aspectgaming.gdx.component.drawable.reel.Symbol;
import com.aspectgaming.net.game.data.MathParam;
import com.aspectgaming.util.CommonUtil;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static com.aspectgaming.common.data.State.GambleChoice;
import static com.aspectgaming.common.data.State.GameIdle;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

public class SubSymbolComponent extends DrawableComponent {
    private final int numReels;
    private final int numRows;
    private boolean bAnimShowing;

    private HashMap<Integer, Image> subImages;
    private HashMap<Integer, Animation> subAnims;

    public SubSymbolComponent() {
        numReels = GameConfiguration.getInstance().reel.reels.length;
        numRows = GameData.getInstance().Context.Result.Stops.length / numReels;

        subImages = new HashMap<>();
        subAnims = new HashMap<>();

        registerEvent(new GameResetEvent() {
            @Override
            public void execute(Object... obj) {
                hideSubSymbol();
                stopProgressiveAnimation();
                int gameState = GameData.getInstance().Context.GameState;
                if (gameState == State.ProgressiveIntro || gameState == State.ProgressiveStarted ||
                        gameState == State.AwardSASProgressive || gameState == State.ProgressiveResults) {
                    showSubSymbol(numRows);
                }

                if (gameState == GameIdle || gameState == GambleChoice) {
                    playProgressiveAnimation();
                }
            }
        });

        registerEvent(new ReelStartSpinEvent() {
            @Override
            public void execute(Object... obj) {
                clear();
                hideSubSymbol();
                stopProgressiveAnimation();
            }
        });

        registerEvent(new ProgressiveSingleReelResultsStartEvent() {
            @Override
            public void execute(Object... obj) {
                boolean isDim = (boolean)obj[0];
                boolean isSkip = (boolean)obj[1];
                int line = (int)obj[2];
                int level = (int)obj[3];
                showProSingleReelResultSubAnim(isDim, isSkip, line, level);
            }
        });

        registerEvent(new ProgressiveReelOutroEvent() {
            @Override
            public void execute(Object... obj) {
                hideSubSymbol();
                playProgressiveAnimation();
            }
        });

        registerEvent(new LanguageChangedEvent() {
            @Override
            public void execute(Object... obj) {
                OnLanguageChanged();
            }
        });

        registerEvent(new GameStateChangedEvent() {
            @Override
            public void execute(Object... obj) {
                if (GameData.getInstance().Context.GameState == GameIdle || GameData.getInstance().Context.GameState == GambleChoice) {
                    hideSubSymbol();
                    playProgressiveAnimation();
                }
            }
        });

        registerEvent(new OutFreeGameOutroEvent() {
            @Override
            public void execute(Object... obj) {
                hideSubSymbol();
                stopProgressiveAnimation();
                playProgressiveAnimation();
            }
        });

        registerEvent(new OutFreeGameIntroEvent() {
            @Override
            public void execute(Object... obj) {
                stopProgressiveAnimation();
            }
        });

        registerEvent(new ProgressiveSkipEndEvent() {
            @Override
            public void execute(Object... obj) {
                hideSubSymbol();
                playProgressiveAnimation();
            }
        });

        registerEvent(new InTiltEvent() {
            @Override
            public void execute(Object... obj) {
                pause();
            }
        });

        registerEvent(new OutTiltEvent() {
            @Override
            public void execute(Object... obj) {
                resume();
            }
        });
    }

    private void OnLanguageChanged() {
        Iterator iter = subImages.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            Image image = (Image) entry.getValue();

            ImageLoader.getInstance().reload(image);
        }
    }

    public void showSubSymbol(int spinedCount) {
        int []jackpots = getJackpotInfo();
        int count = 0;
        if (jackpots != null) {
            for (int pos = 0; pos < jackpots.length; pos ++) {
                if (jackpots[pos] != 0 && count < spinedCount) {
                    int level = jackpots[pos];
                    showSubSymbol(pos, level);
                    count ++;
                }
            }
        }
    }

    public void showSubSymbol(int pos, int level) {
        if (level < 1 || level > Symbol.BN - Symbol.BL) return;
        if (subAnims.get(pos) != null) {
            subAnims.get(pos).setVisible(false);
            subAnims.remove(pos);
        }

        int subSymbolId = Symbol.BL + level;
        Image image = ImageLoader.getInstance().load("Symbol/" + subSymbolId);
        Rectangle rect = CoordinateLoader.getInstance().getBound("SubSymbol" + pos);
        image.setPosition(rect.getX(), rect.getY());
        image.setViewArea(rect);
        image.addAction(fadeIn(0.5f));
        addActor(image);
        subImages.put(pos, image);
    }

    private void createProgressiveAnimation() {
        subAnims.clear();
        int[] jackpots = getJackpotInfo();
        if (jackpots != null && jackpots.length > 0) {
            for (int pos = 0; pos < jackpots.length; pos++) {
                int level = jackpots[pos];
                if (level > 0) {
                    Rectangle rect = CoordinateLoader.getInstance().getBound("SubSymbol" + pos);
                    Animation anim = new Animation("WinShow/" + (Symbol.BL + level)+ "/" );
                    anim.setPosition(rect.getX(), rect.getY());
                    anim.setAutoVisible(true);
                    addActor(anim);
                    subAnims.put(pos, anim);
                }
            }
        }
    }

    private void playProgressiveAnimation() {
//        if (bAnimShowing) return;
//
//        createProgressiveAnimation();
//
//        if (!subAnims.isEmpty()) {
//            bAnimShowing = true;
//
//            Iterator iter = subAnims.entrySet().iterator();
//            while (iter.hasNext()) {
//                Map.Entry entry = (Map.Entry) iter.next();
//                Animation anim = (Animation) entry.getValue();
//                anim.loop();
//            }
//        }
    }

    public void playOneProgressiveAnimation(boolean bShow, int line) {
        if (subAnims.isEmpty()) return;

        if (bShow) {
             Iterator iter = subAnims.entrySet().iterator();
             while (iter.hasNext()) {
                 Map.Entry entry = (Map.Entry) iter.next();
                 Animation Anim = (Animation) entry.getValue();
                 Anim.loop();
             }
        } else {
            int index = GameConst.getSelectionPositions(GameData.getInstance().Setting.MaxSelections, "")[line][numReels - 1];
            int pos = index/numReels;

            Iterator iter = subAnims.entrySet().iterator();
            while (iter.hasNext()) {
                 Map.Entry entry = (Map.Entry) iter.next();
                 Animation Anim = (Animation) entry.getValue();
                 if ((int)entry.getKey() == pos) {
                     Anim.stop();
                 } else {
                     Anim.loop();
                 }
            }
        }
    }

    private void stopProgressiveAnimation() {
//        if (!subAnims.isEmpty()) {
//            Iterator iter = subAnims.entrySet().iterator();
//            while (iter.hasNext()) {
//                Map.Entry entry = (Map.Entry) iter.next();
//                Animation Anim = (Animation) entry.getValue();
//                Anim.stop();
//            }
//
//            subAnims.clear();
//        }
//
//        bAnimShowing = false;
    }

    private void showProSingleReelResultSubAnim(boolean isDim, boolean isSkip, int line, int level) {
        if (!isDim) {
//            clearActions();
            setProSubSymsbolDim(isDim);
            return;
        }

        if (isSkip) {
//            clearActions();
            setProSubSymsbolDim(true);
            return;
        }

        int index = GameConst.getSelectionPositions(GameData.getInstance().Setting.MaxSelections, "")[line][numReels - 1];
        Image image = subImages.get(index/numReels);
        if (image != null) {
            setProSubSymsbolDim(true);

            image.setColor(Color.WHITE);
//            image.addAction(sequence(delay(0.5f, hide()), delay(0.5f,show())));
        }
    }

    private void setProSubSymsbolDim(boolean isDim) {
        Iterator iter = subImages.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            Image image = (Image) entry.getValue();
            image.setVisible(true);
            if (isDim) {
                image.setColor(Color.DARK_GRAY);
            } else {
                image.setColor(Color.WHITE);
            }
        }
    }

    private int[] getJackpotInfo() {
        int []jackpots;
        for (MathParam param: GameData.getInstance().Context.MathParams) {
            if (param.Key.equals("JACKPOTINFO")) {
                jackpots = CommonUtil.stringToArray(param.Value);
                return jackpots;
            }
        }
        return null;
    }

    public void hideSubSymbol() {
        //clearActions();
        if (!subImages.isEmpty()) {
            Iterator iter = subImages.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                Image image = (Image) entry.getValue();
                //image.setVisible(false);
                image.addAction(fadeOut(0.5f));
            }
            subImages.clear();
        }
    }
}
