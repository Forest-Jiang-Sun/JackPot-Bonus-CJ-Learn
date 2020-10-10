package com.aspectgaming.gdx.component.ledcontroller;

import com.aspectgaming.common.configuration.*;
import com.aspectgaming.common.data.GameData;
import com.aspectgaming.common.data.GameMode;
import com.aspectgaming.common.data.State;
import com.aspectgaming.common.event.freegame.OutFreeGameIntroEvent;
import com.aspectgaming.common.event.freegame.OutFreeGameOutroEvent;
import com.aspectgaming.common.event.game.*;
import com.aspectgaming.gdx.component.Component;
import com.ltgame.hid.LedController;

public class LEDControllerComponent extends Component {
    private final int VENDORID = 1003;
    private final int PRODUCTID = 9474;

    private final int BASE_STRING_ID = 0;
    private final int LOGO_STRING_ID = 1;
    private final int BUTTON_PANEL_STRING_ID = 2;
    private final int BODY_STRING_ID = 3;
    private final int L_MONITOR_STRING_ID = 4;
    private final int U_MONITOR_STRING_ID = 5;
    private final int BUTTON_LIGHT_RING_STRING_ID = 6;

    private final int BASE_NUMBER_LED = 74;
    private final int LOGO_NUMBER_LED = 65;
    private final int BUTTON_PANEL_NUMBER_LED = 86;
    private final int BODY_NUMBER_LED = 162;
    private final int L_MONITOR_NUMBER_LED = 92;
    private final int U_MONITOR_NUMBER_LED = 92;
    private final int BUTTON_LIGHT_RING_NUMBER_LED = 12;

    private LedController ledController;
    private LedIdleCfg idle;
    private LedFreeGamesCfg freeGames;
    private LedDuringSpinCfg duringSpin;
    private LedDuringWinCelebrationsCfg duringWinCelebrations;

