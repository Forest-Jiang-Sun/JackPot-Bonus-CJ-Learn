package com.aspectgaming.gdx.component.drawable.winshow;

import com.aspectgaming.common.actor.Animation;
import com.aspectgaming.common.actor.Image;
import com.aspectgaming.common.actor.ShapeAnimation;
import com.aspectgaming.common.actor.Sound;
import com.aspectgaming.common.configuration.GameConfiguration;
import com.aspectgaming.common.configuration.SymbolConfiguration;
import com.aspectgaming.common.data.*;
import com.aspectgaming.common.event.EventMachine;
import com.aspectgaming.common.event.bonus.InBonusEvent;
import com.aspectgaming.common.event.freegame.*;
import com.aspectgaming.common.event.gamble.GambleDisplayPendingEvent;
import com.aspectgaming.common.event.gamble.InGambleEvent;
import com.aspectgaming.common.event.gamble.OutGambleEndEvent;
import com.aspectgaming.common.event.game.*;
import com.aspectgaming.common.event.machine.*;
import com.aspectgaming.common.event.progressive.ProgressiveSkipEndEvent;
import com.aspectgaming.common.event.screen.HelpHideEvent;
import com.aspectgaming.common.event.screen.HelpShowEvent;
import com.aspectgaming.common.event.screen.ShowDiagnosticUIEvent;
import com.aspectgaming.common.loader.CoordinateLoader;
import com.aspectgaming.common.loader.ImageLoader;
import com.aspectgaming.common.loader.SoundLoader;
import com.aspectgaming.gdx.component.drawable.DrawableComponent;
import com.aspectgaming.gdx.component.drawable.messagebar.MessageBarComponent;
import com.aspectgaming.gdx.component.drawable.meter.MetersComponent;
import com.aspectgaming.gdx.component.drawable.progressivereel.ProgressiveReelComponent;
import com.aspectgaming.gdx.component.drawable.reel.ReelComponent;
import com.aspectgaming.gdx.component.drawable.reel.Symbol;
import com.aspectgaming.gdx.component.drawable.subsymbol.SubSymbolComponent;
import com.aspectgaming.net.game.GameClient;
import com.aspectgaming.net.game.data.MathParam;
import com.aspectgaming.net.game.data.SettingData;
import com.aspectgaming.util.CommonUtil;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;

import java.io.*;
import java.util.*;

import static com.aspectgaming.common.data.GameConst.getP2Position;
import static com.aspectgaming.common.data.GameConst.getSelectionPositions;
import static com.aspectgaming.gdx.component.drawable.reel.Symbol.D7;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

public class WinShowComponent extends DrawableComponent {
    public final int SYMBOLWIN = 1;
    public final int SCATTERWIN = 2;
    public final int COMMANDJACKPOTWIN = 3;
    public final int JACKPOTWIN = 4;
    public final int P2Win = 5;

    private final float duration;
    private final int cols;
    private boolean isWaysGame;
    private boolean isWildMultiplier;
    private boolean isP2Win;
    private boolean isJACKTOPWIN;

    private int currentWinshowCount;
    private int loopCount;
    private boolean isPlaying;
    private boolean isInPrewinShow;
    private ReelComponent reels;
    private PaylinesComponent paylines;
    private MetersComponent metersComp;
    private SubSymbolComponent subSymbolCmp;
    private long freeProgressiveWin = 0;

    private final List<WinShowGroup> groups = new ArrayList<>();
    private final int numReels;
    private final int numRows;

    private long beforFreeGameJackPotWin;

    private final Sound sndFreeGameBell;
//    private final Sound sndWildSymbol;

    private final List<Actor> animationsExceptWild = new ArrayList<>();
    private final Map<Symbol, ShapeAnimation> wildAnimations = new HashMap<>();
    private final Map<Symbol, ShapeAnimation> scatterAnimations = new HashMap<>();

    private SymbolAnimationAssets symbolAssets;

    private ShapeAnimation[][] winShowAnimation;
    private ShapeAnimation[][] animation=new ShapeAnimation[3][3];

