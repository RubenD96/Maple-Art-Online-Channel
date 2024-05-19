package net.netty

import constants.ServerConstants
import io.netty.channel.Channel
import io.netty.util.AttributeKey
import util.HexTool
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
        println("[SEND] " + HexTool.toHex(msg.data))
        ch.writeAndFlush(msg)
    }

    fun close(cl: Any, reason: String) {
        println("[${cl.javaClass.name}] $reason - $ip")
        close()
    }

    fun close() {
        if (!ServerConstants.DEBUG) ch.flush().close()
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
        val CRYPTO_KEY: AttributeKey<MapleAESOFB> = AttributeKey.valueOf("A")
        val CLIENT_KEY: AttributeKey<NettyClient> = AttributeKey.valueOf("C")
    }
}