    public LEDControllerComponent() {
        ledController = new LedController(VENDORID, PRODUCTID);

        ledController.ConfigLedString(BASE_STRING_ID, (GameConfiguration.getInstance().led == null || GameConfiguration.getInstance().led.base <= 0)  ? BASE_NUMBER_LED : GameConfiguration.getInstance().led.base);
        ledController.ConfigLedString(LOGO_STRING_ID, (GameConfiguration.getInstance().led == null ||GameConfiguration.getInstance().led.logo <= 0) ? LOGO_NUMBER_LED : GameConfiguration.getInstance().led.logo);
        ledController.ConfigLedString(BUTTON_PANEL_STRING_ID, (GameConfiguration.getInstance().led == null ||GameConfiguration.getInstance().led.buttonPanel <= 0) ? BUTTON_PANEL_NUMBER_LED : GameConfiguration.getInstance().led.buttonPanel);
        ledController.ConfigLedString(BODY_STRING_ID, (GameConfiguration.getInstance().led == null ||GameConfiguration.getInstance().led.body <= 0) ? BODY_NUMBER_LED : GameConfiguration.getInstance().led.body);
        ledController.ConfigLedString(L_MONITOR_STRING_ID, (GameConfiguration.getInstance().led == null ||GameConfiguration.getInstance().led.lowerMonitor <= 0) ? L_MONITOR_NUMBER_LED : GameConfiguration.getInstance().led.lowerMonitor);
        ledController.ConfigLedString(U_MONITOR_STRING_ID, (GameConfiguration.getInstance().led == null ||GameConfiguration.getInstance().led.upperMonitor <= 0) ? U_MONITOR_NUMBER_LED : GameConfiguration.getInstance().led.upperMonitor);
        ledController.ConfigLedString(BUTTON_LIGHT_RING_STRING_ID, (GameConfiguration.getInstance().led == null ||GameConfiguration.getInstance().led.buttonLightRing <= 0) ? BUTTON_LIGHT_RING_NUMBER_LED : GameConfiguration.getInstance().led.buttonLightRing);

        idle = GameConfiguration.getInstance().led.idle;
        freeGames = GameConfiguration.getInstance().led.freeGames;
        duringSpin = GameConfiguration.getInstance().led.duringSpin;
        duringWinCelebrations = GameConfiguration.getInstance().led.duringWinCelebrations;

        registerEvent(new GameResetEvent() {
            @Override
            public void execute(Object... obj) {
                int state = GameData.getInstance().Context.GameState;

                if (state == State.GameIdle || state == State.GambleChoice) {
                    idle();
                } else if (GameData.currentGameMode == GameMode.FreeGame) {
                    freeGames();
                } else {
                    idle();
                }
            }
        });

        registerEvent(new ReelStartSpinEvent() {
            @Override
            public void execute(Object... obj) {
                if (GameData.currentGameMode == GameMode.BaseGame) {
                    duringSpin();
                }
            }
        });

        registerEvent(new ReelStoppedEvent() {
            @Override
            public void execute(Object... obj) {
                int state = GameData.getInstance().Context.GameState;
                if (state == State.GameIdle || state == State.GambleChoice) {
                    idle();
                } else if (GameData.currentGameMode == GameMode.FreeGame) {
                    freeGames();
                } else {
                    idle();
                }
            }
        });

        registerEvent(new OutFreeGameIntroEvent() {
            @Override
            public void execute(Object... obj) {
                freeGames();
            }
        });

        registerEvent(new OutFreeGameOutroEvent() {
            @Override
            public void execute(Object... obj) {
                idle();
            }
        });

        registerEvent(new PlayCelebrationEvent() {
            @Override
            public void execute(Object... obj) {
                duringWinCelebrations();
            }
        });

        registerEvent(new StopCelebrationEvent() {
            @Override
            public void execute(Object... obj) {
                int state = GameData.getInstance().Context.GameState;

                if (state == State.GameIdle || state == State.GambleChoice) {
                    idle();
                } else if (GameData.currentGameMode == GameMode.FreeGame) {
                    freeGames();
                }
            }
        });
    }