    public WinShowComponent() {
        duration = GameConfiguration.getInstance().winShow.duration;
        cols = GameConfiguration.getInstance().reel.reels.length;
        isWaysGame = GameData.getInstance().isWaysGame();
        isWildMultiplier = GameConfiguration.getInstance().winShow.wildMultiplier;
        numReels = GameConfiguration.getInstance().reel.reels.length;
        numRows = GameData.getInstance().Context.Result.Stops.length / numReels;
        sndFreeGameBell = SoundLoader.getInstance().get("freegame/Bell");
        symbolAssets = new SymbolAnimationAssets();
        initWinShowAnimation();

        registerEvent(new GameResetEvent() {
            @Override
            public void execute(Object... obj) {
                if (GameData.getInstance().Context.FreeGameMode) {
                    boolean isFreeGameP2=false;
                    for (MathParam mathParamaram : GameData.getInstance().Context.MathParams) {
                        if (mathParamaram.Key.equals("P2Win")) {
                            isFreeGameP2= mathParamaram.Value.equals("true");
                            break;
                        }
                    }
                    for (MathParam param : GameData.getInstance().Context.MathParams) {
                        if (param.Key.equals("ProgressiveWin")) {
                            long win = Long.parseLong(param.Value);
                            freeProgressiveWin = GameData.getInstance().Context.ProgressiveTotalWin - win;
                            if (isFreeGameP2){
                                freeProgressiveWin += GameData.getInstance().Context.Denomination*800;
                            }
                            break;
                        }
                    }
                }
                loopCount = Integer.MAX_VALUE;
                reels = ((ReelComponent) Content.getInstance().getComponent(Content.REELCOMPONENT));
                paylines = ((PaylinesComponent) Content.getInstance().getComponent(Content.PAYLINESCOMPONENT));
                metersComp = (MetersComponent) Content.getInstance().getComponent(Content.METERSCOMPONENT);
                subSymbolCmp = ((SubSymbolComponent) Content.getInstance().getComponent(Content.SUBSYMBOLCOMPONENT));
                isInPrewinShow = false;

                stopWinShow();

                resumeWinShow();
            }
        });

        registerEvent(new InTiltEvent() {
            @Override
            public void execute(Object... obj) {
                if (GameData.getInstance().Context.State == State.Handpay && !isPlaying) {
                    creatWinShowGroup();
                    startWinShow();
                }

                pause();
            }
        });

        registerEvent(new OutTiltEvent() {
            @Override
            public void execute(Object... obj) {
                if (winShowAnimation!=null) {
                    for (int i=0;i<winShowAnimation.length;i++) {
                        if (winShowAnimation[i]!=null) {
                            for (int j = 0; j < winShowAnimation[i].length; j++) {
                                winShowAnimation[i][j].resume();
                            }
                        }
                    }
                }
                resume();
            }
        });

        registerEvent(new WinMeterStartRollingEvent() {
            @Override
            public void execute(Object... obj) {
                    creatWinShowGroup2();
                    startWinShow();
            }
        });

        registerEvent(new ProgressiveSkipEndEvent() {
            @Override
            public void execute(Object... obj) {
                if (!GameData.getInstance().Context.FreeGameMode && GameData.getInstance().Context.Result.ScatterWin > 0) {
                    creatWinShowGroup();
                    startWinShow();
                }
            }
        });

        registerEvent(new InBonusEvent() {
            @Override
            public void execute(Object... obj) {
                stopWinShow();
            }
        });

        registerEvent(new OutFreeGameIntroEvent() {
            @Override
            public void execute(Object... obj) {
                stopWinShow();
                freeProgressiveWin = 0;
            }
        });

        registerEvent(new HelpShowEvent() {
            @Override
            public void execute(Object... obj) {
                stopWinShow();
                loopCount = Integer.MAX_VALUE;
            }
        });

        registerEvent(new HelpHideEvent() {
            @Override
            public void execute(Object... obj) {
                resumeWinShow();
            }
        });

        registerEvent(new InFreeGameIntroEvent() {
            @Override
            public void execute(Object... obj) {
                beforFreeGameJackPotWin = GameData.getInstance().Context.ProgressiveTotalWin / GameData.getInstance().Context.Denomination
                        + GameData.getInstance().Context.BonusTotalWin;
                //GameClient.getInstance().selectBeforFreeGameJackPotWin(beforFreeGameJackPotWin);

                if (!isPlaying) {
                    clearActions();
                    creatWinShowGroup();
                    startWinShow();
                }
            }
        });

        registerEvent(new InFreeGameOutroEvent() {
            @Override
            public void execute(Object... obj) {
                GameClient.getInstance().selectReturnFreeGame(true);
                long freeGameWin = GameData.getInstance().Context.FreeGameTotalWin +
                        GameData.getInstance().Context.ProgressiveTotalWin / GameData.getInstance().Context.Denomination +
                        GameData.getInstance().Context.BonusTotalWin - beforFreeGameJackPotWin;
                GameClient.getInstance().selectFreeGameWin(freeGameWin);
                stopWinShow();
            }
        });

        registerEvent(new ReelStartSpinEvent() {
            @Override
            public void execute(Object... obj) {
                if (!GameData.getInstance().Context.FreeGameMode) {
                    GameClient.getInstance().selectReturnFreeGame(false);
                    GameClient.getInstance().selectFreeGameWin(0);
                } else {
                    freeProgressiveWin = GameData.getInstance().Context.ProgressiveTotalWin;
                }

                loopCount = 0;
                stopWinShow();
                groups.clear();
            }
        });

        registerEvent(new OutFreeGameOutroEvent() {
            @Override
            public void execute(Object... obj) {
                creatWinShowGroup();
                startWinShow();
            }
        });

        registerEvent(new ProgressiveSingleReelResultsStartEvent() {
            @Override
            public void execute(Object... obj) {
                boolean isDim = (boolean) obj[0];
                boolean isSkip = (boolean) obj[1];
                int line = (int) obj[2];
                int level = (int) obj[3];
                showProSingleReelResultAnim(isDim, isSkip, line, level);
            }
        });

        registerEvent(new StartPreWinShowEvent() {
            @Override
            public void execute(Object... obj) {
                preWinShow();
            }
        });

        registerEvent(new InGambleEvent() {
            @Override
            public void execute(Object... obj) {
                stopWinShow();
            }
        });

        registerEvent(new OutGambleEndEvent() {
            @Override
            public void execute(Object... obj) {
                resumeWinShow();
            }
        });

        registerEvent(new GambleDisplayPendingEvent() {
            @Override
            public void execute(Object... obj) {
                stopWinShow();
            }
        });

        registerEvent(new ChangeTestReelEvent() {
            @Override
            public void execute(Object... obj) {
                if (GameData.getInstance().Context.GameState == State.GameIdle) {
                    stopWinShow();
                }
            }
        });

        registerEvent(new ChangeTestReelStopEvent() {
            @Override
            public void execute(Object... obj) {
                stopWinShow();
            }
        });

        registerEvent(new ShowDiagnosticUIEvent() {
            @Override
            public void execute(Object... obj) {
                stopWinShow();
            }
        });

        registerEvent(new ReelStoppedEvent() {
            @Override
            public void execute(Object... obj) {
                if (GameData.getInstance().Context.Result.ScatterWin > 0 && GameData.currentGameMode == GameMode.FreeGame) {
                    showScatterSymbolWin();
                }
            }
        });

        registerEvent(new ChangeBetEvent() {
            @Override
            public void execute(Object... obj) {
                if (!isPlaying) {
                    clearActions();
                    creatWinShowGroup();
                    startWinShow();
                }
            }
        });
    }

