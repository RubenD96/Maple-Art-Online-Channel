package client.player.quest.requirement

import client.player.quest.QuestRequirementType
import util.packet.PacketReader
import java.util.*

class StartingRequirement : Requirement() {

    val jobs: MutableList<Short> = ArrayList()
    var maxLevel = 0
    var endDate: Long = 0

    override fun decode(flags: Int, reader: PacketReader) {
        super.decode(flags, reader)

        if (containsFlag(flags, QuestRequirementType.JOB)) {
            val size = reader.readShort().toInt()
            for (i in 0 until size) {
                jobs.add(reader.readShort())
            }
        }
        if (containsFlag(flags, QuestRequirementType.MAX_LEVEL)) maxLevel = reader.readShort().toInt()
        if (containsFlag(flags, QuestRequirementType.END_DATE)) endDate = reader.readLong()
    }
}