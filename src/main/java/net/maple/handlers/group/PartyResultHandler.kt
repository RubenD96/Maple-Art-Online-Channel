package net.maple.handlers.group

import client.Client
import client.party.PartyOperationType
import net.maple.handlers.PacketHandler
import net.maple.packets.PartyPackets.getServerMsgPacket
import net.server.Server.getCharacter
import net.server.Server.parties
import util.HexTool.toHex
import util.logging.LogType
import util.logging.Logger
import util.packet.PacketReader

class PartyResultHandler : PacketHandler {

    override fun handlePacket(reader: PacketReader, c: Client) {
        println("[" + javaClass.name.replace("net.maple.handlers.misc.", "") + "] " + toHex(reader.data))

        val operation = reader.readByte()
        val pid = reader.readInteger()
        val party = parties[pid] ?: return

        when {
            operation.toInt() == PartyOperationType.PARTYRES_INVITEPARTY_REJECTED.value -> {
                getCharacter(party.leaderId)?.write(getServerMsgPacket(c.character.name + " has rejected the invite to the party."))
                Logger.log(LogType.PARTY, "[pid: ${party.id}] ${c.character} rejected", this)
            }
            operation.toInt() == PartyOperationType.PARTYRES_INVITEPARTY_ACCEPTED.value -> {
                val chr = c.character
                chr.party ?: run {
                    party.addMember(chr)
                    chr.party = party
                    party.update()
                }
            }
            operation.toInt() == PartyOperationType.PARTYRES_INVITEPARTY_ALREADYINVITEDBYINVITER.value -> {
                getCharacter(party.leaderId)?.write(getServerMsgPacket(c.character.name + " is busy."))
            }
            operation.toInt() == PartyOperationType.PARTYRES_INVITEPARTY_SENT.value -> {
                // nothing?
            }
            else -> {
                Logger.log(LogType.UNCODED, "Unknown party op (${toHex(operation)})", this, c)
            }
        }
    }
}