    private void resumeWinShow() {
        // must use game state, otherwise game will not play winshow if power on with tilts.
        int state = GameData.getInstance().Context.GameState;

        // still need to play win show if power cycle after take win in gamble
        if (state == State.GameIdle || state == State.GambleChoice) {
            creatWinShowGroup();
            startWinShow();
        }
    }

    private void creatWinShowGroup2() {
        groups.clear();
        createWinScatterGroup();
        if (!isWaysGame) {
            createWinLineGroups();
        } else {
            createWinWayGroups();
        }
    }

    private void creatWinShowGroup() {
        stopWinShow();
        groups.clear();

        createWinScatterGroup();
        if (!isWaysGame) {
            createWinLineGroups();
        } else {
            createWinWayGroups();
        }
    }

    private void preWinShow() {
        isInPrewinShow = true;

        float multiple = ((float) metersComp.getCurrentGameWin()) / GameData.getInstance().Context.Selections;

        boolean delayRollUp = false;
        long mask = GameData.getInstance().Context.Result.ScatterMask;
        int scatterWin = GameData.getInstance().Context.Result.ScatterWin;

        float delayWinner = 0.0f;

        if (mask != 0 && scatterWin > 0) {
            playWildSymbolWin();
            showScatterSymbolWin();
            delayRollUp = true;
            addAction(delay(delayWinner, run(() -> {
                sndFreeGameBell.play();
            })));

            addAction(delay(delayWinner + sndFreeGameBell.duration(), run(() -> {
                preWinShowFinished();
            })));
        } else {
            if (!GameData.getInstance().Context.FreeGameMode) {
                if (wildAnimations.size() >= 5 && multiple > 10.0f) {
                    delayRollUp = true;
                    addAction(delay(delayWinner, run(() -> {
                        isInPrewinShow = false;
//                        sndWildSymbol.play();
                        playWildSymbolWin();
                    })));
//                    addAction(delay(delayWinner + sndWildSymbol.duration(), run(()->{ preWinShowFinished();})));
                    addAction(delay(delayWinner, run(() -> {
                        preWinShowFinished();
                    })));
                }
            } else {
                if (wildAnimations.size() > 5 && multiple >= 15.0f) {
                    EventMachine.getInstance().offerEvent(ModifyFreeBGMVolEvent.class, 0.01f);
                    delayRollUp = true;
                    addAction(delay(delayWinner, run(() -> {
                        isInPrewinShow = false;
//                        sndWildSymbol.play();
                        playWildSymbolWin();
                    })));
//                    addAction(delay(delayWinner + sndWildSymbol.duration(), run(() -> {
//                        preWinShowFinished();
//                    })));
                    addAction(delay(delayWinner, run(() -> {
                        preWinShowFinished();
                    })));
                }
            }
        }

        if (!delayRollUp) {
            EventMachine.getInstance().offerEvent(PreWinShowStopEvent.class);
            isInPrewinShow = false;
            playWildSymbolWin();
        }
    }

    private void preWinShowFinished() {
        clearAnimationsExceptWild();
        EventMachine.getInstance().offerEvent(PreWinShowStopEvent.class);
        isInPrewinShow = false;
        playWildSymbolWin();
    }

