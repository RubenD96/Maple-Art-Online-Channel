package field.obj.life

import client.Character
import client.inventory.item.slots.ItemSlotBundle
import client.inventory.item.templates.ItemEquipTemplate
import client.inventory.item.variation.ItemVariationType
import client.messages.IncEXPMessage
import client.player.quest.QuestState
import field.obj.drop.AbstractFieldDrop
import field.obj.drop.ItemDrop
import field.obj.drop.MesoDrop
import managers.ItemManager
import net.database.DropAPI.getMobDrops
import net.maple.SendOpcode
import net.maple.packets.CharacterPackets.message
import net.server.Server
import scripting.mob.MobScriptManager
import util.packet.Packet
import util.packet.PacketWriter
import java.awt.Point
import java.util.*
import kotlin.collections.HashMap
import kotlin.math.max
import kotlin.math.min

class FieldMob(val template: FieldMobTemplate, left: Boolean) : AbstractFieldControlledLife() {

    var hp = 0
    var mp = 0
    var home: Short = 0
    var time = -1
    var controllerDistance = 0

    // Stores which CID has done X dmg to the mob
    private val damageIndex: MutableMap<Int, Int> = HashMap()

    init {
        moveAction = 3
    }

    fun damage(chr: Character, damage: Int) {
        synchronized(this) {
            hp -= damage

            var add = damage
            if (hp <= 0) add = damage - hp
            addToDamageIndex(chr, add)

            if (template.onHit) MobScriptManager.onHit(chr.client, this)
        }

        var indicator = hp / template.maxHP.toFloat() * 100f
        indicator = min(100f, indicator)
        indicator = max(0f, indicator)
        chr.write(showHpBar(indicator))

        if (hp <= 0) {
            kill(chr)
        }
    }

    private fun addToDamageIndex(chr: Character, damage: Int) {
        damageIndex[chr.id]?.let {
            damageIndex[chr.id]?.plus(damage)
        } ?: run {
            damageIndex[chr.id] = damage
        }
    }

    fun kill(chr: Character) {
        if (template.onDeath) MobScriptManager.onDeath(chr.client, this)

        field.leave(this, leaveFieldPacket)
        chr.gainExp(template.exp) // todo share

        if (template.isBoss) { // only give party a kc if its a boss, not for regular mobs
            chr.party?.onlineMembers?.forEach {
                if (it.field == field.template.id) { // same id
                    Server.getCharacter(it.cid)?.run {
                        if (this.field == field) { // same instance
                            this.updateMobKills(template)
                        }
                    }
                }
            } ?: chr.updateMobKills(template)
        } else {
            chr.updateMobKills(template)
        }

        val msg = IncEXPMessage()
        msg.isLastHit = true
        msg.exp = template.exp
        chr.message(msg)

        if (chr.registeredQuestMobs.contains(template.id)) {
            chr.quests.values.stream()
                    .filter { it.state === QuestState.PERFORM }
                    .filter { it.mobs.containsKey(template.id) }
                    .forEach { it.progress(template.id) }
        }

        if (time != -1) {
            field.queueRespawn(template.id, time, System.currentTimeMillis() + time * 1000)
        }

        drop(chr)
    }

    fun drop(owner: Character) {
        if (template.drops == null) {
            template.drops = getMobDrops(template.id)
        }
        val drops: MutableList<AbstractFieldDrop> = ArrayList<AbstractFieldDrop>()

        template.drops?.forEach {
            if (it.quest != 0) {
                val quest = owner.quests[it.quest]
                if (quest != null && quest.state !== QuestState.PERFORM) {
                    return@forEach
                }
            }
            if (Math.random() * 100 < it.chance) {
                if (it.id == 0) { // meso
                    val amount = (Math.random() * it.max + it.min).toInt()
                    drops.add(MesoDrop(owner.id, this, amount, it.quest))
                } else { // item
                    ItemManager.getItem(it.id).let { template ->
                        val item = if (template is ItemEquipTemplate) template.toItemSlot(ItemVariationType.getRandom())
                        else template.toItemSlot()

                        if (item is ItemSlotBundle) {
                            item.number = ((Math.random() * it.max + it.min).toInt().toShort())
                        }
                        drops.add(ItemDrop(owner.id, this, item, it.quest))
                    }
                }
            }
        }

        val bounds = field.template.mapArea
        drops.forEach {
            it.field = field
            var x = position.x + (drops.indexOf(it) - (drops.size - 1) / 2) * 28
            val y = position.y
            x = min(bounds.maxX - 10, x.toDouble()).toInt()
            x = max(bounds.minX + 10, x.toDouble()).toInt()
            it.position = Point(x, y)
            it.expire = System.currentTimeMillis() + 300000
        }

        drops.forEach {
            field.enter(it)
        }
    }

    private fun showHpBar(indicator: Float): Packet {
        val pw = PacketWriter(7)

        pw.writeHeader(SendOpcode.MOB_HP_INDICATOR)
        pw.writeInt(id)
        pw.write(indicator.toInt())

        return pw.createPacket()
    }

    override fun getChangeControllerPacket(setAsController: Boolean): Packet {
        val pw = PacketWriter(32)

        pw.writeHeader(SendOpcode.MOB_CHANGE_CONTROLLER)
        pw.writeBool(setAsController)
        pw.writeInt(id)
        if (setAsController) encode(pw, MobSummonType.REGEN)

        return pw.createPacket()
    }

    private fun encode(pw: PacketWriter, type: MobSummonType) {
        pw.write(1)
        pw.writeInt(template.id)

        // temp stats
        pw.writeLong(0)
        pw.writeLong(0)

        pw.writePosition(position)
        pw.write(moveAction.toInt())
        pw.writeShort(foothold)
        pw.writeShort(home)
        pw.write(type.type)

        if (type == MobSummonType.REVIVED || type.type >= 0) {
            pw.writeInt(0) // summon option
        }

        pw.write(0)
        pw.writeInt(0)
        pw.writeInt(0)
    }

    fun getEnterFieldPacket(type: MobSummonType): Packet {
        val pw = PacketWriter(32)

        pw.writeHeader(SendOpcode.MOB_ENTER_FIELD)
        pw.writeInt(id)
        encode(pw, type)

        return pw.createPacket()
    }

    override val enterFieldPacket: Packet
        get() {
            return getEnterFieldPacket(MobSummonType.REGEN)
        }

    override val leaveFieldPacket: Packet
        get() {
            val pw = PacketWriter(7)

            pw.writeHeader(SendOpcode.MOB_LEAVE_FIELD)
            pw.writeInt(id)
            pw.write(1)

            return pw.createPacket()
        }
}