    private void idle() {

        ledController.Chase(BASE_STRING_ID, idle.Chase.foregroundRed, idle.Chase.foregroundGreen, idle.Chase.foregroundBlue, idle.Chase.backgroundRed, idle.Chase.backgroundGreen, idle.Chase.backgroundBlue, idle.Chase.segmentSize, idle.Chase.segmentIndexIncrement, idle.Chase.activeTimeMs, idle.Chase.delayTimeMs);
        ledController.Fade(L_MONITOR_STRING_ID, idle.getLedFadeCfg("L_MONITOR_STRING_ID").redA,idle.getLedFadeCfg("L_MONITOR_STRING_ID").greenA,idle.getLedFadeCfg("L_MONITOR_STRING_ID").blueA,idle.getLedFadeCfg("L_MONITOR_STRING_ID").redB,idle.getLedFadeCfg("L_MONITOR_STRING_ID").greenB,idle.getLedFadeCfg("L_MONITOR_STRING_ID").blueB,idle.getLedFadeCfg("L_MONITOR_STRING_ID").fadeTimeMs,idle.getLedFadeCfg("L_MONITOR_STRING_ID").delayTimeMs,idle.getLedFadeCfg("L_MONITOR_STRING_ID").reverse);
        ledController.Fade(U_MONITOR_STRING_ID, idle.getLedFadeCfg("U_MONITOR_STRING_ID").redA,idle.getLedFadeCfg("U_MONITOR_STRING_ID").greenA,idle.getLedFadeCfg("U_MONITOR_STRING_ID").blueA,idle.getLedFadeCfg("U_MONITOR_STRING_ID").redB,idle.getLedFadeCfg("U_MONITOR_STRING_ID").greenB,idle.getLedFadeCfg("U_MONITOR_STRING_ID").blueB,idle.getLedFadeCfg("U_MONITOR_STRING_ID").fadeTimeMs,idle.getLedFadeCfg("U_MONITOR_STRING_ID").delayTimeMs,idle.getLedFadeCfg("U_MONITOR_STRING_ID").reverse);
        ledController.Fade(BUTTON_PANEL_STRING_ID, idle.getLedFadeCfg("BUTTON_PANEL_STRING_ID").redA,idle.getLedFadeCfg("BUTTON_PANEL_STRING_ID").greenA,idle.getLedFadeCfg("BUTTON_PANEL_STRING_ID").blueA,idle.getLedFadeCfg("BUTTON_PANEL_STRING_ID").redB,idle.getLedFadeCfg("BUTTON_PANEL_STRING_ID").greenB,idle.getLedFadeCfg("BUTTON_PANEL_STRING_ID").blueB,idle.getLedFadeCfg("BUTTON_PANEL_STRING_ID").fadeTimeMs,idle.getLedFadeCfg("BUTTON_PANEL_STRING_ID").delayTimeMs,idle.getLedFadeCfg("BUTTON_PANEL_STRING_ID").reverse);
        ledController.Fade(BUTTON_LIGHT_RING_STRING_ID, idle.getLedFadeCfg("BUTTON_LIGHT_RING_STRING_ID").redA,idle.getLedFadeCfg("BUTTON_LIGHT_RING_STRING_ID").greenA,idle.getLedFadeCfg("BUTTON_LIGHT_RING_STRING_ID").blueA,idle.getLedFadeCfg("BUTTON_LIGHT_RING_STRING_ID").redB,idle.getLedFadeCfg("BUTTON_LIGHT_RING_STRING_ID").greenB,idle.getLedFadeCfg("BUTTON_LIGHT_RING_STRING_ID").blueB,idle.getLedFadeCfg("BUTTON_LIGHT_RING_STRING_ID").fadeTimeMs,idle.getLedFadeCfg("BUTTON_LIGHT_RING_STRING_ID").delayTimeMs,idle.getLedFadeCfg("BUTTON_LIGHT_RING_STRING_ID").reverse);

        ledController.SetSolidColor(LOGO_STRING_ID, idle.getLedSetSolidColorCfg("LOGO_STRING_ID").red,idle.getLedSetSolidColorCfg("LOGO_STRING_ID").green,idle.getLedSetSolidColorCfg("LOGO_STRING_ID").blue);
        ledController.SetSolidColor(BODY_STRING_ID, idle.getLedSetSolidColorCfg("BODY_STRING_ID").red,idle.getLedSetSolidColorCfg("BODY_STRING_ID").green,idle.getLedSetSolidColorCfg("BODY_STRING_ID").blue);

    }

