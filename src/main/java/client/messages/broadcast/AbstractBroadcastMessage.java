package client.messages.broadcast;

import util.packet.PacketWriter;

public abstract class AbstractBroadcastMessage implements BroadcastMessage {

    @Override
    abstract public BroadcastMessageType getType();

    @Override
    public void encode(PacketWriter pw) {
        pw.write(getType().getValue());
        encodeData(pw);
    }

    protected abstract void encodeData(PacketWriter pw);
}
