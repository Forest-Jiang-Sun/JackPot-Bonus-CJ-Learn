package com.aspectgaming.gdx.component.drawable.button;

import com.aspectgaming.common.actor.*;
import com.aspectgaming.common.configuration.ButtonsConfiguration;
import com.aspectgaming.common.configuration.GameConfiguration;
import com.aspectgaming.common.data.Content;
import com.aspectgaming.common.data.GameData;
import com.aspectgaming.common.data.GameMode;
import com.aspectgaming.common.data.State;
import com.aspectgaming.common.event.EventMachine;
import com.aspectgaming.common.event.GameModeChangeEvent;
import com.aspectgaming.common.event.freegame.OutFreeGameOutroEvent;
import com.aspectgaming.common.event.gamble.OutGambleEndEvent;
import com.aspectgaming.common.event.gamble.TakeWinEvent;
import com.aspectgaming.common.event.game.*;
import com.aspectgaming.common.event.machine.*;
import com.aspectgaming.common.event.minigame.ShowMiniGameEvent;
import com.aspectgaming.common.event.progressive.ProgressiveSkipEndEvent;
import com.aspectgaming.common.event.screen.DisableAllButtonEvent;
import com.aspectgaming.common.event.screen.HelpHideEvent;
import com.aspectgaming.common.event.screen.HelpShowEvent;
import com.aspectgaming.common.event.screen.LogoShowEvent;
import com.aspectgaming.common.loader.CoordinateLoader;
import com.aspectgaming.common.loader.ImageLoader;
import com.aspectgaming.common.loader.SoundLoader;
import com.aspectgaming.gdx.component.drawable.DrawableComponent;
import com.aspectgaming.gdx.component.drawable.meter.MetersComponent;
import com.aspectgaming.gdx.component.drawable.progressivereel.ProgressiveReelComponent;
import com.aspectgaming.log.Debug;
import com.aspectgaming.net.game.GameClient;
import com.aspectgaming.net.game.data.ButtonData;
import com.aspectgaming.net.game.data.ContextData;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.utils.Align;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

/**
 * button base class.
 *
 * @author ligang.yao & johnny.shi
 */
public class ButtonsComponent extends DrawableComponent implements State {

    private final int FLAG_GAME_IN_PROGRESS = 1;

    private final int FLAG_BASE_GAME = 1 << 1;
    private final int FLAG_FREE_GAME = 1 << 2;
    private final int FLAG_GAMBLE_GAME = 1 << 3;
    private final int FLAG_BONUS_GAME = 1 << 4;
    private final int FLAG_SPECIAL_GAME = 1 << 5;

    private final int FLAG_HELP_SHOW = 1 << 6;
    private final int FLAG_REEL_SPIN = 1 << 7;
    private final int FLAG_BASE_REEL_SPIN = 1 << 8;
    private final int FLAG_FREE_REEL_SPIN = 1 << 9;

    private final int FLAG_BASE_GAME_PLAY = 1 << 10;
    private final int FLAG_NO_CREDIT = 1 << 11;
    private final int FLAG_INTRO = 1 << 12;
    private final int FLAG_TILT = 1 << 13;
    private final int FLAG_DISABLED_BY_PLATFORM = 1 << 14; // disabled by platform

    private final int FLAG_GAMBLE_DISABLED = 1 << 15;
    private final int FLAG_SERVICE = 1 << 16;
    private final int FLAG_HELP_DISABLED = 1 << 17;

    private final int FLAG_ROLLING_UP = 1 << 18;

    private final int FLAG_NO_WIN = 1 << 19;


    private final int FLAG_TESTMODE = 1 << 20;

    private int status; // used to set flags
    private boolean isAutoPlay;
    private static final String CASHOUT_BTN_NAME = "CashOut";