    private void freeGames() {
        ledController.Chase(BASE_STRING_ID, freeGames.Chase.foregroundRed, freeGames.Chase.foregroundGreen, freeGames.Chase.foregroundBlue, freeGames.Chase.backgroundRed, freeGames.Chase.backgroundGreen, freeGames.Chase.backgroundBlue, freeGames.Chase.segmentSize, freeGames.Chase.segmentIndexIncrement, freeGames.Chase.activeTimeMs, freeGames.Chase.delayTimeMs);

        ledController.Fade(U_MONITOR_STRING_ID, freeGames.getLedFadeCfg("U_MONITOR_STRING_ID").redA,freeGames.getLedFadeCfg("U_MONITOR_STRING_ID").greenA,freeGames.getLedFadeCfg("U_MONITOR_STRING_ID").blueA,freeGames.getLedFadeCfg("U_MONITOR_STRING_ID").redB,freeGames.getLedFadeCfg("U_MONITOR_STRING_ID").greenB,freeGames.getLedFadeCfg("U_MONITOR_STRING_ID").blueB,freeGames.getLedFadeCfg("U_MONITOR_STRING_ID").fadeTimeMs,freeGames.getLedFadeCfg("U_MONITOR_STRING_ID").delayTimeMs,freeGames.getLedFadeCfg("U_MONITOR_STRING_ID").reverse);
        ledController.Fade(L_MONITOR_STRING_ID, freeGames.getLedFadeCfg("L_MONITOR_STRING_ID").redA,freeGames.getLedFadeCfg("L_MONITOR_STRING_ID").greenA,freeGames.getLedFadeCfg("L_MONITOR_STRING_ID").blueA,freeGames.getLedFadeCfg("L_MONITOR_STRING_ID").redB,freeGames.getLedFadeCfg("L_MONITOR_STRING_ID").greenB,freeGames.getLedFadeCfg("L_MONITOR_STRING_ID").blueB,freeGames.getLedFadeCfg("L_MONITOR_STRING_ID").fadeTimeMs,freeGames.getLedFadeCfg("L_MONITOR_STRING_ID").delayTimeMs,freeGames.getLedFadeCfg("L_MONITOR_STRING_ID").reverse);
        ledController.Fade(BUTTON_PANEL_STRING_ID, freeGames.getLedFadeCfg("BUTTON_PANEL_STRING_ID").redA,freeGames.getLedFadeCfg("BUTTON_PANEL_STRING_ID").greenA,freeGames.getLedFadeCfg("BUTTON_PANEL_STRING_ID").blueA,freeGames.getLedFadeCfg("BUTTON_PANEL_STRING_ID").redB,freeGames.getLedFadeCfg("BUTTON_PANEL_STRING_ID").greenB,freeGames.getLedFadeCfg("BUTTON_PANEL_STRING_ID").blueB,freeGames.getLedFadeCfg("BUTTON_PANEL_STRING_ID").fadeTimeMs,freeGames.getLedFadeCfg("BUTTON_PANEL_STRING_ID").delayTimeMs,freeGames.getLedFadeCfg("BUTTON_PANEL_STRING_ID").reverse);
        ledController.Fade(BUTTON_LIGHT_RING_STRING_ID, freeGames.getLedFadeCfg("BUTTON_LIGHT_RING_STRING_ID").redA,freeGames.getLedFadeCfg("BUTTON_LIGHT_RING_STRING_ID").greenA,freeGames.getLedFadeCfg("BUTTON_LIGHT_RING_STRING_ID").blueA,freeGames.getLedFadeCfg("BUTTON_LIGHT_RING_STRING_ID").redB,freeGames.getLedFadeCfg("BUTTON_LIGHT_RING_STRING_ID").greenB,freeGames.getLedFadeCfg("BUTTON_LIGHT_RING_STRING_ID").blueB,freeGames.getLedFadeCfg("BUTTON_LIGHT_RING_STRING_ID").fadeTimeMs,freeGames.getLedFadeCfg("BUTTON_LIGHT_RING_STRING_ID").delayTimeMs,freeGames.getLedFadeCfg("BUTTON_LIGHT_RING_STRING_ID").reverse);
        ledController.Fade(LOGO_STRING_ID, freeGames.getLedFadeCfg("LOGO_STRING_ID").redA,freeGames.getLedFadeCfg("LOGO_STRING_ID").greenA,freeGames.getLedFadeCfg("LOGO_STRING_ID").blueA,freeGames.getLedFadeCfg("LOGO_STRING_ID").redB,freeGames.getLedFadeCfg("LOGO_STRING_ID").greenB,freeGames.getLedFadeCfg("LOGO_STRING_ID").blueB,freeGames.getLedFadeCfg("LOGO_STRING_ID").fadeTimeMs,freeGames.getLedFadeCfg("LOGO_STRING_ID").delayTimeMs,freeGames.getLedFadeCfg("LOGO_STRING_ID").reverse);

        ledController.SetSolidColor(BODY_STRING_ID, freeGames.SetSolidColor.red, freeGames.SetSolidColor.green, freeGames.SetSolidColor.blue);
    }

