package client.effects;

import util.packet.PacketWriter;

public abstract class AbstractEffect implements EffectInterface {

    public abstract EffectType getType();

    public void encode(PacketWriter pw) {
        pw.write(getType().getValue());
        encodeData(pw);
    }

    protected abstract void encodeData(PacketWriter pw);
}
