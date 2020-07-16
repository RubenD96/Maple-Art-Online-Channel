package client.messages.broadcast.types;

import client.messages.broadcast.AbstractBroadcastMessage;
import client.messages.broadcast.BroadcastMessageType;
import util.packet.PacketWriter;

/**
 * Color: Purple on pink bg
 * Prefix: Medalname charactername ch01
 * <p>
 * Super Megaphone
 */
public class SpeakerworldMessage extends AbstractBroadcastMessage {

    private final String message;
    private final String medal;
    private final String name;
    private final byte channel;
    private final boolean ear;

    public SpeakerworldMessage(String message, String medal, String name, int channel, boolean ear) {
        this.message = message;
        this.medal = medal;
        this.name = name;
        this.channel = (byte) channel;
        this.ear = ear;
    }

    @Override
    public BroadcastMessageType getType() {
        return BroadcastMessageType.SPEAKERWORLD;
    }

    @Override
    protected void encodeData(PacketWriter pw) {
        pw.writeMapleString(medal + name + " : " + message);
        pw.write(channel);
        pw.writeBool(ear);
    }
}