package client.messages.broadcast.types;

import client.messages.broadcast.AbstractBroadcastMessage;
import client.messages.broadcast.BroadcastMessageType;
import util.packet.PacketWriter;

/**
 * Color: Blue
 * Prefix: [Notice]
 */
public class NoticeMessage extends AbstractBroadcastMessage {

    private final String message;

    public NoticeMessage(String message) {
        this.message = message;
    }

    @Override
    public BroadcastMessageType getType() {
        return BroadcastMessageType.NOTICE;
    }

    @Override
    protected void encodeData(PacketWriter pw) {
        pw.writeMapleString(message);
    }
}