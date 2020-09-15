package net.maple.handlers.group;

import client.Character;
import client.Client;
import client.messages.broadcast.types.EventMessage;
import net.maple.handlers.PacketHandler;
import net.maple.packets.CharacterPackets;
import net.maple.packets.GuildPackets.GuildRes;
import util.HexTool;
import util.packet.PacketReader;

public class GuildResultHandler extends PacketHandler {

    @Override
    public void handlePacket(PacketReader reader, Client c) {
        Character chr = c.getCharacter();
        System.out.println("[GuildResultHandler] " + HexTool.INSTANCE.toHex(reader.getData()));
        byte res = reader.readByte();

        switch (res) {
            case GuildRes.INVITE_GUILD_REJECTED:
                String inviterName = reader.readMapleString();
                String rejecterName = reader.readMapleString();

                Character inviter = c.getWorldChannel().getCharacter(inviterName);
                if (inviter == null) return;
                if (!inviter.getGuildInvitesSent().contains(rejecterName)) return;

                chr.getGuildInvitesSent().remove(rejecterName);
                inviter.write(CharacterPackets.message(new EventMessage(rejecterName + " has rejected your request for the guild invite.")));
                break;
        }
    }
}
