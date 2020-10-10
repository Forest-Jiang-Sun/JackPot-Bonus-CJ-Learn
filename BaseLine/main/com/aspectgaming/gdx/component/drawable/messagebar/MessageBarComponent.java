package com.aspectgaming.gdx.component.drawable.messagebar;

import com.aspectgaming.common.actor.TextureLabel;
import com.aspectgaming.common.configuration.GameConfiguration;
import com.aspectgaming.common.configuration.MessageBarConfiguration;
import com.aspectgaming.common.data.Content;
import com.aspectgaming.common.data.GameConst;
import com.aspectgaming.common.data.GameData;
import com.aspectgaming.common.data.State;
import com.aspectgaming.common.event.freegame.OutFreeGameOutroEvent;
import com.aspectgaming.common.event.freegame.RetriggerEvent;
import com.aspectgaming.common.event.gamble.InGambleEvent;
import com.aspectgaming.common.event.game.*;
import com.aspectgaming.common.event.machine.*;
import com.aspectgaming.common.event.screen.AttractStartEvent;
import com.aspectgaming.common.event.screen.AttractStopEvent;
import com.aspectgaming.common.loader.CoordinateLoader;
import com.aspectgaming.common.loader.MessageLoader;
import com.aspectgaming.gdx.component.drawable.DrawableComponent;
import com.aspectgaming.gdx.component.drawable.meter.MetersComponent;
import com.aspectgaming.math.SlotGameMode;
import com.aspectgaming.net.game.GameClient;
import com.aspectgaming.net.game.data.MathParam;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Align;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.delay;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.run;

/**
 * Show game message in bar.
 *
 * @author johnny.shi & ligang.yao
 */
public class MessageBarComponent extends DrawableComponent implements State {

    private final MessageBarConfiguration cfg;
    private final Rectangle rectangle1;
    private final Rectangle rectangle2;

    private Object[] currentArgs1;
    private String currentMessage1;

    private Object[] currentArgs2;
    private String currentMessage2;

    private TextureLabel label1;
    private TextureLabel label2;

    private boolean isReset;
    private boolean isPlayAgainAndUnGamble;
    private boolean isLanguageChange;
    NumberFormat numberFormat;
    DecimalFormat decimalFormat;
    private SlotGameMode gameMode;

