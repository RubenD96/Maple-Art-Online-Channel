package client.player.quest.requirement

import client.player.quest.QuestRequirementType
import util.packet.PacketReader

abstract class Requirement {

    var minLevel = 0
    var npc = 0
    val items: MutableMap<Int, Short> = HashMap()
    val quests: MutableMap<Int, Byte> = HashMap()

    open fun decode(flags: Int, reader: PacketReader) {
        if (containsFlag(flags, QuestRequirementType.MIN_LEVEL)) minLevel = reader.readShort().toInt()
        if (containsFlag(flags, QuestRequirementType.NPC)) npc = reader.readInteger()
        if (containsFlag(flags, QuestRequirementType.ITEM)) {
            val size = reader.readShort().toInt()
            for (i in 0 until size) {
                items[reader.readInteger()] = reader.readShort()
            }
        }
        if (containsFlag(flags, QuestRequirementType.QUEST)) {
            val size = reader.readShort().toInt()
            for (i in 0 until size) {
                quests[reader.readInteger()] = reader.readByte()
            }
        }
    }

    fun containsFlag(flags: Int, flag: QuestRequirementType): Boolean {
        return flags and flag.value == flag.value
    }
}