    private void startWinShow() {
        if (!isPlaying) {
            currentWinshowCount = 0;
            isPlaying = true;
            loopCount = 0;
            setStopAnimMask();

            playWinShow();

            // isOverLoop() should return true immediately if win is not caused by paylines
            if (groups.isEmpty()) {
                loopCount = Integer.MAX_VALUE;
            }
        } else {
            loopCount = Integer.MAX_VALUE;
        }
    }

    private void stopWinShow() {
        clearAnimationsExceptWild();
        hideWildSymbolWin();
        hideScatterSymbolWin();
        paylines.hidePaylines();
        clearActions();

        if (isPlaying) {
            paylines.hidePaylines();
            reels.stopWinShow();
            isPlaying = false;
            clear();
        }
    }

    private void addWildSymbolWin() {
    }

    private void playWildSymbolWin() {
        if (wildAnimations.size() <= 0) return;

        for (Symbol key : wildAnimations.keySet()) {
            if (isInPrewinShow) {
                int stopIndex = key.stopIndex;
                if (wildAndScatterSymbolLandSamePos(stopIndex)) {
                    wildAnimations.get(key).stop();
                }
            } else {
                wildAnimations.get(key).play(true);
            }
        }
    }

    private void hideWildSymbolWin() {
        for (Symbol key : wildAnimations.keySet()) {
            removeActor(wildAnimations.get(key));
            wildAnimations.get(key).stop();
        }
        wildAnimations.clear();
    }

    private void showScatterSymbolWin() {
        int[] stops = GameData.getInstance().Context.Result.Stops;
        long mask = GameData.getInstance().Context.Result.ScatterMask;
        int scatterWin = GameData.getInstance().Context.Result.ScatterWin;
        if (mask != 0 && scatterWin > 0) {
            for (int stopIndex = 0; stopIndex < stops.length; stopIndex++) {
                Symbol symbol = reels.getSymbol(stopIndex);
                if ((mask & (1 << stopIndex)) != 0) {
                    if (scatterAnimations.get(symbol) == null) {
                        addScatterSymbolAnimation(stopIndex, symbol);
                    } else if (!scatterAnimations.get(symbol).isPlaying()) {
                        symbol.setVisible(false);
                        scatterAnimations.get(symbol).play(true);
                        scatterAnimations.get(symbol).setEndListener(() -> {
                                    symbol.setVisible(true);
                                }
                        );
                    }
                }
            }
        }
    }

    private void hideScatterSymbolWin() {
        for (Symbol key : scatterAnimations.keySet()) {
            scatterAnimations.get(key).stop();
            removeActor(scatterAnimations.get(key));
        }
        scatterAnimations.clear();
    }

    private boolean wildAndScatterSymbolLandSamePos(int stopIndex) {
        return false;
    }

    public void setScAndWildAnimPlay(int stopIndex, boolean isPlay) {
        for (Symbol key : scatterAnimations.keySet()) {
            if (key.stopIndex == stopIndex) {
                if (isPlay) {
                    scatterAnimations.get(key).play(true);
                } else {
                    scatterAnimations.get(key).stop();
                }
            }
        }

        for (Symbol key : wildAnimations.keySet()) {
            if (key.stopIndex == stopIndex) {
                if (isPlay) {
                    wildAnimations.get(key).play(true);
                } else {
                    wildAnimations.get(key).stop();
                }
            }
        }
    }

    public void setLoopCount(int val) {
        loopCount = val;
    }

    public boolean isOverLoop() {
        return (loopCount > 0);
    }

    private void clearAnimationsExceptWild() {
        for (Actor actor : animationsExceptWild) {
            removeActor(actor);
        }
        animationsExceptWild.clear();
    }

