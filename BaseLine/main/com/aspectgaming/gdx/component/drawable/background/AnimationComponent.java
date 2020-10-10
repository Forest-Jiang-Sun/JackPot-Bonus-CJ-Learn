package com.aspectgaming.gdx.component.drawable.background;

import com.aspectgaming.common.actor.Image;
import com.aspectgaming.common.actor.ShapeAnimation;
import com.aspectgaming.common.actor.Sound;
import com.aspectgaming.common.actor.TextureLabel;
import com.aspectgaming.common.configuration.GameConfiguration;
import com.aspectgaming.common.configuration.SoundCfg;
import com.aspectgaming.common.data.GameData;
import com.aspectgaming.common.data.GameMode;
import com.aspectgaming.common.data.State;
import com.aspectgaming.common.event.EventMachine;
import com.aspectgaming.common.event.freegame.ModifyFreeBGMVolEvent;
import com.aspectgaming.common.event.freegame.OutFreeGameOutroEvent;
import com.aspectgaming.common.event.game.*;
import com.aspectgaming.common.loader.ImageLoader;
import com.aspectgaming.common.loader.SoundLoader;
import com.aspectgaming.gdx.component.drawable.DrawableComponent;
import com.aspectgaming.net.game.data.MathParam;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Align;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.delay;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.run;

public class AnimationComponent  extends DrawableComponent {
    private final Image growingBg;
//    private Video ProgressiveCelebrationStart;
//    private Video ProgressiveCelebrationLoop;
//    private Video ProgressiveCelebrationEnd;
    private ShapeAnimation ProgressiveCelebrationStart;
    private ShapeAnimation ProgressiveCelebrationLoop;
    private ShapeAnimation ProgressiveCelebrationEnd;
    private ShapeAnimation ProgressiveFireworksStart;
    private ShapeAnimation ProgressiveFireworksLoop;
    private ShapeAnimation ProgressiveFireworksEnd;
    private ShapeAnimation JackpotCityLogo;
    private ShapeAnimation JackpotCityLogoWin;
    private ShapeAnimation P_777;
    private ShapeAnimation Animation_777;
    private ShapeAnimation In_777;
    private ShapeAnimation WinXIn;
    private ShapeAnimation WinXLoop;
    private ShapeAnimation WinXOut;
    private Sound sndMajorWIN;
    private Sound sndMinorWin;
    String animationName;
    float animationTime;
    float animOverlap;
    private TextureLabel winMeter;
    private TextureLabel winXMeter;
    private float transitionDuration1=0.2f;
    private boolean isShowTotalWin=true;
    private boolean isJACKPOTWIN=false;
    private boolean isP2Win=false;
    private boolean isTestMode=false;
    private SoundCfg scfg;


