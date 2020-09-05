package client.effects.user;

import client.effects.AbstractEffect;
import client.effects.EffectType;
import util.packet.PacketWriter;

public class SquibEffect extends AbstractEffect {

    private final String path;

    public SquibEffect(String path) {
        this.path = path;
    }

    @Override
    public EffectType getType() {
        return EffectType.SQUIB_EFFECT;
    }

    @Override
    protected void encodeData(PacketWriter pw) {
        pw.writeMapleString(path);
    }
}