    private Button helpButton;
    private Button logoButton;
    private Button volumeButton;
    private Button languageButton;
    private Button denomButton;
    private Button autoPlayButton;
    private Button playButton;
    private Button serviceButton;
    private Button cashOutButton;
    private Button gambleButton;
    private Button takeWinButton;
    private Image leaf;


    private Animation languageAnim;
    private Animation helpAnim;
    private Animation serviceLightAnim;
    private Animation takeWinLightAnim;
    private Animation cashOutLightAnim;

    private Image imgLanguage;

    private TextureLabel denomLabel;

    private Sound sndButton;
    private Sound sndRuleIn;
    private Sound sndRuleOut;
    private Sound sndVolume;
    private Sound sndLanuage;

    private List<Button> allButtons = new ArrayList<>();
    private List<Button> baseGameButtons = new ArrayList<>();
    private List<Button> freeGameButtons = new ArrayList<>();
    private List<Button> testModeButtons = new ArrayList<>();

    private boolean bProReelSpinReday = false;
    private boolean bShowHelp = false;

    private final ButtonsConfiguration cfg;

    public ButtonsComponent() {
        setTouchable(Touchable.enabled);

        cfg = GameConfiguration.getInstance().buttons;
        int levels = ImageLoader.getInstance().countFrames("Button/Volume/volume_", "_up");
        GameData.setMaxVolumeLevel(levels - 1);

        List<String> screenButtons = Arrays.asList(GameData.getInstance().Setting.ScreenButtons);
        sndButton = SoundLoader.getInstance().get("button/button");
        sndRuleIn = SoundLoader.getInstance().get("help/GameRulesIn");
        sndRuleOut = SoundLoader.getInstance().get("help/GameRulesOut");
        sndVolume = SoundLoader.getInstance().get("volume/Volume");
        sndLanuage = SoundLoader.getInstance().get("language/SelectLanguage");

        String name = "Help";
        if (cfg.hasButton(name)) {
            helpButton = new Button("Button/Help/help_");
            helpButton.setFlag(FLAG_HELP_DISABLED | FLAG_FREE_GAME | FLAG_TILT | FLAG_ROLLING_UP | FLAG_GAME_IN_PROGRESS);
            helpButton.setDisabled(true);
            helpButton.setOnClicked(() -> {
                if (hasFlag(FLAG_HELP_SHOW)) {
                    sndRuleOut.play();
                    EventMachine.getInstance().offerEvent(HelpHideEvent.class);
                } else {
                    sndRuleIn.play();
                    EventMachine.getInstance().offerEvent(HelpShowEvent.class);
                }

//                helpAnim.play();
                if (GameData.getInstance().Context.GameState == GambleChoice) {
                    GameClient.getInstance().gambleTakeWin();
                }
            });

            addActor(helpButton, "HelpBt");
            checkMode(helpButton, name);

//            helpAnim = new Animation("Button/Help/Animation/", 0.5f);
//            helpAnim.setAutoVisible(true);
//            addActor(helpAnim, name);
        }

        name = "Logo";
        if (cfg.hasButton(name)) {
            logoButton = new Button("Button/Logo/logo_");
            logoButton.setFlag(FLAG_HELP_DISABLED | FLAG_FREE_GAME | FLAG_TILT | FLAG_ROLLING_UP | FLAG_GAME_IN_PROGRESS);
            logoButton.setDisabled(true);
            logoButton.setOnClicked(() -> {
                if (hasFlag(FLAG_HELP_SHOW)) {
                    sndRuleOut.play();
                    EventMachine.getInstance().offerEvent(HelpHideEvent.class);
                } else {
                    sndRuleIn.play();
                    EventMachine.getInstance().offerEvent(LogoShowEvent.class);
                }

//                helpAnim.play();
                if (GameData.getInstance().Context.GameState == GambleChoice) {
                    GameClient.getInstance().gambleTakeWin();
                }
            });

            addActor(logoButton, "LogoBt");
            checkMode(logoButton, name);

//            helpAnim = new Animation("Button/Help/Animation/", 0.5f);
//            helpAnim.setAutoVisible(true);
//            addActor(helpAnim, name);
        }

        name = "Volume";
        if (cfg.hasButton(name)) {
            volumeButton = new Button("Button/Volume/volume_" + GameData.getVolumeLevel() + "_");
            volumeButton.setFlag(FLAG_TILT);
            volumeButton.setOnClicked(() -> {
                GameData.increaseVolume();
                EventMachine.getInstance().offerEvent(VolumeChangeEvent.class);
            });
            addActor(volumeButton, name);
            checkMode(volumeButton, name);
        }

        name = "Language";
        if (cfg.hasButton(name)) {
            languageButton = new Button("Button/Language/Static/language_");
            languageButton.setFlag(FLAG_HELP_DISABLED);
            languageButton.setOnClicked(() -> {
                sndLanuage.play();
                if (GameData.getInstance().Context.Language.equals("en-US")) {
                    //  GameClient.getInstance().selectLanguage("zh-CHT");
                } else {
                    //  GameClient.getInstance().selectLanguage("en-US");
                }

                //languageAnim.play();
            });
            addActor(languageButton, "LanguageBt");
//            checkMode(languageButton, name);
            languageButton.setDisabled(true);
            //languageAnim = new Animation("Button/Language/Animation/", 0.5f);
            //languageAnim.setAutoVisible(true);
            //addActor(languageAnim, name);

            imgLanguage = ImageLoader.getInstance().load("Button/Language/Static/ImgLanguage_up", "ImagLanguage");
            addActor(imgLanguage);
        }

        name = "Denomination";
        if (cfg.hasButton(name)) {
            denomButton = new Button("Denom/denom_");
            denomButton.setFlag(FLAG_TILT | FLAG_SPECIAL_GAME | FLAG_GAME_IN_PROGRESS);
            denomButton.setOnClicked(() -> {
                int[] denoms = GameData.getInstance().Setting.Denominations;
                if (denoms.length <= 1) {
                    return;
                }
                if (GameData.getInstance().Context.GameState == GambleChoice) {
                    GameClient.getInstance().gambleTakeWin();
                    clearFlag(FLAG_ROLLING_UP);
                    updateButtons();
                }
                sndButton.play();
                //EventMachine.getInstance().offerEvent(DenomShowEvent.class);

                for (int i = 0; i < denoms.length; i++) {
                    if (denoms[i] == GameData.getInstance().Context.Denomination) {
                        GameClient.getInstance().selectDenom(denoms[(i + 1) % denoms.length]);
                    }
                }
            });
//            addActor(denomButton, name);
            checkMode(denomButton, name);

            Rectangle bounds = CoordinateLoader.getInstance().getBound("Denomination");
            denomLabel = new TextureLabel("DenomFont", Align.center, Align.center);
            denomLabel.setBounds(bounds.x, bounds.y, bounds.width, bounds.height);
            denomLabel.setText(GameData.Currency.denomFormat(GameData.getInstance().Context.Denomination));
            denomLabel.setDenomMaxWidth();
            addActor(denomLabel);
        }

        //leaf = ImageLoader.getInstance().load("Denom/leaf");
        //Vector2 point = CoordinateLoader.getInstance().getCoordinate(leaf, "Leaf");
        //leaf.setPosition(point.x, point.y);
        //addActor(leaf);


        name = "AutoPlay";
        if (cfg.hasButton(name) && screenButtons.contains(name)) {
            autoPlayButton = new Button("Button/AutoPlay/autoplay_");
            autoPlayButton.setFlag(FLAG_TILT | FLAG_SPECIAL_GAME | FLAG_BONUS_GAME | FLAG_NO_CREDIT | FLAG_INTRO | FLAG_DISABLED_BY_PLATFORM);
            autoPlayButton.setOnClicked(() -> {
                if (GameData.getInstance().Context.AutoPlay) {
                    GameClient.getInstance().stopAutoPlay();
                    autoPlayButton.updateSkin("Button/AutoPlay/autoplay_");
                } else {
                    GameClient.getInstance().startAutoPlay();
                    autoPlayButton.updateSkin("Button/AutoPlay/stop_");
                }

                sndButton.play();

                GameData.getInstance().Context.AutoPlay = !GameData.getInstance().Context.AutoPlay;
                isAutoPlay = GameData.getInstance().Context.AutoPlay;
            });
            addActor(autoPlayButton, name);
            checkMode(autoPlayButton, name);
        }

        name = "Play";
        if (cfg.hasButton(name) && screenButtons.contains(name)) {
            playButton = new Button("Button/Play/play_");
            if (GameConfiguration.getInstance().reel.manualStop) {
                playButton.setFlag(FLAG_TILT | FLAG_SPECIAL_GAME | FLAG_BONUS_GAME | FLAG_NO_CREDIT | FLAG_INTRO | FLAG_DISABLED_BY_PLATFORM);
            } else {
                playButton.setFlag(FLAG_TILT | FLAG_SPECIAL_GAME | FLAG_BONUS_GAME | FLAG_NO_CREDIT | FLAG_INTRO | FLAG_BASE_GAME_PLAY | FLAG_BASE_REEL_SPIN | FLAG_DISABLED_BY_PLATFORM);
            }
            playButton.setOnClicked(() -> {
                GameClient.getInstance().buttonPlay();
                //sndButton.play();
            });
            addActor(playButton, name);
            checkMode(playButton, name);
        }

        name = "Service";
        if (cfg.hasButton(name)) {
            serviceButton = new Button("Button/Service/Static/service_");
            serviceButton.setOnClicked(() -> {
                sndButton.play();
                GameClient.getInstance().buttonAttendant();
                if (!hasFlag(FLAG_SERVICE)) {
                    serviceLightAnim.loop();
                    setFlag(FLAG_SERVICE);
                } else {
                    serviceLightAnim.stop();
                    clearFlag(FLAG_SERVICE);
                }
            });

            addActor(serviceButton, "ServiceBt");
            checkMode(serviceButton, name);
            serviceLightAnim = new Animation("Button/Highlight/Service/");
            serviceLightAnim.setAutoVisible(true);
            serviceLightAnim.setPosition(serviceButton.getX(), serviceButton.getY());
            addActor(serviceLightAnim);

        }

        name = CASHOUT_BTN_NAME;
        if (cfg.hasButton(name)) {
            cashOutButton = new Button("Button/CashOut/Static/cashout_");
            cashOutButton.setName(name);
            cashOutButton.setFlag(FLAG_TILT | FLAG_GAME_IN_PROGRESS | FLAG_NO_CREDIT | FLAG_ROLLING_UP | FLAG_TESTMODE);
            cashOutButton.setOnClicked(() -> {
                //cashOutAnim.play(false);
                GameClient.getInstance().buttonCashout();
                addAction(delay(0.2f, run(()-> {
                    cashOutLightAnim.loop();
                })));

            });

            addActor(cashOutButton, "CashOutBt");
            checkMode(cashOutButton, name);

            cashOutLightAnim = new Animation("Button/Highlight/CashOut/");
            cashOutLightAnim.setAutoVisible(true);
            addActor(cashOutLightAnim,"CashOutBt");

        }

        name = "Gamble";
        if (cfg.hasButton(name)) {
            gambleButton = new Button("Button/Gamble/Static/gamble_");
            gambleButton.setFlag(FLAG_TILT | FLAG_GAMBLE_DISABLED | FLAG_GAMBLE_GAME);
            gambleButton.setOnClicked(() -> {
                //gambleAnim.play(false);
                GameClient.getInstance().buttonGamble();
            });

            addActor(gambleButton, "GambleBt");
            checkMode(gambleButton, name);

            /*
            gambleAnim = new ShapeAnimation("Buttons", "Buttons", "boqv", "GambleBtnAnim");
            addActor(gambleAnim);

            gambleLightAnim = new ShapeAnimation("Buttons", "Buttons", "boqv2", "GambleBtnAnim");
            addActor(gambleLightAnim);
            */
        }

        name = "TakeWin";
        if (cfg.hasButton(name)) {
            takeWinButton = new Button("Button/TakeWin/Static/TakeWinButton_");
            takeWinButton.setFlag(FLAG_TILT | FLAG_BASE_REEL_SPIN | FLAG_FREE_REEL_SPIN | FLAG_FREE_GAME | FLAG_NO_WIN);
            Rectangle rect = CoordinateLoader.getInstance().getBound("TakeWinBtnTouchArea");
//            takeWinButton.setTouchArea(rect);
            takeWinButton.setOnClicked(() -> {
                GameClient.getInstance().gambleTakeWin();
                EventMachine.getInstance().offerEvent(TakeWinEvent.class);
                clearFlag(FLAG_ROLLING_UP);
//                updateButtons();
                takeWinLightAnim.stop();
                takeWinButton.setDisabled(true);
                takeWinButton.setDisabled(true);
                takeWinButton.setDisabled(true);
            });

            addActor(takeWinButton, "TakeWinBtnBt");
            checkMode(takeWinButton, name);


            takeWinLightAnim = new Animation("Button/Highlight/TakeWin/");
            takeWinLightAnim.setAutoVisible(true);
            addActor(takeWinLightAnim, "TakeWinBtnBt");
        }

        registerEvent(new DisableAllButtonEvent(){

            @Override
            public void execute(Object... obj) {
                disableAllButton();
            }
        });

        registerEvent(new GameResetEvent() {
            @Override
            public void execute(Object... obj) {
                clearActions();

                for (Button btn : allButtons) {
                    boolean visible = isControllable(btn);
                    btn.setAlpha(visible ? 1 : 0);
                    btn.setDisabled(false);
                    if (btn == denomButton) {
                        denomLabel.setAlpha(visible ? 1 : 0);
                    }

                }

                isAutoPlay = false;
                status = 0;

                updateButtons();
                updateDenomButton();
            }
        });

        registerEvent(new GameModeChangeEvent() {
            @Override
            public void execute(Object... obj) {
                for (Button btn : allButtons) {
                    boolean visible = isControllable(btn);
                    if (visible) {
                        btn.addAction(fadeIn(1));
                        if (btn == denomButton) {
                            denomLabel.addAction(fadeIn(1));
                        }
                    } else {
                        btn.addAction(fadeOut(1));
                        if (btn == denomButton) {
                            denomLabel.addAction(fadeOut(1));
                        }
                    }
                    btn.setDisabled(false);
                }

                updateButtons();
                updateDenomButton();
            }
        });

        registerEvent(new LanguageChangedEvent() {
            @Override
            public void execute(Object... obj) {
                for (Button btn : allButtons) {
                    btn.updateLanguage();
                }
                reLoadImgLanguage();
                sndButton = SoundLoader.getInstance().get("button/button");
            }
        });

        registerEvent(new GameStateChangedEvent() {
            @Override
            public void execute(Object... obj) {
                    updateButtons();
            }
        });

        registerEvent(new StateChangedEvent() {
            @Override
            public void execute(Object... obj) {
                updateButtons();
            }
        });

        registerEvent(new VolumeChangeEvent() {
            @Override
            public void execute(Object... obj) {
                volumeButton.updateSkin("Button/Volume/volume_" + GameData.getVolumeLevel() + "_");
                sndVolume.play();
            }
        });

        if (denomButton != null) {
            registerEvent(new DenomChangedEvent() {
                @Override
                public void execute(Object... obj) {
                    updateDenomButton();
                }
            });
        }

        registerEvent(new ReelStartSpinEvent() {
            @Override
            public void execute(Object... obj) {
                if (playButton != null) {
                    if (GameConfiguration.getInstance().reel.manualStop || GameData.currentGameMode == GameMode.FreeGame) {
                        playButton.updateSkin("Button/AutoPlay/stop_");
                    }
                }

                clearFlag(FLAG_ROLLING_UP);
                setFlag(FLAG_REEL_SPIN);
                setFlag(GameData.currentGameMode == GameMode.FreeGame ? FLAG_FREE_REEL_SPIN : FLAG_BASE_REEL_SPIN);
                updateButtons();
            }
        });


        registerEvent(new ReelStoppedEvent() {
            @Override
            public void execute(Object... obj) {
                if (playButton != null) {
                    playButton.updateSkin("Button/Play/play_");
                }

                clearFlag(FLAG_REEL_SPIN);
                clearFlag(FLAG_BASE_REEL_SPIN);
                clearFlag(FLAG_FREE_REEL_SPIN);

                if (GameData.getInstance().Context.TotalWin > 0) {
                    setFlag(FLAG_ROLLING_UP);
                }

                updateButtons();
            }
        });

        registerEvent(new ShowMiniGameEvent() {
            @Override
            public void execute(Object... obj) {
                updateButtons();
            }
        });

        registerEvent(new HelpShowEvent() {
            @Override
            public void execute(Object... obj) {
                bShowHelp = true;
                setFlag(FLAG_HELP_SHOW);
                updateButtons();
            }
        });

        registerEvent(new HelpHideEvent() {
            @Override
            public void execute(Object... obj) {
                bShowHelp = false;
                clearFlag(FLAG_HELP_SHOW);
                updateButtons();
            }
        });

        registerEvent(new CreditsChangedEvent() {
            @Override
            public void execute(Object... obj) {
                    updateButtons();
            }
        });

        registerEvent(new ProgressiveSpinStateEvent() {
            @Override
            public void execute(Object... obj) {
                int proReelSpinState = (int) obj[0];
                if (proReelSpinState == 0) {
                    bProReelSpinReday = true;
                } else {
                    bProReelSpinReday = false;
                }
                updateButtons();
            }
        });

        registerEvent(new OutGambleEndEvent() {
            @Override
            public void execute(Object... obj) {
                clearFlag(FLAG_ROLLING_UP);
                updateButtons();
            }
        });

        registerEvent(new WinMeterStopRollingEvent() {
            @Override
            public void execute(Object... obj) {
                clearFlag(FLAG_ROLLING_UP);
                updateButtons();
            }
        });

        registerEvent(new ChangeBetEvent() {
            @Override
            public void execute(Object... obj) {
                clearFlag(FLAG_ROLLING_UP);
                updateButtons();
            }
        });

        registerEvent(new OutFreeGameOutroEvent() {
            @Override
            public void execute(Object... obj) {
                clearFlag(FLAG_ROLLING_UP);
                updateButtons();
            }
        });

//        registerEvent(new ProgressiveSkipEndEvent() {
//            @Override
//            public void execute(Object... obj) {
//                clearFlag(FLAG_ROLLING_UP);
//                updateButtons();
//            }
//        });

        registerEvent(new OutTiltEvent() {
            @Override
            public void execute(Object... obj) {
                int gameState = GameData.getInstance().Context.GameState;
                MetersComponent meters = (MetersComponent) Content.getInstance().getComponent(Content.METERSCOMPONENT);
                if (meters.isWinMeterStop() && (gameState == State.GambleChoice || gameState == State.GameIdle)) {
                    clearFlag(FLAG_ROLLING_UP);
                }
                updateButtons();
            }
        });

        registerEvent(new SystemMessageChangedEvent() {
            @Override
            public void execute(Object... obj) {
                updateButtons();
            }
        });
    }

