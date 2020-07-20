package client.effects;

import util.packet.PacketWriter;

public abstract class AbstractFieldEffect implements FieldEffectInterface {

    public abstract FieldEffectType getType();

    public void encode(PacketWriter pw) {
        pw.write(getType().getValue());
        encodeData(pw);
    }

    protected abstract void encodeData(PacketWriter pw);
}
