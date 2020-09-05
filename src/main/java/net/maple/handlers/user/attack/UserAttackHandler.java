package net.maple.handlers.user.attack;

import client.Character;
import client.Client;
import net.maple.SendOpcode;
import net.maple.handlers.PacketHandler;
import util.packet.Packet;
import util.packet.PacketReader;
import util.packet.PacketWriter;

import java.util.Arrays;

public class UserAttackHandler extends PacketHandler {

    private final AttackType type;

    public UserAttackHandler(AttackType type) {
        this.type = type;
    }

    @Override
    public void handlePacket(PacketReader reader, Client c) {
        Character chr = c.getCharacter();
        AttackInfo info = new AttackInfo(type, chr, reader);
        info.decode();

        chr.getField().broadcast(showAttack(chr, info), chr);
        info.apply();
    }

    public Packet showAttack(Character chr, AttackInfo info) {
        PacketWriter pw = new PacketWriter(32);

        pw.writeHeader((short) (SendOpcode.USER_MELEE_ATTACK.getValue() + type.getType()));
        pw.writeInt(chr.getId());
        pw.write((info.getDamagePerMob() | 16 * info.getMobCount()));
        pw.write(chr.getLevel());

        if (info.getSkillId() > 0) {
            pw.write(0); // todo
            pw.writeInt(info.getSkillId());
        } else {
            pw.write(0);
        }

        pw.write(0x20);
        pw.writeShort((info.getAction() & 0x7FFF | (info.isLeft() ? 1 : 0) << 15));

        if (info.getAction() <= 0x110) {
            pw.write(0);
            pw.write(0);
            pw.writeInt(2070000);

            Arrays.stream(info.getDamageInfo()).forEach(i -> {
                pw.writeInt(i.getMobId());
                if (i.getMobId() <= 0) return;

                pw.write(i.getHitAction());

                Arrays.stream(i.getDamage()).forEach(d -> {
                    pw.writeBool(false);
                    pw.writeInt(d);
                });
            });
        }

        if (type == AttackType.SHOOT) {
            pw.writeShort(0);
            pw.writeShort(0);
        }

        // pw.writeInt(0); // keydown

        return pw.createPacket();
    }
}
