package com.aspectgaming.net.game;

import com.aspectgaming.common.configuration.GameConfiguration;
import com.aspectgaming.common.data.Content;
import com.aspectgaming.common.data.Currency;
import com.aspectgaming.common.data.GameData;
import com.aspectgaming.common.data.GameMode;
import com.aspectgaming.common.event.EventMachine;
import com.aspectgaming.common.event.GameModeChangeEvent;
import com.aspectgaming.common.event.freegame.InFreeGameIntroEvent;
import com.aspectgaming.common.event.freegame.OutFreeGameIntroEvent;
import com.aspectgaming.common.event.freegame.OutFreeGameOutroEvent;
import com.aspectgaming.common.event.gamble.InGambleEvent;
import com.aspectgaming.common.event.game.*;
import com.aspectgaming.common.event.machine.*;
import com.aspectgaming.common.event.minigame.PlayMiniGameEvent;
import com.aspectgaming.common.event.minigame.ShowMiniGameEvent;
import com.aspectgaming.common.event.progressive.ProgressiveConfiguredEvent;
import com.aspectgaming.common.event.progressive.ProgressivePlayerSkipEvent;
import com.aspectgaming.common.event.progressive.ProgressiveSkipEndEvent;
import com.aspectgaming.common.event.progressive.ProgressiveWaitSpinEvent;
import com.aspectgaming.common.event.recall.GameRecallChangedEvent;
import com.aspectgaming.common.event.recall.GameRecallStartedEvent;
import com.aspectgaming.common.event.recall.GameRecallStoppedEvent;
import com.aspectgaming.common.event.screen.ActionEvent;
import com.aspectgaming.common.event.screen.AttractStartEvent;
import com.aspectgaming.common.event.screen.AttractStopEvent;
import com.aspectgaming.common.event.screen.ChangeLinkedMediaEvent;
import com.aspectgaming.gdx.component.drawable.progressive.TopScreenProgressiveComponent;
import com.aspectgaming.net.game.data.*;
import com.badlogic.gdx.Gdx;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class GameServerHandler extends SimpleChannelInboundHandler<Message> {

    private final Logger log = LoggerFactory.getLogger(GameServerHandler.class);
    private final Queue<Message> queue = new ConcurrentLinkedQueue<>();
    private final Message msgHeartBeat = new Message(ProtocolTypes.HEART_BEAT, null);

    String name;
    List<String> data;
    List<String> actions;
    long hWnd;
    boolean isLocked;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        GameClient.getInstance().onConnected(this);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        GameClient.getInstance().onDisconnected(this);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            ctx.writeAndFlush(msgHeartBeat).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {
        queue.offer(msg);

        if (!GameConfiguration.getInstance().display.continuousRendering && Gdx.graphics != null) {
            Gdx.graphics.requestRendering();
        }
    }

    public boolean processMessage() {
        Message msg = queue.poll();

        if (msg == null) return false;

        switch (msg.type) {
            case HEART_BEAT:
                break;

            case COMMAND:
                CommandData cmd = (CommandData) msg.message;
                if ("Action".equals(cmd.Type)) {
                    onAction(cmd);
                } else {
                    onScreenToScreen(cmd);
                }
                break;

            case CONTEXT_S2C:
                onContextData((ContextData) msg.message);
                break;

            case GAME_RECALL_S2C:
                onGameRecall((GameRecallData) msg.message);
                break;

            case PAYTABLE_S2C:
                onPaytable((PaytableData) msg.message);
                break;

            case PROGRESSIVE_S2C:
                onProgressiveValues((ProgressiveValuesData) msg.message);
                break;

            case RESET_S2C:
                onGameResetData(msg.message);
                break;

            case SETTING_S2C:
                onSettingData((SettingData) msg.message);
                break;

            default:
                log.info("Unhandled protocol type: " + msg.type);
                break;
        }

        return true;
    }

    private void onGameResetData(Object object) {
        GameResetData msg = (GameResetData) object;

        GameData.getInstance().Setting = msg.Setting;
        GameData.getInstance().Context = msg.Context;
        GameData.getInstance().MathParams = MathParamsParser.parse(msg.Context.MathParams);
        GameData.getInstance().Paytable = msg.Paytable;
        GameData.getInstance().ProgressiveValues = msg.ProgressiveValues;
        GameData.getInstance().ReelStrips = msg.ReelStrips;

        GameData.Currency = Currency.valueOf(msg.Setting.Currency);

        int state = GameData.getInstance().Context.GameState;

        if (GameData.getInstance().Context.NumFreeSpinsTotalWon > 0) {
            GameData.currentGameMode = GameMode.FreeGame;
        } else {
            GameData.currentGameMode = GameMode.BaseGame;
        }

        EventMachine.getInstance().offerEvent(GameResetEvent.class);
        EventMachine.getInstance().offerEvent(LanguageChangedEvent.class);

        if (GameData.getInstance().isTilt()) {
            EventMachine.getInstance().offerEvent(InTiltEvent.class);
        } else {
            EventMachine.getInstance().offerEvent(OutTiltEvent.class);
        }
    }

    private void onPaytable(PaytableData msg) {
        GameData.getInstance().Paytable = msg;
        EventMachine.getInstance().offerEvent(ChangePaytableEvent.class);
    }

    private void onProgressiveValues(ProgressiveValuesData msg) {
        GameData.getInstance().ProgressiveValues = msg;
        TopScreenProgressiveComponent component = (TopScreenProgressiveComponent) Content.getInstance().getComponent(Content.TOPSCREENPROGRESSIVECOMPONENT);
        if (component != null) {
            component.update(GameData.getInstance().ProgressiveValues.Values);
        }
    }

    private void onAction(CommandData msg) {
        switch (msg.Name) {
            case "StartAttract":
                EventMachine.getInstance().offerEvent(AttractStartEvent.class, (Object[]) msg.Values);
                break;
            case "StopAttract":
                EventMachine.getInstance().offerEvent(AttractStopEvent.class);
                break;
//            case "ChangeTestReel":
//                EventMachine.getInstance().offerEvent(ChangeTestReelEvent.class);
//                break;
//            case "ChangeTestReelStop":
//                EventMachine.getInstance().offerEvent(ChangeTestReelStopEvent.class);
//                break;
            case "MultiReelStop":
                EventMachine.getInstance().offerEvent(MultiReelStopEvent.class);
                break;
            case "PlayMiniGame":
                EventMachine.getInstance().offerEvent(PlayMiniGameEvent.class);
                break;
            case "ButtonPressed":
                System.out.println("The line " + msg.Values[0] + " button from the Emulation Panel/Button Deck was pressed");
                //EventMachine.getInstance().offerEvent(ButtonPressedByButtonPanelEvent.class, msg.Values[0]);
                break;
            default:
                log.warn("Unknown message: {}", msg.Name);
                break;
        }
    }

    private void onScreenToScreen(CommandData msg) {
        switch (msg.Name) {
            case "GameReset":
                EventMachine.getInstance().offerEvent(MainToTopGameResetEvent.class);
                break;
            case "PlayProgressive":
                EventMachine.getInstance().offerEvent(PlayProgressiveEvent.class,(Object[]) msg.Values);
                break;
            case "StopCelebration":
                EventMachine.getInstance().offerEvent(StopCelebrationEvent.class,(Object[]) msg.Values);
                break;
            case "StopCelebrationClickSpin":
                EventMachine.getInstance().offerEvent(StopCelebrationClickSpinEvent.class,(Object[]) msg.Values);
                break;
            case "GameModeChange":
                GameData.currentGameMode = GameMode.valueOf(msg.Values[0]);
                EventMachine.getInstance().offerEvent(GameModeChangeEvent.class);
                break;
            case "ShowWinBox":
                GameData.getInstance().Context.Win = Long.parseLong(msg.Values[0]);
                EventMachine.getInstance().offerEvent(WinMeterStartRollingEvent.class);
                break;
            case "HideWinBox":
                EventMachine.getInstance().offerEvent(WinMeterStopRollingEvent.class);
                break;
            case "FlashPaytable":
                EventMachine.getInstance().offerEvent(FlashPaytableEvent.class, (Object[]) msg.Values);
                break;
            case "StopFlashPaytable":
                EventMachine.getInstance().offerEvent(StopFlashPaytableEvent.class);
                break;
            case "Action":
                EventMachine.getInstance().offerEvent(ActionEvent.class, (Object[]) msg.Values);
                break;
            case "SoundVolume":
                GameData.Volume = Float.parseFloat(msg.Values[0]);
                EventMachine.getInstance().offerEvent(VolumeChangeEvent.class);
                break;
            case "ReelStartSpin":
                EventMachine.getInstance().offerEvent(ReelStartSpinEvent.class);
                break;
            case "ReelStopped":
                EventMachine.getInstance().offerEvent(ReelStoppedEvent.class);
                break;
            case "WinMeterStopRolling":
                EventMachine.getInstance().offerEvent(WinMeterStopRollingEvent.class);
                break;
            case "InFreeGameIntro":
                EventMachine.getInstance().offerEvent(InFreeGameIntroEvent.class);
                break;
            case "OutFreeGameIntro":
                EventMachine.getInstance().offerEvent(OutFreeGameIntroEvent.class);
                break;
            case "OutFreeGameOutro":
                EventMachine.getInstance().offerEvent(OutFreeGameOutroEvent.class);
                break;
            case "ProgressiveReelOutro":
                EventMachine.getInstance().offerEvent(ProgressiveReelOutroEvent.class);
                break;
            case "ProgressivePlayerSkip":
                EventMachine.getInstance().offerEvent(ProgressivePlayerSkipEvent.class);
                break;
            case "ProgressiveSkipEnd":
                EventMachine.getInstance().offerEvent(ProgressiveSkipEndEvent.class);
                break;
            case "ProgressiveCoin":
                int line = Integer.parseInt(msg.Values[0]);
                EventMachine.getInstance().offerEvent(ProgressiveSingleReelResultsStartEvent.class, line);
                break;
            case "ProgressiveWaitSpin":
                EventMachine.getInstance().offerEvent(ProgressiveWaitSpinEvent.class);
                break;
            case "InGamble":
                EventMachine.getInstance().offerEvent(InGambleEvent.class);
                break;
            case "CreditsChanged":
                EventMachine.getInstance().offerEvent(CreditsChangedEvent.class);
                break;
            default:
                log.warn("Unknown message: {}", msg.Name);
                break;
        }
    }

    private void onGameRecall(GameRecallData msg) {
        GameData.getPrevious().GameRecall = GameData.getInstance().GameRecall;
        GameData.getInstance().GameRecall = msg.Context;

        ContextData old = GameData.getPrevious().GameRecall;
        ContextData now = GameData.getInstance().GameRecall;

        if (old == null) {
            if (now != null) {
                EventMachine.getInstance().offerEvent(GameRecallStartedEvent.class, msg.Type, msg.Value);
            }
        } else {
            if (now != null) {
                EventMachine.getInstance().offerEvent(GameRecallChangedEvent.class, msg.Type, msg.Value);
            } else {
                EventMachine.getInstance().offerEvent(GameRecallStoppedEvent.class);
            }
        }
    }

    private void onSettingData(SettingData msg) {
        GameData.getPrevious().Setting = GameData.getInstance().Setting;
        GameData.getInstance().Setting = msg;

        SettingData old = GameData.getPrevious().Setting;
        SettingData now = GameData.getInstance().Setting;

        GameData.Currency = Currency.valueOf(msg.Currency);

        if (old != null) {
            if (old.LinkedMedia != null) {
                if (now.LinkedMedia != null) {
                    LinkedMediaData sOld = GameData.getPrevious().Setting.LinkedMedia;
                    LinkedMediaData sNow = GameData.getInstance().Setting.LinkedMedia;
                    if (sOld.Enabled != sNow.Enabled || sOld.Location != sNow.Location || !sOld.Type.equals(sNow.Type)) {
                        EventMachine.getInstance().offerEvent(ChangeLinkedMediaEvent.class);
                    }
                } else {
                    EventMachine.getInstance().offerEvent(ChangeLinkedMediaEvent.class);
                }

            } else {
                if (now.LinkedMedia != null) {
                    EventMachine.getInstance().offerEvent(ChangeLinkedMediaEvent.class);
                }
            }

            if (old.ProgressiveType != now.ProgressiveType) {
                EventMachine.getInstance().offerEvent(ProgressiveConfiguredEvent.class);
            }
            if (!old.SelectedGame.equals(now.SelectedGame)) {
                EventMachine.getInstance().offerEvent(GameChangedEvent.class);
            }
        } else {
            if (now.LinkedMedia != null) {
                EventMachine.getInstance().offerEvent(ChangeLinkedMediaEvent.class);
            }
        }
    }

    private void onContextData(ContextData msg) {
        GameData.getPrevious().Context = GameData.getInstance().Context;
        GameData.getInstance().Context = msg;

        ContextData old = GameData.getPrevious().Context;
        ContextData now = GameData.getInstance().Context;

        if (old != null) {
            if (!GameData.getPrevious().isTilt()) {
                if (GameData.getInstance().isTilt()) {
                    EventMachine.getInstance().offerEvent(InTiltEvent.class);
                }
            } else {
                if (!GameData.getInstance().isTilt()) {
                    EventMachine.getInstance().offerEvent(OutTiltEvent.class);
                } else if (old.State != now.State) {
                    // send again if tilt state changed, for handpay handling
                    EventMachine.getInstance().offerEvent(InTiltEvent.class);
                }
            }
            if (old.GameState != now.GameState) {
                EventMachine.getInstance().offerEvent(GameStateChangedEvent.class);
            } else if (old.State != now.State) {
                EventMachine.getInstance().offerEvent(StateChangedEvent.class);
            }

            if (!old.Language.equals(now.Language)) {
                EventMachine.getInstance().offerEvent(LanguageChangedEvent.class);
            }
            if (!old.Paytable.equals(now.Paytable)) {
                EventMachine.getInstance().offerEvent(PaytableChangedEvent.class);
            }
            if (old.Denomination != now.Denomination) {
                EventMachine.getInstance().offerEvent(DenomChangedEvent.class);
            }
            if (old.BetMultiplier != now.BetMultiplier || old.Selections != now.Selections || old.BetCredits != now.BetCredits) {
                EventMachine.getInstance().offerEvent(ChangeBetEvent.class);
            }
            if (!now.Messages.equals(old.Messages)) {
                EventMachine.getInstance().offerEvent(SystemMessageChangedEvent.class);
            }
            if (old.Cash != now.Cash) {
                EventMachine.getInstance().offerEvent(CreditsChangedEvent.class);
            }
            if (now.MoneyRelatedMessages != null) {
                EventMachine.getInstance().offerEvent(ShowMessageEvent.class);
            }
            if (old.SpecialGameMode != now.SpecialGameMode) {
                EventMachine.getInstance().offerEvent(ShowMiniGameEvent.class);
            }
        }

        onMathParams(msg.MathParams);
    }

    private void onMathParams(List<MathParam> params) {
        GameData.getPrevious().MathParams = GameData.getInstance().MathParams;
        GameData.getInstance().MathParams = MathParamsParser.parse(params);
    }
}
