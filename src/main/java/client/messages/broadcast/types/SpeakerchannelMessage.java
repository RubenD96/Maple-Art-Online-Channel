package client.messages.broadcast.types;

import client.messages.broadcast.AbstractBroadcastMessage;
import client.messages.broadcast.BroadcastMessageType;
import util.packet.PacketWriter;

/**
 * Color: Dark blue on light blue background
 * Prefix: Medalname charactername
 * <p>
 * Megaphone
 */
public class SpeakerchannelMessage extends AbstractBroadcastMessage {

    private final String message;
    private final String medal;
    private final String name;

    public SpeakerchannelMessage(String message, String medal, String name) {
        this.message = message;
        this.medal = medal;
        this.name = name;
    }

    @Override
    public BroadcastMessageType getType() {
        return BroadcastMessageType.SPEAKERCHANNEL;
    }

    @Override
    protected void encodeData(PacketWriter pw) {
        pw.writeMapleString(medal + name + " : " + message);
    }
}