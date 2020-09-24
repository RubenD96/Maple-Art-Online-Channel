package client.player.quest.requirement

import client.player.quest.QuestRequirementType
import util.packet.PacketReader

class EndingRequirement : Requirement() {

    val mobs: MutableMap<Int, Short> = LinkedHashMap()

    override fun decode(flags: Int, reader: PacketReader) {
        super.decode(flags, reader)

        if (containsFlag(flags, QuestRequirementType.MOB)) {
            val size = reader.readShort().toInt()
            repeat(size) {
                val b = reader.readInteger()
                mobs[b] = reader.readShort()
            }
        }
    }
}