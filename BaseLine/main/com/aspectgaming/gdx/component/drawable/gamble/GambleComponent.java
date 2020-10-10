package com.aspectgaming.gdx.component.drawable.gamble;

import com.aspectgaming.common.actor.Button;
import com.aspectgaming.common.actor.Image;
import com.aspectgaming.common.actor.Sound;
import com.aspectgaming.common.actor.TextureLabel;
import com.aspectgaming.common.data.GameData;
import com.aspectgaming.common.data.State;
import com.aspectgaming.common.event.EventMachine;
import com.aspectgaming.common.event.gamble.GambleDisplayPendingEvent;
import com.aspectgaming.common.event.gamble.GambleWinEvent;
import com.aspectgaming.common.event.gamble.InGambleEvent;
import com.aspectgaming.common.event.gamble.OutGambleEndEvent;
import com.aspectgaming.common.event.game.GameResetEvent;
import com.aspectgaming.common.event.game.IntoSetupBetEvent;
import com.aspectgaming.common.event.machine.InTiltEvent;
import com.aspectgaming.common.event.machine.LanguageChangedEvent;
import com.aspectgaming.common.event.machine.OutTiltEvent;
import com.aspectgaming.common.event.screen.HelpHideEvent;
import com.aspectgaming.common.event.screen.HelpShowEvent;
import com.aspectgaming.common.loader.CoordinateLoader;
import com.aspectgaming.common.loader.ImageLoader;
import com.aspectgaming.common.loader.MessageLoader;
import com.aspectgaming.common.loader.SoundLoader;
import com.aspectgaming.gdx.component.drawable.DrawableComponent;
import com.aspectgaming.net.game.GameClient;
import com.aspectgaming.net.game.data.SettingData;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.utils.Align;

import java.util.ArrayList;
import java.util.List;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

/**
 * Gamble Component about background button meter history.
 *
 * @author johnny.shi & ligang.yao
 */
public class GambleComponent extends DrawableComponent implements State {
    private static final int NOT_GAMBLE = 0;
    private static final int ALL_GAMBLE = 1;
    private static final int HALF_GAMBLE = 2;
    private static final int ALL_HALF_GAMBLE = 3;

    private static final int HISTORY_SIZE = 14; // gamble history has at most 18 records
    private static final float FADING_TIME = 0.5f;

    private boolean inGamble;

    private Image bg;

    private Button riskAllButton;
    private Button riskHalfButton;

    private Button blackButton;
    private Button redButton;

    private Button spadeButton;
    private Button heartButton;
    private Button clubsButton;
    private Button diamondButton;

    private Button takeWinButton;
    private Button gambleButton;
    private Image message;
    private Image card;
    private Image mask;

    private TextureLabel riskAllLabel;
    private TextureLabel riskHalfLabel;
    private TextureLabel takeWinLabel;
    private TextureLabel promptLabel;

    private Sound sndButton;

    private Action upSwapAlternate;
    private boolean isShowTakeWinSwap = false;

    private List<Image> history = new ArrayList<>();
    private List<Button> allGambleButtons = new ArrayList<>();
    private List<Button> showHelpDisabledButtons = new ArrayList<>();

