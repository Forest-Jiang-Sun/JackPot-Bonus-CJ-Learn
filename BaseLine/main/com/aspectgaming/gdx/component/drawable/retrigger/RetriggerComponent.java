package com.aspectgaming.gdx.component.drawable.retrigger;

import com.aspectgaming.common.actor.ShapeAnimation;
import com.aspectgaming.common.actor.Sound;
import com.aspectgaming.common.data.Content;
import com.aspectgaming.common.data.GameData;
import com.aspectgaming.common.data.GameMode;
import com.aspectgaming.common.data.State;
import com.aspectgaming.common.event.EventMachine;
import com.aspectgaming.common.event.freegame.RetriggerAnimEndEvent;
import com.aspectgaming.common.event.freegame.RetriggerEvent;
import com.aspectgaming.common.event.game.*;
import com.aspectgaming.common.event.machine.InTiltEvent;
import com.aspectgaming.common.event.machine.LanguageChangedEvent;
import com.aspectgaming.common.event.machine.OutTiltEvent;
import com.aspectgaming.common.loader.SoundLoader;
import com.aspectgaming.common.loader.VideoLoader;
import com.aspectgaming.common.video.Video;
import com.aspectgaming.gdx.component.drawable.DrawableComponent;
import com.aspectgaming.gdx.component.drawable.meter.MetersComponent;
import com.aspectgaming.gdx.component.drawable.progressivereel.ProgressiveReelComponent;
import com.aspectgaming.gdx.component.drawable.winshow.WinShowComponent;
import com.aspectgaming.net.game.GameClient;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.AddAction;

import java.awt.*;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

/**
 * If win more freegame in freegame, show the message.
 *
 * @author ligang.yao
 */
public class RetriggerComponent extends DrawableComponent {

    private static final float FADING_TIME = 0.5f;

    private Sound sndAlert;
    private Sound sndBellRinging;
    private ShapeAnimation logoIn;
    private ShapeAnimation logoLoop;
    private ShapeAnimation logoFade;
    private WinShowComponent winShowComp;


    private boolean inRetrigger;
    private boolean inRetriggerAnim;

