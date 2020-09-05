package client.effects.user;

import client.effects.AbstractEffect;
import client.effects.EffectType;
import util.packet.PacketWriter;

public class AvatarOrientedEffect extends AbstractEffect {

    private final String path;

    public AvatarOrientedEffect(String path) {
        this.path = path;
    }

    @Override
    public EffectType getType() {
        return EffectType.AVATAR_ORIENTED;
    }

    @Override
    protected void encodeData(PacketWriter pw) {
        pw.writeMapleString(path);
        pw.writeInt(0);
    }
}
