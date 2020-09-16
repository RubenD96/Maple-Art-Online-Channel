package scripting.npc

import client.Client
import client.player.Beauty
import field.obj.drop.DropEntry
import field.obj.life.FieldMobTemplate
import managers.BeautyManager
import managers.MobManager
import net.database.BeautyAPI
import net.database.DropAPI
import net.database.DropAPI.updateDropChance
import net.database.DropAPI.updateMinMaxChance
import net.maple.packets.ConversationPackets
import scripting.AbstractPlayerInteraction
import java.util.*
import java.util.function.Consumer
import java.util.stream.Collectors
import kotlin.collections.ArrayList
import kotlin.collections.LinkedHashMap
import kotlin.collections.set

open class NPCConversationManager(c: Client, val npcId: Int) : AbstractPlayerInteraction(c) {

    var text = ""

    fun sendOk(text: String) {
        c.write(ConversationPackets.getOkMessagePacket(npcId, 0, text))
    }

    fun sendOk(text: String, speaker: Int) {
        c.write(ConversationPackets.getOkMessagePacket(npcId, speaker, text))
    }

    fun sendNext(text: String) {
        c.write(ConversationPackets.getNextMessagePacket(npcId, 0, text))
    }

    fun sendNext(text: String, speaker: Int) {
        c.write(ConversationPackets.getNextMessagePacket(npcId, speaker, text))
    }

    fun sendPrev(text: String) {
        c.write(ConversationPackets.getPrevMessagePacket(npcId, 0, text))
    }

    fun sendPrev(text: String, speaker: Int) {
        c.write(ConversationPackets.getPrevMessagePacket(npcId, speaker, text))
    }

    fun sendNextPrev(text: String) {
        c.write(ConversationPackets.getNextPrevMessagePacket(npcId, 0, text))
    }

    fun sendNextPrev(text: String, speaker: Int) {
        c.write(ConversationPackets.getNextPrevMessagePacket(npcId, speaker, text))
    }

    fun sendYesNo(text: String) {
        c.write(ConversationPackets.getYesNoMessagePacket(npcId, 0, text))
    }

    fun sendYesNo(text: String, speaker: Int) {
        c.write(ConversationPackets.getYesNoMessagePacket(npcId, speaker, text))
    }

    fun sendGetText(text: String, def: String, min: Int, max: Int) {
        c.write(ConversationPackets.getTextMessagePacket(npcId, 0, text, def, min, max))
    }

    fun sendGetText(text: String, def: String, min: Int, max: Int, speaker: Int) {
        c.write(ConversationPackets.getTextMessagePacket(npcId, speaker, text, def, min, max))
    }

    fun sendGetNumber(text: String, def: Int, min: Int, max: Int) {
        c.write(ConversationPackets.getNumberMessagePacket(npcId, 0, text, def, min, max))
    }

    fun sendGetNumber(text: String, def: Int, min: Int, max: Int, speaker: Int) {
        c.write(ConversationPackets.getNumberMessagePacket(npcId, speaker, text, def, min, max))
    }

    fun sendSimple(text: String) {
        c.write(ConversationPackets.getSimpleMessagePacket(npcId, 0, text))
    }

    fun sendSimple(text: String, speaker: Int) {
        c.write(ConversationPackets.getSimpleMessagePacket(npcId, speaker, text))
    }

    fun sendAcceptDecline(text: String) {
        c.write(ConversationPackets.getAcceptMessagePacket(npcId, 0, text))
    }

    fun sendAcceptDecline(text: String, speaker: Int) {
        c.write(ConversationPackets.getAcceptMessagePacket(npcId, speaker, text))
    }

    fun sendGetTextBox() {
        c.write(ConversationPackets.getBoxTextMessagePacket(npcId, 0, "", 48, 6))
    }

    fun sendGetTextBox(speaker: Int) {
        c.write(ConversationPackets.getBoxTextMessagePacket(npcId, speaker, "", 48, 6))
    }

    fun sendGetTextBox(def: String, cols: Int, rows: Int) {
        c.write(ConversationPackets.getBoxTextMessagePacket(npcId, 0, def, cols, rows))
    }

    fun sendGetTextBox(def: String, cols: Int, rows: Int, speaker: Int) {
        c.write(ConversationPackets.getBoxTextMessagePacket(npcId, speaker, def, cols, rows))
    }

    fun sendSlide(text: String, type: Int, selected: Int) {
        c.write(ConversationPackets.getSlideMenuMessagePacket(npcId, 0, text, type, selected))
    }

