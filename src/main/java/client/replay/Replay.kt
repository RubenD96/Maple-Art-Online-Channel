package client.replay

import client.Avatar
import client.player.Job
import field.movement.MovePath
import field.obj.FieldObjectType
import kotlinx.coroutines.*
import managers.Loadable
import net.maple.packets.CharacterPackets.move
import net.maple.packets.FieldPackets.leaveField
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

    fun load(field: Int) {
        val reader = getData("data/replays/$field.replay")
                ?: return Logger.log(LogType.MISSING, "Replay on field $field not found", this@Replay)

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
    }

    fun start() {
        movements.forEach {
            coroutine = GlobalScope.launch {
                async { it.move() }
            }
        }
    }

    private suspend fun ReplayMovement.move() {
        delay(this.timestamp)
        move(this.path)
    }

    // todo test
    fun stop() {
        coroutine?.cancel()
        leaveField()
    }

    private class ReplayMovement(val timestamp: Long, val path: MovePath)
}