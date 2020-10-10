package com.aspectgaming.gdx.component.drawable.meter;

import com.aspectgaming.common.action.LongIntAction;
import com.aspectgaming.common.actor.Image;
import com.aspectgaming.common.actor.Sound;
import com.aspectgaming.common.actor.TextureLabel;
import com.aspectgaming.common.configuration.GameConfiguration;
import com.aspectgaming.common.configuration.MeterConfiguration;
import com.aspectgaming.common.configuration.SoundCfg;
import com.aspectgaming.common.data.Content;
import com.aspectgaming.common.data.GameData;
import com.aspectgaming.common.data.GameMode;
import com.aspectgaming.common.data.State;
import com.aspectgaming.common.event.EventMachine;
import com.aspectgaming.common.event.GameModeChangeEvent;
import com.aspectgaming.common.event.bonus.InBonusEvent;
import com.aspectgaming.common.event.freegame.*;
import com.aspectgaming.common.event.gamble.GambleDisplayPendingEvent;
import com.aspectgaming.common.event.gamble.InGambleEvent;
import com.aspectgaming.common.event.gamble.OutGambleEndEvent;
import com.aspectgaming.common.event.gamble.TakeWinEvent;
import com.aspectgaming.common.event.game.*;
import com.aspectgaming.common.event.machine.*;
import com.aspectgaming.common.event.progressive.ProgressiveSkipEndEvent;
import com.aspectgaming.common.event.screen.HelpShowEvent;
import com.aspectgaming.common.loader.CoordinateLoader;
import com.aspectgaming.common.loader.ImageLoader;
import com.aspectgaming.common.loader.SoundLoader;
import com.aspectgaming.common.loader.VideoLoader;
import com.aspectgaming.common.video.Video;
import com.aspectgaming.gdx.component.drawable.DrawableComponent;
import com.aspectgaming.gdx.component.drawable.progressivereel.ProgressiveReelComponent;
import com.aspectgaming.gdx.component.drawable.reel.ReelComponent;
import com.aspectgaming.gdx.component.drawable.winshow.WinShowComponent;
import com.aspectgaming.gdx.component.statemachine.StateMachineComponent;
import com.aspectgaming.net.game.GameClient;
import com.aspectgaming.net.game.data.ContextData;
import com.aspectgaming.net.game.data.MathParam;
import com.aspectgaming.util.CommonUtil;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.AddAction;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import org.lwjgl.Sys;

import java.io.Console;
import java.util.ArrayList;
import java.util.List;

import static com.aspectgaming.common.data.State.*;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

/**
 * Show all meter.
 *
 * @author ligang.yao
 */
public class MetersComponent extends DrawableComponent {
    //private Video[] celebrationVideos;
    private TextureLabel txtLinesPlayed;
    private TextureLabel txtbetPerLine;
    private TextureLabel txtBet;
    private TextureLabel txtCredit;
    private TextureLabel txtWin;

    private TextureLabel txtLinesPlayedTouchArea;
    private TextureLabel txtBetPerLineTouchArea;
    private TextureLabel txtCreditTouchArea;
    private TextureLabel txtBetTouchArea;
    private TextureLabel txtWinTouchArea;

    private Image imgBetWinCredits;
    private Sound sndBeforMeterRolling;
    private Sound sndMeterRolling;

    private boolean isMeterSndStop;
    private boolean isInPreWinShow;
    private boolean isInFreeGameIntro;
    private boolean isP2Win;
    private boolean isJACKPOTWIN;

    public static boolean isCredits = true;
    private boolean isManualStopRollUp = false;
    private int reelStopCount = 0;
    private int freeGameOutroCount = 0;
    private int progressiveResultsCount = 0;
    private long count=0;

    private List<Actor> allActors = new ArrayList<>();
    private List<Actor> baseGameActors = new ArrayList<>();
    private List<Actor> freeGameActors = new ArrayList<>();

    private float multiple;
    private SoundCfg scfg;

    private long lastCredits;
    private long lastCash;

    private long rollCredits;

    private StateMachineComponent stateMachine;
    private ReelComponent reel;

