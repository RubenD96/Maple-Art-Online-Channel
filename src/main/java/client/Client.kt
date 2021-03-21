package client

import client.interaction.storage.ItemStorage
import client.inventory.item.slots.ItemSlotLocker
import database.jooq.Tables
import io.netty.channel.Channel
import io.netty.util.concurrent.ScheduledFuture
import net.maple.packets.ConnectionPackets
import net.maple.packets.ConnectionPackets.getChangeChannelPacket
import net.maple.packets.GuildPackets.getLoadGuildPacket
import net.maple.packets.GuildPackets.notifyLoginLogout
import net.maple.packets.PartyPackets.getTransferLeaderMessagePacket
import net.maple.packets.PartyPackets.updateParty
import net.netty.NettyClient
import net.netty.central.CentralPackets
import net.server.ChannelServer
import net.server.MigrateInfo
import net.server.Server
import net.server.Server.removeCharacter
import org.jooq.Record
import java.util.*
import java.util.concurrent.TimeUnit
import javax.script.ScriptEngine
import kotlin.collections.HashMap

class Client(c: Channel, siv: ByteArray, riv: ByteArray) : NettyClient(c, siv, riv) {

    var isAdmin = false
    var accId = 0
        private set
    lateinit var accountName: String
    var lastPong: Long = 0
    var clientStart: Long = 0
    var lastNpcClick: Long = 0
    var isDisconnecting = false
        private set
    var isLoggedIn = false
        private set
    var isCc = false
        private set
    lateinit var worldChannel: ChannelServer
    lateinit var pic: String
        private set
    lateinit var character: Character

    /*var macs: Set<String>? = null
    var hwids: Set<String>? = null
    var ips: Set<String>? = null*/
    var isBanned = false
    val engines: MutableMap<String, ScriptEngine> = HashMap()
    var cash: Int = -1
    lateinit var ping: ScheduledFuture<*>
        private set
    val locker: MutableList<ItemSlotLocker> = ArrayList()
    lateinit var storage: ItemStorage

    fun login(data: Record, mi: MigrateInfo) {
        accId = data.getValue(Tables.ACCOUNTS.ID)
        isBanned = data.getValue(Tables.ACCOUNTS.BANNED) == 1.toByte()
        isAdmin = data.getValue(Tables.ACCOUNTS.ADMIN) == 1.toByte()
        pic = data.getValue(Tables.ACCOUNTS.PIC)
        worldChannel = Server.channels[mi.channelId]
        worldChannel.write(CentralPackets.getAddOnlinePlayerPacket(mi.port, accId))
        isLoggedIn = true
    }

    fun startPing() {
        ping = ch.eventLoop().scheduleAtFixedRate({
            ch.writeAndFlush(ConnectionPackets.ping)
        }, 5, 5, TimeUnit.SECONDS)
    }

    fun cancelPingTask() {
        ping.cancel(true)
    }

    fun executeIn(r: Runnable, seconds: Int) {
        ch.eventLoop().schedule(r, seconds.toLong(), TimeUnit.SECONDS)
    }

    fun canClickNPC(): Boolean {
        return lastNpcClick + 500 < System.currentTimeMillis()
    }

    fun disconnect() {
        println("disconnecting")
        if (!isDisconnecting) {
            isDisconnecting = true
            worldChannel.write(CentralPackets.getRemoveOnlinePlayerPacket(worldChannel.port, accId))

            if (ch.isOpen) {
                close(this, "Disconnect function called")
            }

            isLoggedIn = false
            character.field.leave(character)

            character.coroutines.cancelAll()

            notifyGuildLogout()
            notifyPartyLogout()
            character.friendList.notifyMutualFriends()
            removeCharacter(character)
            if (!isCc) character.save() // we save when changing channel at another point
            isDisconnecting = false // uuh dont think i need this anymore
        }
    }

    fun changeChannel(channel: ChannelServer) {
        isCc = true
        character.save()
        worldChannel = channel
        migrate()
    }

    fun migrate() {
        Server.clients[accId]?.let {
            it.channelId = worldChannel.channelId
            write(worldChannel.getChangeChannelPacket())
        } ?: close(this, "No MigrateInfo on migrate method")
    }

    private fun notifyGuildLogout() {
        character.guild?.let {
            val inCS = Server.clients[accId]?.cashShop ?: false
            if (!inCS || character.isInCashShop) {
                it.getMemberSecure(character.id).isOnline = false
                it.broadcast(it.getLoadGuildPacket())
                it.notifyLoginLogout(character, false)
            }
        }
    }

    private fun notifyPartyLogout() {
        val party = character.party ?: return
        val member = party.getMember(character.id) ?: return

        if (!isCc) {
            val online = party.onlineMembers
            member.isOnline = false
            member.channel = -2

            if (online.size > 1 && character.id == party.leaderId) { // change leader
                val newLeader = party.getRandomOnline(character.id) ?: return
                party.leaderId = newLeader.cid

                party.getMembers().forEach {
                    if (it.isOnline) {
                        val pm = Server.getCharacter(it.cid) ?: return@forEach
                        pm.write(getTransferLeaderMessagePacket(newLeader.cid, true))
                        pm.write(updateParty(party, it.channel))
                    }
                }
            } else { // just tell everybody you're going offline
                party.getMembers().forEach {
                    if (it.isOnline) {
                        val pm = Server.getCharacter(it.cid) ?: return@forEach
                        pm.write(updateParty(party, it.channel))
                    }
                }
            }
        }
    }

    fun isInitialized(): Boolean {
        return this::worldChannel.isInitialized
    }
}