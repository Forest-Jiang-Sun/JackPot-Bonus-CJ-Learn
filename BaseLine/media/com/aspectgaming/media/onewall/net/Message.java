package com.aspectgaming.media.onewall.net;

import com.aspectgaming.media.onewall.data.ProtocolTypes;

public class Message {

    public final ProtocolTypes type;
    public final Object message;

    public Message(ProtocolTypes type, Object message) {
        this.type = type;
        this.message = message;
    }
}
