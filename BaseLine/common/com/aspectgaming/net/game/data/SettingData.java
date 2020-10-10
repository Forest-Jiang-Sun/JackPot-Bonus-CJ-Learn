// Generated by http://code.google.com/p/protostuff/ ... DO NOT EDIT!
// Generated from GameProtocol.proto

package com.aspectgaming.net.game.data;

import com.aspectgaming.net.ProtoUtil;
import io.protostuff.*;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.List;

public final class SettingData implements Externalizable, Message<SettingData>, Schema<SettingData> {

    public static Schema<SettingData> getSchema() {
        return DEFAULT_INSTANCE;
    }

    public static SettingData getDefaultInstance() {
        return DEFAULT_INSTANCE;
    }

    static final SettingData DEFAULT_INSTANCE = new SettingData();

    public String Currency;
    public boolean GambleEnabled;
    public int GambleMaxRound;
    public long GambleLimit;
    public int ProgressiveType;
    public long[] ProgressiveSeeds;
    public long[] ProgressiveMaxs;
    public int[] Denominations;
    public int[] ScreenResolution;
    public String[] ScreenButtons;
    public List<String> Games;
    public String SelectedGame;
    public int GameFeature;
    public int MinSelections;
    public int MaxSelections;
    public int MinPaylines;
    public int MaxPaylines;
    public int MinBetMultiplier;
    public int MaxBetMultiplier;
    public long MinBet;
    public long MaxBet;
    public LinkedMediaData LinkedMedia;

    public SettingData() {}

    @Override
    public String toString() {
        return "SettingData{" +
                    "currency=" + Currency +
                    ", gambleEnabled=" + GambleEnabled +
                    ", gambleMaxRound=" + GambleMaxRound +
                    ", gambleLimit=" + GambleLimit +
                    ", progressiveType=" + ProgressiveType +
                    ", progressiveSeeds=" + ProgressiveSeeds +
                    ", progressiveMaxs=" + ProgressiveMaxs +
                    ", denominations=" + Denominations +
                    ", screenResolution=" + ScreenResolution +
                    ", screenButtons=" + ScreenButtons +
                    ", games=" + Games +
                    ", selectedGame=" + SelectedGame +
                    ", gameFeature=" + GameFeature +
                    ", minSelections=" + MinSelections +
                    ", maxSelections=" + MaxSelections +
                    ", minPaylines=" + MinPaylines +
                    ", maxPaylines=" + MaxPaylines +
                    ", minBetMultiplier=" + MinBetMultiplier +
                    ", maxBetMultiplier=" + MaxBetMultiplier +
                    ", minBet=" + MinBet +
                    ", maxBet=" + MaxBet +
                    ", linkedMedia=" + LinkedMedia +
                '}';
    }

    // java serialization

    public void readExternal(ObjectInput in) throws IOException {
        GraphIOUtil.mergeDelimitedFrom(in, this, this);
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        GraphIOUtil.writeDelimitedTo(out, this, this);
    }

    // message method

    public Schema<SettingData> cachedSchema() {
        return DEFAULT_INSTANCE;
    }

    // schema methods

    public SettingData newMessage() {
        return new SettingData();
    }

    public Class<SettingData> typeClass() {
        return SettingData.class;
    }

    public String messageName() {
        return SettingData.class.getSimpleName();
    }

    public String messageFullName() {
        return SettingData.class.getName();
    }

    public boolean isInitialized(SettingData message) {
        return true;
    }

