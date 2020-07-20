package client.effects;

import lombok.RequiredArgsConstructor;
import util.packet.PacketWriter;

@RequiredArgsConstructor
public class Effect extends AbstractEffect {

    private final EffectType type;

    @Override
    public EffectType getType() {
        return type;
    }

    @Override
    protected void encodeData(PacketWriter pw) {
    }
}
