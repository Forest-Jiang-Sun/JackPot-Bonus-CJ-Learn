package com.aspectgaming.gdx.component.statemachine;

import com.aspectgaming.common.configuration.GameConfiguration;
import com.aspectgaming.common.data.Content;
import com.aspectgaming.common.data.GameData;
import com.aspectgaming.common.data.GameMode;
import com.aspectgaming.common.data.State;
import com.aspectgaming.common.event.EventMachine;
import com.aspectgaming.common.event.bonus.BonusDisplayPendingEvent;
import com.aspectgaming.common.event.bonus.InBonusEvent;
import com.aspectgaming.common.event.freegame.InFreeGameIntroEvent;
import com.aspectgaming.common.event.freegame.InFreeGameOutroEvent;
import com.aspectgaming.common.event.freegame.OutFreeGameIntroEvent;
import com.aspectgaming.common.event.freegame.RetriggerEvent;
import com.aspectgaming.common.event.gamble.GambleDisplayPendingEvent;
import com.aspectgaming.common.event.gamble.GambleWinEvent;
import com.aspectgaming.common.event.gamble.InGambleEvent;
import com.aspectgaming.common.event.gamble.TakeWinEvent;
import com.aspectgaming.common.event.game.*;
import com.aspectgaming.common.event.machine.InTiltEvent;
import com.aspectgaming.common.event.machine.OutTiltEvent;
import com.aspectgaming.common.event.progressive.ProgressiveEndedEvent;
import com.aspectgaming.common.event.progressive.ProgressiveIntroEvent;
import com.aspectgaming.common.event.progressive.ProgressiveStartedEvent;
import com.aspectgaming.common.event.progressive.ProgressiveWinShowEvent;
import com.aspectgaming.gdx.component.Component;
import com.aspectgaming.gdx.component.drawable.freegames.FreeGameAnticipationSpinComponent;
import com.aspectgaming.gdx.component.drawable.meter.MetersComponent;
import com.aspectgaming.gdx.component.drawable.progressivereel.ProgressiveReelComponent;
import com.aspectgaming.gdx.component.drawable.reel.ReelComponent;
import com.aspectgaming.gdx.component.drawable.retrigger.RetriggerComponent;
import com.aspectgaming.gdx.component.drawable.winshow.WinShowComponent;
import com.aspectgaming.net.game.GameClient;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.delay;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.run;

/**
 * @author ligang.yao
 */
public class StateMachineComponent extends Component implements State {

    private boolean delayToIntro;
    private boolean delayToGameEnd;
    private boolean gameEndPending;
    private boolean manualStopInFreeGames;
    private boolean manualStopInBaseGames;

    private int currentReelStopCount;
    private long timeReelStopped;

