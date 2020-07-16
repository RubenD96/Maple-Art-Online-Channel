package client.messages.broadcast.types;

import client.messages.broadcast.AbstractBroadcastMessage;
import client.messages.broadcast.BroadcastMessageType;
import util.packet.PacketWriter;

/**
 * Color: Pink
 * Prefix:
 */
public class EventMessage extends AbstractBroadcastMessage {

    private final String message;

    public EventMessage(String message) {
        this.message = message;
    }

    @Override
    public BroadcastMessageType getType() {
        return BroadcastMessageType.EVENT;
    }

    @Override
    protected void encodeData(PacketWriter pw) {
        pw.writeMapleString(message);
    }
}