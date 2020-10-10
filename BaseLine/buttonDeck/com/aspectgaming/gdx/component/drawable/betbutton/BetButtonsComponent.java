package com.aspectgaming.gdx.component.drawable.betbutton;

import com.aspectgaming.common.actor.Image;
import com.aspectgaming.common.actor.Sound;
import com.aspectgaming.common.data.GameData;
import com.aspectgaming.common.data.GameMode;
import com.aspectgaming.common.event.EventMachine;
import com.aspectgaming.common.event.freegame.InFreeGameIntroEvent;
import com.aspectgaming.common.event.freegame.OutFreeGameOutroEvent;
import com.aspectgaming.common.event.game.*;
import com.aspectgaming.common.event.machine.*;
import com.aspectgaming.common.loader.SoundLoader;
import com.aspectgaming.gdx.component.drawable.betbutton.BetButton;
import com.aspectgaming.common.data.State;
import com.aspectgaming.gdx.component.drawable.DrawableComponent;
import com.aspectgaming.net.game.GameClient;
import com.badlogic.gdx.scenes.scene2d.Touchable;

public class BetButtonsComponent extends DrawableComponent implements State {
    private final int FLAG_FREE_GAME = 1;
    private final int FLAG_GAMBLE_GAME = 1 << 1;
    private final int FLAG_BONUS_GAME = 1 << 2;
    private final int FLAG_SPECIAL_GAME = 1 << 3;
    private final int FLAG_REEL_SPIN = 1 << 4;
    private final int FLAG_INTRO = 1 << 5;
    private final int FLAG_TILT = 1 << 6;
    private final int FLAG_PROGRESSIVE = 1 << 7;

    private int status; // used to set flags

    private static final int BET_BTN_NUM = 5;
    private BetButton[] betButtons = new BetButton[BET_BTN_NUM];

    private Sound sndButton;

    public BetButtonsComponent() {
        sndButton = SoundLoader.getInstance().get("button/button");

        status = 0;
        setTouchable(Touchable.enabled);
        loadButtons();
        updateButtons();

        registerEvent(new GameResetEvent() {
            @Override
            public void execute(Object... obj) {
                updateButtons();
            }
        });

        registerEvent(new ChangeBetEvent() {
            @Override
            public void execute(Object... obj) {
                updateButtons();
            }
        });

        registerEvent(new CreditsChangedEvent() {
            @Override
            public void execute(Object... obj) {
                updateButtons();
            }
        });

        registerEvent(new DenomChangedEvent() {
            @Override
            public void execute(Object... obj) {
                updateButtons();
            }
        });

        registerEvent(new GameStateChangedEvent() {
            @Override
            public void execute(Object... obj) {
//                updateButtonState();
            }
        });

        registerEvent(new StateChangedEvent() {
            @Override
            public void execute(Object... obj) {
//                updateButtonState();
            }
        });

        registerEvent(new LanguageChangedEvent() {
            @Override
            public void execute(Object... obj) {
                updateButtonSkin();
            }
        });

        registerEvent(new ReelStartSpinEvent() {
            @Override
            public void execute(Object... obj) {
//                setFlag(FLAG_REEL_SPIN);
//                updateButtonState();
            }
        });

        registerEvent(new ReelStoppedEvent() {
            @Override
            public void execute(Object... obj) {
//                clearFlag(FLAG_REEL_SPIN);
//                updateButtonState();
            }
        });
   }

    private void setFlag(int flag) {
        status = status | flag;
    }

    private void clearFlag(int flag) {
        status = status & ~flag;
    }

    private boolean hasFlag(int flags) {
        return (status & flags) != 0;
    }

    private void updateFlag() {
        int gameState = GameData.getInstance().Context.GameState;

        if (GameData.currentGameMode == GameMode.BaseGame && GameData.getInstance().Context.Result.NumFreeSpinsWon > 0 ) {
            setFlag(FLAG_FREE_GAME);
        }else if (GameData.currentGameMode == GameMode.FreeGame || gameState == FreeGameStarted) {
            setFlag(FLAG_FREE_GAME);
        } else {
            clearFlag(FLAG_FREE_GAME);
        }

        if (gameState == GambleStarted || gameState == State.GambleWin || gameState == State.GambleDisplayPending) {
            setFlag(FLAG_GAMBLE_GAME);
        } else {
            clearFlag(FLAG_GAMBLE_GAME);
        }

        if (GameData.getInstance().Context.SpecialGameMode) {
            setFlag(FLAG_SPECIAL_GAME);
        } else {
            clearFlag(FLAG_SPECIAL_GAME);
        }

        if (gameState == FreeGameIntro || gameState == FreeGameOutro || gameState == StartFreeSpin) {
            setFlag(FLAG_INTRO);
        } else {
            clearFlag(FLAG_INTRO);
        }

        if (gameState == ProgressiveIntro || gameState == ProgressiveStarted || gameState == ProgressiveResults){
            setFlag(FLAG_PROGRESSIVE);
        }else {
            clearFlag(FLAG_PROGRESSIVE);
        }

        if (gameState != GameData.getInstance().Context.State) {
            setFlag(FLAG_TILT);
        } else {
            clearFlag(FLAG_TILT);
        }

        if (gameState == BonusActive || gameState == BonusDisplayPending) {
            setFlag(FLAG_BONUS_GAME);
        } else {
            clearFlag(FLAG_BONUS_GAME);
        }
    }

    private void loadButtons() {
        for (int i=0; i<BET_BTN_NUM; ++i) {
            int[] multiplerList = GameData.getInstance().getBetMultipler();
            betButtons[i] = new BetButton("Buttons/BetButton/", i, multiplerList[i]);
            int multipler = multiplerList[i];
            int betAmount = GameData.getInstance().getBetAmount(i);
            int finalI = i;
            betButtons[i].setOnClicked(new Runnable() {
                @Override
                public void run() {
//                    sndButton.play();
                    if (betAmount <=  GameData.getInstance().Context.Credits + GameData.getInstance().Context.TotalWin) {
                        GameClient.getInstance().changeBet(multipler);
                    }
                    if(finalI ==4){
                        GameClient.getInstance().buttonPlay();
                    }
                }
            });
            betButtons[i].setState(BetButton.SELECTABLE);
            addActor(betButtons[i]);
        }
    }

    private void updateButtonState() {
        updateFlag();
        if (status != 0) {
            setTouchable(Touchable.disabled);
        } else {
            setTouchable(Touchable.enabled);
        }
    }

    private void updateButtonSkin() {
        for (int i=0; i<BET_BTN_NUM; ++i) {
            betButtons[i].updateLanguage();
        }
    }

    public void updateButtons() {
       /* for (int i=BET_BTN_NUM-1; i>=0; --i) {
            int bet = GameData.getInstance().getBetAmount(i);
            if (GameData.getInstance().Context.BetMultiplier == betButtons[i].getMultipler()) {
                betButtons[i].setState(BetButton.SELECTED);
            } else {
                if (GameData.getInstance().Context.Credits + GameData.getInstance().Context.TotalWin < bet) {
                    betButtons[i].setState(BetButton.UNAVAIABLE);
                } else {
                    betButtons[i].setState(BetButton.SELECTABLE);
                }
            }
        }*/
    }
}