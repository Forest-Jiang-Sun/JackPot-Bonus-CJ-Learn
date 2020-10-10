// Generated by http://code.google.com/p/protostuff/ ... DO NOT EDIT!
// Generated from GameProtocol.proto

package com.aspectgaming.net.game.data;

import io.protostuff.GraphIOUtil;
import io.protostuff.Input;
import io.protostuff.Message;
import io.protostuff.Output;
import io.protostuff.Schema;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public final class GameResetData implements Externalizable, Message<GameResetData>, Schema<GameResetData> {

    public static Schema<GameResetData> getSchema() {
        return DEFAULT_INSTANCE;
    }

    public static GameResetData getDefaultInstance() {
        return DEFAULT_INSTANCE;
    }

    static final GameResetData DEFAULT_INSTANCE = new GameResetData();

    public SettingData Setting;
    public ContextData Context;
    public PaytableData Paytable;
    public ProgressiveValuesData ProgressiveValues;
    public ReelStripsData ReelStrips;

    public GameResetData() {}

    @Override
    public String toString() {
        return "GameResetData{" +
                    "setting=" + Setting +
                    ", context=" + Context +
                    ", paytable=" + Paytable +
                    ", progressiveValues=" + ProgressiveValues +
                    ", reelStrips=" + ReelStrips +
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

    public Schema<GameResetData> cachedSchema() {
        return DEFAULT_INSTANCE;
    }

    // schema methods

    public GameResetData newMessage() {
        return new GameResetData();
    }

    public Class<GameResetData> typeClass() {
        return GameResetData.class;
    }

    public String messageName() {
        return GameResetData.class.getSimpleName();
    }

    public String messageFullName() {
        return GameResetData.class.getName();
    }

    public boolean isInitialized(GameResetData message) {
        return true;
    }

    public void mergeFrom(Input input, GameResetData message) throws IOException {
        for (int number = input.readFieldNumber(this);; number = input.readFieldNumber(this)) {
            switch (number) {
            case 0:
                return;
            case 1:
                message.Setting = input.mergeObject(message.Setting, SettingData.getSchema());
                break;

            case 2:
                message.Context = input.mergeObject(message.Context, ContextData.getSchema());
                break;

            case 3:
                message.Paytable = input.mergeObject(message.Paytable, PaytableData.getSchema());
                break;

            case 4:
                message.ProgressiveValues = input.mergeObject(message.ProgressiveValues, ProgressiveValuesData.getSchema());
                break;

            case 5:
                message.ReelStrips = input.mergeObject(message.ReelStrips, ReelStripsData.getSchema());
                break;

            default:
                input.handleUnknownField(number, this);
            }
        }
    }

    public void writeTo(Output output, GameResetData message) throws IOException {
        if (message.Setting != null) output.writeObject(1, message.Setting, SettingData.getSchema(), false);

        if (message.Context != null) output.writeObject(2, message.Context, ContextData.getSchema(), false);

        if (message.Paytable != null) output.writeObject(3, message.Paytable, PaytableData.getSchema(), false);

        if (message.ProgressiveValues != null) output.writeObject(4, message.ProgressiveValues, ProgressiveValuesData.getSchema(), false);

        if (message.ReelStrips != null) output.writeObject(5, message.ReelStrips, ReelStripsData.getSchema(), false);

    }

    public String getFieldName(int number) {
        return Integer.toString(number);
    }

    public int getFieldNumber(String name) {
        return Integer.parseInt(name);
    }

}