    public GambleComponent() {
        setAlpha(0);

        sndButton = SoundLoader.getInstance().get("button/button");

        mask = ImageLoader.getInstance().load("Gamble/mask", "GambleMask");
        addActor(mask);
        if (mask != null) mask.setTouchable(Touchable.enabled); // disable buttons under mask

        bg = ImageLoader.getInstance().load("Gamble/gamble_background", "GambleBg");
        addActor(bg);
        if (bg != null) bg.setTouchable(Touchable.enabled); // disable buttons under background

        card = ImageLoader.getInstance().load("Gamble/Card/cards_back", "Card");
        addActor(card);

        message = ImageLoader.getInstance().load("Gamble/gamble_lose", "GambleMessage");
        addActor(message);

        riskAllButton = new Button("Gamble/Risk/riskall_");
        riskAllButton.setOnClicked(new Runnable() {
            @Override
            public void run() {
                if (!riskAllButton.isChecked()) {
                    long affordWager = GameData.getInstance().Context.Gamble.AffordWager;

                    setRiskAllGamble(optionGamble(affordWager));
                    setDoubleFourGamble(ALL_GAMBLE, affordWager);

                    riskHalfButton.setChecked(false);
                    riskAllButton.setChecked(true);
                    GameClient.getInstance().gambleAll();

                    sndButton.play();
                }
            }
        });
        addActor(riskAllButton, "RiskAll");
        Rectangle rect = CoordinateLoader.getInstance().getBound("RiskAllTouchArea");
        riskAllButton.setTouchArea(rect);
        allGambleButtons.add(riskAllButton);

        riskHalfButton = new Button("Gamble/Risk/riskhalf_");
        riskHalfButton.setOnClicked(new Runnable() {
            @Override
            public void run() {
                if (!riskHalfButton.isChecked()) {
                    long affordWager = GameData.getInstance().Context.Gamble.AffordWager;

                    setRiskAllGamble(optionGamble(affordWager));
                    setDoubleFourGamble(HALF_GAMBLE, affordWager);

                    riskAllButton.setChecked(false);
                    riskHalfButton.setChecked(true);
                    GameClient.getInstance().gambleHalf();

                    sndButton.play();
                }
            }
        });
        addActor(riskHalfButton, "RiskHalf");
        rect = CoordinateLoader.getInstance().getBound("RiskHalfTouchArea");
        riskHalfButton.setTouchArea(rect);
        allGambleButtons.add(riskHalfButton);

        blackButton = new Button("Gamble/Color/black_");
        blackButton.setOnClicked(new Runnable() {
            @Override
            public void run() {
                GameClient.getInstance().gamblePick(0);
            }
        });
        addActor(blackButton, "Black");
        rect = CoordinateLoader.getInstance().getBound("BlackTouchArea");
        blackButton.setTouchArea(rect);
        allGambleButtons.add(blackButton);

        redButton = new Button("Gamble/Color/red_");
        redButton.setOnClicked(new Runnable() {
            @Override
            public void run() {
                GameClient.getInstance().gamblePick(1);
            }
        });
        addActor(redButton, "Red");
        rect = CoordinateLoader.getInstance().getBound("RedTouchArea");
        redButton.setTouchArea(rect);
        allGambleButtons.add(redButton);

        spadeButton = new Button("Gamble/Suits/spade_");
        spadeButton.setOnClicked(new Runnable() {
            @Override
            public void run() {
                GameClient.getInstance().gamblePick(2);
            }
        });
        addActor(spadeButton, "Spade");
        rect = CoordinateLoader.getInstance().getBound("SpadeTouchArea");
        spadeButton.setTouchArea(rect);
        allGambleButtons.add(spadeButton);

        heartButton = new Button("Gamble/Suits/heart_");
        heartButton.setOnClicked(new Runnable() {
            @Override
            public void run() {
                GameClient.getInstance().gamblePick(5);
            }
        });
        addActor(heartButton, "Heart");
        rect = CoordinateLoader.getInstance().getBound("HeartTouchArea");
        heartButton.setTouchArea(rect);
        allGambleButtons.add(heartButton);

        clubsButton = new Button("Gamble/Suits/clubs_");
        clubsButton.setOnClicked(new Runnable() {
            @Override
            public void run() {
                GameClient.getInstance().gamblePick(3);
            }
        });
        addActor(clubsButton, "Clubs");
        rect = CoordinateLoader.getInstance().getBound("ClubsTouchArea");
        clubsButton.setTouchArea(rect);
        allGambleButtons.add(clubsButton);

        diamondButton = new Button("Gamble/Suits/diamond_");
        diamondButton.setOnClicked(new Runnable() {
            @Override
            public void run() {
                GameClient.getInstance().gamblePick(4);
            }
        });
        addActor(diamondButton, "Diamond");
        rect = CoordinateLoader.getInstance().getBound("DiamondTouchArea");
        diamondButton.setTouchArea(rect);
        allGambleButtons.add(diamondButton);

        takeWinButton = new Button("Gamble/Takewin/takewin_");
        takeWinButton.setOnClicked(new Runnable() {
            @Override
            public void run() {
                GameClient.getInstance().gambleTakeWin();
                sndButton.play();
            }
        });
        addActor(takeWinButton, "TakeWin");
        rect = CoordinateLoader.getInstance().getBound("TakeWinTouchArea");
        takeWinButton.setTouchArea(rect);
        allGambleButtons.add(takeWinButton);

        gambleButton = new Button("Gamble/Gamble/gamble_");
        gambleButton.setOnClicked(new Runnable() {
            @Override
            public void run() {
                GameClient.getInstance().buttonGamble();
                sndButton.play();
            }
        });
        addActor(gambleButton, "GambleInGamle");
        rect = CoordinateLoader.getInstance().getBound("GambleTouchArea");
        gambleButton.setTouchArea(rect);
        allGambleButtons.add(gambleButton);

        riskAllLabel = new TextureLabel("GambleFont", Align.center, Align.center, "RiskAll");
        addActor(riskAllLabel);

        riskHalfLabel = new TextureLabel("GambleFont", Align.center, Align.center, "RiskHalf");
        addActor(riskHalfLabel);

        takeWinLabel = new TextureLabel("GambleFont", Align.center, Align.center, "TakeWin");
        addActor(takeWinLabel);

        promptLabel = new TextureLabel("MessageFont", Align.center, Align.center, "Prompt");
        addActor(promptLabel);

        registerEvent(new GameResetEvent() {
            @Override
            public void execute(Object... obj) {
                setTouchable(Touchable.disabled);
                setAlpha(0);

                switch (GameData.getInstance().Context.GameState) {
                    case GambleStarted:
                        EventMachine.getInstance().offerEvent(InGambleEvent.class);
                        break;
                    case GambleDisplayPending:
                        EventMachine.getInstance().offerEvent(GambleDisplayPendingEvent.class);
                        break;
                    case GambleWin:
                        EventMachine.getInstance().offerEvent(GambleWinEvent.class);
                        break;
                    default:
                        break;
                }
            }
        });

        registerEvent(new LanguageChangedEvent() {
            @Override
            public void execute(Object... obj) {
                ImageLoader.getInstance().reload(bg);
                ImageLoader.getInstance().reload(message);

                riskAllButton.updateLanguage();
                riskHalfButton.updateLanguage();
                redButton.updateLanguage();
                blackButton.updateLanguage();
                takeWinButton.updateLanguage();
                gambleButton.updateLanguage();

                setPrompt();
            }
        });

        registerEvent(new InTiltEvent() {
            @Override
            public void execute(Object... obj) {
                pause();
                riskAllButton.setDisabled(true);
                riskHalfButton.setDisabled(true);
                blackButton.setDisabled(true);
                redButton.setDisabled(true);
                spadeButton.setDisabled(true);
                heartButton.setDisabled(true);
                clubsButton.setDisabled(true);
                diamondButton.setDisabled(true);
                takeWinButton.setDisabled(true);
                gambleButton.setDisabled(true);
            }
        });

        registerEvent(new OutTiltEvent() {
            @Override
            public void execute(Object... obj) {
                resume();
                riskAllButton.setDisabled(false);
                riskHalfButton.setDisabled(false);
                blackButton.setDisabled(false);
                redButton.setDisabled(false);
                spadeButton.setDisabled(false);
                heartButton.setDisabled(false);
                clubsButton.setDisabled(false);
                diamondButton.setDisabled(false);
                takeWinButton.setDisabled(false);
                gambleButton.setDisabled(false);
            }
        });

        registerEvent(new InGambleEvent() {
            @Override
            public void execute(Object... obj) {
                enterGamble();
            }
        });

        registerEvent(new GambleWinEvent() {
            @Override
            public void execute(Object... obj) {
                onGamebleWin();
            }
        });

        registerEvent(new GambleDisplayPendingEvent() {
            @Override
            public void execute(Object... obj) {
                onGambleLose();
            }
        });

        registerEvent(new HelpShowEvent() {
            @Override
            public void execute(Object... obj) {
                if (inGamble) {
                    for (Button btn : allGambleButtons) {
                        if (!btn.isDisabled()) {
                            btn.setDisabled(true);
                            showHelpDisabledButtons.add(btn);
                        }
                    }
                }
            }
        });

        registerEvent(new HelpHideEvent() {
            @Override
            public void execute(Object... obj) {
                if (inGamble) {
                    for (Button btn : showHelpDisabledButtons) {
                        if (btn.isDisabled()) {
                            btn.setDisabled(false);
                        }
                    }
                    showHelpDisabledButtons.clear();
                }

            }
        });

        registerEvent(new IntoSetupBetEvent() {
            @Override
            public void execute(Object... obj) {
                setTouchable(Touchable.disabled);

                if (inGamble) {
                    addAction(fadeOut(FADING_TIME));
                    removeAction(upSwapAlternate);
                    upSwapAlternate = null;

                    inGamble = false;
                    EventMachine.getInstance().offerEvent(OutGambleEndEvent.class);
                }
            }
        });
    }

