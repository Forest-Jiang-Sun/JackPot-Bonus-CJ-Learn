package com.aspectgaming.media.onewall.net;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtobufIOUtil;
import io.protostuff.Schema;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aspectgaming.common.data.GameData;
import com.aspectgaming.media.onewall.data.MediaData;
import com.aspectgaming.media.onewall.data.ProtocolTypes;
import com.aspectgaming.media.onewall.data.SettingData;
import com.aspectgaming.net.game.data.LinkedMediaData;

public class MessageUdpDecoder extends MessageToMessageDecoder<DatagramPacket> {

    private final LinkedBuffer buffer;
    private final Logger log = LoggerFactory.getLogger(MessageUdpDecoder.class);

    public MessageUdpDecoder(int maxFrameLength) {
        buffer = LinkedBuffer.allocate(maxFrameLength);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, DatagramPacket pkt, List<Object> out) throws Exception {
        LinkedMediaData cfg = GameData.getInstance().Setting.LinkedMedia;

        if (cfg == null || !cfg.Enabled) return;

        ByteBuf data = pkt.content();

        try {
            int magicNum = data.readInt();
            data.readInt(); // read length which is useless now;

            ProtocolTypes type = ProtocolTypes.valueOf(magicNum);
            if (type == null) {
                log.warn("Unsupported ProtocolTypes code: {}", magicNum);
                return;
            }

            // log.info("received ProtocolTypes: {}", type);

            Message msg = null;
            try (ByteBufInputStream input = new ByteBufInputStream(data)) {
                msg = readMsg(type, input, ctx.channel());
            }

            if (msg != null) {
                out.add(msg);
            }
        } catch (Exception e) {
            // ignore corrupted message
        }
    }

    @SuppressWarnings("unchecked")
    private Message readMsg(ProtocolTypes type, InputStream content, Channel channel) throws IOException {
        // long time = System.nanoTime();
        Object obj = null;

        switch (type) {

        case SETTING_S2C:
            obj = new SettingData();
            break;

        case MEDIA_S2C:
            obj = new MediaData();
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
