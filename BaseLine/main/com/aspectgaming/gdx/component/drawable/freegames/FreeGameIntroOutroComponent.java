package com.aspectgaming.gdx.component.drawable.freegames;

import com.aspectgaming.common.action.LongIntAction;
import com.aspectgaming.common.actor.*;
import com.aspectgaming.common.data.GameData;
import com.aspectgaming.common.data.GameMode;
import com.aspectgaming.common.data.State;
import com.aspectgaming.common.event.EventMachine;
import com.aspectgaming.common.event.GameModeChangeEvent;
import com.aspectgaming.common.event.freegame.*;
import com.aspectgaming.common.event.game.GameResetEvent;
import com.aspectgaming.common.event.game.GameStateChangedEvent;
import com.aspectgaming.common.event.game.ReelStartSpinEvent;
import com.aspectgaming.common.event.machine.InTiltEvent;
import com.aspectgaming.common.event.machine.LanguageChangedEvent;
import com.aspectgaming.common.event.machine.OutTiltEvent;
import com.aspectgaming.common.event.screen.HelpHideEvent;
import com.aspectgaming.common.loader.CoordinateLoader;
import com.aspectgaming.common.loader.ImageLoader;
import com.aspectgaming.common.loader.SoundLoader;
import com.aspectgaming.common.loader.VideoLoader;
import com.aspectgaming.common.video.Video;
import com.aspectgaming.gdx.component.drawable.DrawableComponent;
import com.aspectgaming.net.game.GameClient;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Align;

import java.text.DecimalFormat;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.run;

public class FreeGameIntroOutroComponent extends DrawableComponent implements State {
    private final Sound sndFreeIntro;
    private final Sound sndFreeOutro;
    private final Sound sndFreeBackground;

    private ShapeAnimation logoIn;
    private ShapeAnimation logoLoop;
    private ShapeAnimation logoFade;

    private final TextureLabel outroMeter;
    private final Image outroBg;
    private final Image credits;
    private boolean isOutroState;

//    private Button touchButton;

    private Action IntroAction;