    private int optionGamble(long affordWager) {
        long gambleLimit = GameData.getInstance().Setting.GambleLimit / GameData.getInstance().Context.Denomination;
        boolean allGamble = false;
        boolean halfGamble = false;

        if (affordWager * 4 <= gambleLimit || affordWager * 2 <= gambleLimit) {
            allGamble = true;
        } else if (affordWager / 2 * 4 <= gambleLimit || affordWager / 2 * 2 <= gambleLimit) {
            halfGamble = true;
        }

        if (allGamble) {
            return ALL_HALF_GAMBLE;
        } else if (halfGamble) {
            return HALF_GAMBLE;
        }

        return NOT_GAMBLE;
    }

    private void setRiskAllGamble(int gambleType) {
        switch (gambleType) {
            case ALL_HALF_GAMBLE: {
                riskHalfButton.setDisabled(false);
                riskAllButton.setDisabled(false);
            }
            break;

            case HALF_GAMBLE: {
                riskHalfButton.setDisabled(false);
                riskAllButton.setDisabled(true);
            }
            break;

            case NOT_GAMBLE: {
                riskHalfButton.setDisabled(true);
                riskAllButton.setDisabled(true);
            }
            break;
        }
    }

    private void setDoubleButton(boolean doubleGamble) {
        redButton.setChecked(false);
        blackButton.setChecked(false);

        redButton.setDisabled(!doubleGamble);
        blackButton.setDisabled(!doubleGamble);
    }

