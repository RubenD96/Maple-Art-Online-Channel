package scripting

import client.Character
import client.Client
import client.effects.field.TrembleFieldEffect
import client.effects.user.QuestEffect
import client.interaction.storage.ItemStorageInteraction
import client.inventory.item.templates.ItemEquipTemplate
import client.inventory.slots.ItemSlotEquip
import client.messages.IncEXPMessage
import client.messages.broadcast.types.AlertMessage
import client.messages.broadcast.types.NoticeWithoutPrefixMessage
import client.player.quest.Quest
import field.obj.FieldObjectType
import field.obj.life.FieldMob
import field.obj.life.FieldMobTemplate
import managers.ItemManager
import managers.MobManager
import net.maple.packets.CharacterPackets
import net.maple.packets.GuildPackets
import org.graalvm.collections.Pair
import scripting.npc.NPCScriptManager
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.function.Function

abstract class AbstractPlayerInteraction(val c: Client) {

    val player: Character get() = c.character

    fun gainItem(id: Int, quantity: Int) {
        gainItemInternal(id, quantity)
        c.write(CharacterPackets.localEffect(QuestEffect(id, quantity)))
    }

    fun massGainItem(items: Array<IntArray>) {
        val entries: MutableList<Pair<Int, Int>> = ArrayList()
        Arrays.stream(items).forEach { item: IntArray ->
            gainItemInternal(item[0], item[1])
            entries.add(Pair.create(item[0], item[1]))
        }
        c.write(CharacterPackets.localEffect(QuestEffect(entries)))
    }

    private fun gainItemInternal(id: Int, quantity: Int) {
        if (quantity > 0) {
            val item = ItemManager.getItem(id)
            if (item != null) {
                CharacterPackets.modifyInventory(player,
                        { it.add(item, quantity.toShort()) },
                        false)
            }
        } else {
            CharacterPackets.modifyInventory(player,
                    { it.take(id, (-quantity).toShort()) },
                    false)
        }
    }

    fun gainMeso(meso: Int) {
        player.gainMeso(meso, true)
    }

    fun haveItem(id: Int, quantity: Int): Boolean {
        return player.getItemQuantity(id) >= quantity
    }

    fun gainExp(exp: Int) {
        player.gainExp(exp)
        val msg = IncEXPMessage()
        msg.exp = exp
        msg.onQuest = true
        player.write(CharacterPackets.message(msg))
    }

    fun warp(id: Int, portal: String) {
        player.changeField(id, portal)
    }

    fun warp(id: Int) {
        player.changeField(id)
    }

    val mapId: Int get() = player.fieldId

    fun sendBlue(message: String) {
        player.write(CharacterPackets.message(NoticeWithoutPrefixMessage(message)))
    }

    fun setQuestProgress(qid: Int, mob: Int, progress: String) {
        val quest: Quest = player.quests[qid] ?: return
        quest.progress(mob, progress.toInt())
    }

    fun getQuest(id: Int): Quest? {
        return player.quests[id]
    }

    fun openNpc(id: Int) {
        if (!NPCScriptManager.converse(c, id)) {
            sendBlue("Npc does not have a script")
        }
    }

    fun isEquip(id: Int): Boolean {
        val template = ItemManager.getItem(id) ?: return false
        return template is ItemEquipTemplate
    }

    fun getEquipById(id: Int): ItemSlotEquip? {
        val template = ItemManager.getItem(id) ?: return null
        return template.toItemSlot() as ItemSlotEquip
    }

    fun gainStatItem(id: Int, obj: Any) {
        val stats: AbstractMap<String, Int> = obj as AbstractMap<String, Int>
        val equip = getEquipById(id)
        val template = ItemManager.getItem(id) as ItemEquipTemplate
        if (equip != null) {
            equip.str = stats["STR"]?.toShort() ?: template.incSTR
            equip.dex = stats["DEX"]?.toShort() ?: template.incDEX
            equip.luk = stats["LUK"]?.toShort() ?: template.incLUK
            equip.int = stats["INT"]?.toShort() ?: template.incINT
            equip.pad = stats["PAD"]?.toShort() ?: template.incPAD
            equip.mad = stats["MAD"]?.toShort() ?: template.incMAD
            equip.acc = stats["ACC"]?.toShort() ?: template.incACC
            equip.eva = stats["EVA"]?.toShort() ?: template.incEVA
            equip.jump = stats["JUMP"]?.toShort() ?: template.incJump
            equip.speed = stats["SPEED"]?.toShort() ?: template.incSpeed
            equip.pdd = stats["PDD"]?.toShort() ?: template.incPDD
            equip.mdd = stats["MDD"]?.toShort() ?: template.incMDD
            equip.maxHP = stats["HP"]?.toShort() ?: template.incMaxHP.toShort()
            equip.maxMP = stats["MP"]?.toShort() ?: template.incMaxMP.toShort()
            equip.ruc = stats["SLOTS"]?.toByte() ?: 7
            CharacterPackets.modifyInventory(player, { it.add(equip) }, false)
        }
    }

    fun tremble(heavy: Boolean, delay: Int) {
        player.field.broadcast(CharacterPackets.fieldEffect(TrembleFieldEffect(heavy, delay)))
    }

    @JvmOverloads
    fun openNpcIn(npc: Int, time: Int, dispose: Boolean = true) {
        c.ch.eventLoop().schedule({
            if (dispose) {
                c.lastNpcClick = 0
                NPCScriptManager.dispose(c)
            }
            NPCScriptManager.converse(c, npc)
        }, time.toLong(), TimeUnit.MILLISECONDS)
    }

    fun executeAfter(func: Function<AbstractPlayerInteraction?, Void?>, after: Int) {
        c.ch.eventLoop().schedule<Void?>({ func.apply(this) }, after.toLong(), TimeUnit.MILLISECONDS)
    }

    fun alert(msg: String) {
        c.write(CharacterPackets.message(AlertMessage(msg)))
    }

    fun openStorage(npcId: Int) {
        ItemStorageInteraction(npcId, c.storage).open(player)
    }

    fun changeGuildName(name: String?) {
        GuildPackets.changeGuildName(player, name)
    }

    fun loadGuild() {
        if (player.guild == null) return
        c.write(GuildPackets.getLoadGuildPacket(player.guild))
    }

    fun getMobTemplate(id: Int): FieldMobTemplate? {
        return MobManager.getMob(id)
    }

    val mobsOnField: List<Any>
        get() {
            val field = c.character.field
            if (field != null) {
                val mobs: MutableList<FieldMob> = ArrayList<FieldMob>()
                field.getObjects(FieldObjectType.MOB).forEach { mobs.add(it as FieldMob) }
                return mobs
            }
            return ArrayList()
        }

}