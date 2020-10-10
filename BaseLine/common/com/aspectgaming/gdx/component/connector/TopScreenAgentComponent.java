package com.aspectgaming.gdx.component.connector;

import com.aspectgaming.common.data.GameData;
import com.aspectgaming.common.event.GameModeChangeEvent;
import com.aspectgaming.common.event.freegame.InFreeGameIntroEvent;
import com.aspectgaming.common.event.freegame.OutFreeGameIntroEvent;
import com.aspectgaming.common.event.freegame.OutFreeGameOutroEvent;
import com.aspectgaming.common.event.gamble.InGambleEvent;
import com.aspectgaming.common.event.game.*;
import com.aspectgaming.common.event.machine.VolumeChangeEvent;
import com.aspectgaming.common.event.progressive.ProgressivePlayerSkipEvent;
import com.aspectgaming.common.event.progressive.ProgressiveSkipEndEvent;
import com.aspectgaming.common.event.progressive.ProgressiveWaitSpinEvent;
import com.aspectgaming.gdx.component.Component;
import com.aspectgaming.net.game.GameClient;

/**
 * @author johnny.shi & ligang.yao
 */
public class TopScreenAgentComponent extends Component {

    public TopScreenAgentComponent() {
        registerEvent(new GameResetEvent() {
            @Override
            public void execute(Object... obj) {
                GameClient.getInstance().sendToTopScreen("GameReset");
            }
        });

        registerEvent(new PlayProgressiveEvent() {
            @Override
            public void execute(Object... obj) {
                String[] params=new String[]{(String)obj[0],(String)obj[1],(String)obj[2]};
                GameClient.getInstance().sendToTopScreen("PlayProgressive",params);
            }
        });

        registerEvent(new StopCelebrationEvent() {
            @Override
            public void execute(Object... obj) {
                GameClient.getInstance().sendToTopScreen("StopCelebration");
            }
        });

        registerEvent(new StopCelebrationClickSpinEvent() {
            @Override
            public void execute(Object... obj) {
                GameClient.getInstance().sendToTopScreen("StopCelebrationClickSpin");
            }
        });

        registerEvent(new GameModeChangeEvent() {
            @Override
            public void execute(Object... obj) {
                String[] params = new String[] { GameData.currentGameMode.toString() };
                GameClient.getInstance().sendToTopScreen("GameModeChange", params);
            }
        });

        registerEvent(new WinMeterStartRollingEvent() {
            @Override
            public void execute(Object... obj) {
                // only show winbox if win > wager
                if (GameData.getInstance().Context.Win > GameData.getInstance().Context.TotalBet) {
                    String[] params = new String[] { String.valueOf(GameData.getInstance().Context.Win) };
                    GameClient.getInstance().sendToTopScreen("ShowWinBox", params);
                }
            }
        });

        registerEvent(new WinMeterStopRollingEvent() {
            @Override
            public void execute(Object... obj) {
                GameClient.getInstance().sendToTopScreen("HideWinBox");
            }
        });

        registerEvent(new ReelStoppedEvent() {
            @Override
            public void execute(Object... obj) {
                GameClient.getInstance().sendToTopScreen("ReelStopped");
            }
        });


        registerEvent(new ProgressiveReelOutroEvent() {
            @Override
            public void execute(Object... obj) {
                GameClient.getInstance().sendToTopScreen("ProgressiveReelOutro");
            }
        });

        registerEvent(new ProgressivePlayerSkipEvent() {
            @Override
            public void execute(Object... obj) {
                GameClient.getInstance().sendToTopScreen("ProgressivePlayerSkip");
            }
        });

        registerEvent(new ProgressiveSkipEndEvent() {
            @Override
            public void execute(Object... obj) {
                GameClient.getInstance().sendToTopScreen("ProgressiveSkipEnd");
            }
        });

        registerEvent(new VolumeChangeEvent() {
            @Override
            public void execute(Object... obj) {
                GameClient.getInstance().sendToTopScreen("SoundVolume", Float.toString(GameData.Volume));
            }
        });

        registerEvent(new ReelStartSpinEvent() {
            @Override
            public void execute(Object... obj) {
                GameClient.getInstance().sendToTopScreen("ReelStartSpin");
            }
        });

        registerEvent(new InFreeGameIntroEvent() {
            @Override
            public void execute(Object... obj) {
                GameClient.getInstance().sendToTopScreen("InFreeGameIntro");
            }
        });

        registerEvent(new OutFreeGameIntroEvent() {
            @Override
            public void execute(Object... obj) {
                GameClient.getInstance().sendToTopScreen("OutFreeGameIntro");
            }
        });

        registerEvent(new OutFreeGameOutroEvent() {
            @Override
            public void execute(Object... obj) {
                GameClient.getInstance().sendToTopScreen("OutFreeGameOutro");
            }
        });

        registerEvent(new InGambleEvent() {
            @Override
            public void execute(Object... obj) {
                GameClient.getInstance().sendToTopScreen("InGamble");
            }
        });

        registerEvent(new ProgressiveSingleReelResultsStartEvent() {
            @Override
            public void execute(Object... obj) {
                boolean isDim = (boolean)obj[0];
                boolean isSkip = (boolean)obj[1];
                int line = (int)obj[2];
                if (isDim && !isSkip) {
                    GameClient.getInstance().sendToTopScreen("ProgressiveCoin", Integer.toString(line));
                }
            }
        });

        registerEvent(new ProgressiveWaitSpinEvent() {
            @Override
            public void execute(Object... obj) {
                GameClient.getInstance().sendToTopScreen("ProgressiveWaitSpin");
            }
        });

        registerEvent(new CreditsChangedEvent() {
            @Override
            public void execute(Object... obj) {
                GameClient.getInstance().sendToTopScreen("CreditsChanged");
            }
        });
    }
}