    private void setFourButton(boolean fourGamble) {
        spadeButton.setChecked(false);
        heartButton.setChecked(false);
        clubsButton.setChecked(false);
        diamondButton.setChecked(false);

        spadeButton.setDisabled(!fourGamble);
        heartButton.setDisabled(!fourGamble);
        clubsButton.setDisabled(!fourGamble);
        diamondButton.setDisabled(!fourGamble);
    }

    private void setDoubleFourGamble(int gambleChecked, long affordWager) {
        long gambleLimit = GameData.getInstance().Setting.GambleLimit / GameData.getInstance().Context.Denomination;
        boolean doubleGamble = false;
        boolean fourGamble = false;

        switch (gambleChecked) {
            case ALL_GAMBLE: {
                riskHalfButton.setChecked(false);
                riskAllButton.setChecked(true);

                if (affordWager * 4 <= gambleLimit) {
                    fourGamble = true;
                    doubleGamble = true;
                } else if (affordWager * 2 <= gambleLimit) {
                    fourGamble = false;
                    doubleGamble = true;
                }
            }
            break;

            case HALF_GAMBLE: {
                riskHalfButton.setChecked(true);
                riskAllButton.setChecked(false);

                if (affordWager / 2 * 4 <= gambleLimit) {
                    fourGamble = true;
                    doubleGamble = true;
                } else if (affordWager / 2 * 2 <= gambleLimit) {
                    fourGamble = false;
                    doubleGamble = true;
                }
            }
            break;
        }

        setDoubleButton(doubleGamble);
        setFourButton(fourGamble);
    }