    private void playWinShow() {
//        clearActions();
//        clear();
        if (!isPlaying) {
            return;
        }

        clearAnimationsExceptWild();

        addWildSymbolWin();

        showScatterSymbolWin();

        if (groups.isEmpty()) {
            stopWinShow();
            return;
        }

        isP2Win = false;
        isJACKTOPWIN = false;

        for (MathParam param : GameData.getInstance().Context.MathParams) {
            if (param.Key.equals("P2Win")) {
                if (param.Value.equals("true")) {
                    isP2Win = true;
                    break;
                }
            }
            if (param.Key.equals("JACKPOTWIN")) {
                if (param.Value.equals("true")) {
                    isJACKTOPWIN = true;
                    break;
                }
            }

        }
        int[] stops = GameData.getInstance().Context.Result.Stops;
        WinShowGroup group = groups.get(currentWinshowCount);

        paylines.showPaylines(group.line, group.winMask);

        reels.stopWinShow();

        for (Symbol key : scatterAnimations.keySet()) {
            key.setVisible(false);
        }

        for (Symbol key : wildAnimations.keySet()) {
            key.setVisible(false);
        }

        if (group.stoppinAnimMask != 0) {
            playStopAnim(group.stoppinAnimMask);
            // only show once
            group.stoppinAnimMask = 0;
        } else {
            long[] jackpotlineInfo = getJackpotLineInfo(group.line);
            {
                if (group.symbolID != Symbol.BN) {
                    for (int stopIndex = 0; stopIndex < stops.length; stopIndex++) {
                        Symbol symbol = reels.getSymbol(stopIndex);
                        if ((group.winMask & (1 << stopIndex)) != 0) {
                            if (group.symbolID != Symbol.BN) {
                                showSymbolAnimation(symbol, symbol.symbolIndex, stopIndex / cols, stopIndex % cols, group.line, group.multiplier);
                            }
                        }
                    }
                }

                if (group.symbolID == Symbol.BN) {
                    showTickerMessage(group, jackpotlineInfo, SCATTERWIN);
                    //subSymbolCmp.playOneProgressiveAnimation(true, group.line);
                } else if (isJACKTOPWIN) {

                    showTickerMessage(group, jackpotlineInfo, JACKPOTWIN);

                    //subSymbolCmp.playOneProgressiveAnimation(true, group.line);
                } else if (isP2Win) {

                    showTickerMessage(group, jackpotlineInfo, P2Win);
                } else {
                    showTickerMessage(group, jackpotlineInfo, SYMBOLWIN);
                    //subSymbolCmp.playOneProgressiveAnimation(false, group.line);
                }

                if (++currentWinshowCount >= groups.size()) {
                    currentWinshowCount = 0;
                    loopCount++;
                }
            }

            if (loopCount == 0) {
                SymbolConfiguration sc = GameConfiguration.getInstance().winShow.getSymbolConfiguration(group.symbolID);
                SoundLoader.getInstance().play(sc.sound);
            }

            addAction(delay(duration, run(() -> paylines.hidePaylines())));
            addAction(delay(duration + 0.5f, run(this::playWinShow)));
        }
    }

    private void showTickerMessage(WinShowGroup group, long[] jackpotInfo, int showType) {
        MessageBarComponent message = (MessageBarComponent) Content.getInstance().getComponent(Content.MESSAGEBARCOMPONENT);
        if (message == null) return;

        String extra = null;
        if (group.numWays > 1) {
            extra = "(x" + group.numWays + ")=" + (group.win * group.numWays);
        }

        long level = -1;
        long win = 0;

        switch (showType) {
            case SYMBOLWIN:
                message.setMessageArea1("SymbolWin", group.line, group.win / group.multiplier, group.multiplier, extra);
                break;
            case SCATTERWIN:
                boolean isRetrunFreeGame = false;
                long freeGameWin = 0;
                for (MathParam param : GameData.getInstance().Context.MathParams) {
                    if (param.Key.equals("ISRETURNFREEGAME")) {
                        if (param.Value.equals("true")) {
                            isRetrunFreeGame = true;

                            for (MathParam param1 : GameData.getInstance().Context.MathParams) {
                                if (param1.Key.equals("FREEGAMEWIN")) {
                                    freeGameWin = Long.parseLong(param1.Value);
                                    break;
                                }
                            }
                        }
                        break;
                    }
                }

                if (isRetrunFreeGame) {
                    win = freeGameWin;
                    message.setMessageArea1("BonusWin", win, 1, extra);
                } else {
                    message.setMessageArea1("ScatterWin", extra);
                }

                break;
            case COMMANDJACKPOTWIN:
                level = jackpotInfo[0];
                win = jackpotInfo[1];
                message.setMessageArea1("CommAndJackpotWin", group.line, group.win / group.multiplier, group.multiplier, extra, level, win);
                break;
            case JACKPOTWIN:
                message.setMessageArea1("JackpotWin", group.win, group.line, group.win / group.multiplier, group.multiplier, extra);
                break;
            case P2Win:
                message.setMessageArea1("P2Win", group.win, group.line, group.win / group.multiplier, group.multiplier, extra);
                break;
            default:
                message.setMessageArea1("blank");
                return;
        }
    }

    private long[] getJackpotLineInfo(int line) {
        boolean isRetrunFreeGame = false;
        for (MathParam param : GameData.getInstance().Context.MathParams) {
            if (param.Key.equals("ISRETURNFREEGAME")) {
                if (param.Value.equals("true")) {
                    isRetrunFreeGame = true;
                }
                break;
            }
        }

        if (isRetrunFreeGame) {
            return null;//progressiveReelCom.getJackpotLineWin(line);
        } else {
            long level = -1;
            long win = 0;
            long[] jackpotInfo = new long[2];

            boolean hasJackpot = false;
            for (MathParam param : GameData.getInstance().Context.MathParams) {
                if (param.Key.equals("JACKPOTLINEWIN" + line)) {
                    jackpotInfo = CommonUtil.stringToLongArray(param.Value);
                    hasJackpot = true;
                    break;
                }
            }

            if (hasJackpot) {
                if (jackpotInfo != null && jackpotInfo.length == 2) {
                    level = jackpotInfo[0];
                    win = jackpotInfo[1];

                    if (level >= 0 && win > 0) {
                        return jackpotInfo;
                    }
                }
            }
            return null;
        }
    }

