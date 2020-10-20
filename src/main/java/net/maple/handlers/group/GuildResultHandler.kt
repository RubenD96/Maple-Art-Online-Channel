package net.maple.handlers.group

import client.Client
import client.messages.broadcast.types.EventMessage
import net.maple.handlers.PacketHandler
import net.maple.packets.CharacterPackets.message
import net.maple.packets.GuildPackets.GuildRes
import net.server.Server.getCharacter
import util.HexTool.toHex
import util.packet.PacketReader

class GuildResultHandler : PacketHandler {

    override fun handlePacket(reader: PacketReader, c: Client) {
        val chr = c.character
        println("[GuildResultHandler] " + toHex(reader.data))

        when (reader.readByte()) {
            GuildRes.INVITE_GUILD_REJECTED -> {
                val inviterName = reader.readMapleString()
                val rejecterName = reader.readMapleString()
                val inviter = getCharacter(inviterName) ?: return
                if (!inviter.guildInvitesSent.contains(rejecterName)) return

                chr.guildInvitesSent.remove(rejecterName)
                inviter.message(EventMessage("$rejecterName has rejected your request for the guild invite."))
            }
        }
    }
}