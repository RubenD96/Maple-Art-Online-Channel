package client.player.quest

import client.Character
import client.messages.quest.AbstractQuestRecordMessage
import client.messages.quest.PerformQuestRecordMessage
import client.player.quest.requirement.Requirement
import managers.QuestTemplateManager
import net.database.QuestAPI.register
import net.database.QuestAPI.remove
import net.database.QuestAPI.update
import net.maple.SendOpcode
import net.maple.packets.CharacterPackets.message
import scripting.quest.QuestScriptManager
import util.logging.LogType
import util.logging.Logger.log
import util.packet.Packet
import util.packet.PacketWriter

class Quest(val id: Int, val character: Character) {

    lateinit var state: QuestState
    val mobs: MutableMap<Int, String> = LinkedHashMap()
    var dbId = 0

    fun initializeMobs() {
        val template = QuestTemplateManager.getQuest(id) ?: return
        val reqs = template.endingRequirements

        if (reqs.mobs.isNotEmpty()) {
            reqs.mobs.keys.forEach {
                character.registeredQuestMobs.add(it)
                mobs[it] = "000"
            }
        }
    }

    private fun reqCheck(reqs: Requirement): Boolean {
        if (character.level < reqs.minLevel) {
            return false
        }

        val qcm = QuestScriptManager.qms[character.client] ?: return false
        if (reqs.npc != 0 && qcm.npcId != reqs.npc) {
            return false
        }

        if (reqs.items.isNotEmpty()) { // unnecessary?
            reqs.items.forEach {
                if (character.getItemQuantity(it.key) < it.value) {
                    return false
                }
            }
        }

        if (reqs.quests.isNotEmpty()) {
            reqs.quests.forEach {
                if (it.value == 0.toByte()) { // not started
                    character.quests[it.key]?.let { quest ->
                        if (quest.state != QuestState.NONE) { // shouldn't happen, I think? Checking anyway.
                            return false
                        }
                    }
                } else {
                    val q = character.quests[it.key]
                            ?: return false // does not exist but quest should be started or finished
                    if (q.state.value != it.value) { // quest is registered at started but should be completed, or other way around
                        return false
                    }
                }
            }
        }
        return true
    }

    fun canStart(): Boolean {
        val template = QuestTemplateManager.getQuest(id) ?: return false
        val reqs = template.startingRequirements

        if (!reqCheck(reqs)) {
            return false
        }

        if (reqs.jobs.isNotEmpty()) {
            val isJob = reqs.jobs.any { it == character.job.value.toShort() }
            if (!isJob) return false
        }

        return !(reqs.maxLevel != 0 && character.level > reqs.maxLevel)

        // todo date check
    }

    fun canFinish(): Boolean {
        val template = QuestTemplateManager.getQuest(id) ?: return false
        val reqs = template.endingRequirements

        if (!reqCheck(reqs)) {
            return false
        }

        if (reqs.mobs.isNotEmpty()) { // unnecessary?
            reqs.mobs.forEach {
                val mobCount = mobs[it.key] ?: return false
                if (mobCount.toInt() < it.value) {
                    log(LogType.INVALID, "${it.key} - ${mobs[it.key]} - ${it.value}", this, character.client)
                    return false
                }
            }
        }
        return true
    }

    fun progress(mob: Int, increase: Int = 1) {
        val mobCount = mobs[mob] ?: return
        val count = mobCount.toInt() + increase
        val template = QuestTemplateManager.getQuest(id) ?: return
        val countReq = template.endingRequirements.mobs[mob] ?: return
        if (count > countReq) return

        val newCount = StringBuilder(count.toString())
        while (newCount.length < 3) {
            newCount.insert(0, "0")
        }

        mobs[mob] = newCount.toString()
        updateMobs(PerformQuestRecordMessage(id.toShort(), progress))
    }

    val progress: String
        get() {
            val sb = StringBuilder()
            mobs.values.forEach { sb.append(it) }
            return sb.toString()
        }

    private fun updateMobs(message: PerformQuestRecordMessage) {
        character.message(message)
    }

    fun updateState(message: AbstractQuestRecordMessage) {
        state = message.state
        character.message(message)
        when (state) {
            QuestState.NONE -> remove(this)
            QuestState.PERFORM -> register(this)
            QuestState.COMPLETE -> update(this)
            else -> throw IllegalArgumentException("Unsupported quest state update")
        }
    }

    fun startQuestPacket(npc: Int): Packet {
        val pw = PacketWriter(5)

        pw.writeHeader(SendOpcode.USER_QUEST_RESULT)
        pw.write(0x0A) // QUESTRES_ACT_SUCCESS

        pw.writeShort(id)
        pw.writeInt(npc)
        pw.writeInt(0) // nextQuest

        return pw.createPacket()
    }
}