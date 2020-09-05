package client.effects.user;

import client.effects.AbstractEffect;
import client.effects.EffectType;
import util.packet.PacketWriter;

import java.util.function.Consumer;

public class SkillUseEffect extends AbstractEffect {

    private final int skillId;
    private final byte skillLevel;
    private Consumer<PacketWriter> additional = null;

    public SkillUseEffect(int skillId, byte skillLevel) {
        this.skillId = skillId;
        this.skillLevel = skillLevel;
    }

    public void setAdditional(Consumer<PacketWriter> additional) {
        this.additional = additional;
    }

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