    public void mergeFrom(Input input, SettingData message) throws IOException {
        List<Integer> ScreenResolution = null;
        List<Integer> Denominations = null;
        List<Long> ProgressiveSeeds = null;
        List<Long> ProgressiveMaxs = null;
        List<String> ScreenButtons = null;

        try {
            for (int number = input.readFieldNumber(this);; number = input.readFieldNumber(this)) {
                switch (number) {
                case 0:
                    return;
                case 1:
                    message.Currency = input.readString();
                    break;
                case 2:
                    message.GambleEnabled = input.readBool();
                    break;
                case 3:
                    message.ProgressiveType = input.readInt32();
                    break;
                case 4:
                    if (ProgressiveSeeds == null) ProgressiveSeeds = new ArrayList<Long>();
                    ProgressiveSeeds.add(input.readInt64());
                    break;
                case 5:
                    if (ProgressiveMaxs == null) ProgressiveMaxs = new ArrayList<Long>();
                    ProgressiveMaxs.add(input.readInt64());
                    break;
                case 6:
                    if (Denominations == null) Denominations = new ArrayList<Integer>();
                    Denominations.add(input.readInt32());
                    break;
                case 7:
                    if (ScreenResolution == null) ScreenResolution = new ArrayList<Integer>();
                    ScreenResolution.add(input.readInt32());
                    break;
                case 8:
                    if (ScreenButtons == null) ScreenButtons = new ArrayList<String>();
                    ScreenButtons.add(input.readString());
                    break;
                case 9:
                    if (message.Games == null) message.Games = new ArrayList<String>();
                    message.Games.add(input.readString());
                    break;
                case 10:
                    message.SelectedGame = input.readString();
                    break;
                case 11:
                    message.GameFeature = input.readInt32();
                    break;
                case 20:
                    message.MinSelections = input.readInt32();
                    break;
                case 21:
                    message.MaxSelections = input.readInt32();
                    break;
                case 22:
                    message.MinPaylines = input.readInt32();
                    break;
                case 23:
                    message.MaxPaylines = input.readInt32();
                    break;
                case 24:
                    message.MinBetMultiplier = input.readInt32();
                    break;
                case 25:
                    message.MaxBetMultiplier = input.readInt32();
                    break;
                case 26:
                    message.MinBet = input.readInt64();
                    break;
                case 27:
                    message.MaxBet = input.readInt64();
                    break;
                case 30:
                    message.LinkedMedia = input.mergeObject(message.LinkedMedia, LinkedMediaData.getSchema());
                    break;
                case 31:
                    message.GambleMaxRound = input.readInt32();
                    break;
                case 32:
                    message.GambleLimit = input.readInt64();
                    break;

                default:
                    input.handleUnknownField(number, this);
                }
            }
        } finally {
            message.ProgressiveSeeds = ProtoUtil.toLongArray(ProgressiveSeeds);
            message.ProgressiveMaxs = ProtoUtil.toLongArray(ProgressiveMaxs);
            message.Denominations = ProtoUtil.toIntArray(Denominations);
            message.ScreenResolution = ProtoUtil.toIntArray(ScreenResolution);
            message.ScreenButtons = ProtoUtil.toStringArray(ScreenButtons);
            if (message.Games != null) message.Games = java.util.Collections.unmodifiableList(message.Games);
            else message.Games = java.util.Collections.emptyList();
        }
    }

    public void writeTo(Output output, SettingData message) throws IOException {
        if (message.Currency != null) output.writeString(1, message.Currency, false);

        if (message.GambleEnabled) output.writeBool(2, message.GambleEnabled, false);

        if (message.ProgressiveType != 0) output.writeInt32(3, message.ProgressiveType, false);

        if (message.ProgressiveSeeds != null) {
            for (Long progressiveSeeds : message.ProgressiveSeeds) {
                if (progressiveSeeds != null) output.writeInt64(4, progressiveSeeds, true);
            }
        }

        if (message.ProgressiveMaxs != null) {
            for (Long progressiveMaxs : message.ProgressiveMaxs) {
                if (progressiveMaxs != null) output.writeInt64(5, progressiveMaxs, true);
            }
        }

        if (message.Denominations != null) {
            for (Integer denominations : message.Denominations) {
                if (denominations != null) output.writeInt32(6, denominations, true);
            }
        }

        if (message.ScreenResolution != null) {
            for (Integer screenResolution : message.ScreenResolution) {
                if (screenResolution != null) output.writeInt32(7, screenResolution, true);
            }
        }

        if (message.ScreenButtons != null) {
            for (String screenButtons : message.ScreenButtons) {
                if (screenButtons != null) output.writeString(8, screenButtons, true);
            }
        }

        if (message.Games != null) {
            for (String games : message.Games) {
                if (games != null) output.writeString(9, games, true);
            }
        }

        if (message.SelectedGame != null) output.writeString(10, message.SelectedGame, false);

        if (message.GameFeature != 0) output.writeInt32(11, message.GameFeature, false);

        if (message.MinSelections != 0) output.writeInt32(20, message.MinSelections, false);

        if (message.MaxSelections != 0) output.writeInt32(21, message.MaxSelections, false);

        if (message.MinPaylines != 0) output.writeInt32(22, message.MinPaylines, false);

        if (message.MaxPaylines != 0) output.writeInt32(23, message.MaxPaylines, false);

        if (message.MinBetMultiplier != 0) output.writeInt32(24, message.MinBetMultiplier, false);

        if (message.MaxBetMultiplier != 0) output.writeInt32(25, message.MaxBetMultiplier, false);

        if (message.MinBet != 0) output.writeInt64(26, message.MinBet, false);

        if (message.MaxBet != 0) output.writeInt64(27, message.MaxBet, false);

        if (message.LinkedMedia != null) output.writeObject(30, message.LinkedMedia, LinkedMediaData.getSchema(), false);

        if (message.GambleMaxRound != 0) output.writeInt32(31, message.GambleMaxRound, false);

        if (message.GambleLimit != 0) output.writeInt64(32, message.GambleLimit, false);

    }

    public String getFieldName(int number) {
        return Integer.toString(number);
    }

    public int getFieldNumber(String name) {
        return Integer.parseInt(name);
    }

}