    public MessageBarComponent() {
        switch (GameData.getInstance().Setting.ProgressiveType)
        {
            case 0:
                gameMode= SlotGameMode.None;
                break;
            case 1:
                gameMode=SlotGameMode.Linked;
                break;
            case 2:
                gameMode= SlotGameMode.StandAlone;
                break;
            default:
                gameMode= SlotGameMode.None;
                break;
        }

        cfg = GameConfiguration.getInstance().messageBar;
        rectangle1 = CoordinateLoader.getInstance().getBound("MessageBar1");
        rectangle2 = CoordinateLoader.getInstance().getBound("MessageBar2");

        label1 = new TextureLabel(cfg.font, Align.center, Align.center);
        label1.setColor(cfg.color);
        label1.setBounds(rectangle1);
        addActor(label1);

        label2 = new TextureLabel(cfg.font, Align.center, Align.center);
        label2.setColor(cfg.color);
        label2.setBounds(rectangle2);
        addActor(label2);

        numberFormat = NumberFormat.getNumberInstance();
        decimalFormat=new DecimalFormat("##,##0.00");

        registerEvent(new GameResetEvent() {
            @Override
            public void execute(Object... obj) {
                isReset = true;
                isLanguageChange = false;
                isPlayAgainAndUnGamble = false;

                setMessageArea1("blank");
                for (MathParam param: GameData.getInstance().Context.MathParams) {
                    if (param.Key.equals("PLAYAGAINANDUNGAMBLE")) {
                        if (param.Value.equals("true")) {
                            isPlayAgainAndUnGamble = true;
                        }
                        break;
                    }
                }

                ChangeMessageByGameState();
            }
        });

        registerEvent(new ReelStartSpinEvent() {
            @Override
            public void execute(Object... obj) {
                isReset = false;
                isPlayAgainAndUnGamble = false;
                GameClient.getInstance().selectPlayAgainAndUnGamble(false);
                setMessageArea1("blank");
            }
        });

        registerEvent(new RetriggerEvent() {
            @Override
            public void execute(Object... obj) {
                if (GameData.getInstance().Context.FreeGameMode) {
                    int numFreeSpinsRemaining = GameData.getInstance().Context.NumFreeSpinsRemaining + GameData.getInstance().Context.Result.NumFreeSpinsWon;
                    int numFreeSpinsTotalWon = GameData.getInstance().Context.NumFreeSpinsTotalWon + GameData.getInstance().Context.Result.NumFreeSpinsWon;

                    setMessageArea2("NumFreeSpins", numFreeSpinsTotalWon - numFreeSpinsRemaining, numFreeSpinsTotalWon);
                }
            }
        });

        registerEvent(new InGambleEvent() {
            @Override
            public void execute(Object... obj) {
                setMessageArea1("blank");
                setMessageArea2("blank");
            }
        });

        registerEvent(new LanguageChangedEvent() {
            @Override
            public void execute(Object... obj) {
                isLanguageChange = true;
                if (currentMessage1 != null) {
                    setMessageArea1(currentMessage1, currentArgs1);
                }

                if (currentMessage2 != null) {
                    setMessageArea2(currentMessage2, currentArgs2);
                }

                isLanguageChange = false;
            }
        });

        registerEvent(new GameStateChangedEvent() {
            @Override
            public void execute(Object... obj) {
                ChangeMessageByGameState();
            }
        });

        registerEvent(new StateChangedEvent() {
            @Override
            public void execute(Object... obj) {
                ChangeMessageByGameState();
            }
        });

        registerEvent(new InTiltEvent() {
            @Override
            public void execute(Object... obj) {
                ChangeMessageByGameState();
            }
        });

        registerEvent(new OutTiltEvent() {
            @Override
            public void execute(Object... obj) {
                ChangeMessageByGameState();
            }
        });

        registerEvent(new CreditsChangedEvent() {
            @Override
            public void execute(Object... obj) {
                if (GameData.getInstance().Context.GameState != FreeGameIntro &&
                        GameData.getInstance().Context.GameState != GambleWin &&
                        GameData.getInstance().Context.GameState != FreeGameStarted &&
                        GameData.getInstance().Context.GameState != PrimaryGameStarted &&
                        GameData.getInstance().Context.GameState != PayGameResults &&
                        !GameData.getInstance().Context.FreeGameMode &&
                        GameData.getInstance().Context.GameState != ReelStop
                        ) {
                    //int selected  = GameData.getInstance().getBetButtonIndex();
                    //int bet = GameData.getInstance().getBetAmountList()[selected];
                    //if (GameData.getInstance().Context.Credits + GameData.getInstance().Context.TotalWin > bet ) {
                    {
                        if (isPlayAgainAndUnGamble) {
                            Area2PlayAgainAndUnGambleCycle();
                        } else {
                            MetersComponent meters = (MetersComponent) Content.getInstance().getComponent(Content.METERSCOMPONENT);
                            if (meters.isWinMeterStop()) {
                                Area2PlayAgainOrCycle();
                            }
                        }
                    }
                }
            }
        });

        registerEvent(new ChangeBetEvent() {
            @Override
            public void execute(Object... obj) {
                if (isPlayAgainAndUnGamble) {
                    Area2PlayAgainAndUnGambleCycle();
                } else {
                    Area2PlayAgainOrCycle();
                }
            }
        });

        registerEvent(new DenomChangedEvent() {
            @Override
            public void execute(Object... obj) {
                if (isPlayAgainAndUnGamble) {
                    Area2PlayAgainAndUnGambleCycle();
                } else {
                    Area2PlayAgainOrCycle();
                }
            }
        });

        registerEvent(new WinMeterStopRollingEvent() {
            @Override
            public void execute(Object... obj) {
                if (!GameData.getInstance().Context.FreeGameMode && GameData.getInstance().Context.Result.ScatterWin <= 0) {
                    if (GameData.getInstance().Context.ProgressiveTotalWin / GameData.getInstance().Context.Denomination
                            + GameData.getInstance().Context.BonusTotalWin <= 0 && GameData.getInstance().Context.TotalWin >
                            GameData.getInstance().Setting.GambleLimit / GameData.getInstance().Context.Denomination) {
                        GameClient.getInstance().selectPlayAgainAndUnGamble(true);
                        isPlayAgainAndUnGamble = true;
                        Area2PlayAgainAndUnGambleCycle();
                    } else {
                        setMessageArea2("PlayAgain");
                    }
                }
            }
        });

        registerEvent(new OutFreeGameOutroEvent() {
            @Override
            public void execute(Object... obj) {
                Area2PlayAgainOrCycle();
            }
        });

        registerEvent(new AttractStartEvent() {
            @Override
            public void execute(Object... obj) {
                setMessageArea2("blank");
            }
        });

        registerEvent(new AttractStopEvent() {
            @Override
            public void execute(Object... obj) {
                if (GameData.getInstance().Context.Credits <= 0) {
                    setMessageArea2("LackCredit1");
                }
            }
        });

//        registerEvent(new ProgressiveSpinStateEvent() {
//            @Override
//            public void execute(Object... obj) {
//                int proReelSpinState = (int)obj[0];
//                if (proReelSpinState == 0) {
//                    setMessageArea2("blank");
//                }
//            }
//        });
    }