    private void reLoadImgLanguage() {
//        if (languageButton.isDisabled()) {
//            ImageLoader.getInstance().reload(imgLanguage, "Button/Language/Static/ImgLanguage_disable");
//        } else {
//            ImageLoader.getInstance().reload(imgLanguage, "Button/Language/Static/ImgLanguage_up");
//        }
    }

    private void checkMode(Button btn, String name) {
        allButtons.add(btn);
        String mode = cfg.getButton(name).mode;

        if (mode == null) {
            baseGameButtons.add(btn);
            freeGameButtons.add(btn);
            testModeButtons.add(btn);
        } else {
            if (mode.contains("BaseGame")) {
                baseGameButtons.add(btn);
            }
            if (mode.contains("FreeGame")) {
                freeGameButtons.add(btn);
            }
            if (mode.contains("Test")) {
                testModeButtons.add(btn);
            }
        }
    }

    private void enableButton(Button btn) {
        if (btn != null && isControllable(btn)) {
            btn.setDisabled(false);
        }
    }

    private void disableButton(Button btn) {
        if (btn != null) {
            btn.setDisabled(true);
        }
    }

    private void disableAllButton(){
        for (Button btn : allButtons) {
            disableButton(btn);
        }
    }

    private boolean isControllable(Button btn) {
        int gameState = GameData.getInstance().Context.GameState;
        boolean isFreeGame = GameData.currentGameMode == GameMode.FreeGame || gameState == FreeGameStarted;
        boolean isTestMode = GameData.getInstance().Context.TestMode;

        if (isFreeGame && !freeGameButtons.contains(btn)) return false;
        if (!isFreeGame && !baseGameButtons.contains(btn)) return false;
        if (isTestMode && !testModeButtons.contains(btn)) return false;

        return true;
    }

