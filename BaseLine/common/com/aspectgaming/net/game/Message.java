package com.aspectgaming.net.game;

import com.aspectgaming.net.game.data.ProtocolTypes;

public class Message {

    public final ProtocolTypes type;
    public final Object message;

    public Message(ProtocolTypes type, Object message) {
        this.type = type;
        this.message = message;
    }
}
