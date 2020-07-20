package client.effects.field;

import client.effects.AbstractFieldEffect;
import client.effects.FieldEffectType;
import lombok.RequiredArgsConstructor;
import util.packet.PacketWriter;

@RequiredArgsConstructor
public class TrembleFieldEffect extends AbstractFieldEffect {

    private final boolean heavy;
    private final int delay;

    @Override
    public FieldEffectType getType() {
        return FieldEffectType.TREMBLE;
    }

    @Override
    protected void encodeData(PacketWriter pw) {
        pw.writeBool(!heavy);
        pw.writeInt(delay * 1000); // milliseconds
    }
}
