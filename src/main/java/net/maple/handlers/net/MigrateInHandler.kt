package net.maple.handlers.net

import client.Character
import client.Client
import client.player.key.KeyBinding.Companion.defaultBindings
import database.jooq.Tables
import net.database.AccountAPI.getAccountInfoTemporary
import net.database.CharacterAPI.getNewCharacter
import net.database.FriendAPI.loadFriends
import net.database.FriendAPI.loadPending
import net.database.ItemAPI.loadItemInventories
import net.database.QuestAPI
import net.database.TownsAPI
import net.database.WishlistAPI
import net.maple.SendOpcode
import net.maple.handlers.PacketHandler
import net.maple.packets.CharacterPackets.updateMacroSettings
import net.maple.packets.GuildPackets.getLoadGuildPacket
import net.maple.packets.GuildPackets.notifyLoginLogout
import net.server.Server
import net.server.Server.clients
import util.packet.Packet
import util.packet.PacketReader
import util.packet.PacketWriter

class MigrateInHandler : PacketHandler {

    override fun handlePacket(reader: PacketReader, c: Client) {
        c.acquireMigrateState()
        try {
            val cid = reader.readInteger()

            val accInfo = getAccountInfoTemporary(cid)
            val mi = clients[accInfo.getValue(Tables.ACCOUNTS.ID)]
                ?: return c.close(this, "MI is null (Channel reset?)")

            if (mi.ip == c.ip) {
                //Server.getInstance().getClients().remove(accInfo.getValue(ACCOUNTS.ID));
                c.login(accInfo, mi)
                val chr = getNewCharacter(c, cid)
                Server.addCharacter(chr)
                c.character = chr

                QuestAPI.loadAll(chr)
                WishlistAPI.load(chr)
                TownsAPI.load(chr) // before entering field, in case of FirstVisit mapscript

                if (chr.towns.isEmpty()) {
                    chr.addTown(100) // FM
                }

                chr.loadGuild()
                chr.loadMobKills()

                val field = c.worldChannel.fieldManager.getField(chr.fieldId)
                chr.field = field

                loadItemInventories(chr)
                chr.validateStats()

                field.enter(chr)

                loadFriends(chr)
                loadPending(chr)
                chr.friendList.sendPendingRequest()

                chr.loadParty()

                chr.guild?.let {
                    c.write(it.getLoadGuildPacket())
                    if (!mi.cashShop && !mi.changingChannel) {
                        it.notifyLoginLogout(chr, true)
                    }
                }

                c.write(initFuncKey(chr))
                c.write(initQuickslot(chr))
                chr.updateMacroSettings()

                mi.cashShop = false
            } else {
                c.close(this, "IP mismatch")
            }
        } finally {
            c.releaseMigrateState()
        }
    }

    override fun validateState(c: Client): Boolean {
        return true
    }

    companion object {
        private fun initFuncKey(chr: Character): Packet {
            val pw = PacketWriter(453)

            pw.writeHeader(SendOpcode.FUNC_KEY_MAPPED_INIT)
            pw.writeBool(false)

            for (i in 0 until 90) {
                val keyBinding = chr.keyBindings[i]
                var type: Byte = 0
                var action = 0

                keyBinding?.let {
                    type = it.type
                    action = it.action
                } ?: run {
                    defaultBindings[i]?.let {
                        type = it.type
                        action = it.action
                    }
                }

                pw.write(type.toInt())
                pw.writeInt(action)
            }

            return pw.createPacket()
        }

        private fun initQuickslot(chr: Character): Packet {
            val pw = PacketWriter(35)

            pw.writeHeader(SendOpcode.QUICKSLOT_MAPPED_INIT)
            pw.writeBool(true)
            for (key in chr.quickSlotKeys) {
                pw.writeInt(key)
            }

            return pw.createPacket()
        }
    }
}