package client.messages;

import util.packet.PacketWriter;

public abstract class AbstractMessage implements Message {

    @Override
    public abstract MessageType getType();

    @Override
    public void encode(PacketWriter pw) {
        pw.write(getType().getValue());
        encodeData(pw);
    }

    protected abstract void encodeData(PacketWriter pw);
}
