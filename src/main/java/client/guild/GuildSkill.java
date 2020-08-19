package client.guild;

import lombok.RequiredArgsConstructor;
import util.packet.PacketWriter;

@RequiredArgsConstructor
public class GuildSkill {

    private final short level;
    private final long expire;
    private final String name;

    public void encode(PacketWriter pw) {
        pw.writeShort(level);
        pw.writeLong(expire);
        pw.writeMapleString(name);
    }
}