package client.replay

import client.Avatar
import client.player.Job
import field.Field
import field.movement.MovePath
import field.obj.FieldObjectType
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import managers.Loadable
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

    override val fieldObjectType = FieldObjectType.REPLAY
    override var job: Job = Job.getById(jobId)

    private val movements = ArrayList<ReplayMovement>()
    private var coroutine: kotlinx.coroutines.Job? = null

    fun load(field: Field): Boolean {
        val reader = getData("data/replays/${field.id}.replay")
                ?: return run {
                    Logger.log(LogType.MISSING, "Replay on field ${field.id} not found", this@Replay)
                    false
                }

        this.field = field
        gender = reader.readInteger()
        skinColor = reader.readInteger()
        face = reader.readInteger()
        hair = reader.readInteger()
        level = reader.readInteger()
        name = reader.readMapleString()
        job = Job.getById(reader.readInteger())

        val count = reader.readShort()
        repeat(count.toInt()) {
            val timestamp = reader.readInteger()
            val byteCount = reader.readShort().toInt()
            val data = ByteArray(byteCount)
            repeat(byteCount) {
                data[it] = reader.readByte()
            }

            movements.add(ReplayMovement(timestamp.toLong(), move(PacketReader().next(data))))
        }
        position = movements[0].path.position
        return true
    }

    fun start() {
        coroutine = GlobalScope.launch {
            movements.forEach {
                async { it.move() }
            }
            async {
                delay(movements[movements.size - 1].timestamp + 1000)
                start()
            }
        }
    }

    private suspend fun ReplayMovement.move() {
        delay(this.timestamp)
        field.broadcast(move(this.path))
    }

    fun stop() {
        coroutine?.cancel()
        field.leave(this)
    }

    private class ReplayMovement(val timestamp: Long, val path: MovePath)
}