    private final LongIntAction rollAction = new LongIntAction() {
        @Override
        protected void update(float percent) {
            super.update(percent);
            updateWinMeter();
        }

        @Override
        protected void end() {
            super.end();
            setValue(getEnd());
            updateWinMeterEnd();
            isMeterSndStop = true;
        }

        private void updateWinMeter() {
            int denominate = 1;
            for (MathParam param : GameData.getInstance().Context.MathParams) {
                if (param.Key.equals("DENOMINATION")) {
                    denominate = Integer.parseInt(param.Value);
                    break;
                }
            }

            if (isCredits) {
                txtWin.setForamtVal(getValue());
                txtWin.setMaxWidth(5f);
//                if ((GameData.currentGameMode != GameMode.FreeGame)) {
//                    txtCredit.setForamtVal(rollCredits + getValue());
//                    txtCredit.setMaxWidth(10f);
//                }
            } else {
                txtWin.setText(GameData.Currency.format(getValue() * denominate));
                txtWin.setMaxWidth(5f);
//                if ((GameData.currentGameMode != GameMode.FreeGame)) {
//                    txtCredit.setText(GameData.Currency.format((rollCredits + getValue()) * denominate));
//                    txtCredit.setMaxWidth(10f);
//                }
            }
        }

        private void updateWinMeterEnd() {
            int denominate = 1;
            for (MathParam param : GameData.getInstance().Context.MathParams) {
                if (param.Key.equals("DENOMINATION")) {
                    denominate = Integer.parseInt(param.Value);
                    break;
                }
            }

            long baseGameTotalWin = GameData.getInstance().Context.BaseGameTotalWin*denominate;
            long freeGameTotalWin = GameData.getInstance().Context.FreeGameTotalWin*denominate;
            long progressiveTotalWin = GameData.getInstance().Context.ProgressiveTotalWin;
            count=baseGameTotalWin+progressiveTotalWin+freeGameTotalWin;

            if (isCredits) {
                txtWin.setForamtVal(getValue());
                txtWin.setMaxWidth(5f);
                if ((GameData.currentGameMode != GameMode.FreeGame)) {
                    txtCredit.setForamtVal(rollCredits + getValue());
                    txtCredit.setMaxWidth(10f);
                }
            } else {
                if (progressiveTotalWin>0){
                    txtWin.setText(GameData.Currency.format(count));
                }else {
                    txtWin.setText(GameData.Currency.format(getValue() * denominate));
                }
                txtWin.setMaxWidth(5f);
                if ((GameData.currentGameMode != GameMode.FreeGame)) {
                    txtCredit.setText(GameData.Currency.format(GameData.getInstance().Context.Cash));
                    txtCredit.setMaxWidth(10f);
                }
            }
        }
    };


    private final LongIntAction rollActionCredit = new LongIntAction() {
        @Override
        protected void update(float percent) {
            super.update(percent);
//            updateWinMeter();
        }

        @Override
        protected void end() {
            super.end();
            setValue(getEnd());
            isMeterSndStop = true;
        }

        private void updateWinMeter() {
            int denominate = 1;
            for (MathParam param : GameData.getInstance().Context.MathParams) {
                if (param.Key.equals("DENOMINATION")) {
                    denominate = Integer.parseInt(param.Value);
                    break;
                }
            }

            if (isCredits) {
                txtCredit.setForamtVal(getValue());
                txtCredit.setMaxWidth(10f);

            } else {
                txtCredit.setText(GameData.Currency.format(getValue() * denominate));
                txtCredit.setMaxWidth(10f);
            }
        }
    };

    private final Runnable onRollingStopped = () -> {
        if (GameData.currentGameMode == GameMode.FreeGame) {
//            EventMachine.getInstance().offerEvent(ModifyFreeBGMVolEvent.class, 1.0f);
        }
        EventMachine.getInstance().offerEvent(WinMeterStopRollingEvent.class);
        clearActions();

        if (GameData.getInstance().Context.State== PayGameResults||GameData.getInstance().Context.State== ProgressiveResults)
        {
            addAction(run(GameClient.getInstance().gameEnd));
        }
        if (GameData.getInstance().Context.State==FreeGameOutro)
        {
            addAction(run(GameClient.getInstance().freeGameOutroEnd));
        }

        addAction(delay(0.1f, run(() -> GameClient.getInstance().gambleTakeWin())));


        if (multiple < 1.0f) {
            if (sndMeterRolling != null) {
                sndMeterRolling.stop();
                sndMeterRolling = null;
            }
        }

        stopCelebrationAnim();
//        updateCreditAndBetMeters();

        if (GameData.currentGameMode != GameMode.FreeGame && GameData.getInstance().Context.State != FreeGameOutro && (GameData.getInstance().Context.TotalWin > (5 * GameData.getInstance().Context.TotalBet))) {
            if (isManualStopRollUp) {
                addAction(run(GameClient.getInstance().gameEnd));
            }
        }

        if (GameData.getInstance().Context.State == FreeGameOutro) {
            if (isManualStopRollUp) {
                addAction(run(GameClient.getInstance().freeGameOutroEnd));
            }
        }
    };