    private void enterGamble() {
        int option = optionGamble(GameData.getInstance().Context.Gamble.AffordWager);
        if (option == NOT_GAMBLE) {
            addAction(run(GameClient.getInstance().gameEnd));
            return;
        }

        setTouchable(Touchable.enabled);

        if (!inGamble) {
            addAction(fadeIn(FADING_TIME));
        }

        updateHistory();

        setRiskAllGamble(option);

        long affordWager = GameData.getInstance().Context.Gamble.AffordWager;
        if (!GameData.getInstance().Context.Gamble.RiskHalf && (option == ALL_HALF_GAMBLE)) {
            riskAllButton.setChecked(true);
            riskHalfButton.setChecked(false);
            setDoubleFourGamble(ALL_GAMBLE, affordWager);
            GameClient.getInstance().gambleAll();
        } else {
            riskAllButton.setChecked(false);
            riskHalfButton.setChecked(true);
            setDoubleFourGamble(HALF_GAMBLE, affordWager);
            GameClient.getInstance().gambleHalf();
        }

        riskAllLabel.setValue(GameData.getInstance().Context.Gamble.AffordWager);
        riskHalfLabel.setValue(GameData.getInstance().Context.Gamble.AffordWager / 2);

        if (upSwapAlternate != null) {
            removeAction(upSwapAlternate);
            upSwapAlternate = null;
            takeWinButton.showSwap(false);
        }

        takeWinButton.setDisabled(false);
        gambleButton.setDisabled(true);

        ImageLoader.getInstance().reload(card, "Gamble/Card/cards_back");

        takeWinLabel.setValue(GameData.getInstance().Context.Gamble.AffordWager);

        setPrompt();

        message.setVisible(false);

        inGamble = true;
    }

    private void setPrompt() {
        String prompt = MessageLoader.getInstance().getMessage("GamblePrompt");
        if (prompt != null) {
            SettingData cfg = GameData.getInstance().Setting;
            prompt = prompt.replace("@{GambleMaxRound}", String.valueOf(cfg.GambleMaxRound));
            prompt = prompt.replace("@{GambleLimit}", String.valueOf(GameData.Currency.format(cfg.GambleLimit)));
            promptLabel.setText(prompt);
        }
    }

    private void onGamebleWin() {
        setTouchable(Touchable.enabled);

        if (!inGamble) {
            addAction(fadeIn(FADING_TIME));
        }
        updateHistory();

        ImageLoader.getInstance().reload(message, "Gamble/gamble_win");

        riskHalfButton.setDisabled(true);
        riskAllButton.setDisabled(true);

        if (GameData.getInstance().Context.Gamble.RiskHalf) {
            riskHalfButton.setChecked(true);
            riskAllButton.setChecked(false);
        } else {
            riskHalfButton.setChecked(false);
            riskAllButton.setChecked(true);
        }

        takeWinButton.setDisabled(false);

        takeWinLabel.setValue(GameData.getInstance().Context.Gamble.TotalWin);

        riskAllLabel.setValue(GameData.getInstance().Context.Gamble.AffordWager);
        riskHalfLabel.setValue(GameData.getInstance().Context.Gamble.AffordWager / 2);

        showPlayerPick();
        showCardFace();

        message.setVisible(true);
        inGamble = true;

        SoundLoader.getInstance().play("gamble/gamble_win");

        setPrompt();

        if (optionGamble(GameData.getInstance().Context.Gamble.TotalWin) == NOT_GAMBLE) {
            GameClient.getInstance().gambleTakeWin();
        } else {
            gambleButton.setDisabled(false);
            if (upSwapAlternate == null) {
                upSwapAlternate = forever(delay(0.5f, run(() -> {
                    takeWinButton.showSwap(isShowTakeWinSwap);
                    gambleButton.showSwap(!isShowTakeWinSwap);
                    isShowTakeWinSwap = !isShowTakeWinSwap;
                })));
                addAction(upSwapAlternate);
            }
        }
    }