    private void updateFlags() {
        int gameState = GameData.getInstance().Context.GameState;

        if (gameState == State.GambleChoice || gameState == State.GameIdle) {
            clearFlag(FLAG_GAME_IN_PROGRESS);
        } else {
            setFlag(FLAG_GAME_IN_PROGRESS);
        }

        // game mode is still BaseGame at the beginning of first FreeGameStarted state.
        if (GameData.currentGameMode == GameMode.FreeGame || gameState == FreeGameStarted) {
            setFlag(FLAG_FREE_GAME);
            clearFlag(FLAG_BASE_GAME);
        } else {
            clearFlag(FLAG_FREE_GAME);
            setFlag(FLAG_BASE_GAME);
        }

        if (gameState == GambleStarted || gameState == State.GambleWin || gameState == State.GambleDisplayPending) {
            if (bShowHelp) {
                setFlag(FLAG_GAMBLE_GAME);
            } else {
                clearFlag(FLAG_GAMBLE_GAME);
            }
        }

        if (gameState == State.GambleChoice || gameState == State.GambleWin) {
            clearFlag(FLAG_GAMBLE_DISABLED);
        } else {
            setFlag(FLAG_GAMBLE_DISABLED);
        }

        if (GameData.getInstance().Context.Cash == 0) {
            setFlag(FLAG_NO_CREDIT);
        } else {
            clearFlag(FLAG_NO_CREDIT);
        }

        if (GameData.getInstance().Context.TotalWin == 0) {
            setFlag(FLAG_NO_WIN);
        } else {
            clearFlag(FLAG_NO_WIN);
        }

        if (GameData.getInstance().Context.SpecialGameMode) {
            setFlag(FLAG_SPECIAL_GAME);
        } else {
            clearFlag(FLAG_SPECIAL_GAME);
        }

        if (GameData.currentGameMode != GameMode.FreeGame && (gameState == PrimaryGameStarted || gameState == PayGameResults || gameState == ReelStop)) {
            setFlag(FLAG_BASE_GAME_PLAY);
        } else {
            clearFlag(FLAG_BASE_GAME_PLAY);
        }

        if (gameState == FreeGameIntro || gameState == FreeGameOutro || gameState == StartFreeSpin) {
            setFlag(FLAG_INTRO);
        } else {
            clearFlag(FLAG_INTRO);
        }

        if (gameState != GameData.getInstance().Context.State) {
            setFlag(FLAG_TILT);
        } else {
            clearFlag(FLAG_TILT);
        }

        if (isDisabledByPlatform()) {
            setFlag(FLAG_DISABLED_BY_PLATFORM);
        } else {
            clearFlag(FLAG_DISABLED_BY_PLATFORM);
        }

        if (gameState == BonusActive || gameState == BonusDisplayPending) {
            setFlag(FLAG_BONUS_GAME);
        } else {
            clearFlag(FLAG_BONUS_GAME);
        }

        if (gameState == State.GameIdle || gameState == State.GambleChoice || gameState == State.GambleStarted || gameState == State.GambleDisplayPending
                || gameState == State.GambleWin || gameState == State.StartFreeSpin || bProReelSpinReday||gameState== State.ReelStop) {
            clearFlag(FLAG_HELP_DISABLED);
        } else {
            setFlag(FLAG_HELP_DISABLED);
        }

        if (GameData.getInstance().Context.TestMode) {
            setFlag(FLAG_TESTMODE);
        } else {
            clearFlag(FLAG_TESTMODE);
        }
    }