    private void duringSpin() {
        ledController.Chase(BASE_STRING_ID, duringSpin.Chase.foregroundRed, duringSpin.Chase.foregroundGreen, duringSpin.Chase.foregroundBlue, duringSpin.Chase.backgroundRed, duringSpin.Chase.backgroundGreen, duringSpin.Chase.backgroundBlue, duringSpin.Chase.segmentSize, duringSpin.Chase.segmentIndexIncrement, duringSpin.Chase.activeTimeMs, duringSpin.Chase.delayTimeMs);

        ledController.Fade(L_MONITOR_STRING_ID, duringSpin.getLedFadeCfg("L_MONITOR_STRING_ID").redA,duringSpin.getLedFadeCfg("L_MONITOR_STRING_ID").greenA,duringSpin.getLedFadeCfg("L_MONITOR_STRING_ID").blueA,duringSpin.getLedFadeCfg("L_MONITOR_STRING_ID").redB,duringSpin.getLedFadeCfg("L_MONITOR_STRING_ID").greenB,duringSpin.getLedFadeCfg("L_MONITOR_STRING_ID").blueB,duringSpin.getLedFadeCfg("L_MONITOR_STRING_ID").fadeTimeMs,duringSpin.getLedFadeCfg("L_MONITOR_STRING_ID").delayTimeMs,duringSpin.getLedFadeCfg("L_MONITOR_STRING_ID").reverse);
        ledController.Fade(U_MONITOR_STRING_ID, duringSpin.getLedFadeCfg("U_MONITOR_STRING_ID").redA,duringSpin.getLedFadeCfg("U_MONITOR_STRING_ID").greenA,duringSpin.getLedFadeCfg("U_MONITOR_STRING_ID").blueA,duringSpin.getLedFadeCfg("U_MONITOR_STRING_ID").redB,duringSpin.getLedFadeCfg("U_MONITOR_STRING_ID").greenB,duringSpin.getLedFadeCfg("U_MONITOR_STRING_ID").blueB,duringSpin.getLedFadeCfg("U_MONITOR_STRING_ID").fadeTimeMs,duringSpin.getLedFadeCfg("U_MONITOR_STRING_ID").delayTimeMs,duringSpin.getLedFadeCfg("U_MONITOR_STRING_ID").reverse);
        ledController.Fade(BUTTON_PANEL_STRING_ID, duringSpin.getLedFadeCfg("BUTTON_PANEL_STRING_ID").redA,duringSpin.getLedFadeCfg("BUTTON_PANEL_STRING_ID").greenA,duringSpin.getLedFadeCfg("BUTTON_PANEL_STRING_ID").blueA,duringSpin.getLedFadeCfg("BUTTON_PANEL_STRING_ID").redB,duringSpin.getLedFadeCfg("BUTTON_PANEL_STRING_ID").greenB,duringSpin.getLedFadeCfg("BUTTON_PANEL_STRING_ID").blueB,duringSpin.getLedFadeCfg("BUTTON_PANEL_STRING_ID").fadeTimeMs,duringSpin.getLedFadeCfg("BUTTON_PANEL_STRING_ID").delayTimeMs,duringSpin.getLedFadeCfg("BUTTON_PANEL_STRING_ID").reverse);
        ledController.Fade(BUTTON_LIGHT_RING_STRING_ID, duringSpin.getLedFadeCfg("BUTTON_LIGHT_RING_STRING_ID").redA,duringSpin.getLedFadeCfg("BUTTON_LIGHT_RING_STRING_ID").greenA,duringSpin.getLedFadeCfg("BUTTON_LIGHT_RING_STRING_ID").blueA,duringSpin.getLedFadeCfg("BUTTON_LIGHT_RING_STRING_ID").redB,duringSpin.getLedFadeCfg("BUTTON_LIGHT_RING_STRING_ID").greenB,duringSpin.getLedFadeCfg("BUTTON_LIGHT_RING_STRING_ID").blueB,duringSpin.getLedFadeCfg("BUTTON_LIGHT_RING_STRING_ID").fadeTimeMs,duringSpin.getLedFadeCfg("BUTTON_LIGHT_RING_STRING_ID").delayTimeMs,duringSpin.getLedFadeCfg("BUTTON_LIGHT_RING_STRING_ID").reverse);

        ledController.SetSolidColor(LOGO_STRING_ID, duringSpin.getLedSetSolidColorCfg("LOGO_STRING_ID").red,duringSpin.getLedSetSolidColorCfg("LOGO_STRING_ID").green,duringSpin.getLedSetSolidColorCfg("LOGO_STRING_ID").blue);
        ledController.SetSolidColor(BODY_STRING_ID, duringSpin.getLedSetSolidColorCfg("BODY_STRING_ID").red,duringSpin.getLedSetSolidColorCfg("BODY_STRING_ID").green,duringSpin.getLedSetSolidColorCfg("BODY_STRING_ID").blue);

    }

