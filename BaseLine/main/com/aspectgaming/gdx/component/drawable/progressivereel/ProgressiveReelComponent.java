package com.aspectgaming.gdx.component.drawable.progressivereel;

import com.aspectgaming.common.actor.Sound;
import com.aspectgaming.common.configuration.GameConfiguration;
import com.aspectgaming.common.data.Content;
import com.aspectgaming.common.data.GameData;
import com.aspectgaming.common.data.GameMode;
import com.aspectgaming.common.data.State;
import com.aspectgaming.common.event.EventMachine;
import com.aspectgaming.common.event.freegame.ModifyFreeBGMVolEvent;
import com.aspectgaming.common.event.game.*;
import com.aspectgaming.common.event.machine.InTiltEvent;
import com.aspectgaming.common.event.machine.OutTiltEvent;
import com.aspectgaming.common.event.progressive.*;
import com.aspectgaming.common.event.screen.HelpHideEvent;
import com.aspectgaming.common.loader.SoundLoader;
import com.aspectgaming.gdx.component.drawable.DrawableComponent;
import com.aspectgaming.gdx.component.drawable.randomwild.RandomWildComponent;
import com.aspectgaming.gdx.component.drawable.reel.ReelComponent;
import com.aspectgaming.gdx.component.drawable.subsymbol.SubSymbolComponent;
import com.aspectgaming.gdx.component.drawable.winshow.WinShowComponent;
import com.aspectgaming.net.game.GameClient;
import com.aspectgaming.net.game.data.MathParam;
import com.aspectgaming.net.game.data.SettingData;
import com.aspectgaming.util.CommonUtil;
import com.badlogic.gdx.scenes.scene2d.Touchable;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.delay;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.run;


public class ProgressiveReelComponent extends DrawableComponent {
    final static int SPIN_READY = 0;
    final static int SPIN_START = 1;
    final static int SPIN_ING = 2;
    final static int SPIN_ENDED = 3;
    private static final int LEVELS = 4;

    private String[] jackpotLineWin;

    private int spinState;
    private int[] jackPotLevelInfo = new int[2];

    private boolean firstComeResult;

    private int jackpotPos;
    private int jackpotLineIdx;
    private long jackpotLineWinCredit;
    private int pos;
    private int lineIdx;

    private int spinedCout;

    private SubSymbolComponent subSymbolCmp;
    private RandomWildComponent randomWildCmp;
    private ReelComponent reelCmp;
    private WinShowComponent winShowCmp;

    private boolean bPressPlay;
    private boolean bSkipProgressive;

    private int[]   levelWin = new int[LEVELS];