    fun sendSlide(text: String, type: Int, selected: Int, speaker: Int) {
        c.write(ConversationPackets.getSlideMenuMessagePacket(npcId, speaker, text, type, selected))
    }

    open fun dispose() {
        NPCScriptManager.dispose(this)
    }

    fun startQuest(qid: Int) {
        player.startQuest(qid, npcId)
    }

    fun completeQuest(qid: Int) {
        player.completeQuest(qid)
    }

    /**
     * cm.letters("Hello world"); will show Christmas letters Hello world
     *
     * @param input the text to turn into christmas :)
     * @return String with item images
     */
    fun letters(input: String): String {
        val str = StringBuilder()
        for (i in input.indices) {
            if (input[i] == ' ') {
                str.append("\t")
            } else {
                str.append("#i").append(convert(input[i])).append("#")
            }
        }
        return str.toString()
    }

    private fun convert(input: Char): Int {
        val upper = 3991000
        val lower = 3991026
        var output = if (Character.isUpperCase(input)) upper else lower
        when (Character.toLowerCase(input)) {
            'a' -> output += 0
            'b' -> output += 1
            'c' -> output += 2
            'd' -> output += 3
            'e' -> output += 4
            'f' -> output += 5
            'g' -> output += 6
            'h' -> output += 7
            'i' -> output += 8
            'j' -> output += 9
            'k' -> output += 10
            'l' -> output += 11
            'm' -> output += 12
            'n' -> output += 13
            'o' -> output += 14
            'p' -> output += 15
            'q' -> output += 16
            'r' -> output += 17
            's' -> output += 18
            't' -> output += 19
            'u' -> output += 20
            'v' -> output += 21
            'w' -> output += 22
            'x' -> output += 23
            'y' -> output += 24
            'z' -> output += 25
        }
        return output
    }

    fun getMobDrops(id: Int): List<DropEntry> {
        val template: FieldMobTemplate = MobManager.getMob(id) ?: return ArrayList()
        if (template.drops == null) {
            template.drops = DropAPI.getMobDrops(template.id)
        }
        return template.drops?.toList() ?: return ArrayList()
    }

    fun addMobDrop(mid: Int, iid: Int, chance: Double) {
        addMobDrop(mid, iid, 1, 1, 0, chance)
    }

    fun addMobDrop(mid: Int, iid: Int, min: Int, max: Int, chance: Double) {
        addMobDrop(mid, iid, min, max, 0, chance)
    }

    fun addMobDrop(mid: Int, iid: Int, min: Int, max: Int, questid: Int, chance: Double) {
        DropAPI.addMobDrop(mid, iid, min, max, questid, chance)
    }

    fun editDropChance(mid: Int, iid: Int, chance: Double) {
        updateDropChance(mid, iid, chance)
    }

    fun removeDrop(mid: Int, iid: Int) {
        DropAPI.removeDrop(mid, iid)
    }

    fun editMinMaxChance(mid: Int, iid: Int, min: Int, max: Int, chance: Double) {
        updateMinMaxChance(mid, iid, min, max, chance)
    }

    fun openStorage() {
        openStorage(npcId)
    }

    fun postRewards(rewards: Any) {
        val map: AbstractMap<*, *> = rewards as AbstractMap<*, *>
        val counter = 1
        val isNull = false
        val exp = map["exp"]
        val mesos = map["mesos"]
        val fame = map["fame"]
        val random = map["random"]
        val items = map["items"] as AbstractMap<*, *>?
        val readableItemMap: MutableMap<Int, Int> = LinkedHashMap()
        items!!.values.forEach(Consumer { pair: Any -> readableItemMap[(pair as AbstractMap<*, *>)["0"] as Int] = pair["1"] as Int })
        println(readableItemMap)
    }

    val allHairs: List<Beauty> get() = ArrayList(BeautyManager.getHairs().values)

    fun getEnabledHairs(gender: Int): List<Beauty> {
        return BeautyManager.getHairs().values.stream()
                .filter(Beauty::isEnabled)
                .filter { it.gender == gender }
                .collect(Collectors.toList())
    }

    fun getDisabledHairs(gender: Int): List<Beauty> {
        return BeautyManager.getHairs().values.stream()
                .filter { !it.isEnabled }
                .filter { it.gender == gender }
                .collect(Collectors.toList())
    }

    fun updateHair(id: Int) {
        val b = BeautyManager.getHairs()[id] ?: return
        b.isEnabled = !b.isEnabled
        BeautyAPI.updateHair(id)
    }
}