package client.effects.user;

import client.effects.AbstractEffect;
import client.effects.EffectType;
import lombok.RequiredArgsConstructor;
import util.packet.PacketWriter;

@RequiredArgsConstructor
public class SquibEffect extends AbstractEffect {

    private final String path;

    @Override
    public EffectType getType() {
        return EffectType.SQUIB_EFFECT;
    }

    @Override
    protected void encodeData(PacketWriter pw) {
        pw.writeMapleString(path);
    }
}
