package field.obj.life

import client.Character
import client.inventory.item.slots.ItemSlotBundle
import client.inventory.item.templates.ItemEquipTemplate
import client.inventory.item.variation.ItemVariationType
import client.messages.IncEXPMessage
import client.player.quest.QuestState
import field.obj.FieldObjectType
import field.obj.drop.AbstractFieldDrop
import field.obj.drop.ItemDrop
import field.obj.drop.MesoDrop
import managers.ItemManager
import net.database.DropAPI.getMobDrops
import net.maple.SendOpcode
import net.maple.packets.CharacterPackets.message
import scripting.mob.MobScriptManager
import util.packet.Packet
import util.packet.PacketWriter
import java.awt.Point
import java.util.*
import kotlin.math.max
import kotlin.math.min

class FieldMob(val template: FieldMobTemplate, left: Boolean) : AbstractFieldControlledLife() {

    var hp = 0
    var mp = 0
    var home: Short = 0
    var time = 0
    var controllerDistance = 0

    init {
        moveAction = 3
    }

    fun damage(chr: Character, damage: Int) {
        synchronized(this) {
            hp -= damage
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

    fun kill(chr: Character) {
        if (template.onDeath) MobScriptManager.onDeath(chr.client, this)

        field.leave(this, leaveFieldPacket)
        chr.gainExp(template.exp) // todo share

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

        field.queueRespawn(template.id, time, System.currentTimeMillis() + time * 1000)

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

        val bounds = field.mapArea
        drops.forEach {
            var x = position.x + (drops.indexOf(it) - (drops.size - 1) / 2) * 28
            val y = position.y
            x = min(bounds.maxX - 10, x.toDouble()).toInt()
            x = max(bounds.minX + 10, x.toDouble()).toInt()
            it.position = Point(x, y)
            it.expire = System.currentTimeMillis() + 300000
        }

        drops.forEach {
            it.field = field
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

    override val fieldObjectType = FieldObjectType.MOB

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