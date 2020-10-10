package com.aspectgaming.common.data;

import com.aspectgaming.net.game.data.ContextData;
import com.aspectgaming.net.game.data.MathParamsData;
import com.aspectgaming.net.game.data.PaytableData;
import com.aspectgaming.net.game.data.ProgressiveValuesData;
import com.aspectgaming.net.game.data.ReelStripsData;
import com.aspectgaming.net.game.data.SettingData;
import com.sun.jna.platform.win32.WinDef.HWND;

/**
 * @author ligang.yao
 */
public class GameData {

    public static int reelStopCount;
    public static boolean isReelStopped;
    public static int dignosticReelIndex;
    public static int specialSelection;
    public static boolean isFreeIntroPlaying;
    public static boolean isTransitionInPlaying;

    private static final GameData instance = new GameData();
    private static final GameData previous = new GameData();

    public static GameData getInstance() {
        return instance;
    }

    public static GameData getPrevious() {
        return previous;
    }

    public static HWND Window;
    public static String Screen;
    public static GameMode currentGameMode = GameMode.BaseGame;
    public static Currency Currency;

    private static int volumeLevel = 1;
    private static int maxVolumeLevel = 3;
    public static float Volume = ((float) volumeLevel) / maxVolumeLevel;

    public SettingData Setting;
    public ContextData Context;
    public ContextData GameRecall;
    public MathParamsData MathParams;
    public PaytableData Paytable;
    public ProgressiveValuesData ProgressiveValues;
    public ReelStripsData ReelStrips;
    private static final int[]  BetMultipler = {1, 3, 5, 10, 20};
    private static final int[]  Lines = {1, 2, 3, 4, 5};
    private static final int BetAmountOneMultipler = 20;
    public boolean isTilt() {
        // DelayGame is not a tilt state and should not cause screen freezing
        return Context.State != Context.GameState && Context.State != State.DelayGame;
    }

    public boolean isWaysGame() {
        return (Setting.GameFeature & 1) != 0;
    }

    public int[] getBetMultipler() {
        return BetMultipler;
    }

    public int[] getLines(){
        return Lines;
    }

    public int getBetAmountOneMultipler() {
        return BetAmountOneMultipler;
    }

    public int getBetAmount(int betIndex) {
        return BetMultipler[betIndex] * BetAmountOneMultipler;
    }

    public static float getVolume() {
        return Volume;
    }

    public static void setMaxVolumeLevel(int val) {
        maxVolumeLevel = val;
        Volume = ((float) volumeLevel) / maxVolumeLevel;
    }

    public static int getVolumeLevel() {
        return volumeLevel;
    }

    public static void increaseVolume() {
        if (volumeLevel >= GameData.maxVolumeLevel) {
            volumeLevel = 0;
        } else {
            volumeLevel++;
        }
        Volume = ((float) volumeLevel) / maxVolumeLevel;
    }

    public static void setVolumeLevel(int val) {
        volumeLevel = val;
        Volume = ((float) volumeLevel) / maxVolumeLevel;
    }
}