    private void ChangeMessageByGameState() {
        switch (GameData.getInstance().Context.GameState) {
            case FreeGameIntro:
            case StartFreeSpin:
                int numFreeSpinsRemaining = GameData.getInstance().Context.NumFreeSpinsRemaining;
                int numFreeSpinsTotalWon = GameData.getInstance().Context.NumFreeSpinsTotalWon;
                setMessageArea2("NumFreeSpins", numFreeSpinsTotalWon - numFreeSpinsRemaining, numFreeSpinsTotalWon);
                break;
            case GambleWin:
                setMessageArea1("blank");
                setMessageArea2("blank");
                break;
            case FreeGameStarted:
                setMessageArea1("blank");
                break;
            case PrimaryGameStarted:
                setMessageArea1("blank");
                setMessageArea2("GoodLuck");
                break;
            case PayGameResults:
                setMessageArea2("GoodLuck");
                break;
            case ReelStop:
                break;
            case GameIdle:
                if (isPlayAgainAndUnGamble ||
                        (GameData.getPrevious().Context != null && GameData.getPrevious().Context.GameState == GambleDisplayPending
                                && GameData.getInstance().Context.TotalWin > GameData.getInstance().Setting.GambleLimit / GameData.getInstance().Context.Denomination)) {
                    isPlayAgainAndUnGamble = true;
                    GameClient.getInstance().selectPlayAgainAndUnGamble(true);
                    Area2PlayAgainAndUnGambleCycle();

                } else if (GameData.getInstance().Context.TotalWin <= 0 || isReset) {
                    Area2PlayAgainOrCycle();
                } else {
                    if (GameData.getInstance().Context.TotalWin <= 0) {
                        setMessageArea2("PlayAgain");
                    }
                }
                break;
            case GambleChoice:
                if (isReset) {
                    Area2PlayAgainOrCycle();
                }
                break;
            default:
                if (GameData.getInstance().Context.FreeGameMode) {
                    numFreeSpinsRemaining = GameData.getInstance().Context.NumFreeSpinsRemaining;
                    numFreeSpinsTotalWon = GameData.getInstance().Context.NumFreeSpinsTotalWon;

                    setMessageArea2("NumFreeSpins", numFreeSpinsTotalWon - numFreeSpinsRemaining, numFreeSpinsTotalWon);
                }

                break;
        }
    }

    private void Area2PlayAgainAndUnGambleCycle() {
        setMessageArea2("PlayAgain");
        addAction(delay(2.0f, run(() -> Area2UnGamble())));
    }

    private void Area2UnGamble() {
        setMessageArea2("UnGamble");
        addAction(delay(2.0f, run(() -> Area2PlayAgainAndUnGambleCycle())));
    }

    private void Area2PlayAgainOrCycle() {
        boolean isRetrunFreeGame = false;
        for (MathParam param: GameData.getInstance().Context.MathParams) {
            if (param.Key.equals("ISRETURNFREEGAME")) {
                if (param.Value.equals("true")) {
                    isRetrunFreeGame = true;
                }
                break;
            }
        }

        if (isRetrunFreeGame) {
            if (GameData.getInstance().Context.ProgressiveTotalWin / GameData.getInstance().Context.Denomination
                    + GameData.getInstance().Context.BonusTotalWin <= 0 && GameData.getInstance().Context.TotalWin
                    > GameData.getInstance().Setting.GambleLimit / GameData.getInstance().Context.Denomination) {
                GameClient.getInstance().selectPlayAgainAndUnGamble(true);
                isPlayAgainAndUnGamble = true;
                Area2PlayAgainAndUnGambleCycle();
            } else {
                Area2PlayAgain();
            }
        } else {
            if (isReset || GameData.getInstance().Context.TotalWin <= 0) {
                setMessageArea2("PlayAgain");
            }
        }
    }

