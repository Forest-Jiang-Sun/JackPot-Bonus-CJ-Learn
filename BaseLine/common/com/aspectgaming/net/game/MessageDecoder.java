package com.aspectgaming.net.game;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtobufIOUtil;
import io.protostuff.Schema;

import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aspectgaming.net.game.data.CommandData;
import com.aspectgaming.net.game.data.ContextData;
import com.aspectgaming.net.game.data.GameRecallData;
import com.aspectgaming.net.game.data.GameResetData;
import com.aspectgaming.net.game.data.PaytableData;
import com.aspectgaming.net.game.data.ProgressiveValuesData;
import com.aspectgaming.net.game.data.ProtocolTypes;
import com.aspectgaming.net.game.data.RegisterData;
import com.aspectgaming.net.game.data.SettingData;

public class MessageDecoder extends LengthFieldBasedFrameDecoder {

    private final LinkedBuffer buffer;
    private final Logger log = LoggerFactory.getLogger(MessageDecoder.class);

    public MessageDecoder(int maxFrameLength) {
        super(maxFrameLength, 4, 4, 0, 0);
        buffer = LinkedBuffer.allocate(maxFrameLength);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        ByteBuf frame = (ByteBuf) super.decode(ctx, in);
        if (frame == null) return null;

        int magicNum = frame.readInt();
        frame.readInt(); // read length which is useless now;

        ProtocolTypes type = ProtocolTypes.valueOf(magicNum);
        if (type == null) {
            log.warn("Unsupported ProtocolTypes code: {}", magicNum);
            return null;
        }

        // log.info("received ProtocolTypes: {}", type);

        if (ProtocolTypes.HEART_BEAT == type) {
            return readMsg(type, null, ctx.channel());
        } else {
            try (ByteBufInputStream input = new ByteBufInputStream(frame)) {
                return readMsg(type, input, ctx.channel());
            }
        }
    }

    @Override
    protected ByteBuf extractFrame(ChannelHandlerContext ctx, ByteBuf buffer, int index, int length) {
        return buffer.slice(index, length);
    }

    @SuppressWarnings("unchecked")
    private Message readMsg(ProtocolTypes type, InputStream content, Channel channel) throws IOException {
        // long time = System.nanoTime();
        Object obj = null;

        switch (type) {
        case HEART_BEAT:
            break;

        case COMMAND:
            obj = new CommandData();
            break;

        case REGISTER_C2S:
            obj = new RegisterData();
            break;

        case CONTEXT_S2C:
            obj = new ContextData();
            break;

        case GAME_RECALL_S2C:
            obj = new GameRecallData();
            break;

        case PAYTABLE_S2C:
            obj = new PaytableData();
            break;

        case PROGRESSIVE_S2C:
            obj = new ProgressiveValuesData();
            break;

        case RESET_S2C:
            obj = new GameResetData();
            break;

        case SETTING_S2C:
            obj = new SettingData();
            break;

        default:
            log.warn("Unhandled ProtocolTypes: {}", type);
            break;
        }

        if (obj == null) return null;

        ProtobufIOUtil.mergeFrom(content, obj, (Schema<Object>) obj, buffer);
        // log.info("Received: {} within {}ns", obj, (System.nanoTime() - time));
        return new Message(type, obj);
    }
}