    public AnimationComponent() {

        growingBg = ImageLoader.getInstance().load("Background/GrowingBG", "GrowingBg");
        growingBg.setScale(0.00001f, 0.00001f);


        addActor(growingBg);
        winMeter = new TextureLabel("WinnerProgressive", Align.center, Align.center, "JackpotWinMeter");
        addActor(winMeter);
        winMeter.setAlpha(0);
        winMeter.addAction(Actions.scaleTo(0.00001f, 0.00001f, transitionDuration1));

        JackpotCityLogo = new ShapeAnimation("logo/main", "logo", "animation", "LogoAnimation");
        addActor(JackpotCityLogo);

        JackpotCityLogoWin = new ShapeAnimation("logo/main", "logo", "animation2", "LogoAnimation");
        addActor(JackpotCityLogoWin);

        P_777 = new ShapeAnimation("logo/main", "777", "777_P", "LogoAnimation");
        addActor(P_777);

        Animation_777 = new ShapeAnimation("logo/main", "777", "777_animation", "LogoAnimation");
        addActor(Animation_777);

        In_777 = new ShapeAnimation("logo/main", "777", "777_in", "LogoAnimation");
        addActor(In_777);

        ProgressiveFireworksStart = new ShapeAnimation("ProgressiveCelebration", "1213B", "animation_in", "MainScreenCelebration");
        addActor(ProgressiveFireworksStart);

        ProgressiveFireworksLoop = new ShapeAnimation("ProgressiveCelebration", "1213B", "animation", "MainScreenCelebration");
        addActor(ProgressiveFireworksLoop);

        ProgressiveFireworksEnd = new ShapeAnimation("ProgressiveCelebration", "1213B", "animation_out", "MainScreenCelebration");
        addActor(ProgressiveFireworksEnd);

        ProgressiveCelebrationStart = new ShapeAnimation("ProgressiveCelebration", "J", "animation_in", "MainScreenCelebration");
        addActor(ProgressiveCelebrationStart);

        ProgressiveCelebrationLoop = new ShapeAnimation("ProgressiveCelebration", "J", "animation", "MainScreenCelebration");
        addActor(ProgressiveCelebrationLoop);

        ProgressiveCelebrationEnd = new ShapeAnimation("ProgressiveCelebration", "J", "animation_out", "MainScreenCelebration");
        addActor(ProgressiveCelebrationEnd);

        WinXIn = new ShapeAnimation("WinX", "126Xwin", "animation_in", "WinX");
        addActor(WinXIn);

        WinXLoop = new ShapeAnimation("WinX", "126Xwin", "animation", "WinX");
        addActor(WinXLoop);

        WinXOut = new ShapeAnimation("WinX", "126Xwin", "animation_out", "WinX");
        addActor(WinXLoop);

        winXMeter = new TextureLabel("WinXFont", Align.center, Align.center, "WinXLabel");
        addActor(winXMeter);
        winXMeter.setAlpha(0);


        P_777.setEndListener(() -> Animation_777.play(false));

        Animation_777.setEndListener(() -> In_777.play(false));

        In_777.setEndListener(() -> P_777.play(false));

        P_777.play(false);
        JackpotCityLogo.play(true);

        sndMajorWIN = SoundLoader.getInstance().get("winmeter/VSCelebration2a");
        sndMinorWin = SoundLoader.getInstance().get("winmeter/VSCelebration1a");

        registerEvent(new GameStateChangedEvent() {
            @Override
            public void execute(Object... obj) {
                if (isTestMode == false)
                    isTestMode = GameData.getInstance().Context.TestMode;

                switch (GameData.getInstance().Context.GameState) {
                    case State.ProgressiveStarted:

                        break;

                    case State.PrimaryGameStarted:
                        StopAllAnim();
                        break;
                }
            }
        });

        registerEvent(new StopCelebrationClickSpinEvent() {
            @Override
            public void execute(Object... obj) {
                StopAllAnim();
            }
        });

        registerEvent(new PlayCelebrationEvent() {
            @Override
            public void execute(Object... obj) {
                animationName = (String) obj[0];
                animationTime = (float) obj[1];
                animOverlap = (float) obj[2];
                EventMachine.getInstance().offerEvent(PlayProgressiveEvent.class, animationName, Float.toString(animationTime), Float.toString(animOverlap));
                if (GameData.getInstance().Context.FreeGameMode == false) {
                    if (animationName.equals("CelebrationMajor")) {
                        PlayCelebrationMajor();
                    }
                    if (animationName.equals("CelebrationMinor")) {
                        PlayCelebrationMinor();
                    }
                }
            }
        });

        registerEvent(new OutFreeGameOutroEvent() {
            @Override
            public void execute(Object... obj) {
                if (GameData.getInstance().Context.Credits != 0) {
                    float multiple = GameData.getInstance().Context.TotalWin / GameData.getInstance().Context.TotalBet;
                    if (multiple >= 160) {
                        EventMachine.getInstance().offerEvent(PlayProgressiveEvent.class, "CelebrationMajor", Float.toString(animationTime), Float.toString(animOverlap));
                        PlayCelebrationMajor();
                    } else if (multiple >= 20) {
                        EventMachine.getInstance().offerEvent(PlayProgressiveEvent.class, "CelebrationMinor", Float.toString(animationTime), Float.toString(animOverlap));
                        PlayCelebrationMinor();
                    }
                }
            }
        });

        registerEvent(new WinMeterStartRollingEvent() {
            @Override
            public void execute(Object... obj) {
                float multiple = GameData.getInstance().Context.TotalWin / GameData.getInstance().Context.TotalBet;
                if (GameData.getInstance().Context.FreeGameMode) {
                    if (multiple >= 20) {
                        showWinx();
                    }
                }
            }
        });


        registerEvent(new StopCelebrationEvent() {
            @Override
            public void execute(Object... obj) {
//                  StopAnim();
                boolean testMode = GameData.getInstance().Context.TestMode;
                int state = GameData.getInstance().Context.GameState;
                if (isTestMode) {
                    if (testMode == false && state == State.GameIdle) {
                        StopAllAnim();
                        isTestMode = false;
                    }
                }
            }
        });

        registerEvent(new ReelStoppedEvent() {
            @Override
            public void execute(Object... obj) {
                isP1OrP2();
                if (GameData.getInstance().Context.TotalWin >= 1) {
                    JackpotCityLogo.stop();
                    JackpotCityLogoWin.play(true);
                } else if (isJACKPOTWIN || isP2Win) {
                    JackpotCityLogo.stop();
                    JackpotCityLogoWin.play(true);
                }

            }
        });

        registerEvent(new ReelStartSpinEvent() {
            @Override
            public void execute(Object... obj) {
                JackpotCityLogoWin.stop();
                JackpotCityLogo.play(true);
            }
        });

        registerEvent(new GameResetEvent() {
            @Override
            public void execute(Object... obj) {
                float multiple = GameData.getInstance().Context.TotalWin / GameData.getInstance().Context.TotalBet;
                scfg = GameConfiguration.getInstance().winMeter.getSoundCfg(multiple);
                int playCount = scfg.playCount;
                if (GameData.getInstance().Context.TestMode) {
                    StopAllAnim();
                } else {
                    if (GameData.getInstance().Context.State != State.PayGameResults) {
                        if (multiple >= 20 && GameData.getInstance().Context.Credits != 0) {
                            showWinx();
                        }
                        if (playCount > 0 && GameData.getInstance().Context.Credits != 0) {
                            if (GameData.currentGameMode == GameMode.FreeGame) {
                                EventMachine.getInstance().offerEvent(ModifyFreeBGMVolEvent.class, 0.01f);
                            }
                            EventMachine.getInstance().offerEvent(PlayCelebrationEvent.class, scfg.animation, scfg.lowTime, scfg.animOverlap);
                        }
                    }
                }
                if (GameData.getPrevious().Context.TestMode) {
                    StopAllAnim();
                    if (playCount > 0) {
                        if (GameData.currentGameMode == GameMode.FreeGame) {
                            EventMachine.getInstance().offerEvent(ModifyFreeBGMVolEvent.class, 0.01f);
                        }
                        EventMachine.getInstance().offerEvent(PlayCelebrationEvent.class, scfg.animation, scfg.lowTime, scfg.animOverlap);
                    }
                }
            }
        });

        registerEvent(new CreditsChangedEvent(){
            @Override
            public void execute(Object... obj) {
                if (GameData.getInstance().Context.Credits==0)
                {
                    StopAllAnim();
                }
            }
        });
    }