    private void disabledAllButtons()
    {
        for (Button btn : allButtons) {
            disableButton(btn);
        }
    }

    private void updateButtons() {
        updateFlags();

        for (Button btn : allButtons) {
            if (hasFlag(btn.getFlag())) {
                disableButton(btn);
            } else {
                enableButton(btn);
            }

            if(CASHOUT_BTN_NAME.equals(btn.getName()))
            {
                for (ButtonData btnData: GameData.getInstance().Context.ButtonPanel.getButtonsList())
                {
                    String btnTypeCashout = "Cashout";
                    if(btnTypeCashout.equals(btnData.getType()))
                    {
                        if(hasFlag(FLAG_TILT) && btnData.getEnabled())
                        {
                            enableButton(btn);
                        }
                    }

                }
            }
        }


        if (cashOutButton != null) {
            if (cashOutButton.isDisabled()) {
                cashOutLightAnim.stop();
            }
        }
 /*
        if (gambleButton != null) {
            if (gambleButton.isDisabled()) {
                gambleLightAnim.stop();
            } else {
                gambleLightAnim.play(true);
            }
        }
        */
        if (takeWinButton != null) {
            if (takeWinButton.isDisabled()) {
                takeWinLightAnim.stop();
            } else {
                takeWinLightAnim.loop();
            }
        }


        reLoadImgLanguage();
    }

    private void updateAutoPlay() {
        if (autoPlayButton != null && isAutoPlay != GameData.getInstance().Context.AutoPlay) {
            isAutoPlay = GameData.getInstance().Context.AutoPlay;
            if (isAutoPlay) {
                autoPlayButton.updateSkin("Button/AutoPlay/stop_");
            } else {
                autoPlayButton.updateSkin("Button/AutoPlay/autoplay_");
            }
        }
    }

    private void updateDenomButton() {
        if (denomButton != null) {
            if (hasFlag(FLAG_FREE_GAME)) {
                denomButton.updateSkin("Denom/denom_");
            } else {
                denomButton.updateSkin("Denom/denom_");
            }
            denomLabel.setText(GameData.Currency.denomFormat(GameData.getInstance().Context.Denomination));
        }
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

    private boolean isDisabledByPlatform() {
        byte[] states = GameData.getInstance().Context.ScreenButtonStates;
        return states != null && states.length > 0 && states[0] == 0;
    }

    @Override
    protected void update(float delta) throws Exception {
        if (hasFlag(FLAG_DISABLED_BY_PLATFORM) && !isDisabledByPlatform()) {
            updateButtons();
        }

        updateAutoPlay();
    }
}