    private void Area2PlayAgain() {
        setMessageArea2("PlayAgain");
        addAction(delay(2.0f, run(() -> Area2FeatureCompleted())));
    }

    private void Area2FeatureCompleted() {
        setMessageArea2("FeatureCompleted");
        addAction(delay(2.0f, run(() -> Area2PlayAgain())));
    }

    private boolean HasEnoughMoney() {
        if (GameData.getInstance().Context.Credits < GameData.getInstance().Context.TotalBet) {
            if (GameData.getInstance().Context.Credits < GameData.getInstance().getBetAmountOneMultipler()) {
                setMessageArea2("LackCredit1");
            } else {
                setMessageArea2("LackCredit0");
            }
            return false;
        }
        return true;
    }

    public void setMessageArea2(String messageKey, Object... args) {
        if (!isLanguageChange) {
            clearActions();
        }
        String message = MessageLoader.getInstance().getMessage(messageKey);
        if (message == null) {messageKey = "blank";}

        String oldMessage2 = currentMessage2;
        Object[] oldArgs2 = currentArgs2;

        currentMessage2 = messageKey;
        currentArgs2 = args;

        switch (messageKey) {
            case "blank":
                label2.setText("");
                break;
            case "PlayAgain":
                int gameState = GameData.getInstance().Context.GameState;
                if (gameState == State.GambleChoice || gameState == State.GambleWin) {
//                    setMessageArea2("TakewinOrGamble");
                }else {
                    if (HasEnoughMoney()) {
                        if (GameData.getInstance().Context.BetMultiplier >  0) {
                            MetersComponent meters = (MetersComponent) Content.getInstance().getComponent(Content.METERSCOMPONENT);
                            if (meters.isWinMeterStop()) {
                                if (!GameData.getInstance().isTilt()) {
                                    label2.setText(message);
                                } else {
                                    label2.setText("");
                                }
                            } else {
                                currentMessage2 = oldMessage2;
                                currentArgs2 = oldArgs2;

                                message = MessageLoader.getInstance().getMessage(oldMessage2);
                                if (message == null) {message = "";}

                                label2.setText(message);
                            }
                        }else {
                            label2.setText("");
                        }
                    }
                }
                break;
            case "GoodLuck":
                label2.setText(message);
                break;
            case "LackCredit1":
            case "LackCredit0":
                if (!GameData.getInstance().isTilt()) {
                    label2.setText(message);
                } else {
                    label2.setText("");
                }
                break;
            case "NumFreeSpins":
                message = message.replace("@{numFreeSpinsRemaining}", String.valueOf(args[0]));
                message = message.replace("@{numFreeSpinsTotalWon}", String.valueOf(args[1]));
                label2.setText(message);
                break;
            case "FeatureCompleted":
                label2.setText(message);
                break;
            case "TakewinOrGamble":
                label2.setText(message);
                break;
            case "UnGamble":
                message = message.replace("@{GambleLimit}", String.valueOf(GameData.Currency.format(GameData.getInstance().Setting.GambleLimit)));
                label2.setText(message);
                break;
            default:
                break;
        }
    }

