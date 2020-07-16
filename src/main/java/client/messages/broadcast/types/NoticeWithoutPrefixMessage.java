package client.messages.broadcast.types;

import client.messages.broadcast.AbstractBroadcastMessage;
import client.messages.broadcast.BroadcastMessageType;
import util.packet.PacketWriter;

/**
 * Color: Blue
 * Prefix:
 */
public class NoticeWithoutPrefixMessage extends AbstractBroadcastMessage {

    private final String message;

    public NoticeWithoutPrefixMessage(String message) {
        this.message = message;
    }

    @Override
    public BroadcastMessageType getType() {
        return BroadcastMessageType.NOTICE_WITHOUT_PREFIX;
    }

    @Override
    protected void encodeData(PacketWriter pw) {
        pw.writeMapleString(message);
        pw.writeInt(0);
    }
}