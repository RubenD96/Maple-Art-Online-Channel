package client.effects;

import util.packet.PacketWriter;

public class Effect extends AbstractEffect {

    private final EffectType type;

    public Effect(EffectType type) {
        this.type = type;
    }

    @Override
    public EffectType getType() {
        return type;
    }

    @Override
    protected void encodeData(PacketWriter pw) {
    }
}
