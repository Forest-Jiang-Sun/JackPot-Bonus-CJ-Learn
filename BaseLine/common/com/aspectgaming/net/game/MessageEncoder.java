package com.aspectgaming.net.game;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtobufIOUtil;
import io.protostuff.Schema;

import java.io.OutputStream;

public class MessageEncoder extends MessageToByteEncoder<Message> {

    private final LinkedBuffer buffer;

    // private final Logger log = LoggerFactory.getLogger(ProtobufEncoder.class);

    public MessageEncoder(int maxFrameLength) {
        buffer = LinkedBuffer.allocate(maxFrameLength);
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, ByteBuf out) throws Exception {
        // long time = System.nanoTime();
        try {
            if (msg.message != null) {
                @SuppressWarnings("unchecked")
                int bodyLen = ProtobufIOUtil.writeTo(buffer, msg.message, (Schema<Object>) msg.message);

                out.ensureWritable(8 + bodyLen);

                out.writeInt(msg.type.number);
                out.writeInt(bodyLen);

                try (OutputStream output = new ByteBufOutputStream(out)) {
                    LinkedBuffer.writeTo(output, buffer);
                }
            } else {
                out.ensureWritable(8);

                out.writeInt(msg.type.number);
                out.writeInt(0);
            }

        } finally {
            buffer.clear();
        }
        // log.info("{} message encoded within {}ns", msg.type, (System.nanoTime() - time));
    }
}
