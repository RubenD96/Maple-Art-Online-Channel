package net.netty

import client.interaction.shop.NPCShop
import io.netty.channel.Channel
import io.netty.util.AttributeKey
import util.crypto.MapleAESOFB
import util.packet.Packet
import util.packet.PacketReader
import java.util.concurrent.locks.ReentrantLock

open class NettyClient(val ch: Channel, var sendIV: ByteArray, var recvIV: ByteArray) {

    var storedLength = -1
    private val encodeLock: ReentrantLock = ReentrantLock(true)
    private val migrateLock: ReentrantLock = ReentrantLock()
    val reader: PacketReader = PacketReader()

    fun write(msg: Packet) {
        ch.writeAndFlush(msg)
    }

    fun close(cl: Any, reason: String) {
        println("[" + cl.javaClass.name + "] " + reason + " - " + ip)
        ch.flush().close()
    }

    fun close() {
        ch.flush().close()
    }

    val ip: String get() = ch.remoteAddress().toString().split(":".toRegex()).toTypedArray()[0].substring(1)

    fun acquireEncoderState() {
        encodeLock.lock()
    }

    fun releaseEncodeState() {
        encodeLock.unlock()
    }

    fun acquireMigrateState() {
        if (!migrateLock.tryLock()) {
            releaseMigrateState()
            close(this, "User was already in migration")
        }
    }

    fun releaseMigrateState() {
        migrateLock.unlock()
    }

    companion object {
        val CRYPTO_KEY = AttributeKey.valueOf<MapleAESOFB>("A")
        val CLIENT_KEY = AttributeKey.valueOf<NettyClient>("C")
    }
}