    public StateMachineComponent() {
        GameClient.getInstance().stopAutoPlay();

        registerEvent(new GameResetEvent() {
            @Override
            public void execute(Object... obj) {
                switch (GameData.getInstance().Context.GameState) {
                    case PrimaryGameStarted:
                    case FreeGameStarted:
                        addAction(run(GameClient.getInstance().startPlay));
                        break;
                    case PayGameResults:
                    case FreeGameResults:
                    case ReelStop:
                        EventMachine.getInstance().offerEvent(ReelStartSpinEvent.class);
                        break;
                    case FreeGameOutro:
                        EventMachine.getInstance().offerEvent(InFreeGameOutroEvent.class);
                        addAction(run(GameClient.getInstance().buttonPlay));
                        break;
                    default:
                        break;
                }
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

        registerEvent(new ReelStartSpinEvent() {
            @Override
            public void execute(Object... obj) {
                manualStopInBaseGames = false;
                delayToGameEnd = false;
                gameEndPending = false;
                clearActions();
            }
        });

        registerEvent(new ReelStoppedEvent() {
            @Override
            public void execute(Object... obj) {
                currentReelStopCount = GameData.reelStopCount;

                if (GameData.currentGameMode == GameMode.FreeGame) {
                    if (!manualStopInFreeGames) {
                        if (GameData.getInstance().Context.State != State.ReelStop)
                            currentReelStopCount = GameData.reelStopCount - 1;
                    }
                }

                manualStopInFreeGames = false;
                timeReelStopped = System.nanoTime();


                if (GameData.currentGameMode == GameMode.BaseGame) {
                    if (GameData.getInstance().Context.Result.NumFreeSpinsWon != 0) {
                        delayToIntro = true;
                    } else {
                        if (GameData.getInstance().Context.AutoPlay) {
                            delayToGameEnd = true;
                        } else {
                            if (!(GameData.getInstance().Context.TotalWin > (5 * GameData.getInstance().Context.TotalBet))) {
                                gameEnd(0);
                            } else {
                                log.info("a  win is greater than 5 times bet!");
                            }
                        }
                    }
                } else {
                    if (GameData.getInstance().Context.Result.NumFreeSpinsWon != 0) {
                        EventMachine.getInstance().offerEvent(RetriggerEvent.class);
                    } else {
                        delayToGameEnd = true;
                    }
                }
            }
        });

        registerEvent(new ReadyToGameEndEvent() {
            @Override
            public void execute(Object... obj) {
                delayToGameEnd = true;
            }
        });

        registerEvent(new MultiReelStopEvent() {
            @Override
            public void execute(Object... obj) {
                GameData.reelStopCount++;
            }
        });

        registerEvent(new GameStateChangedEvent() {
            @Override
            public void execute(Object... obj) {
                switch (GameData.getPrevious().Context.GameState) {
                    case ProgressiveResults:
                        log.info("GameStateChangedEvent getPrevious() GameState => ProgressiveResults");
                        EventMachine.getInstance().offerEvent(ProgressiveEndedEvent.class);
                        break;
                }

                switch (GameData.getInstance().Context.GameState) {
                    case PrimaryGameStarted:
                    case FreeGameStarted:
                        log.info("GameStateChangedEvent GameState => " + GameData.getInstance().Context.GameState);
                        if (GameData.isFreeIntroPlaying) {
                            EventMachine.getInstance().offerEvent(OutFreeGameIntroEvent.class);
                            addAction(delay(0.1f, run(GameClient.getInstance().startPlay)));
                        } else {
                            if (GameData.getInstance().Context.LoggedIn) {
                                EventMachine.getInstance().offerEvent(ReelStartSpinEvent.class);
                            }
                            addAction(run(GameClient.getInstance().startPlay));
                        }
                        break;
                    case PayGameResults:
                    case FreeGameResults:
                        log.info("GameStateChangedEvent GameState => " + GameData.getInstance().Context.GameState);
                        if (!GameData.getInstance().Context.LoggedIn) {
                            EventMachine.getInstance().offerEvent(ReelStartSpinEvent.class);
                        }
                        break;
                    case BonusActive:
                        log.info("GameStateChangedEvent GameState => BonusActive");
                        EventMachine.getInstance().offerEvent(InBonusEvent.class);
                        break;
                    case BonusDisplayPending:
                        log.info("GameStateChangedEvent GameState => BonusDisplayPending");
                        EventMachine.getInstance().offerEvent(BonusDisplayPendingEvent.class);
                        break;
                    case FreeGameIntro:
                        log.info("GameStateChangedEvent GameState => FreeGameIntro");
                        EventMachine.getInstance().offerEvent(InFreeGameIntroEvent.class);
                        break;
                    case StartFreeSpin:
                        log.info("GameStateChangedEvent GameState => StartFreeSpin");
                        if (GameData.isTransitionInPlaying) {
                            EventMachine.getInstance().offerEvent(OutFreeGameIntroEvent.class);
                            addAction(delay(0.1f, run(GameClient.getInstance().startPlay)));
                        }
                        break;
                    case FreeGameOutro:
                        log.info("GameStateChangedEvent GameState => FreeGameOutro");
                        EventMachine.getInstance().offerEvent(InFreeGameOutroEvent.class);
                        break;
                    case GambleStarted:
                        log.info("GameStateChangedEvent GameState => GambleStarted");
                        EventMachine.getInstance().offerEvent(InGambleEvent.class);
                        break;
                    case GambleDisplayPending:
                        log.info("GameStateChangedEvent GameState => GambleDisplayPending");
                        EventMachine.getInstance().offerEvent(GambleDisplayPendingEvent.class);
                        break;
                    case GambleWin:
                        log.info("GameStateChangedEvent GameState => GambleWin");
                        EventMachine.getInstance().offerEvent(GambleWinEvent.class);
                        break;
                    case GameIdle:
                        log.info("GameStateChangedEvent GameState => GameIdle");
                        // TODO: if exit from progressive
//                    if (GameData.getPrevious().Context.GameState == State.GambleChoice) {
//                        EventMachine.getInstance().offerEvent(TakeWinEvent.class);
//                    }
//                    EventMachine.getInstance().offerEvent(IntoSetupBetEvent.class);
                        break;
                    case ProgressiveIntro:
                        log.info("GameStateChangedEvent GameState => ProgressiveIntro");
                        EventMachine.getInstance().offerEvent(ProgressiveIntroEvent.class);
                        break;
                    case ProgressiveStarted:
                        log.info("GameStateChangedEvent GameState => ProgressiveStarted");
                        EventMachine.getInstance().offerEvent(ProgressiveStartedEvent.class);
                        break;
                    case AwardSASProgressive:
                        log.info("GameStateChangedEvent GameState => AwardSASProgressive");
                        break;
                    case ProgressiveResults:
                        log.info("GameStateChangedEvent GameState => ProgressiveResults");
                        EventMachine.getInstance().offerEvent(ProgressiveWinShowEvent.class);
                        break;
                    case GambleChoice:
                        log.info("GameStateChangedEvent GameState => GambleChoice");
                        break;
                    case ReelStop:
                        log.info("GameStateChangedEvent GameState => ReelStop");
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private void gameEnd(float delayTime) {
        delayToGameEnd = false;
        delayToIntro = false;
        gameEndPending = false;

        clearActions();

        if (delayTime >= 0) {
            addAction(delay(delayTime, run(GameClient.getInstance().gameEnd)));
        } else {
            addAction(run(GameClient.getInstance().gameEnd));
        }
    }

    @Override
    protected void update(float delta) throws Exception {
        super.update(delta);

        if (!GameData.getInstance().Context.MultipleResults.isEmpty()) return;

        if (delayToGameEnd) {
            if (isReadyForGameEnd()) {
                gameEnd(1.0f);
                gameEndPending = true;
            }
        }

        if (delayToIntro) {
            if (isReadyForGameEnd()) {
                gameEnd(0);
            }
        }

        // handling multiple play button events in ReelStop state.
        if (GameData.getInstance().Context.State == State.ReelStop) {
            ReelComponent reel = (ReelComponent) Content.getInstance().getComponent(Content.REELCOMPONENT);
            if (reel.isSpinning && !reel.isInRandomWild && !reel.isInPreshow) {
                if (GameData.currentGameMode == GameMode.FreeGame) {
                    if (GameConfiguration.getInstance().reel.manualStopInFreeGames) {
                        manualStopInFreeGames = true;
                        reel.reelStop();
                    }
                } else {
                    if (GameConfiguration.getInstance().reel.manualStop) {
                        manualStopInBaseGames = true;
                        reel.reelStop();
                    }
                }
            }

            if (currentReelStopCount < GameData.reelStopCount) {
                if (delayToIntro) {
                    gameEnd(0);
                }


                if ((delayToGameEnd || gameEndPending) && !reel.isSpinning) {
                    float time = (System.nanoTime() - timeReelStopped) / 1000000000f;
                    // must wait 1 second before next spinning
                    //gameEnd(1 - time);
                    gameEnd(0.3f - time);
                }
            }
        }
    }

    public boolean isManualStopInBaseGames() {
        return manualStopInBaseGames;
    }

    private boolean isReadyForGameEnd() {
        WinShowComponent winShow = (WinShowComponent) Content.getInstance().getComponent(Content.WINSHOWCOMPONENT);
        MetersComponent meters = (MetersComponent) Content.getInstance().getComponent(Content.METERSCOMPONENT);
        RetriggerComponent retrigger = (RetriggerComponent) Content.getInstance().getComponent(Content.RETRIGGERCOMPONENT);

        return !GameData.getInstance().isTilt() && winShow.isOverLoop() && meters.isWinMeterStop() && !retrigger.IsinRetrigger();
    }
}
