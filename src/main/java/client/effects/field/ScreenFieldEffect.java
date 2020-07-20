package client.effects.field;

import client.effects.AbstractFieldEffect;
import client.effects.FieldEffectType;
import lombok.RequiredArgsConstructor;
import util.packet.PacketWriter;

@RequiredArgsConstructor
public class ScreenFieldEffect extends AbstractFieldEffect {

    private final String path;

    @Override
    public FieldEffectType getType() {
        return FieldEffectType.SCREEN;
    }

    @Override
    protected void encodeData(PacketWriter pw) {
        pw.writeMapleString(path);
    }
}
