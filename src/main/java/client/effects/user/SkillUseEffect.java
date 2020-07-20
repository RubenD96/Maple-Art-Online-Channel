package client.effects.user;

import client.effects.AbstractEffect;
import client.effects.EffectType;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import util.packet.PacketWriter;

import java.util.function.Consumer;

@RequiredArgsConstructor
public class SkillUseEffect extends AbstractEffect {

    private final int skillId;
    private final byte skillLevel;
    private @Setter Consumer<PacketWriter> additional = null;

    @Override
    public EffectType getType() {
        return EffectType.SKILL_USE;
    }

    @Override
    protected void encodeData(PacketWriter pw) {
        pw.writeInt(skillId);
        pw.write(0);
        pw.write(skillLevel);

        if (additional != null) {
            additional.accept(pw);
        }
    }
}
