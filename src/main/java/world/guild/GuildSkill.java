package world.guild;

import util.packet.PacketWriter;

public class GuildSkill {

    private final short level;
    private final long expire;
    private final String name;

    public GuildSkill(short level, long expire, String name) {
        this.level = level;
        this.expire = expire;
        this.name = name;
    }

    public void encode(PacketWriter pw) {
        pw.writeShort(level);
        pw.writeLong(expire);
        pw.writeMapleString(name);
    }
}