    private void duringWinCelebrations() {
        ledController.Fade(BASE_STRING_ID, duringWinCelebrations.getLedFadeCfg("BASE_STRING_ID").redA,duringWinCelebrations.getLedFadeCfg("BASE_STRING_ID").greenA,duringWinCelebrations.getLedFadeCfg("BASE_STRING_ID").blueA,duringWinCelebrations.getLedFadeCfg("BASE_STRING_ID").redB,duringWinCelebrations.getLedFadeCfg("BASE_STRING_ID").greenB,duringWinCelebrations.getLedFadeCfg("BASE_STRING_ID").blueB,duringWinCelebrations.getLedFadeCfg("BASE_STRING_ID").fadeTimeMs,duringWinCelebrations.getLedFadeCfg("BASE_STRING_ID").delayTimeMs,duringWinCelebrations.getLedFadeCfg("BASE_STRING_ID").reverse);
        ledController.Fade(L_MONITOR_STRING_ID, duringWinCelebrations.getLedFadeCfg("L_MONITOR_STRING_ID").redA,duringWinCelebrations.getLedFadeCfg("L_MONITOR_STRING_ID").greenA,duringWinCelebrations.getLedFadeCfg("L_MONITOR_STRING_ID").blueA,duringWinCelebrations.getLedFadeCfg("L_MONITOR_STRING_ID").redB,duringWinCelebrations.getLedFadeCfg("L_MONITOR_STRING_ID").greenB,duringWinCelebrations.getLedFadeCfg("L_MONITOR_STRING_ID").blueB,duringWinCelebrations.getLedFadeCfg("L_MONITOR_STRING_ID").fadeTimeMs,duringWinCelebrations.getLedFadeCfg("L_MONITOR_STRING_ID").delayTimeMs,duringWinCelebrations.getLedFadeCfg("L_MONITOR_STRING_ID").reverse);
        ledController.Fade(U_MONITOR_STRING_ID, duringWinCelebrations.getLedFadeCfg("U_MONITOR_STRING_ID").redA,duringWinCelebrations.getLedFadeCfg("U_MONITOR_STRING_ID").greenA,duringWinCelebrations.getLedFadeCfg("U_MONITOR_STRING_ID").blueA,duringWinCelebrations.getLedFadeCfg("U_MONITOR_STRING_ID").redB,duringWinCelebrations.getLedFadeCfg("U_MONITOR_STRING_ID").greenB,duringWinCelebrations.getLedFadeCfg("U_MONITOR_STRING_ID").blueB,duringWinCelebrations.getLedFadeCfg("U_MONITOR_STRING_ID").fadeTimeMs,duringWinCelebrations.getLedFadeCfg("U_MONITOR_STRING_ID").delayTimeMs,duringWinCelebrations.getLedFadeCfg("U_MONITOR_STRING_ID").reverse);
        ledController.Fade(BUTTON_PANEL_STRING_ID, duringWinCelebrations.getLedFadeCfg("BUTTON_PANEL_STRING_ID").redA,duringWinCelebrations.getLedFadeCfg("BUTTON_PANEL_STRING_ID").greenA,duringWinCelebrations.getLedFadeCfg("BUTTON_PANEL_STRING_ID").blueA,duringWinCelebrations.getLedFadeCfg("BUTTON_PANEL_STRING_ID").redB,duringWinCelebrations.getLedFadeCfg("BUTTON_PANEL_STRING_ID").greenB,duringWinCelebrations.getLedFadeCfg("BUTTON_PANEL_STRING_ID").blueB,duringWinCelebrations.getLedFadeCfg("BUTTON_PANEL_STRING_ID").fadeTimeMs,duringWinCelebrations.getLedFadeCfg("BUTTON_PANEL_STRING_ID").delayTimeMs,duringWinCelebrations.getLedFadeCfg("BUTTON_PANEL_STRING_ID").reverse);
        ledController.Fade(BUTTON_LIGHT_RING_STRING_ID, duringWinCelebrations.getLedFadeCfg("BUTTON_LIGHT_RING_STRING_ID").redA,duringWinCelebrations.getLedFadeCfg("BUTTON_LIGHT_RING_STRING_ID").greenA,duringWinCelebrations.getLedFadeCfg("BUTTON_LIGHT_RING_STRING_ID").blueA,duringWinCelebrations.getLedFadeCfg("BUTTON_LIGHT_RING_STRING_ID").redB,duringWinCelebrations.getLedFadeCfg("BUTTON_LIGHT_RING_STRING_ID").greenB,duringWinCelebrations.getLedFadeCfg("BUTTON_LIGHT_RING_STRING_ID").blueB,duringWinCelebrations.getLedFadeCfg("BUTTON_LIGHT_RING_STRING_ID").fadeTimeMs,duringWinCelebrations.getLedFadeCfg("BUTTON_LIGHT_RING_STRING_ID").delayTimeMs,duringWinCelebrations.getLedFadeCfg("BUTTON_LIGHT_RING_STRING_ID").reverse);
        ledController.Fade(LOGO_STRING_ID, duringWinCelebrations.getLedFadeCfg("LOGO_STRING_ID").redA,duringWinCelebrations.getLedFadeCfg("LOGO_STRING_ID").greenA,duringWinCelebrations.getLedFadeCfg("LOGO_STRING_ID").blueA,duringWinCelebrations.getLedFadeCfg("LOGO_STRING_ID").redB,duringWinCelebrations.getLedFadeCfg("LOGO_STRING_ID").greenB,duringWinCelebrations.getLedFadeCfg("LOGO_STRING_ID").blueB,duringWinCelebrations.getLedFadeCfg("LOGO_STRING_ID").fadeTimeMs,duringWinCelebrations.getLedFadeCfg("LOGO_STRING_ID").delayTimeMs,duringWinCelebrations.getLedFadeCfg("LOGO_STRING_ID").reverse);
        ledController.Fade(BODY_STRING_ID, duringWinCelebrations.getLedFadeCfg("BODY_STRING_ID").redA,duringWinCelebrations.getLedFadeCfg("BODY_STRING_ID").greenA,duringWinCelebrations.getLedFadeCfg("BODY_STRING_ID").blueA,duringWinCelebrations.getLedFadeCfg("BODY_STRING_ID").redB,duringWinCelebrations.getLedFadeCfg("BODY_STRING_ID").greenB,duringWinCelebrations.getLedFadeCfg("BODY_STRING_ID").blueB,duringWinCelebrations.getLedFadeCfg("BODY_STRING_ID").fadeTimeMs,duringWinCelebrations.getLedFadeCfg("BODY_STRING_ID").delayTimeMs,duringWinCelebrations.getLedFadeCfg("BODY_STRING_ID").reverse);
    }
}