    public ProgressiveReelComponent() {
        setTouchable(Touchable.childrenOnly);

        SettingData cfg = GameData.getInstance().Setting;
        jackpotLineWin = new String[cfg.MaxSelections];
        for (int i = 0; i <jackpotLineWin.length; i ++) {
            jackpotLineWin[i] = "";
        }

        registerEvent(new GameResetEvent() {
            @Override
            public void execute(Object... obj) {
                clear();
                spinState = SPIN_ENDED;

                jackpotPos = 0;
                jackpotLineIdx = -1;
                spinedCout = 0;
                firstComeResult = true;
                jackPotLevelInfo = new int[0];;

                bPressPlay = false;
                bSkipProgressive = false;

                subSymbolCmp = ((SubSymbolComponent) Content.getInstance().getComponent(Content.SUBSYMBOLCOMPONENT));
                randomWildCmp = ((RandomWildComponent) Content.getInstance().getComponent(Content.RANDOMWILDCOMPONENT));
                reelCmp = ((ReelComponent) Content.getInstance().getComponent(Content.REELCOMPONENT));
                winShowCmp = (WinShowComponent) Content.getInstance().getComponent(Content.WINSHOWCOMPONENT);

                setJackpotInfo();

                for (MathParam param : GameData.getInstance().Context.MathParams) {
                    for (int i = 1; i <= LEVELS; i ++) {
                        if (param.Key.equals("PRONUM" + i)) {
                            levelWin[i - 1] = Integer.parseInt(param.Value);
                            break;
                        }
                    }
                }

                switch (GameData.getInstance().Context.GameState) {
                    case State.ProgressiveIntro:
                        if (bSkipProgressive) bPressPlay = true;

                        if (firstComeResult) {
                            float delayStartNext = GameConfiguration.getInstance().progressiveReel.delayStartNext;
                            addAction(delay(delayStartNext - 2.0f, run(GameClient.getInstance().progressiveIntroEnd)));
                            firstComeResult = false;
                        } else {
                            addAction(run(GameClient.getInstance().progressiveIntroEnd));
                        }
                        break;
                    case State.ProgressiveStarted:
                        addAction(run(GameClient.getInstance().awardProgressive));
                        break;

                    case State.ProgressiveResults:
                        if (!bPressPlay) bSkipProgressive = true;

                        for (MathParam param: GameData.getInstance().Context.MathParams) {
                            if (param.Key.equals("JACKPOTPOS")) {
                                jackpotPos = Integer.parseInt(param.Value);
                            }
                            if (param.Key.equals("JACKPOTPOSLINEIDX")) {
                                jackpotLineIdx = Integer.parseInt(param.Value);
                            }
                        }

                        if (jackpotPos == -1) {
                            endProResults();
                        } else {
                            startProgressiveResults();
                        }
                        break;
                }
            }
        });

        registerEvent(new GameStateChangedEvent() {
            @Override
            public void execute(Object... obj) {
                if (GameData.getPrevious().Context.GameState == State.ProgressiveResults && bSkipProgressive && !bPressPlay) {
                    clear();
//                    proReelRoll.endAllSpin();
//                    proReelAnim.endAllSpin();

                    EventMachine.getInstance().offerEvent(ProgressiveSingleReelResultsStartEvent.class, true, true, -1, 0);
                    //EventMachine.getInstance().offerEvent(ProgressivePlayerSkipEvent.class);

                    bPressPlay = true;
                }

                if (inProgressiveState()) {
                    switch (GameData.getInstance().Context.GameState) {
                        case State.ProgressiveIntro:
                            if (firstComeResult) {
                                float delayStartNext = GameConfiguration.getInstance().progressiveReel.delayStartNext;
                                addAction(delay(delayStartNext - 2.0f, run(GameClient.getInstance().progressiveIntroEnd)));
                                firstComeResult = false;
                            } else {
                                addAction(run(GameClient.getInstance().progressiveIntroEnd));
                            }
                            break;
                        case State.ProgressiveStarted:
                            addAction(run(GameClient.getInstance().awardProgressive));
                            break;
                        case State.AwardSASProgressive:
                            break;
                        case State.ProgressiveResults:
                            EventMachine.getInstance().offerEvent(ProgressiveReelOutroEvent.class);
                            bPressPlay = false;
                            if (!bPressPlay) bSkipProgressive = true;
                            startProgressiveResults();
                            break;

                        default:
                            break;
                    }
                } else {
                    if (GameData.getPrevious().Context.GameState == State.ProgressiveResults && bPressPlay) {
                        EventMachine.getInstance().offerEvent(ProgressiveSingleReelResultsStartEvent.class, false, true, -1, 0);
                        EventMachine.getInstance().offerEvent(ProgressiveSkipEndEvent.class);
                    }

                    bPressPlay = false;
                    bSkipProgressive = false;

                    if (spinState == SPIN_READY) {
                        startSpin();
                    }
                }
            }
        });

        registerEvent(new MultiReelStopEvent() {
            @Override
            public void execute(Object... obj) {
                if (spinState == SPIN_READY) {
                    startSpin();
                }
            }
        });

        registerEvent(new ReelStartSpinEvent() {
            @Override
            public void execute(Object... obj) {
                jackpotPos = 0;
                jackpotLineIdx = -1;
                jackPotLevelInfo = new int[0];
                firstComeResult = true;
                spinState = SPIN_START;

                for (int i = 0; i < LEVELS; i ++) {
                    levelWin[i] = 0;
                    GameClient.getInstance().selectProNum(i + 1, levelWin[i]);
                }

                GameClient.getInstance().selectJackpotPos(jackpotPos);
                GameClient.getInstance().selectJackpotLineIdx(jackpotLineIdx);

                SettingData cfg = GameData.getInstance().Setting;

                for (int line = 0; line < cfg.MaxSelections; line++) {
                    GameClient.getInstance().selectJackpotLineWin(line,-1,0);
                }

                if (!GameData.getInstance().Context.FreeGameMode) {
                    for (int i = 0; i <jackpotLineWin.length; i ++) {
                        jackpotLineWin[i] = "";
                    }
                }
            }
        });



        registerEvent(new PreWinShowStopEvent() {
            @Override
            public void execute(Object... obj) {
                if (!GameData.getInstance().Context.FreeGameMode && GameData.getInstance().Context.Result.NumFreeSpinsWon > 0 && getNeedSpinCount() > 0) {
                    startProgressive();
                }
            }
        });



        registerEvent(new ProgressiveSkipEvent() {
            @Override
            public void execute(Object... obj) {
                boolean bSkip = (boolean) obj[0];
                if (!bPressPlay)
                    bSkipProgressive = bSkip;
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

    public void startProgressive() {

    }

    private void startProgressiveIntro() {
        if (GameData.currentGameMode == GameMode.FreeGame) {
            EventMachine.getInstance().offerEvent(ModifyFreeBGMVolEvent.class, 0.01f);
        }

        addAction(delay(0.5f, run(()->EventMachine.getInstance().offerEvent(ProgressiveStartIntroEvent.class))));


        setJackpotInfo();

        float delayEndIntro = GameConfiguration.getInstance().progressiveReel.delayEndIntro;

        if (inProgressiveState()) {
            addAction(delay(delayEndIntro, run(() ->endProgressiveIntro())));
            addAction(delay(delayEndIntro, run(GameClient.getInstance().progressiveIntroEnd)));
        } else {
            addAction(delay(delayEndIntro, run(() ->endProgressiveIntro())));
            addAction(delay(delayEndIntro, run(() ->startProgressiveStarted())));
        }
    }

    private void endProgressiveIntro() {

    }

    private void startProgressiveStarted() {
    }

    private void beforStartSpin() {

    }

    private void startSpin() {

    }

    private void endSpin() {

    }

    private void progressiveOutro() {
        EventMachine.getInstance().offerEvent(ProgressiveReelOutroEvent.class);
    }

    private void startProgressiveResults() {
        setNextJackpotLineInfo();
        jackpotPos = pos;
        jackpotLineIdx = lineIdx;
        jackpotLineWinCredit = GameData.getInstance().Context.Progressive.Win;

        if (jackpotPos == -1) {
            GameClient.getInstance().selectJackpotPos(-1);
            if (inProgressiveState()) {
                //addAction(run(GameClient.getInstance().gameEnd));
            }
        } else {
            recordData();

            if (bPressPlay) {
                addAction(run(() -> endProSingleReelResults()));
            } else {
                EventMachine.getInstance().offerEvent(ProgressiveSingleReelResultsStartEvent.class, true, false, jackpotLineIdx, jackPotLevelInfo[jackpotPos]);
                addAction(delay(3.5f, run(() -> endProSingleReelResults())));
            }
        }
    }

    private void recordData() {
        if (jackpotPos != -1) {
            int line = jackpotLineIdx;
            int level = jackPotLevelInfo[jackpotPos];
            long win = jackpotLineWinCredit;

            if (!GameData.getInstance().Context.FreeGameMode && GameData.getInstance().Context.Result.ScatterWin > 0) {
                jackpotLineWin[line] = String.valueOf(level) + "," + String.valueOf(win);
            }

            GameClient.getInstance().selectJackpotLineWin(line, level, win);

//            levelWin[level - 1]++;
//            GameClient.getInstance().selectProNum(level, levelWin[level - 1]);

            GameClient.getInstance().selectJackpotPos(jackpotPos);
//            GameClient.getInstance().selectJackpotLineIdx(jackpotLineIdx);
        }
    }

    private void endProSingleReelResults() {

    }

    private void endProResults() {


    }

    private boolean inProgressiveState() {
        int state = GameData.getInstance().Context.GameState;
        return (state == State.ProgressiveIntro ||
                state == State.ProgressiveStarted ||
                state == State.AwardSASProgressive ||
                state == State.ProgressiveResults);
    }

    private void setJackpotInfo() {
        for (MathParam param: GameData.getInstance().Context.MathParams) {
            if (param.Key.equals("JACKPOTINFO")) {
                jackPotLevelInfo = CommonUtil.stringToArray(param.Value);
                break;
            }
        }
    }

    private void setNextJackpotLineInfo() {
        pos = jackpotPos;
        lineIdx = jackpotLineIdx;

        if (jackPotLevelInfo != null) {
            for (; pos < jackPotLevelInfo.length; pos++) {
                for (MathParam param : GameData.getInstance().Context.MathParams) {
                    if (param.Key.equals("JACKPOTLINE" + pos)) {
                        int[] jackpotLineInfo = CommonUtil.stringToArray(param.Value);
                        if (jackpotLineInfo == null) {
                            break;
                        }

                        for (int i = 0; i < jackpotLineInfo.length; i++) {
                            if (jackpotLineInfo[i] == -1) {
                                lineIdx = -1;
                                break;
                            }

                            if (jackpotLineInfo[i] > lineIdx) {
                                lineIdx = jackpotLineInfo[i];
                                return;
                            }
                        }
                    }
                }
            }
        }

        pos = -1;
        lineIdx = -1;
    }

    private boolean isHaveProgressiveWin() {
        if (jackPotLevelInfo != null) {
            for (int i = 0; i < jackPotLevelInfo.length; i++) {
                if (jackPotLevelInfo[i] > 0) {
                    return true;
                }
            }
        }

        return false;
    }

    public int getNeedSpinCount() {
        setJackpotInfo();

        int needSpinCount = 0;
        if (jackPotLevelInfo != null) {
            for (int i = 0; i < jackPotLevelInfo.length; i++) {
                if (jackPotLevelInfo[i] != 0) {
                    needSpinCount ++;
                }
            }
        }

        return needSpinCount;
    }

    public long[] getJackpotLineWin(int line) {
        if (line < 0 || line >= jackpotLineWin.length) return null;

        if (jackpotLineWin[line] == null || jackpotLineWin[line].equals("")) return null;

        long[] jackpotInfo = CommonUtil.stringToLongArray(jackpotLineWin[line]);

        if (jackpotInfo != null && jackpotInfo.length == 2) {
            long level = jackpotInfo[0];
            long win = jackpotInfo[1];

            if (level >= 0 && win > 0) {
                return jackpotInfo;
            }
        }

        return null;
    }

    public long getSpecialJackpotTotalWin() {
        long toalWin = 0;

        for (int line = 0; line < jackpotLineWin.length; line ++) {
            if (jackpotLineWin[line] != null && (!jackpotLineWin[line].equals(""))) {
                int[] jackpotInfo = CommonUtil.stringToArray(jackpotLineWin[line]);
                if (jackpotInfo != null && jackpotInfo.length == 2) {
                    int level = jackpotInfo[0];
                    long win = jackpotInfo[1];

                    if (level >= 0 && win > 0) {
                        toalWin += win;
                    }
                }
            }
        }

        return toalWin;
    }
}