    private void PlayCelebrationMinor() {
        showWinx();

        ProgressiveCelebrationStart.setEndListener(
                () -> ProgressiveCelebrationLoop.play(true)
        );

        ProgressiveCelebrationLoop.setEndListener(
                () -> ProgressiveCelebrationEnd.play(false)
        );

        addAction(delay(0.01f, run(() -> ProgressiveCelebrationStart.play(false))));
//        addAction(delay(animationTime, run(() -> ProgressiveCelebrationLoop.stop())));

        sndMinorWin.loop();
    }

    private void PlayCelebrationMajor() {
        showWinx();

        ProgressiveCelebrationStart.setEndListener(
                () -> ProgressiveCelebrationLoop.play(true)
        );

        ProgressiveCelebrationLoop.setEndListener(
                () -> ProgressiveCelebrationEnd.play(false)
        );

        ProgressiveFireworksStart.setEndListener(
                ()->ProgressiveFireworksLoop.play(true)
        );

        ProgressiveFireworksLoop.setEndListener(
                () -> ProgressiveFireworksEnd.play(false)
        );

//        ProgressiveFireworksEnd.setEndListener(
//             () -> showTotalWin()
//        );
//        isJACKPOTWIN=false;
//        isP2Win=false;
//        isP1OrP2();
//        if (isJACKPOTWIN){
        sndMajorWIN.loop();
//        }
//        else if (isP2Win){

//        }
        ProgressiveCelebrationStart.setVisible(true);
        ProgressiveFireworksStart.setVisible(true);
//        ProgressiveCelebrationStart.play(false);
//        ProgressiveFireworksStart.play(false);
        addAction(delay(0.01f, run(() -> ProgressiveCelebrationStart.play(false))));
        addAction(delay(0.01f, run(() -> ProgressiveFireworksStart.play(false))));

//        if(isJACKPOTWIN||isP2Win) {
//            addAction(delay(animationTime, run(() -> showTotalWin())));
//        }
//        else
//        {
////            addAction(delay(animationTime, run(() -> ProgressiveCelebrationLoop.stop())));
////            addAction(delay(animationTime, run(() -> ProgressiveFireworksLoop.stop())));
//        }
        isShowTotalWin=true;
    }

