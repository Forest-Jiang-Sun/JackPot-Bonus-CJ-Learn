package com.aspectgaming.gdx.component.connector;

import com.aspectgaming.common.data.GameData;
import com.aspectgaming.common.event.GameModeChangeEvent;
import com.aspectgaming.common.event.freegame.OutFreeGameOutroEvent;
import com.aspectgaming.common.event.game.ReelStartSpinEvent;
import com.aspectgaming.common.event.game.ReelStoppedEvent;
import com.aspectgaming.common.event.game.WinMeterStopRollingEvent;
import com.aspectgaming.gdx.component.Component;
import com.aspectgaming.net.game.GameClient;

public class ButtonDeckScreenAgentComponent extends Component {

    public ButtonDeckScreenAgentComponent() {
        registerEvent(new GameModeChangeEvent() {
            @Override
            public void execute(Object... obj) {
                String[] params = new String[]{GameData.currentGameMode.toString()};
                GameClient.getInstance().sendToButtonDeckScreen("GameModeChange", params);
            }
        });

        registerEvent(new ReelStartSpinEvent() {
            @Override
            public void execute(Object... obj) {
                GameClient.getInstance().sendToButtonDeckScreen("ReelStartSpin");
            }
        });

        registerEvent(new ReelStoppedEvent() {
            @Override
            public void execute(Object... obj) {
                GameClient.getInstance().sendToButtonDeckScreen("ReelStopped");
            }
        });

        registerEvent(new WinMeterStopRollingEvent() {
            @Override
            public void execute(Object... obj) {
                GameClient.getInstance().sendToButtonDeckScreen("WinMeterStopRolling");
            }
        });

        registerEvent(new OutFreeGameOutroEvent() {
            @Override
            public void execute(Object... obj) {
                GameClient.getInstance().sendToButtonDeckScreen("OutFreeGameOutro");
            }
        });
    }
}
