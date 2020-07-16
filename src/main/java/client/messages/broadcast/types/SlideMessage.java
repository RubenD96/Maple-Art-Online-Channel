package client.messages.broadcast.types;

import client.messages.broadcast.AbstractBroadcastMessage;
import client.messages.broadcast.BroadcastMessageType;
import util.packet.PacketWriter;

/**
 * Color: Yellow
 * Prefix:
 * <p>
 * Scrolling message on top
 */
public class SlideMessage extends AbstractBroadcastMessage {

    private final String message;

    public SlideMessage(String message) {
        this.message = message;
    }

    @Override
    public BroadcastMessageType getType() {
        return BroadcastMessageType.SLIDE;
    }

    @Override
    protected void encodeData(PacketWriter pw) {
        pw.writeBool(true);
        pw.writeMapleString(message);
    }
}
