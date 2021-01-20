package scripting

import client.Character
import client.Client
import client.effects.field.TrembleFieldEffect
import client.effects.user.QuestEffect
import client.interaction.storage.ItemStorageInteraction
import client.inventory.item.slots.ItemSlotEquip
import client.inventory.item.templates.ItemEquipTemplate
import client.messages.IncEXPMessage
import client.messages.broadcast.types.AlertMessage
import client.messages.broadcast.types.NoticeWithoutPrefixMessage
import client.messages.broadcast.types.UtilDlgExMessage
import client.player.quest.Quest
import field.Field
import field.obj.drop.AbstractFieldDrop
import field.obj.life.FieldMob
import field.obj.life.FieldMobTemplate
import field.obj.life.FieldNPC
import managers.ItemManager
import managers.MobManager
import net.maple.packets.CharacterPackets.localEffect
import net.maple.packets.CharacterPackets.message
import net.maple.packets.CharacterPackets.modifyInventory
import net.maple.packets.FieldPackets.fieldEffect
import net.maple.packets.GuildPackets
import net.maple.packets.GuildPackets.getLoadGuildPacket
import net.server.Server
import org.graalvm.collections.Pair
import scripting.npc.NPCScriptManager
import world.guild.Guild
import world.ranking.RankingKeeper
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.function.Function

abstract class AbstractPlayerInteraction(val c: Client) {

    val player: Character get() = c.character

    fun gainItem(id: Int, quantity: Int) {
        gainItemInternal(id, quantity)
        player.localEffect(QuestEffect(id, quantity))
    }

    fun massGainItem(items: Array<IntArray>) {
        val entries: MutableList<Pair<Int, Int>> = ArrayList()
        Arrays.stream(items).forEach { item: IntArray ->
            gainItemInternal(item[0], item[1])
            entries.add(Pair.create(item[0], item[1]))
        }
        player.localEffect(QuestEffect(entries))
    }

    private fun gainItemInternal(id: Int, quantity: Int) {
        if (quantity > 0) {
            val item = ItemManager.getItem(id)
            player.modifyInventory({ it.add(item, quantity.toShort()) })
        } else {
            player.modifyInventory({ it.take(id, (-quantity).toShort()) })
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
        player.message(msg)
    }

    fun warp(id: Int, portal: String) {
        player.changeField(id, portal)
    }

    fun warp(id: Int) {
        player.changeField(id)
    }

    val mapId: Int get() = player.fieldId

    fun sendBlue(message: String) {
        player.message(NoticeWithoutPrefixMessage(message))
    }

    fun sendNpcMessage(message: String, npc: Int) {
        player.message(UtilDlgExMessage(message, npc))
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
        val template = ItemManager.getItem(id)
        return template is ItemEquipTemplate
    }

    fun getEquipById(id: Int): ItemSlotEquip? {
        val template = ItemManager.getItem(id)
        return template.toItemSlot() as ItemSlotEquip?
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
            player.modifyInventory({ it.add(equip) })
        }
    }

    fun tremble(heavy: Boolean, delay: Int) {
        player.field.fieldEffect(TrembleFieldEffect(heavy, delay))
    }

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
        player.message(AlertMessage(msg))
    }

    fun openStorage(npcId: Int) {
        ItemStorageInteraction(npcId, c.storage).open(player)
    }

    fun getGuild(): Guild? {
        return player.guild
    }

    fun changeGuildName(name: String) {
        GuildPackets.changeGuildName(player, name)
    }

    fun loadGuild() {
        player.guild?.let {
            c.write(it.getLoadGuildPacket())
        }
    }

    fun getMobTemplate(id: Int): FieldMobTemplate {
        return MobManager.getMob(id)
    }

    fun getMap(): Field {
        return player.field
    }

    val mobsOnField: List<Any>
        get() {
            val mobs: MutableList<FieldMob> = ArrayList<FieldMob>()
            c.character.field.getObjects<FieldMob>().forEach { mobs.add(it) }
            return mobs
        }

    fun getRankings(): RankingKeeper {
        return RankingKeeper
    }

    fun getServer(): Server {
        return Server
    }

    fun getCharacters(): List<Character> {
        return player.field.getObjects<Character>().toList()
    }

    fun getMobs(): List<FieldMob> {
        return player.field.getObjects<FieldMob>().toList()
    }

    fun getNpcs(): List<FieldNPC> {
        return player.field.getObjects<FieldNPC>().toList()
    }

    fun getDrops(): List<AbstractFieldDrop> {
        return player.field.getObjects<AbstractFieldDrop>().toList()
    }

    fun getMobByObjId(id: Int): FieldMob? {
        return player.field.getObject(id)
    }
}