    private void showProSingleReelResultAnim(boolean isDim, boolean isSkip, int line, int level) {
        if (!isDim) {
            paylines.hidePaylines();
            clearActions();
            setProSymsbolDim(false);
            return;
        }

        if (isSkip) {
            clearActions();
            setProSymsbolDim(true);
            return;
        }

        SettingData cfg = GameData.getInstance().Setting;
        int[][] lintPositions = getSelectionPositions(cfg.MaxSelections, "");

        if (line >= 0 && line < cfg.MaxSelections) {
            setProSymsbolDim(true);
            paylines.hidePaylines();
            for (int i = 0; i < numReels; i++) {
                Symbol symbol = reels.getSymbol(lintPositions[line][i]);
                symbol.setColor(Color.WHITE);
                symbol.addAction(sequence(delay(0.5f, hide()), delay(0.5f, show())));
            }

            paylines.showPaylines(line, 0);
        }
    }


    private void setProSymsbolDim(boolean isDim) {
        int[] stops = GameData.getInstance().Context.Result.Stops;

        for (int i = 0; i < stops.length; i++) {
            Symbol symbol = reels.getSymbol(i);
            symbol.setVisible(true);
            if (isDim) {
                symbol.setColor(Color.DARK_GRAY);
            } else {
                symbol.setColor(Color.WHITE);
            }
        }
    }

    private void addWildSymbolAnimation(int stopIndex, Symbol symbol) {

    }

    private void addScatterSymbolAnimation(int stopIndex, Symbol symbol) {

    }

    private void showSymbolAnimation(Symbol symbol, int id, int row, int reelId, int line, long multiplier) {
        SymbolConfiguration configuration = GameConfiguration.getInstance().winShow.getSymbolConfiguration(id);

        float time = duration / 4;

        switch (configuration.type) {
            case "Flash":
                symbol.setVisible(true);
                symbol.addAction(sequence(delay(0.0f, hide()), delay(0.35f, show()), delay(0.8f, hide()), delay(0.35f, show())));
                //symbol.addAction(repeat(2, flash(time)));co
                break;

            case "Alpha":
                symbol.setVisible(true);
                symbol.addAction(repeat(2, sequence(alpha(configuration.typeValue, time, Interpolation.pow2In), alpha(1f, time, Interpolation.pow2Out))));
                break;

            case "Animation":
                symbol.setVisible(true);

                float posX = symbol.getScreenX();
                float posY = symbol.getScreenY();

                Vector2 offset = CoordinateLoader.getInstance().getOffset("WinShow" + id);
                if (offset != null) {
                    posX += offset.x;
                    posY += offset.y;
                }

//                float animDuration = configuration.duration != null ? configuration.duration : duration;
                //if (animDuration > duration) {
                //    animDuration = duration;
                //}

                if (multiplier > 1 && configuration.multi >= 0) {
//                    multiplier = configuration.multi > 0 ? configuration.multi : multiplier;
                    //   animation = new Animation("WinShow/" + id + "/" + multiplier + "x/", animDuration);
                    animation[row-1][reelId]=winShowAnimation[id][(reelId*GameConst.Col+(row-1))];
//                    animation[row-1][reelId] = new ShapeAnimation(symbolAssets, id, "animation", "");
                } else {
                    //animation = new Animation("WinShow/" + id + "/", animDuration);
//                    animation[row-1][reelId]= (ShapeAnimation) winShowAnimation[id].clone();
//                    animation[row-1][reelId] = new ShapeAnimation(symbolAssets, id, "animation", "");
                    animation[row-1][reelId]=winShowAnimation[id][(reelId*GameConst.Col+(row-1))];
                }

                //int loops = (int) (duration / animDuration + 0.5f);

                animation[row-1][reelId].setPosition(posX, posY);
                symbol.setVisible(false);
                animation[row-1][reelId].setEndListener(() -> {
                    symbol.setVisible(true);
                });
                animation[row-1][reelId].play(false);
                addActor(animation[row-1][reelId] );
                animationsExceptWild.add(animation[row-1][reelId]);
                break;

            case "Cutover":
                symbol.setVisible(false);

                Image winImage = ImageLoader.getInstance().load("WinShow/Symbol/" + id);
                winImage.setPosition(symbol.getScreenX(), symbol.getScreenY());
                addActor(winImage);
                animationsExceptWild.add(winImage);
                winImage.setVisible(false);

                winImage.addAction(sequence(delay(time, show()), delay(time, hide())));
                symbol.addAction(delay(time * 3, show()));
                break;

            default:
                break;
        }
    }

