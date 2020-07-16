package client.messages.broadcast.types;

import client.messages.broadcast.AbstractBroadcastMessage;
import client.messages.broadcast.BroadcastMessageType;
import util.packet.PacketWriter;

/**
 * Color:
 * Prefix:
 * <p>
 * Alert popup
 */
public class AlertMessage extends AbstractBroadcastMessage {

    private final String message;

    public AlertMessage(String message) {
        this.message = message;
    }

    @Override
    public BroadcastMessageType getType() {
        return BroadcastMessageType.ALERT;
    }

    @Override
    protected void encodeData(PacketWriter pw) {
        pw.writeMapleString(message);
    }
}