    public void setMessageArea1(String messageKey, Object... args) {
        String message = MessageLoader.getInstance().getMessage(messageKey);
        if (message == null) {messageKey = "blank";}
        currentMessage1 = messageKey;
        currentArgs1 = args;
        long totalwin = 0;
        String dollarWin="";

        switch (messageKey) {
            case "blank":
                label1.setText("");
                break;
            case "SymbolWin":
                message = message.replace("@{X}", String.valueOf(Integer.parseInt(args[0].toString()) + 1));
                totalwin = Integer.parseInt(args[1].toString()) * Integer.parseInt(args[2].toString());
                if(MetersComponent.isCredits==true) {
                    dollarWin = numberFormat.format(totalwin);
                    message = message.replace("@{Y}", dollarWin);
                }
                else
                {
                    dollarWin = creditsToCurrency3(totalwin);
                    message = message.replace("@{Y}", dollarWin);
                }
                label1.setText(message);


                break;
            case "ScatterWin":
//                totalwin = Integer.parseInt(args[0].toString()) * Integer.parseInt(args[1].toString());
//                message = message.replace("@{Y}", String.valueOf(totalwin));
//                message = message.replace(" * @{Z}", "");
//
//                label1.setText(message);
                break;
            case "BonusWin" :
                if (MetersComponent.isCredits==true) {
                    totalwin = Long.parseLong(args[0].toString()) * Long.parseLong(args[1].toString());
                    message = message.replace("@{Z}", String.valueOf(totalwin));
                }
                else
                {
                    int denomination = GameData.getInstance().Context.Denomination;
//                    dollarWin = GameData.Currency.symbol+(((double) GameData.getInstance().Context.FreeGameTotalWin*denomination)/100+((double)GameData.getInstance().Context.ProgressiveTotalWin)/100);
//                    dollarWin=creditsToCurrency3(GameData.getInstance().Context.FreeGameTotalWin+GameData.getInstance().Context.ProgressiveTotalWin);
                    dollarWin=creditsToCurrency4(GameData.getInstance().Context.ProgressiveTotalWin,GameData.getInstance().Context.FreeGameTotalWin);
                    message = message.replace("@{Z}", dollarWin);
                }
                label1.setText(message);
                break;
            case "CommAndJackpotWin":
                int line = Integer.parseInt(args[0].toString());
                totalwin = Long.parseLong(args[1].toString()) * Long.parseLong(args[2].toString());

                message = message.replace("@{X}", String.valueOf(line + 1));
                message = message.replace("@{Y}", String.valueOf(totalwin));

                int level = Integer.parseInt(args[4].toString());
                String jackpotName = "";
                if (level > 0 && level <= GameConst.PROGRESSIVE_EN_NAMES.length){
                    if (GameData.getInstance().Context.Language.equals("en-US")) {
                        jackpotName = GameConst.PROGRESSIVE_EN_NAMES[level-1];
                    } else {
                        jackpotName = GameConst.PROGRESSIVE_CHZ_NAMES[level-1];
                    }
                }

                message = message.replace("@{Z}", jackpotName);

                long win = Long.parseLong(args[5].toString());

                message = message.replace("@{A}", GameData.Currency.format(win));
                label1.setText(message);
                break;
            case "JackpotWin":
                String jackpotWin="";
                if (gameMode!=SlotGameMode.None) {
                    message=MessageLoader.getInstance().getMessage("JackpotWin");
                    jackpotWin=creditsToCurrency((Long) args[0]);
                }
                else
                {
                    message=MessageLoader.getInstance().getMessage("JackpotWinNoProgressive");
                    if(MetersComponent.isCredits==true) {
                        jackpotWin = Long.toString(GameData.getInstance().Context.Win);
                    }else {
                        jackpotWin = creditsToCurrency3(GameData.getInstance().Context.Win);
                    }
                }
                message=message+jackpotWin;
                label1.setText(message);
                break;
            case "P2Win":
                String p2Win="";
                if (gameMode==SlotGameMode.StandAlone) {
                    message=MessageLoader.getInstance().getMessage("P2Win");
                    p2Win = creditsToCurrency((Long) args[0]);
                }
                else
                {
                    message=MessageLoader.getInstance().getMessage("P2WinNoProgressive");
                    if(MetersComponent.isCredits==true) {
                        p2Win = Long.toString(GameData.getInstance().Context.Win);
                    }else {
                        p2Win = creditsToCurrency3(GameData.getInstance().Context.Win);
                    }
                }
                message = message+p2Win;
                label1.setText(message);
                break;
            default:
                break;

        }
    }

    private String creditsToCurrency(long num)
    {
        return (GameData.Currency.symbol+ decimalFormat.format((double) num/100));
    }

    private String creditsToCurrency2(long num)
    {
        return (GameData.Currency.symbol+ decimalFormat.format((double)num*((double) GameData.getInstance().Setting.Denominations[0]/100)));
    }

    private String creditsToCurrency3(long num)
    {
        return GameData.Currency.symbol+decimalFormat.format((double)GameData.getInstance().Setting.Denominations[0]/100*(double)num);
    }

    private String creditsToCurrency4(long num1,long num2)
    {
        return (GameData.Currency.symbol+ decimalFormat.format((double)num1/100+(double)GameData.getInstance().Setting.Denominations[0]/100*(double)num2));
    }
}