    private void playStopAnim(int mask) {
        int[] stops = GameData.getInstance().Context.Result.Stops;

        for (int stopIndex = 0; stopIndex < stops.length; stopIndex++) {
            if ((mask & (1 << stopIndex)) != 0) {
                Symbol symbol = reels.getSymbol(stopIndex);
                symbol.setWinShowMode(true);
                symbol.setVisible(false);

                int symbolID = stops[stopIndex];
                Vector2 offset = CoordinateLoader.getInstance().getOffset("WinShow" + symbolID);
                Animation animation = new Animation("WinShow/" + symbolID + "/Stop/", duration);
                animation.setPosition(symbol.getScreenX() + offset.x, symbol.getScreenY() + offset.y);
                animation.play();
                addActor(animation);
                animationsExceptWild.add(animation);
            }
        }

        addAction(delay(duration, run(this::playWinShow)));
    }

    private void createWinWayGroups() {

    }

    private void createWinLineGroups() {
        int[] stops = GameData.getInstance().Context.Result.Stops;
        //Update for ints to longs
        //int[] masks = GameData.getInstance().Context.Result.PaylineMask;
        long[] masks = GameData.getInstance().Context.Result.PaylineMask;
        int[] win = GameData.getInstance().Context.Result.PaylineWin;
        int[] multipliers = GameData.getInstance().Context.Result.PaylineMultiplier;
        boolean isp = true;

        int colsShow = cols;
        SettingData cfg = GameData.getInstance().Setting;
        int[][] positions = getSelectionPositions(cfg.MaxSelections, cfg.SelectedGame);

        for (MathParam param : GameData.getInstance().Context.MathParams) {
            if (param.Key.equals("P2Win")) {
                if (param.Value.equals("true")) {
                    positions = getP2Position();
                    colsShow = cols * 3;
                    masks[0] = 511;
                    WinShowGroup group = new WinShowGroup();
                    if (GameData.getInstance().Context.FreeGameMode) {
                        group.win = GameData.getInstance().Context.ProgressiveTotalWin+GameData.getInstance().Context.Denomination*800 - freeProgressiveWin;
                        GameClient.getInstance().selectProgressiveWin(String.valueOf(group.win));
                    } else {
                        group.win = GameData.getInstance().Context.ProgressiveTotalWin+GameData.getInstance().Context.Denomination*800;
                    }
                    group.line = -1;
                    group.numWays = 1;
                    group.symbols = null;
                    group.multiplier = 1;
                    group.winMask = 1 << 3 | 1 << 4 | 1 << 5 | 1 << 6 | 1 << 7 | 1 << 8 | 1 << 9 | 1 << 10 | 1 << 11;
                    if (group.win!=0||GameData.getInstance().Context.TotalWin>0) {
                        groups.add(group);
                    }
                    isp = false;
                    break;
                }
            }
            if (param.Key.equals("JACKPOTWIN")) {
                if (param.Value.equals("true")) {
                    WinShowGroup group = new WinShowGroup();
                    if (GameData.getInstance().Context.FreeGameMode) {
                        group.win = GameData.getInstance().Context.ProgressiveTotalWin - freeProgressiveWin;
                        GameClient.getInstance().selectProgressiveWin(String.valueOf(group.win));
                    } else {
                        group.win = GameData.getInstance().Context.ProgressiveTotalWin;
                    }
                    group.line = 0;
                    group.numWays = 1;
                    group.symbols = null;
                    group.multiplier = 1;
                    group.winMask = 1 << 6 | 1 << 7 | 1 << 8;
                    if (group.win!=0||GameData.getInstance().Context.TotalWin>0) {
                        groups.add(group);
                    }
                    isp = false;
                    break;
                }
            }

        }

        if (isp) {
            for (int line = 0; line < masks.length; line++) {
                if (win[line] > 0 || getJackpotLineInfo(line) != null) {
                    //Update for ints to longs
                    //int mask = masks[line];
                    int mask = Math.toIntExact(masks[line]);

                    if (mask != 0) {
                        WinShowGroup group = new WinShowGroup();
                        List<Integer> symbols = new ArrayList<>();

                        boolean bSymbolID = false;
                        for (int col = 0; col < colsShow; col++) {
                            if ((mask & (1 << col)) != 0) {
                                int stopIndex = positions[line][col];
                                int symbolID = stops[stopIndex];
                                //if (symbolID != Symbol.WILD && symbolID != Symbol.WIx) {
                                if (!bSymbolID) {
                                    group.symbolID = symbolID;
                                }

//                            if (group.symbolID != Symbol.WILD) {
//                                bSymbolID = true;
//                            }
                                //计算要播放动画的位置
                                group.winMask |= 1 << stopIndex;

                        /*
                        if (symbolID > Symbol.SCATTER) {
                            group.numSymbols += 2;
                        } else {
                            group.numSymbols += 1;
                        }
                        */
                                group.numSymbols += 1;
                                symbols.add(symbolID);
                            }
                        }

                        group.multiplier = multipliers[line];

//                    if (isWildMultiplier) {
//                        // show multiplier animation
//                        if (group.multiplier > 1) {
//                            for (int stopIndex = 0; stopIndex < stops.length; stopIndex++) {
//                                if (stops[stopIndex] == Symbol.WILD) {
//                                    group.winMask |= 1 << stopIndex;
//                                }
//                            }
//                        }
//                    }

                        group.line = line;
                        group.win = win[line];
                        group.numWays = 1;
                        group.symbols = symbols.toArray(new Integer[symbols.size()]);
                        groups.add(group);
                    }
                }
            }
        }
        Collections.sort(groups, new Comparator<WinShowGroup>() {
            @Override
            public int compare(WinShowGroup group1, WinShowGroup group2) {
                long[] jackpot1 = getJackpotLineInfo(group1.line);
                long[] jackpot2 = getJackpotLineInfo(group2.line);

                if (jackpot1 != null && jackpot2 == null) {
                    return -1;
                } else if (jackpot1 != null && jackpot2 != null) {
                    if (jackpot1[1] == jackpot2[1]) {
                        return group1.line - group2.line;
                    } else {
                        return jackpot2[1] - jackpot1[1] > 0 ? 1 : -1;
                    }
                } else if (jackpot1 == null && jackpot2 == null) {
                    if (group1.win == group2.win) {
                        return group1.line - group2.line;
                    } else {
                        return (int) (group2.win - group1.win);
                    }
                } else {
                    return 1;
                }
            }
        });
    }