    public RetriggerComponent() {

        sndAlert = SoundLoader.getInstance().get("freegame/Retrigger");
        sndBellRinging = SoundLoader.getInstance().get("freegame/Bell");

//        logoIn = new ShapeAnimation("Cupid", "logo", "E_in1", "FreeGameIntro");
//        logoIn.setPosition(0,0);
//        addActor(logoIn);

        logoIn = new ShapeAnimation("FreeGame", "777_free", "animation_in2", "FreeGameIntro");
        logoIn.setPosition(0, 0);
        addActor(logoIn);
        logoLoop = new ShapeAnimation("FreeGame", "777_free", "animation2", "FreeGameIntro");
        logoLoop.setPosition(0, 0);
        addActor(logoLoop);
        logoFade = new ShapeAnimation("FreeGame", "777_free", "animation_out2", "FreeGameIntro");
        logoFade.setPosition(0, 0);
        addActor(logoFade);

        registerEvent(new GameResetEvent() {
            @Override
            public void execute(Object... obj) {
                clearActions();

                winShowComp = (WinShowComponent) Content.getInstance().getComponent(Content.WINSHOWCOMPONENT);

                if (sndAlert != null) sndAlert.stop();

                if (GameData.getPrevious() != null) {
                    if (GameData.getPrevious().Context.TestMode && !GameData.getInstance().Context.TestMode) {
                        if (inRetriggerAnim) {
                            if (logoIn.isPlaying()||logoLoop.isPlaying()||logoFade.isPlaying()||logoIn.isPlaying()) {
                                logoIn.setEndListener(null);
                                logoLoop.setEndListener(null);
                                logoFade.setEndListener(null);
                            }
                            if (logoIn.isPlaying() == true) {
                                logoIn.addAction(fadeOut(0.0f));
                            }
                            else if (logoLoop.isPlaying() == true) {
                                logoLoop.addAction(fadeOut(0.0f));
                            }
                            else if (logoFade.isPlaying() == true)
                            {
                                logoFade.addAction(fadeOut(0.0f));
                            }

                        }
                    }
                }
            }
        });

        registerEvent(new LanguageChangedEvent() {
            @Override
            public void execute(Object... obj) {

            }
        });

        registerEvent(new ReelStartSpinEvent() {
            @Override
            public void execute(Object... obj) {
                clearActions();
                inRetrigger = false;
                inRetriggerAnim = false;
            }
        });

        registerEvent(new WinMeterStartRollingEvent() {
            @Override
            public void execute(Object... obj) {
                if (GameData.getInstance().Context.FreeGameMode) {
                    inRetrigger = false;
                    addAction(run(() -> EventMachine.getInstance().offerEvent(ReadyToGameEndEvent.class)));
                }
            }
        });

        registerEvent(new RetriggerEvent() {
            @Override
            public void execute(Object... obj) {
                inRetrigger = true;
                if (sndBellRinging != null) {
                    sndBellRinging.play();

                    addAction(delay(sndBellRinging.duration()-3, run(() -> {
                        PlayRetriggerAnim();
                    })));
                } else {
                    PlayRetriggerAnim();
                }
            }
        });

        registerEvent(new GameStateChangedEvent() {
            @Override
            public void execute(Object... obj) {
//                System.out.println(GameData.getInstance().Context.GameState);
                if (GameData.getInstance().Context.GameState == State.ReelStop) {
                    if (inRetriggerAnim) {
                        if (logoIn.isPlaying()||logoLoop.isPlaying()||logoFade.isPlaying()||logoIn.isPlaying()) {
                            logoIn.setEndListener(null);
                            logoLoop.setEndListener(null);
                            logoFade.setEndListener(null);
                            logoIn.stop();
                            logoLoop.stop();
                            logoFade.stop();
                        }
                        addAction(run(retriggerAnimEnd));
                    }
                }
            }
        });

        registerEvent(new MultiReelStopEvent() {
            @Override
            public void execute(Object... obj) {
                if (inRetriggerAnim) {
                    if (logoIn.isPlaying()||logoLoop.isPlaying()||logoFade.isPlaying()||logoIn.isPlaying()) {
                        logoIn.setEndListener(null);
                        logoLoop.setEndListener(null);
                        logoFade.setEndListener(null);
                    }
                    addAction(run(retriggerAnimEnd));
                }
            }
        });

        registerEvent(new InTiltEvent() {
            @Override
            public void execute(Object... obj) {
                pause(); // pause actions to avoid missing gameEnd if tilt occurs during action running
            }
        });

        registerEvent(new OutTiltEvent() {
            @Override
            public void execute(Object... obj) {
                resume();
            }
        });
    }

    private void PlayRetriggerAnim() {
        logoIn.setEndListener(() -> {
            logoLoop.play(false);
        });

        logoLoop.setEndListener(() -> {
            logoFade.play(false);
        });

        logoIn.play(false);

        if (sndAlert != null) {
            sndAlert.play();
        }

        inRetriggerAnim = true;

        logoFade.setEndListener(retriggerAnimEnd);
    }

    private final Runnable retriggerAnimEnd = new Runnable() {
        @Override
        public void run() {

            if (logoIn.isPlaying() == true) {
                logoIn.addAction(fadeOut(0.5f));
            }
            else if (logoLoop.isPlaying() == true) {
                logoLoop.addAction(fadeOut(0.5f));
            }
            else if (logoFade.isPlaying() == true)
            {
                logoFade.addAction(fadeOut(0.5f));
            }
            EventMachine.getInstance().offerEvent(RetriggerAnimEndEvent.class);
        }
    };

    public boolean IsinRetrigger() {
        return inRetrigger;
    }
}