    private void onGambleLose() {
        setTouchable(Touchable.enabled);

        if (!inGamble) {
            addAction(sequence(fadeIn(FADING_TIME), delay(2, run(GameClient.getInstance().gameEnd))));
        } else {
            addAction(delay(2, run(GameClient.getInstance().gameEnd)));
        }

        updateHistory();

        riskHalfButton.setDisabled(true);
        riskAllButton.setDisabled(true);

        if (GameData.getInstance().Context.Gamble.RiskHalf) {
            riskHalfButton.setChecked(true);
            riskAllButton.setChecked(false);
        } else {
            riskHalfButton.setChecked(false);
            riskAllButton.setChecked(true);
        }

        takeWinButton.setDisabled(true);
        gambleButton.setDisabled(true);

        showPlayerPick();
        showCardFace();

        takeWinLabel.setValue(GameData.getInstance().Context.Gamble.TotalWin);

        riskAllLabel.setValue(GameData.getInstance().Context.Gamble.AffordWager);
        riskHalfLabel.setValue(GameData.getInstance().Context.Gamble.AffordWager / 2);

        if (GameData.getInstance().Context.Gamble.Win > 0) {
            ImageLoader.getInstance().reload(message, "Gamble/gamble_win");
            SoundLoader.getInstance().play("gamble/gamble_win");
        } else {
            ImageLoader.getInstance().reload(message, "Gamble/gamble_lose");
            SoundLoader.getInstance().play("gamble/gamble_lose");
        }

        message.setVisible(true);
        inGamble = true;
    }

    private void showCardFace() {
        switch (GameData.getInstance().Context.Gamble.Result) {
            case 0:
                ImageLoader.getInstance().reload(card, "Gamble/Card/cards_spade");
                break;
            case 1:
                ImageLoader.getInstance().reload(card, "Gamble/Card/cards_clubs");
                break;
            case 2:
                ImageLoader.getInstance().reload(card, "Gamble/Card/cards_diamond");
                break;
            case 3:
                ImageLoader.getInstance().reload(card, "Gamble/Card/cards_heart");
                break;
            default:
                break;
        }
    }

    private void showPlayerPick() {
        redButton.setChecked(false);
        blackButton.setChecked(false);
        spadeButton.setChecked(false);
        heartButton.setChecked(false);
        clubsButton.setChecked(false);
        diamondButton.setChecked(false);

        redButton.setDisabled(true);
        blackButton.setDisabled(true);
        spadeButton.setDisabled(true);
        heartButton.setDisabled(true);
        clubsButton.setDisabled(true);
        diamondButton.setDisabled(true);

        redButton.setHightlight(false);
        blackButton.setHightlight(false);
        spadeButton.setHightlight(false);
        heartButton.setHightlight(false);
        clubsButton.setHightlight(false);
        diamondButton.setHightlight(false);

        switch (GameData.getInstance().Context.Gamble.Level) {
            case 2:
                switch (GameData.getInstance().Context.Gamble.PlayerPick) {
                    case 0:
                        blackButton.setChecked(true);
                        if (GameData.getInstance().Context.Gamble.Win > 0) {
                            blackButton.setHightlight(true);
                        }
                        break;
                    case 1:
                        redButton.setChecked(true);
                        if (GameData.getInstance().Context.Gamble.Win > 0) {
                            redButton.setHightlight(true);
                        }
                        break;
                }
                break;
            case 4:
                switch (GameData.getInstance().Context.Gamble.PlayerPick) {
                    case 0:
                        spadeButton.setChecked(true);
                        if (GameData.getInstance().Context.Gamble.Win > 0) {
                            spadeButton.setHightlight(true);
                        }
                        break;
                    case 1:
                        clubsButton.setChecked(true);
                        if (GameData.getInstance().Context.Gamble.Win > 0) {
                            clubsButton.setHightlight(true);
                        }
                        break;
                    case 2:
                        diamondButton.setChecked(true);
                        if (GameData.getInstance().Context.Gamble.Win > 0) {
                            diamondButton.setHightlight(true);
                        }
                        break;
                    case 3:
                        heartButton.setChecked(true);
                        if (GameData.getInstance().Context.Gamble.Win > 0) {
                            heartButton.setHightlight(true);
                        }
                        break;
                }
                break;
        }
    }

    private void updateHistory() {
        for (Image image : history) {
            removeActor(image);
        }
        history.clear();

        if (GameData.getInstance().Context.Gamble.History == null) return;

        Rectangle rect = CoordinateLoader.getInstance().getBound("GambleHistory");
        float width = rect.width / HISTORY_SIZE;
        int i = 0;
        for (int rec : GameData.getInstance().Context.Gamble.History) {
            Image image = ImageLoader.getInstance().load("Gamble/History/" + rec);
            float margin = (width - image.getWidth()) / 2;
            image.setViewArea(rect);
            image.setPosition(rect.x + margin + width * (i++), rect.y);
            addActor(image);
            history.add(image);
        }
    }
}