    private void  showWinx()
    {
        WinXIn.play(true);
        WinXIn.setEndListener(()->WinXLoop.play(true));
        WinXLoop.setEndListener(()->WinXOut.play(false));
        WinXIn.play(false);
        long x=GameData.getInstance().Context.TotalWin/GameData.getInstance().Context.Selections;
        winXMeter.setText(x+"X Win!");
        addAction(delay(0f,run(()->winXMeter.setAlpha(1))));
    }

    private void showTotalWin()
    {
        if (isShowTotalWin) {
            growingBg.addAction(Actions.scaleTo(1.0f, 1.0f, transitionDuration1));
            addAction(delay(0.5f,run(()->winMeter.setAlpha(1))));
            winMeter.setMaxWidth(9.5f);
            long progressiveWin = GameData.getInstance().Context.ProgressiveTotalWin;
            winMeter.setText(GameData.Currency.format(progressiveWin));
            addAction(delay(0, run(() -> PlayEndAnim())));
            addAction(delay(6, run(() -> StopTotalShow())));
        }
    }

    private void StopAllAnim() {
        isShowTotalWin=false;
        clearActions();
        ProgressiveCelebrationStart.setEndListener(null);
        ProgressiveCelebrationLoop.setEndListener(null);
        ProgressiveFireworksStart.setEndListener(null);
        ProgressiveFireworksLoop.setEndListener(null);
        ProgressiveCelebrationStart.stop();
        ProgressiveCelebrationLoop.stop();
        ProgressiveCelebrationEnd.stop();
        ProgressiveFireworksStart.stop();
        ProgressiveFireworksLoop.stop();
        ProgressiveFireworksEnd.stop();
        ProgressiveCelebrationStart.clear();
        ProgressiveCelebrationLoop.clear();
        ProgressiveCelebrationEnd.clear();
        ProgressiveFireworksStart.clear();
        ProgressiveFireworksLoop.clear();
        ProgressiveFireworksEnd.clear();

        if (!GameData.getInstance().Context.FreeGameMode) {
            WinXIn.setEndListener(null);
            WinXLoop.setEndListener(null);
            WinXIn.stop();
            WinXLoop.stop();
            WinXOut.stop();
            WinXIn.clear();
            WinXLoop.clear();
            WinXOut.clear();
        }

        growingBg.addAction(Actions.scaleTo(0.00001f, 0.00001f, transitionDuration1));
        winMeter.setAlpha(0);
        sndMajorWIN.stop();
        sndMinorWin.stop();

        winXMeter.setAlpha(0);
    }

    private void StopAnim() {
//        isShowTotalWin=false;
//        ProgressiveCelebrationStart.setEndListener(null);
//        ProgressiveCelebrationLoop.setEndListener(null);
//        ProgressiveFireworksStart.setEndListener(null);
//        ProgressiveFireworksLoop.setEndListener(null);
//        ProgressiveCelebrationStart.stop();
//        ProgressiveCelebrationLoop.stop();
//        ProgressiveCelebrationEnd.stop();
//        ProgressiveFireworksStart.stop();
//        ProgressiveFireworksLoop.stop();
//        ProgressiveFireworksEnd.stop();
//        ProgressiveCelebrationStart.clear();
//        ProgressiveCelebrationLoop.clear();
//        ProgressiveCelebrationEnd.clear();
//        ProgressiveFireworksStart.clear();
//        ProgressiveFireworksLoop.clear();
//        ProgressiveFireworksEnd.clear();
//        ProgressiveCelebrationStart.setVisible(false);
//        ProgressiveFireworksStart.setVisible(false);
        sndMinorWin.stop();
        sndMajorWIN.stop();
    }

    private void PlayEndAnim()
    {
//        ProgressiveCelebrationLoop.stop();
//        ProgressiveFireworksLoop.stop();
//        sndJACKPOTWIN.stop();
//        sndP2Win.stop();
    }

    private void StopTotalShow()
    {
        isShowTotalWin=false;
        growingBg.addAction(Actions.scaleTo(0.00001f, 0.00001f, transitionDuration1));
        winMeter.setAlpha(0);
    }

    private void isP1OrP2(){
        isP2Win=false;
        isJACKPOTWIN=false;
        for (MathParam param: GameData.getInstance().Context.MathParams) {
            if (param.Key.equals("P2Win")) {
                if (param.Value.equals("true")) {
                    isP2Win=true;
                    break;
                }
            }
            if (param.Key.equals("JACKPOTWIN")) {
                if (param.Value.equals("true")) {
                    isJACKPOTWIN=true;
                    break;
                }
            }
        }
    }
}
