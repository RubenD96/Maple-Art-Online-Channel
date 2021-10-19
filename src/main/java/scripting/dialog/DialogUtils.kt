package scripting.dialog

import client.Character
import client.Client
import client.interaction.storage.ItemStorageInteraction
import client.inventory.ModifyInventoriesContext
import client.inventory.item.slots.ItemSlotEquip
import client.player.Beauty
import client.player.quest.reward.ItemQuestReward
import client.player.quest.reward.MasteryQuestReward
import client.player.quest.reward.QuestReward
import client.player.quest.reward.QuestRewardType
import field.obj.drop.DropEntry
import field.obj.life.FieldMobTemplate
import managers.BeautyManager
import managers.ItemManager
import managers.MobManager
import net.database.BeautyAPI
import net.database.DropAPI
import net.maple.packets.CharacterPackets.modifyInventory
import java.lang.Character.isUpperCase
import java.lang.Character.toLowerCase
import java.util.*
import java.util.function.Consumer
import java.util.stream.Collectors
import kotlin.collections.set

object DialogUtils {

    fun String.blue(): String {
        return "#b$this#k"
    }

    fun String.red(): String {
        return "#r$this#k"
    }

    fun String.green(): String {
        return "#g$this#k"
    }

    fun String.purple(): String {
        return "#d$this#k"
    }

    fun String.black(): String {
        return "#k$this#k"
    }

    fun String.bold(): String {
        return "#e$this#n"
    }

    fun Number.blue(): String {
        return "#b$this#k"
    }

    fun Number.red(): String {
        return "#r$this#k"
    }

    fun Number.green(): String {
        return "#g$this#k"
    }

    fun Number.purple(): String {
        return "#d$this#k"
    }

    fun Number.black(): String {
        return "#k$this#k"
    }

    fun Number.bold(): String {
        return "#e$this#n"
    }

    fun Int.itemName(): String {
        return "#t$this#"
    }

    fun Int.itemCount(): String {
        return "#c$this#"
    }

    fun Int.itemImage(): String {
        return "#i$this#"
    }

    fun Int.itemDetails(): String {
        return "#z$this#"
    }

    fun String.wzImage(): String {
        return "#f$this#"
    }

    val playerName get() = "#h #"

    fun Int.mapName(): String {
        return "#m$this#"
    }

    fun Int.mobName(): String {
        return "#o$this#"
    }

    fun Int.npcName(): String {
        return "#p$this#"
    }

    fun Int.skillName(): String {
        return "#q$this#"
    }

    fun Int.skillImage(): String {
        return "#s$this#"
    }

    fun Int.toProgressBar(): String {
        return "#B$this#"
    }

    val newLine get() = "\r\n"

    fun String.letters(): String {
        val str = StringBuilder()
        for (i in indices) {
            if (this[i] == ' ') {
                str.append("\t")
            } else {
                str.append("#i").append(convert(this[i])).append("#")
            }
        }
        return str.toString()
    }

    fun convert(input: Char): Int {
        val upper = 3991000
        val lower = 3991026
        var output = if (isUpperCase(input)) upper else lower
        when (toLowerCase(input)) {
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
            '1' -> output -= 990
            '2' -> output -= 989
            '3' -> output -= 988
            '4' -> output -= 987
            '5' -> output -= 986
            '6' -> output -= 985
            '7' -> output -= 984
            '8' -> output -= 983
            '9' -> output -= 982
            '0' -> output -= 981
            '+' -> output -= 978
            '-' -> output -= 977
            else -> output = 0
        }
        return output
    }

    fun getMobDrops(id: Int): List<DropEntry> {
        val template: FieldMobTemplate = MobManager.getMob(id)
        if (template.id != id) return ArrayList()

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
        DropAPI.updateDropChance(mid, iid, chance)
    }

    fun removeDrop(mid: Int, iid: Int) {
        DropAPI.removeDrop(mid, iid)
    }

    fun editMinMaxChance(mid: Int, iid: Int, min: Int, max: Int, chance: Double) {
        DropAPI.updateMinMaxChance(mid, iid, min, max, chance)
    }

    fun Client.openStorage() {
        ItemStorageInteraction(script!!.script.id, storage).open(character)
    }

    fun postRewards(rewards: List<QuestReward>, chr: Character? = null, giveRewards: Boolean = false): String {
        var message = ""
        rewards.forEach {
            message += it.message
            if (giveRewards && chr != null) {
                when (it.type) {
                    QuestRewardType.EXP -> chr.gainExp(it.value)
                    QuestRewardType.MESOS -> chr.gainMeso(it.value)
                    QuestRewardType.FAME -> chr.fame += it.value
                    QuestRewardType.RANDOM -> TODO() // ???
                    QuestRewardType.CLOSENESS -> TODO()
                    QuestRewardType.ITEM -> {
                        if (it is ItemQuestReward) {
                            chr.modifyInventory({ i: ModifyInventoriesContext ->
                                i.add(
                                    ItemManager.getItem(it.value),
                                    it.quantity
                                )
                            })
                        }
                    }
                    QuestRewardType.MASTERY -> {
                        if (it is MasteryQuestReward) {
                            TODO()
                        }
                    }
                }
            }
        }

        return message
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
        items!!.values.forEach(Consumer { pair: Any ->
            readableItemMap[(pair as AbstractMap<*, *>)["0"] as Int] = pair["1"] as Int
        })
        println(readableItemMap)
    }

    val allHairs: List<Beauty> get() = ArrayList(BeautyManager.hairs.values)

    fun getEnabledHairs(gender: Int): List<Beauty> {
        return BeautyManager.hairs.values.stream()
            .filter(Beauty::isEnabled)
            .filter { it.gender == gender }
            .collect(Collectors.toList())
    }

    fun getDisabledHairs(gender: Int): List<Beauty> {
        return BeautyManager.hairs.values.stream()
            .filter { !it.isEnabled }
            .filter { it.gender == gender }
            .collect(Collectors.toList())
    }

    fun updateHair(id: Int) {
        val b = BeautyManager.hairs[id] ?: return
        b.isEnabled = !b.isEnabled
        BeautyAPI.updateHair(id)
    }

    fun Character.setHardcore(enable: Boolean) {
        hardcore = enable
    }

    fun createEquip(id: Int): ItemSlotEquip? {
        val template = ItemManager.getItem(id)
        return template.toItemSlot() as? ItemSlotEquip
    }
}