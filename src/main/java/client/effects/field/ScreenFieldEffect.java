package client.effects.field;

import client.effects.AbstractFieldEffect;
import client.effects.FieldEffectType;
import util.packet.PacketWriter;

public class ScreenFieldEffect extends AbstractFieldEffect {

    private final String path;

    public ScreenFieldEffect(String path) {
        this.path = path;
    }

    @Override
    public FieldEffectType getType() {
        return FieldEffectType.SCREEN;
    }

    @Override
    protected void encodeData(PacketWriter pw) {
        pw.writeMapleString(path);
    }
}