    private final Runnable onRollingStart = () -> {
        if (sndMeterRolling == null) return;

        isP2Win = false;
        isJACKPOTWIN = false;

        for (MathParam param : GameData.getInstance().Context.MathParams) {
            if (param.Key.equals("P2Win")) {
                if (param.Value.equals("true")) {
                    isP2Win = true;
                    break;
                }
            }
            if (param.Key.equals("JACKPOTWIN")) {
                if (param.Value.equals("true")) {
                    isJACKPOTWIN = true;
                    break;
                }
            }
        }

        EventMachine.getInstance().offerEvent(WinMeterStartRollingEvent.class, scfg);

        if (GameData.currentGameMode == GameMode.FreeGame) {
//            if (multiple >= 20.0f) {
                sndMeterRolling.play();
//            }
        } else {
            if (isP2Win || isJACKPOTWIN) {
                sndMeterRolling.stop();
            } else {
                sndMeterRolling.play();
            }
        }


        if (GameData.currentGameMode != GameMode.FreeGame) {
            playCelebrationAnim();
        }
        rollCredits = GameData.getInstance().Context.Credits;
        addAction(sequence(rollAction, run(onRollingStopped)));
        rollAction.act(0); // start action to make isWinMeterRolling() return true immediately
    };

    public MetersComponent() {
        setTouchable(Touchable.enabled);

        imgBetWinCredits = addImage("BetWinCredits", "Meter/BetWinCredits");

        txtLinesPlayed = addLabel("LinesPlayed");
        txtbetPerLine = addLabel("BetPerLine");
        txtCredit = addLabel("Credit");
        txtBet = addLabel("Bet");
        txtWin = addLabel("Win");

        txtLinesPlayedTouchArea = addLabel("Credit", "CreditTouchArea");
        txtBetPerLineTouchArea = addLabel("Credit", "CreditTouchArea");
        txtCreditTouchArea = addLabel("Credit", "CreditTouchArea");
        txtBetTouchArea = addLabel("Bet", "BetTouchArea");
        txtWinTouchArea = addLabel("Win", "WinTouchArea");

        isMeterSndStop = true;
        isInPreWinShow = false;

        //celebrationVideos = new Video[2];

        ClickListener onClicked = new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                setCreditMode(!isCredits);
                updateCreditAndBetMeters();
                updateTxtWinMeters(rollAction.getValue());
            }
        };

        txtCreditTouchArea.addListener(onClicked);
        txtCreditTouchArea.setTouchable(Touchable.enabled);
//        txtBetTouchArea.addListener(onClicked);
//        txtBetTouchArea.setTouchable(Touchable.enabled);
        txtWinTouchArea.addListener(onClicked);
        txtWinTouchArea.setTouchable(Touchable.enabled);

        // ---------- events -------------
        registerEvent(new GameResetEvent() {
            @Override
            public void execute(Object... obj) {
                stateMachine = (StateMachineComponent) Content.getInstance().getComponent(Content.STATEMACHINECOMPONENT);
                reel = (ReelComponent) Content.getInstance().getComponent(Content.REELCOMPONENT);
                reelStopCount = 0;
                freeGameOutroCount = 0;
                progressiveResultsCount = 0;
                isManualStopRollUp = false;

                clearActions();
                stopCelebrationAnim();

                if (sndMeterRolling != null) {
                    sndMeterRolling.stop();
                    sndMeterRolling = null;
                }

                if (sndBeforMeterRolling != null) {
                    sndBeforMeterRolling.stop();
                    sndBeforMeterRolling = null;
                }

                isMeterSndStop = true;
                isInPreWinShow = false;
                isInFreeGameIntro = false;

                ContextData contextdata = GameData.getInstance().Context;
                int gameState = contextdata.GameState;
                long winValue = contextdata.TotalWin;

                if (GameData.currentGameMode == GameMode.FreeGame) {
                    if (contextdata.GameState == State.FreeGameResults || contextdata.GameState == State.ReelStop) {
                        winValue -= contextdata.Win; // need to roll up again
                    }
                }

                if (gameState == State.ProgressiveIntro || gameState == State.ProgressiveStarted ||
                        gameState == State.AwardSASProgressive || gameState == State.ProgressiveResults) {
                    winValue -= getCurrentGameWin();
                }

                if (gameState == State.GambleStarted || gameState == State.GambleChoice ||
                        gameState == State.GambleDisplayPending || gameState == State.GambleWin) {
                    winValue -= contextdata.GambleTotalWin;
                }

                rollAction.setValue(winValue);
                rollAction.setEnd(winValue);

                updateTxtWinMeters(winValue);
                if (GameData.getInstance().Context.TestMode&&!GameData.getPrevious().Context.TestMode){
                    rollAction.setValue(0);
                    rollAction.setEnd(0);
                    updateTxtWinMeters(0);
                }
                updateCreditAndBetMeters();

                EventMachine.getInstance().offerEvent(TakeWinEvent.class);
                GameClient.getInstance().gambleTakeWin();

                switch (GameData.getInstance().Context.GameState){
                    case ProgressiveResults :
                            EventMachine.getInstance().offerEvent(ProgressiveSkipEndEvent.class);
                        break;
                }
            }
        });

        registerEvent(new StateChangedEvent() {
            @Override
            public void execute(Object... obj) {
                updateCreditAndBetMeters();
            }
        });

