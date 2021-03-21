package client.replay

import client.Avatar
import client.player.Job
import field.Field
import field.movement.MovePath
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import managers.FieldManager
import managers.ItemManager
import managers.Loadable
import net.maple.handlers.user.UserEmotionHandler.Companion.sendEmotion
import net.maple.handlers.user.UserChatHandler.Companion.sendMessage
import net.maple.packets.CharacterPackets.move
import util.logging.LogType
import util.logging.Logger
import util.packet.PacketReader

class Replay : Avatar(), Loadable {

    override var gender: Int = 0
    override var skinColor: Int = 0
    override var face: Int = 0
    override var hair: Int = 0
    override var level: Int = 0
    override var name: String = ""
    var jobId: Int = 0

    override var job: Job = Job.getById(jobId)

    private val movements = ArrayList<ReplayMovement>()
    private val emotes = ArrayList<ReplayEmote>()
    private val chats = ArrayList<ReplayChat>()
    private var coroutine: kotlinx.coroutines.Job? = null

    fun load(field: Field): Replay? {
        val reader = getData("data/replays/${field.template.id}.replay")
                ?: return run {
                    Logger.log(LogType.MISSING, "Replay on field ${field.template.id} not found", this@Replay)
                    null
                }

        this.field = field
        gender = reader.readInteger()
        skinColor = reader.readInteger()
        face = reader.readInteger()
        hair = reader.readInteger()
        level = reader.readInteger()
        name = reader.readMapleString()
        job = Job.getById(reader.readInteger())

        decodeVisualEquips(reader)

        repeat(reader.readShort().toInt()) {
            val timestamp = reader.readInteger()
            val byteCount = reader.readShort().toInt()
            val data = ByteArray(byteCount)
            repeat(byteCount) {
                data[it] = reader.readByte()
            }

            movements.add(ReplayMovement(timestamp.toLong(), move(PacketReader().next(data))))
        }
        position = movements[0].path.position

        repeat(reader.readShort().toInt()) {
            val timestamp = reader.readInteger()
            val emote = reader.readInteger()

            emotes.add(ReplayEmote(timestamp.toLong(), emote))
        }

        repeat(reader.readShort().toInt()) {
            val timestamp = reader.readInteger()
            val message = reader.readMapleString()

            chats.add(ReplayChat(timestamp.toLong(), message))
        }

        return this
    }

    private fun decodeVisualEquips(reader: PacketReader) {
        var slot: Byte
        while (reader.readByte().also { slot = it } != 0xFF.toByte()) { // base
            getEquips().items[(-slot).toShort()] = ItemManager.getItem(reader.readInteger()).toItemSlot()
        }

        while (reader.readByte().also { slot = it } != 0xFF.toByte()) { // mask
            getEquips().items[(-slot).toShort()] = ItemManager.getItem(reader.readInteger()).toItemSlot()
        }

        val weapon = reader.readInteger()
        if (weapon != 0) getEquips().items[-111] = ItemManager.getItem(weapon).toItemSlot()
    }

    fun start() {
        coroutine = GlobalScope.launch {
            movements.forEach {
                async { it.move() }
            }
            emotes.forEach {
                async { showEmote(it) }
            }
            chats.forEach {
                async { talk(it) }
            }
            async {
                delay(movements[movements.size - 1].timestamp + 1000)
                if (!(FieldManager.isInstanced(field.template.id) && field.isEmpty())) {
                    start()
                }
            }
        }
    }

    private suspend fun ReplayMovement.move() {
        delay(this.timestamp)
        field.broadcast(move(this.path))
    }

    private suspend fun showEmote(emote: ReplayEmote) {
        delay(emote.timestamp)
        field.broadcast(sendEmotion(this, emote.emote, -1, false))
    }

    private suspend fun talk(chat: ReplayChat) {
        delay(chat.timestamp)
        field.broadcast(sendMessage(chat.message, false, isGM = false))
    }

    fun stop() {
        coroutine?.cancel()
        field.leave(this)
    }

    private class ReplayMovement(val timestamp: Long, val path: MovePath)
    private class ReplayEmote(val timestamp: Long, val emote: Int)
    private class ReplayChat(val timestamp: Long, val message: String)
}