    private void createWinScatterGroup() {
        int[] stops = GameData.getInstance().Context.Result.Stops;
        long mask = GameData.getInstance().Context.Result.ScatterMask;
        long win = GameData.getInstance().Context.Result.ScatterWin;
        long multiplier = GameData.getInstance().Context.Result.ScatterMultiplier;

        if (mask != 0) {
            WinShowGroup group = new WinShowGroup();

            for (int stopIndex = 0; stopIndex < stops.length; stopIndex++) {
                if ((mask & (1L << stopIndex)) != 0) {
                    //group.symbolID = stops[stopIndex];
                    group.symbolID = Symbol.BN;
                    group.winMask |= 1 << stopIndex;
                    group.numSymbols++;
                }
            }

            group.multiplier = multiplier;

//            if (isWildMultiplier) {
//                // show multiplier animation
//                if (win > 0 && group.multiplier > 1) {
//                    for (int stopIndex = 0; stopIndex < stops.length; stopIndex++) {
//                        if (stops[stopIndex] == Symbol.WILD) {
//                            group.winMask |= 1 << stopIndex;
//                        }
//                    }
//                }
//            }

            group.line = -1;
            group.win = win;
            group.numWays = 1;
            group.symbols = new Integer[group.numSymbols];
            group.isScWin = true;
            for (int i = 0; i < group.numSymbols; i++) {
                group.symbols[i] = group.symbolID;
            }

            if (group.numSymbols == 3) {
                groups.add(group);
            }
        }
    }

    private void setStopAnimMask() {
        int[] stops = GameData.getInstance().Context.Result.Stops;
        int globalMask = 0;

        for (WinShowGroup group : groups) {
            int lineMask = getStopAnimMask(stops, group.winMask);
            // show stopping animation if one or more symbols in current win line
            // have not played stopping animation before.
            if (globalMask != (globalMask | lineMask)) {
                group.stoppinAnimMask = lineMask;
                globalMask |= lineMask;
            }
        }
    }

    private int getStopAnimMask(int[] stops, long mask) {
        int lineMask = 0;
        for (int i = 0; i < stops.length; i++) {
            int flag = 1 << i;
            if ((mask & flag) != 0) {
                int symbolIndex = stops[i];
                if (GameConfiguration.getInstance().winShow.getSymbolConfiguration(symbolIndex).stopAnim) {
                    lineMask |= flag;
                }
            }
        }
        return lineMask;
    }


    public void initWinShowAnimation() {
        int symbolSize = GameConfiguration.getInstance().winShow.symbol.length;
        winShowAnimation=new ShapeAnimation[symbolSize][9];
        for (int i = 0; i < symbolSize; i++) {
            SymbolConfiguration configuration = GameConfiguration.getInstance().winShow.getSymbolConfiguration(i);
            switch (configuration.type) {
                case "Animation":
                    for (int j = 0; j< GameConst.Col*GameConst.Row; j++) {
                        winShowAnimation[i][j] = new ShapeAnimation(symbolAssets, i, "animation", "");
                    }
                    break;

                default:
                    winShowAnimation[i]=null;
                    break;
            }
        }
    }

    private static class WinShowGroup implements Comparable<WinShowGroup> {
        int stoppinAnimMask;
        int symbolID;
        int line;
        int winMask;
        int numSymbols;
        int numWays;
        long win;
        long multiplier;
        Integer[] symbols;
        boolean isScWin;

        @Override
        public int compareTo(WinShowGroup o) {
            int delta = symbolID - o.symbolID;

            // Symbol.SCATTER should be the first
            if (delta != 0) {
                if (symbolID == Symbol.BL) {
                    return -1;
                } else if (o.symbolID == Symbol.BL) {
                    return 1;
                }
            }
            return delta;
        }
    }
    }