//        registerEvent(new OutFreeGameOutroEvent(){
//            @Override
//            public void execute(Object... obj) {
//                rollActionCredit.reset();
//                rollActionCredit.setStart(GameData.getInstance().Context.Credits);
//                rollActionCredit.setDuration(4f);
//                rollActionCredit.setEnd(GameData.getInstance().Context.Credits+GameData.getInstance().Context.TotalWin);
//                addAction(sequence(rollActionCredit, run(onRollingStopped)));
//                rollActionCredit.act(0); // start action to make isWinMeterRolling() return true immediately
//            }
//        });

        registerEvent(new LanguageChangedEvent() {
            @Override
            public void execute(Object... obj) {
                ImageLoader.getInstance().reload(imgBetWinCredits);
            }
        });

        registerEvent(new DenomChangedEvent() {
            @Override
            public void execute(Object... obj) {
                updateCreditAndBetMeters();
                updateTxtWinMeters(rollAction.getValue());
            }
        });

        registerEvent(new ReelStoppedEvent() {
            @Override
            public void execute(Object... obj) {
                if (!haveJackPots()) {
                    if (GameData.getInstance().Context.Win > 0 || GameData.getInstance().Context.Win != 0) {
                        if (!(GameData.getInstance().Context.FreeGameMode && GameData.getInstance().Context.Result.NumFreeSpinsWon > 0)) {
                            startPreWinShow();
                        }
                    } else {
                        WinShowComponent winShow = (WinShowComponent) Content.getInstance().getComponent(Content.WINSHOWCOMPONENT);
                        winShow.setLoopCount(Integer.MAX_VALUE);
                    }
                } else {
                    if (!GameData.getInstance().Context.FreeGameMode && GameData.getInstance().Context.Result.NumFreeSpinsWon > 0) {
                        startPreWinShow();
                    } else {
                        WinShowComponent winShow = (WinShowComponent) Content.getInstance().getComponent(Content.WINSHOWCOMPONENT);
                        winShow.setLoopCount(Integer.MAX_VALUE);
                    }
                    if(haveP2())
                    {
                        GameClient.getInstance().buttonPlay();
                    }
                }
                if (GameData.getInstance().Context.FreeGameMode) {
                    int denominate = 1;
                    for (MathParam param : GameData.getInstance().Context.MathParams) {
                        if (param.Key.equals("DENOMINATION")) {
                            denominate = Integer.parseInt(param.Value);
                            break;
                        }
                    }
                    long baseGameTotalWin = GameData.getInstance().Context.BaseGameTotalWin * denominate;
                    long freeGameTotalWin = GameData.getInstance().Context.FreeGameTotalWin * denominate;
                    long progressiveTotalWin = GameData.getInstance().Context.ProgressiveTotalWin;
                    count = baseGameTotalWin + progressiveTotalWin + freeGameTotalWin;
                }
            }
        });

        registerEvent(new ProgressiveReelOutroEvent() {
            @Override
            public void execute(Object... obj) {
                rollAction.setEnd(GameData.getInstance().Context.ProgressiveTotalWin);
                if (!(GameData.getInstance().Context.FreeGameMode && GameData.getInstance().Context.Result.NumFreeSpinsWon > 0)) {
                    playRollUp();
                } else {
                    startPreWinShow();
                }
            }
        });

        registerEvent(new RetriggerAnimEndEvent() {
            @Override
            public void execute(Object... obj) {
                playRollUp();
            }
        });

        registerEvent(new ReelStartSpinEvent() {
            @Override
            public void execute(Object... obj) {
                clearActions();

                stopCelebrationAnim();

                if (sndBeforMeterRolling != null) {
                    sndBeforMeterRolling.stop();
                    sndBeforMeterRolling = null;
                }

                if (sndMeterRolling != null) {
                    sndMeterRolling.stop();
                    sndMeterRolling = null;
                }

                isMeterSndStop = true;
                isInPreWinShow = false;
                isInFreeGameIntro = false;
                isManualStopRollUp = false;
                reelStopCount = 0;
                freeGameOutroCount = 0;
                progressiveResultsCount = 0;

                if (isWinMeterRolling()) {
                    rollAction.finish();
                }

                if (GameData.currentGameMode == GameMode.BaseGame) {
                    rollAction.setValue(0);
//                    System.out.println(GameData.getInstance().Context.State);
                    updateTxtWinMeters(0);
                } else {
                    rollAction.setValue(rollAction.getEnd());
                    updateTxtWinMeters(rollAction.getEnd());
                    GameClient.getInstance().selectFreeLastTotalWin(rollAction.getEnd());
                }

                rollAction.setEnd(GameData.getInstance().Context.TotalWin);

                lastCredits = GameData.getInstance().Context.Credits;
                lastCash = GameData.getInstance().Context.Cash;

                GameClient.getInstance().selectDenomination(GameData.getInstance().Context.Denomination);
                updateCreditAndBetMeters();
            }
        });

        registerEvent(new InBonusEvent() {
            @Override
            public void execute(Object... obj) {
                stopWinMeter();
            }
        });

        registerEvent(new InFreeGameIntroEvent() {
            @Override
            public void execute(Object... obj) {
                isInFreeGameIntro = true;
                stopWinMeter();
            }
        });

        registerEvent(new InFreeGameOutroEvent() {
            @Override
            public void execute(Object... obj) {
                stopWinMeter();
                rollActionCredit.reset();
                rollActionCredit.setStart(GameData.getInstance().Context.Credits);
                rollActionCredit.setDuration(4f);
                rollActionCredit.setEnd(GameData.getInstance().Context.Credits + GameData.getInstance().Context.TotalWin);
                addAction(sequence(rollActionCredit, run(onRollingStopped)));
                rollActionCredit.act(0); // start action to make isWinMeterRolling() return true immediately
            }
        });

        registerEvent(new OutGambleEndEvent() {
            @Override
            public void execute(Object... obj) {
                rollAction.setValue(GameData.getInstance().Context.TotalWin);
                rollAction.setEnd(GameData.getInstance().Context.TotalWin);
                updateTxtWinMeters(GameData.getInstance().Context.TotalWin);
            }
        });

        registerEvent(new InTiltEvent() {
            @Override
            public void execute(Object... obj) {
                if (GameData.getInstance().Context.State == State.Handpay) {
                    stopWinMeter();
                } else {
                    pause();
                }
            }
        });

        registerEvent(new OutTiltEvent() {
            @Override
            public void execute(Object... obj) {
                resume();
            }
        });

        registerEvent(new InGambleEvent() {
            @Override
            public void execute(Object... obj) {
                stopWinMeter();
            }
        });

        registerEvent(new TakeWinEvent() {
            @Override
            public void execute(Object... obj) {
                stopWinMeter();
//                EventMachine.getInstance().offerEvent(WinMeterStopRollingEvent.class);
            }
        });

        registerEvent(new GambleDisplayPendingEvent() {
            @Override
            public void execute(Object... obj) {
                stopWinMeter();
            }
        });

        registerEvent(new ChangeBetEvent() {
            @Override
            public void execute(Object... obj) {
                updateCreditAndBetMeters();
                stopWinMeter();
            }
        });

        registerEvent(new CreditsChangedEvent() {
            @Override
            public void execute(Object... obj) {
                if (isWinMeterStop()) {
                    updateCreditAndBetMeters();
                }
            }
        });

        registerEvent(new ProgressiveSkipEndEvent() {
            @Override
            public void execute(Object... obj) {
                if (!isInFreeGameIntro) {
                    if (GameData.getInstance().Context.GameState != State.FreeGameOutro) {
                        clearActions();
                        //resetCelebrationVideo();

                        isMeterSndStop = false;
                        isInPreWinShow = true;

                        rollAction.setEnd(GameData.getInstance().Context.TotalWin);
                        addAction(delay(2.5f, run(() -> playRollUp())));
                        return;
                    }
                }

                rollAction.setValue(GameData.getInstance().Context.TotalWin);
                rollAction.setEnd(GameData.getInstance().Context.TotalWin);
                updateTxtWinMeters(rollAction.getValue());
            }
        });

        registerEvent(new PreWinShowStopEvent() {
            @Override
            public void execute(Object... obj) {
                if (!(!GameData.getInstance().Context.FreeGameMode && GameData.getInstance().Context.Result.NumFreeSpinsWon > 0 && haveJackPots())) {
                    playRollUp();
                }
            }
        });

        registerEvent(new MultiReelStopEvent() {
            @Override
            public void execute(Object... obj) {
                //ReelComponent reel = (ReelComponent) Content.getInstance().getComponent(Content.REELCOMPONENT);
                switch (GameData.getInstance().Context.GameState) {
                    case ProgressiveResults:
                        if (GameData.currentGameMode == GameMode.BaseGame) {
                            if (isWinMeterRolling()) {
                                progressiveResultsCount++;
                                if (progressiveResultsCount > 1) {
                                    isManualStopRollUp = true;
                                    stopWinMeter();
                                    EventMachine.getInstance().offerEvent(StopCelebrationClickSpinEvent.class);
                                    addAction(run(onRollingStopped));
                                    progressiveResultsCount = 0;
                                }
                            } else {
                                if (!isManualStopRollUp && !reel.isSpinning) {
                                    addAction(run(GameClient.getInstance().gameEnd));
                                }
                            }
                        }
                        break;
                    case ReelStop:
                        if (GameData.currentGameMode == GameMode.BaseGame) {
                            if (isWinMeterRolling()) {
                                if (stateMachine.isManualStopInBaseGames()) {
                                    reelStopCount++;
                                    if (reelStopCount > 1) {
                                        isManualStopRollUp = true;
                                        stopWinMeter();
                                        EventMachine.getInstance().offerEvent(StopCelebrationClickSpinEvent.class);
                                        addAction(run(onRollingStopped));
                                        reelStopCount = 0;
                                    }
                                } else {
                                    isManualStopRollUp = true;
                                    stopWinMeter();
                                    EventMachine.getInstance().offerEvent(StopCelebrationClickSpinEvent.class);
                                    addAction(run(onRollingStopped));
                                }
                            } else {
                                if (!isManualStopRollUp && !reel.isSpinning) {
                                    addAction(run(GameClient.getInstance().gameEnd));
                                }
                            }
                        }
                        break;
                    case FreeGameOutro:
                        // TODO if free game total win = 0
                        if (isCreditsMeterRolling()) {
                            freeGameOutroCount++;
                            if (freeGameOutroCount > 1) {
                                //rollActionCredit.setEnd(GameData.getInstance().Context.Credits + GameData.getInstance().Context.TotalWin);
                                isManualStopRollUp = true;
                                stopWinMeter();
                                stopCreditMeter();
                                EventMachine.getInstance().offerEvent(StopCelebrationClickSpinEvent.class);
                                addAction(run(onRollingStopped));
                                freeGameOutroCount = 0;
                            }
                        } else {
                            if (!isManualStopRollUp && !reel.isSpinning) {
                                addAction(run(GameClient.getInstance().freeGameOutroEnd));
                            }
                        }
                        break;
                    default:
                        break;
                }

            }
        });

        registerEvent(new GameStateChangedEvent() {
            @Override
            public void execute(Object... obj) {
                System.out.println("########################################++++++:"+GameData.getInstance().Context.GameState);
                if (GameData.currentGameMode == GameMode.BaseGame) {
                    switch (GameData.getInstance().Context.GameState) {
                        case ReelStop:
                            //ReelComponent reel = (ReelComponent) Content.getInstance().getComponent(Content.REELCOMPONENT);
                            if (!isWinMeterRolling() && !isCreditsMeterRolling() && !reel.isSpinning) {
                                if (!isManualStopRollUp) {
                                    addAction(run(GameClient.getInstance().gameEnd));
                                }
                            }
                            break;
                        default:
                            break;
                    }
                }
            }
        });
    }

    private boolean haveJackPots() {
        for (MathParam param : GameData.getInstance().Context.MathParams) {
            if (param.Key.equals("JACKPOTINFO")) {
                int[] jackpots = CommonUtil.stringToArray(param.Value);

                if (jackpots != null) {
                    for (int i = 0; i < jackpots.length; i++) {
                        if (jackpots[i] != 0) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean haveP2() {
        for (MathParam param : GameData.getInstance().Context.MathParams) {
            if (param.Key.equals("P2Win")) {
                if (param.Value.equals("true"))
                {
                    return true;
                }
            }
        }
        return false;
    }

    private void startPreWinShow() {
        isInPreWinShow = true;
        EventMachine.getInstance().offerEvent(StartPreWinShowEvent.class);
    }

    public long getCurrentGameWin() {
        long win = GameData.getInstance().Context.Win;
        if (!GameData.getInstance().Context.FreeGameMode) {
            win += GameData.getInstance().Context.ProgressiveTotalWin / GameData.getInstance().Context.Denomination + GameData.getInstance().Context.BonusTotalWin;
        } else {
            long previousTotalWin = 0;
            for (MathParam param : GameData.getInstance().Context.MathParams) {
                if (param.Key.equals("FREELASTTOTALWIN")) {
                    previousTotalWin = Long.parseLong(param.Value);
                    break;
                }
            }

            win = GameData.getInstance().Context.TotalWin - previousTotalWin;
        }
        return win;
    }


    private void playRollUp() {
        if (GameData.getInstance().Context.State == State.Handpay) return;

        isInPreWinShow = false;
        multiple = ((float) getCurrentGameWin()) / GameData.getInstance().Context.TotalBet;
        scfg = GameConfiguration.getInstance().winMeter.getSoundCfg(multiple);

        if (scfg == null) {
            log.error("find winmeter sound cfg failed!!!");
            return;
        }

        sndMeterRolling = SoundLoader.getInstance().get(scfg.sPath);

        rollAction.reset();

        if (GameData.currentGameMode == GameMode.FreeGame) {
            rollAction.setStart(GameData.getInstance().Context.TotalWin - getCurrentGameWin());
            log.info("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$"+(GameData.getInstance().Context.TotalWin - getCurrentGameWin()));
        } else {
            rollAction.setStart(0);
        }

        rollAction.setEnd(GameData.getInstance().Context.TotalWin);
        rollAction.setDuration(scfg.rollUpTime);

        isMeterSndStop = false;

        if (GameData.currentGameMode == GameMode.FreeGame && multiple >= 15.0f) {
//            EventMachine.getInstance().offerEvent(ModifyFreeBGMVolEvent.class, 0.01f);
        }

        if (scfg.beforeSound != null) {
            sndBeforMeterRolling = SoundLoader.getInstance().get(scfg.beforeSound);
            if (GameData.currentGameMode == GameMode.FreeGame) {
                sndBeforMeterRolling.play();
            } else {
                sndBeforMeterRolling.play();
            }
            addAction(delay(0.0f, run(onRollingStart)));//sndBeforMeterRolling.duration() - scfg.overlap, run(onRollingStart)));
        } else {
            sndBeforMeterRolling = null;
            addAction(delay(0.0f, run(onRollingStart)));
        }
    }

    private TextureLabel addLabel(String name) {
        return addLabel(name, name);
    }

    private TextureLabel addLabel(String name, String bounds) {
        MeterConfiguration mc = GameConfiguration.getInstance().meters.getMeter(name);
        if (mc == null) return null;

        TextureLabel txt = new TextureLabel(mc.font, mc.color, mc.align, Align.center, bounds);
        addActor(txt);
        checkMeterMode(mc, txt);
        return txt;
    }

    private Image addImage(String name, String path) {
        MeterConfiguration mc = GameConfiguration.getInstance().meters.getMeter(name);
        if (mc == null) return null;

        Image img = ImageLoader.getInstance().load(path, name);
        addActor(img);
        checkMeterMode(mc, img);
        return img;
    }

    private void checkMeterMode(MeterConfiguration mc, Actor actor) {
        if (actor == null) return;

        allActors.add(actor);
        if (mc.mode == null) {
            baseGameActors.add(actor);
            freeGameActors.add(actor);
        } else {
            if (mc.mode.contains("BaseGame")) {
                baseGameActors.add(actor);
            }
            if (mc.mode.contains("FreeGame")) {
                freeGameActors.add(actor);
            }
        }
    }

    private void setCreditMode(boolean isCredits) {
        if (this.isCredits != isCredits) {
            this.isCredits = isCredits;
        }
    }

    private void updateTxtWinMeters(long winVal) {
        int denominate = 1;
        for (MathParam param : GameData.getInstance().Context.MathParams) {
            if (param.Key.equals("DENOMINATION")) {
                denominate = Integer.parseInt(param.Value);
                break;
            }
        }

        if (isCredits) {
//            long win=winVal * denominate / GameData.getInstance().Context.Denomination;
//            if (GameData.getInstance().Context.Progressive!=null)
//            {
//                win+=GameData.getInstance().Context.Progressive.Win;
//            }
//            txtWin.setForamtVal(win);
            txtWin.setForamtVal(winVal * denominate / GameData.getInstance().Context.Denomination);
            txtWin.setMaxWidth(5f);
        } else {
//            long currency=winVal * denominate;
//            if (GameData.getInstance().Context.Progressive!=null)
//            {
//                currency+=GameData.getInstance().Context.Progressive.Win;
//            }
//            txtWin.setText(GameData.Currency.format(currency));

//            txtWin.setText(GameData.Currency.format(winVal * denominate));
            long baseGameTotalWin = GameData.getInstance().Context.BaseGameTotalWin*denominate;
            long freeGameTotalWin = GameData.getInstance().Context.FreeGameTotalWin*denominate;
            long progressiveTotalWin = GameData.getInstance().Context.ProgressiveTotalWin;
            if (GameData.getInstance().Context.FreeGameMode){
                if (progressiveTotalWin>0){
                    txtWin.setText(GameData.Currency.format(count));
                    System.out.println("################################：：：："+count);
                }else {
                    txtWin.setText(GameData.Currency.format(winVal * denominate));
                }
            }else {
                if (progressiveTotalWin>0){
                    txtWin.setText(GameData.Currency.format(baseGameTotalWin+progressiveTotalWin+freeGameTotalWin));
                }else {
                    txtWin.setText(GameData.Currency.format(winVal * denominate));
                }
            }
            txtWin.setMaxWidth(5f);
        }

    }

    private void updateCreditAndBetMeters() {
        int denominate = 1;
        txtbetPerLine.setText("1");
        txtLinesPlayed.setText(String.valueOf(GameData.getInstance().Context.Selections));

        for (MathParam param : GameData.getInstance().Context.MathParams) {
            if (param.Key.equals("DENOMINATION")) {
                denominate = Integer.parseInt(param.Value);
                break;
            }
        }

        if (isCredits) {
            if (isWinMeterStop()) {
                txtCredit.setForamtVal(GameData.getInstance().Context.Credits);
            } else {
                txtCredit.setForamtVal(lastCredits * denominate / GameData.getInstance().Context.Denomination);
            }
            txtCredit.setMaxWidth(10);
            txtBet.setForamtVal(GameData.getInstance().Context.TotalBet);
            txtBet.setMaxWidth(3);
        } else {
            if (isWinMeterStop()) {
                txtCredit.setText(GameData.Currency.format(GameData.getInstance().Context.Cash));
            } else {
                txtCredit.setText(GameData.Currency.format(lastCash));
            }
            txtCredit.setMaxWidth(10);
            txtBet.setForamtVal(GameData.getInstance().Context.TotalBet);
            txtBet.setMaxWidth(3);
        }
    }

    public boolean isWinMeterRolling() {
        return rollAction.isRunning();
    }

    public boolean isCreditsMeterRolling() {
        return rollActionCredit.isRunning();
    }

    public boolean isWinMeterStop() {
        return !isWinMeterRolling() && isMeterSndStop && !isInPreWinShow;
    }

    private void stopCreditMeter() {
        clearActions();
        rollActionCredit.setValue(rollActionCredit.getEnd());
        if (isCreditsMeterRolling()) {
            rollActionCredit.finish();
        }
    }

    private void stopWinMeter() {
        clearActions();

        stopCelebrationAnim();
        if (sndBeforMeterRolling != null) {
            sndBeforMeterRolling.stop();
            sndBeforMeterRolling = null;
        }

        if (sndMeterRolling != null) {
            sndMeterRolling.stop();
            sndMeterRolling = null;
        }

        isMeterSndStop = true;
        isInPreWinShow = false;

        rollAction.setValue(rollAction.getEnd());

        if (isWinMeterRolling()) {
            rollAction.finish();
        } else {
            updateTxtWinMeters(rollAction.getEnd());
        }

        updateCreditAndBetMeters();
    }

    private void playCelebrationAnim() {
        if (scfg == null) {
            return;
        }
        int playCount = scfg.playCount;


        if (playCount > 0) {
            if (GameData.currentGameMode == GameMode.FreeGame) {
                EventMachine.getInstance().offerEvent(ModifyFreeBGMVolEvent.class, 0.01f);
            }
            EventMachine.getInstance().offerEvent(PlayCelebrationEvent.class, scfg.animation, scfg.lowTime, scfg.animOverlap);
        } else {
            EventMachine.getInstance().offerEvent(StopCelebrationEvent.class);
        }

    }

    private void stopCelebrationAnim() {
        if (GameData.currentGameMode == GameMode.FreeGame) {
            EventMachine.getInstance().offerEvent(ModifyFreeBGMVolEvent.class, 1.0f);
        }
        EventMachine.getInstance().offerEvent(StopCelebrationEvent.class);
    }
}