    public FreeGameIntroOutroComponent() {
        sndFreeIntro = SoundLoader.getInstance().get("freegame/FGIntro");
        sndFreeOutro = SoundLoader.getInstance().get("freegame/FGOutro");
        sndFreeBackground = SoundLoader.getInstance().get("freegame/FreeGamesMusic");

        outroBg = ImageLoader.getInstance().load("IntroOutro/TotalWinBox", "TotalWinBox");
        outroBg.setVisible(true);
        outroBg.setAlpha(0);
        addActor(outroBg);

        credits = ImageLoader.getInstance().load("IntroOutro/Credits", "Credits");
        credits.setAlpha(0);
        addActor(credits);

        outroMeter = new TextureLabel("WinnerProgressive", Align.center, Align.center, "TotalWin");
        outroMeter.setAlpha(0);
        addActor(outroMeter);

        setTouchable(Touchable.enabled);
//        touchButton = new Button("Button/SlamIntro/SlamIntro_");
//        touchButton.setVisible(false);
//        touchButton.setBounds(143, 157, 1633, 915);
//        addActor(touchButton);

        logoIn = new ShapeAnimation("FreeGame", "777_free", "animation_in", "FreeGameIntro");
        logoIn.setPosition(0, 0);
        addActor(logoIn);
        logoLoop = new ShapeAnimation("FreeGame", "777_free", "animation", "FreeGameIntro");
        logoLoop.setPosition(0, 0);
        addActor(logoLoop);
        logoFade = new ShapeAnimation("FreeGame", "777_free", "animation_out", "FreeGameIntro");
        logoFade.setPosition(0, 0);
        addActor(logoFade);


//        touchButton.setOnClicked(new Runnable() {
//            @Override
//            public void run() {
////                if (GameData.isFreeIntroPlaying) {
////                    if (GameData.isTransitionInPlaying) {
////                        logoIn.setEndListener(null);
////                        if (GameData.getInstance().Context.GameState == State.FreeGameIntro) {
////                            addAction(Actions.run(GameClient.getInstance().freeGameIntroEnd));
////                        }
////                    } else {
//                        addAction(delay(0.0f, Actions.run(GameClient.getInstance().buttonPlay)));
////                    }
////                }
//            }
//        });

        registerEvent(new GameResetEvent() {
            @Override
            public void execute(Object... obj) {
                if (GameData.getInstance().Context.FreeGameMode) {
                    sndFreeBackground.loop();
                }

                switch (GameData.getInstance().Context.GameState) {
                    case BonusDisplayPending:
                        break;

                    case FreeGameIntro:
                    case StartFreeSpin:
                        InFreeGameIntro();
                        break;

                    case FreeGameStarted:
                    case FreeGameResults:
                        break;

                    case ReelStop:
                        break;

                    case FreeGameOutro:
                        if (GameData.currentGameMode != GameMode.FreeGame) {
                            GameData.currentGameMode = GameMode.FreeGame;
                            EventMachine.getInstance().offerEvent(GameModeChangeEvent.class);
                        }
                        break;
                }

                if (GameData.getPrevious() != null) {
                    if (GameData.getPrevious().Context.TestMode && !GameData.getInstance().Context.TestMode) {
                        System.out.println("quit testmode");
                        GameData.isFreeIntroPlaying = false;
                        GameData.isTransitionInPlaying = false;
                        clearActions();
                        clearListeners();
                        logoIn.setEndListener(null);
//                        logoIn.setAlpha(0);
                        logoIn.stop();
                        logoLoop.setEndListener(null);
//                        logoLoop.setAlpha(0);
                        logoLoop.stop();
//                        logoFade.setAlpha(0);
                        logoFade.stop();
                        outroMeter.addAction(fadeOut(0f));
                        credits.addAction(fadeOut(0f));
                        outroBg.addAction(fadeOut(0.0f));
//                        touchButton.setVisible(false);
                        sndFreeBackground.stop();
                        sndFreeIntro.stop();
                        sndFreeOutro.stop();
                    }
                }
            }
        });

        registerEvent(new InFreeGameIntroEvent() {
            @Override
            public void execute(Object... obj) {
                InFreeGameIntro();
            }
        });

        registerEvent(new OutFreeGameIntroEvent() {
            @Override
            public void execute(Object... obj) {
                if (sndFreeBackground != null) {
                    sndFreeBackground.stop();
                }
                OutFreeGameIntro();
            }
        });

        registerEvent(new InFreeGameOutroEvent() {
            @Override
            public void execute(Object... obj) {
                addAction(run(startOutro));
            }
        });

        registerEvent(new OutFreeGameOutroEvent() {
            @Override
            public void execute(Object... obj) {
                OutFreeGameOutro();
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

        registerEvent(new LanguageChangedEvent() {
            @Override
            public void execute(Object... obj) {
                ImageLoader.getInstance().reload(outroBg);

            }
        });

        registerEvent(new ModifyFreeBGMVolEvent() {
            @Override
            public void execute(Object... obj) {
                float vol = (float) obj[0];
                setFreeBackground(vol);
            }
        });

        registerEvent(new ReelStartSpinEvent() {
            @Override
            public void execute(Object... obj) {
                if (GameData.currentGameMode == GameMode.FreeGame) {
                    setFreeBackground(1.0f);
                }
                BreakFreeGameOutro();
            }
        });

        registerEvent(new GameStateChangedEvent() {
            @Override
            public void execute(Object... obj) {
                switch (GameData.getInstance().Context.GameState) {
                    case GameIdle:
                    case GambleChoice:
                        BreakFreeGameOutro();
                    default:
                        break;
                }
            }
        });
    }

    private final Runnable playIntroEnd = new Runnable() {
        @Override
        public void run() {
//            if (GameData.getInstance().Context.Language.equals("en-US")) {
//                cupidStandBy.switchAnim("E_standby_START");
//            } else {
//                cupidStandBy.switchAnim("C_standby_START");
//            }
//            cupidStandBy.play(true);
            logoLoop.play(true);
            GameData.isTransitionInPlaying = false;
//            addAction(delay(10.0f, Actions.run(GameClient.getInstance().buttonPlay)));

            if (GameData.getInstance().Context.GameState == State.FreeGameIntro) {
                addAction(Actions.run(GameClient.getInstance().freeGameIntroEnd));
            }


        }
    };

    private final LongIntAction rollupAction = new LongIntAction() {
        @Override
        protected void update(float percent) {
            super.update(percent);
            outroMeter.setForamtVal(getValue());
        }
    };

    private final Runnable startOutro = () -> InFreeGameOutro();

    private void StopAll() {
        sndFreeOutro.stop();
    }

    private void InFreeGameIntro() {
        GameClient.getInstance().selectFreeGameProgressiveTotalWin(GameData.getInstance().Context.ProgressiveTotalWin +
                GameData.getInstance().Context.BonusTotalWin * GameData.getInstance().Context.Denomination);

        if (GameData.isFreeIntroPlaying) {
            OutFreeGameIntro();
            return;
        }

        GameData.isFreeIntroPlaying = true;
        GameData.isTransitionInPlaying = true;

        IntroAction = delay(0, run(() -> {
            logoIn.setEndListener(playIntroEnd);
            logoIn.play(false);
            sndFreeIntro.play();
        }));
        addAction(IntroAction);

//        touchButton.setVisible(true);

        addAction(delay(0.5f, run(() -> {
            if (GameData.currentGameMode == GameMode.BaseGame) {

                GameData.currentGameMode = GameMode.FreeGame;
                EventMachine.getInstance().offerEvent(GameModeChangeEvent.class);
            }
        })));
    }

    private void OutFreeGameIntro() {
        if (GameData.isFreeIntroPlaying) {
            GameData.isFreeIntroPlaying = false;

            StopAll();


            if (GameData.currentGameMode == GameMode.BaseGame) {
                GameData.currentGameMode = GameMode.FreeGame;
                EventMachine.getInstance().offerEvent(GameModeChangeEvent.class);
            }

            removeAction(IntroAction);
            if (GameData.isTransitionInPlaying) {
                logoIn.setEndListener(null);
                logoLoop.setEndListener(null);
                logoIn.stop();
                logoLoop.stop();
                logoIn.stop();
                GameData.isTransitionInPlaying = false;
            } else {

                logoLoop.setEndListener(() -> {
                    logoFade.play(false);
                });

                logoLoop.stop();
            }

//            touchButton.setVisible(false);
            sndFreeBackground.loop();

        }
    }

    private void InFreeGameOutro() {
        if (isOutroState) return;
        isOutroState = true;

        sndFreeBackground.stop();
        sndFreeOutro.play();

        outroBg.setVisible(true);
        outroBg.addAction(fadeIn(0.5f)); //Actions.moveTo(outroBg.getX(), 188, 0.5f, Interpolation.pow3In)));

        String target="";
        if(GameData.getInstance().Context.ProgressiveTotalWin==0) {
            target = ""+GameData.getInstance().Context.TotalWin;
        }
        else
        {
            target=creditsToCurrency(GameData.getInstance().Context.ProgressiveTotalWin,GameData.getInstance().Context.FreeGameTotalWin);
        }
        outroMeter.setText(target);
        outroMeter.setMaxWidth(9.5f);
        outroMeter.addAction(delay(0.5f, fadeIn(0.16f)));
        if (!target.contains("$")) {
            credits.addAction(delay(0.5f, fadeIn(0.16f)));
        }
        addAction(delay(5f, run(playOutroEnd)));
    }

    private final Runnable playOutroEnd = () -> EventMachine.getInstance().offerEvent(OutFreeGameOutroEvent.class);

    private void OutFreeGameOutro() {
        if (!isOutroState) return;
        isOutroState = false;

        if (GameData.currentGameMode != GameMode.BaseGame) {
            GameData.currentGameMode = GameMode.BaseGame;
            EventMachine.getInstance().offerEvent(GameModeChangeEvent.class);
        }

        outroMeter.addAction(fadeOut(0.16f));
        credits.addAction(fadeOut(0.16f));
        outroBg.addAction(delay(0.16f, fadeOut(0.5f)));
        // TODO if free game total win = 0
        //addAction(sequence(delay(0.16f), run(GameClient.getInstance().freeGameOutroEnd)));

        addAction(delay(1f, run(() -> StopAll())));

    }

    private void BreakFreeGameOutro() {
        if (!isOutroState) return;

        clearActions();

        isOutroState = false;

        if (GameData.currentGameMode != GameMode.BaseGame) {
            GameData.currentGameMode = GameMode.BaseGame;
            EventMachine.getInstance().offerEvent(GameModeChangeEvent.class);
        }

        outroMeter.setAlpha(0);
        outroBg.setAlpha(0);
        credits.setAlpha(0);
        addAction(run(GameClient.getInstance().freeGameOutroEnd));
        addAction(run(() -> StopAll()));

        EventMachine.getInstance().offerEvent(OutFreeGameOutroEvent.class);
    }

    private void setFreeBackground(float val) {
        if (sndFreeBackground != null) {
            sndFreeBackground.setFade(val);
        }
    }


    private String creditsToCurrency(long num1,long num2)
    {
        DecimalFormat decimalFormat=new DecimalFormat("##,##0.00");
        return (GameData.Currency.symbol+ decimalFormat.format((double) num1/100+(double)GameData.getInstance().Setting.Denominations[0]/100*(double)num2));
    }
}
