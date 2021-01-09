package net.maple.packets

import client.Avatar
import client.Character
import client.effects.EffectInterface
import client.inventory.ItemInventoryType
import client.inventory.ModifyInventoriesContext
import client.inventory.item.slots.ItemSlot
import client.inventory.operations.MoveInventoryOperation
import client.messages.Message
import client.messages.broadcast.BroadcastMessage
import client.player.StatType
import client.player.quest.Quest
import client.player.quest.QuestState
import client.stats.ModifyTemporaryStatContext
import client.stats.TemporaryStatExtensions.encodeLocal
import client.stats.TemporaryStatExtensions.encodeMask
import client.stats.TemporaryStatExtensions.encodeRemote
import field.movement.MovePath
import net.maple.SendOpcode
import net.maple.packets.ItemPackets.encode
import util.logging.LogType
import util.logging.Logger.log
import util.packet.Packet
import util.packet.PacketWriter
import java.util.function.Consumer
import java.util.stream.Collectors
import java.util.stream.Collectors.toMap
import kotlin.math.abs

object CharacterPackets {
    /**
     * -DBCHAR_CHARACTER = 0x1,
     * -DBCHAR_MONEY = 0x2,
     * -DBCHAR_ITEMSLOTEQUIP = 0x4,
     * -DBCHAR_ITEMSLOTCONSUME = 0x8,
     * -DBCHAR_ITEMSLOTINSTALL = 0x10,
     * -DBCHAR_ITEMSLOTETC = 0x20,
     * -DBCHAR_ITEMSLOTCASH = 0x40,
     * -DBCHAR_INVENTORYSIZE = 0x80,
     * -DBCHAR_SKILLRECORD = 0x100,
     * -DBCHAR_QUESTRECORD = 0x200,
     * -BCHAR_QUESTCOMPLETE = 0x4000,
     * -DBCHAR_SKILLCOOLTIME = 0x8000,
     *
     * @param pw  packet
     */
    fun Character.encodeData(pw: PacketWriter) {
        pw.writeLong(50175) // flags
        //pw.writeLong(-1); // all flags
        pw.write(0)
        pw.write(0)
        encodeStats(pw)
        pw.write(250) // friends
        pw.writeBool(false) // something with link
        pw.writeInt(this.meso)

        // inv slots
        pw.write(this.getInventory(ItemInventoryType.EQUIP).slotMax.toInt()) // equips
        pw.write(this.getInventory(ItemInventoryType.CONSUME).slotMax.toInt()) // consumes
        pw.write(this.getInventory(ItemInventoryType.INSTALL).slotMax.toInt()) // install
        pw.write(this.getInventory(ItemInventoryType.ETC).slotMax.toInt()) // etc
        pw.write(this.getInventory(ItemInventoryType.CASH).slotMax.toInt()) // cash

        // admin shop
        /*pw.writeInt(0);
        pw.writeInt(0);*/

        // equips
        val inventory = getInventory(ItemInventoryType.EQUIP).items

        inventory.entries.stream().filter { it.key >= -100 && it.key < 0 }
                .collect(toMap({ it.key }, { it.value }))
                .encodeEquipInventory(pw) // equipped

        inventory.entries.stream().filter { it.key >= -1000 && it.key < -100 }
                .collect(toMap({ it.key }, { it.value }))
                .encodeEquipInventory(pw) // mask

        inventory.entries.stream().filter { it.key >= 0 }
                .collect(toMap({ it.key }, { it.value }))
                .encodeEquipInventory(pw) // equip

        inventory.entries.stream().filter { it.key >= -1100 && it.key < -1000 }
                .collect(toMap({ it.key }, { it.value }))
                .encodeEquipInventory(pw) // dragon

        inventory.entries.stream().filter { it.key >= -1200 && it.key < -1100 }
                .collect(toMap({ it.key }, { it.value }))
                .encodeEquipInventory(pw) // mech

        getInventory(ItemInventoryType.CONSUME).items.encodeBundleInventory(pw)
        getInventory(ItemInventoryType.INSTALL).items.encodeBundleInventory(pw)
        getInventory(ItemInventoryType.ETC).items.encodeBundleInventory(pw)
        getInventory(ItemInventoryType.CASH).items.encodeBundleInventory(pw)

        // skills
        pw.writeShort(0) // count

        // skill cooldowns
        pw.writeShort(0)

        // quests
        val active: Collection<Quest> = this.quests.values.stream().filter { it.state === QuestState.PERFORM }.collect(Collectors.toList())
        pw.writeShort(active.size) // active count
        active.forEach {
            pw.writeShort(it.id)
            pw.writeMapleString(it.progress)
        }

        val completed: Collection<Quest> = this.quests.values.stream().filter { it.state === QuestState.COMPLETE }.collect(Collectors.toList())
        pw.writeShort(completed.size) // completed count
        completed.forEach {
            pw.writeShort(it.id)
            pw.writeLong(System.currentTimeMillis())
        }

        // minigames
        //pw.writeShort(0);

        // couples?
        //pw.writeShort(0); // couple
        //pw.writeShort(0); // friend
        //pw.writeShort(0); // marriage

        // uuuh
        /*for (int i = 0; i < 15; i++) {
            pw.writeInt(0);
        }*/

        // new year card
        //pw.writeShort(0);

        // more quests?
        //pw.writeShort(0); // count

        // wild hunter data (probably wont use this
        /*if (chr.getJob().getId() / 100 == 33) {
            pw.write(0);
            for (int i = 0; i < 5; i++) {
                pw.writeInt(0);
            }
        }

        pw.writeShort(0); // quest complete old?
        pw.writeShort(0); // visitor log*/
    }

    private fun Map<Short, ItemSlot>.encodeEquipInventory(pw: PacketWriter) {
        this.forEach {
            pw.writeShort(abs(it.key.toInt()) % 100)
            it.value.encode(pw)
        }
        pw.writeShort(0)
    }

    private fun Map<Short, ItemSlot>.encodeBundleInventory(pw: PacketWriter) {
        this.forEach {
            pw.write(it.key.toInt())
            it.value.encode(pw)
        }
        pw.write(0)
    }

    private fun Character.encodeStats(pw: PacketWriter) {
        with(this) {
            pw.writeInt(id) // character id
            pw.writeFixedString(name, 13)
            pw.write(gender)
            pw.write(skinColor) // skin color
            pw.writeInt(face) // face
            pw.writeInt(hair) // hair

            pets.forEach { pw.writeLong(it?.id?.toLong() ?: 0) }

            pw.write(level) // 51
            pw.writeShort(job)
            pw.writeShort(strength)
            pw.writeShort(dexterity)
            pw.writeShort(intelligence)
            pw.writeShort(luck) // 61
            pw.writeInt(health)
            pw.writeInt(maxHealth)
            pw.writeInt(mana)
            pw.writeInt(maxMana) // 77
            pw.writeShort(ap)
            pw.writeShort(sp)
            pw.writeInt(exp)
            pw.writeShort(fame) // 87
            pw.writeInt(69) // Gacha Exp (87-91)
            pw.writeInt(field.template.id)
            pw.write(portal.toInt())
            pw.writeInt(0) // playtime
            pw.writeShort(0) // subjob?
        }
    }

    fun Avatar.encodeLooks(pw: PacketWriter, mega: Boolean) {
        with(this) {
            pw.write(gender)
            pw.write(skinColor)
            pw.writeInt(face)
            pw.writeBool(mega)
            pw.writeInt(hair)

            encodeVisualEquips(pw)

            pets.forEach { pw.writeInt(it?.item ?: 0) }
        }
    }

    fun Avatar.encodeVisualEquips(pw: PacketWriter) {
        val equips = this.getEquips().items

        val base = equips.entries.stream()
                .filter { it.key >= -100 && it.key < 0 }
                .collect(toMap({ it.key }, { it.value }))
        val mask = HashMap<Short, ItemSlot>()

        equips.entries.stream()
                .filter { it.key >= -1000 && it.key < -100 && it.key.toInt() != -111 }
                .forEach {
                    val pos = (it.key + 100).toShort()
                    if (base.containsKey(pos)) {
                        mask[pos] = it.value
                    }
                    base[pos] = it.value
                }

        base.forEach { pw.write(abs(it.key.toInt())).writeInt(it.value.templateId) }
        pw.write(0xFF)
        mask.forEach { pw.write(abs(it.key.toInt())).writeInt(it.value.templateId) }
        pw.write(0xFF)

        pw.writeInt(equips[(-111).toShort()]?.templateId ?: 0)
    }

    /*public static void statUpdate(Character chr, List<StatType> statTypes) {
        statUpdate(chr, statTypes, true);
    }*/
    fun Character.statUpdate(statTypes: MutableList<StatType>, enableActions: Boolean) {
        val pw = PacketWriter(32)

        if (statTypes.size > 1) {
            statTypes.sort()
        }

        pw.writeHeader(SendOpcode.STAT_CHANGED)

        // exclusive request flag, causes the player to be unable to perform an action for the next 100ms if enabled
        pw.writeBool(enableActions)

        val flag = statTypes.stream().mapToInt(StatType::stat).reduce(0) { a: Int, b: Int -> a or b }

        pw.writeInt(flag)
        statTypes.forEach {
            when (it) {
                StatType.SKIN -> pw.write(this.skinColor)
                StatType.FACE -> pw.writeInt(this.face)
                StatType.HAIR -> pw.writeInt(this.hair)
                StatType.PET, StatType.PET2, StatType.PET3, StatType.TEMP_EXP -> log(LogType.UNCODED, "Pets and tempexp is not implemented (yet)", this)
                StatType.LEVEL -> pw.write(this.level)
                StatType.JOB -> pw.writeShort(this.job)
                StatType.STR -> pw.writeShort(this.strength)
                StatType.DEX -> pw.writeShort(this.dexterity)
                StatType.INT -> pw.writeShort(this.intelligence)
                StatType.LUK -> pw.writeShort(this.luck)
                StatType.MAX_HP -> pw.writeInt(this.maxHealth)
                StatType.HP -> pw.writeInt(this.health)
                StatType.MAX_MP -> pw.writeInt(this.maxMana)
                StatType.MP -> pw.writeInt(this.mana)
                StatType.AP -> pw.writeShort(this.ap)
                StatType.SP -> pw.writeShort(this.sp)
                StatType.EXP -> pw.writeInt(this.exp)
                StatType.FAME -> pw.writeShort(this.fame)
                StatType.MESO -> pw.writeInt(this.meso)
                else -> log(LogType.UNCODED, "Unimplemented stattype ${it.name}", this)
            }
        }

        pw.writeBool(false)
        pw.writeBool(false)

        this.write(pw.createPacket())

        if (statTypes.stream().anyMatch { it === StatType.SKIN || it === StatType.FACE || it === StatType.HAIR }) {
            modifyAvatar()
        }
    }

    fun Character.modifyTemporaryStats(consumer: Consumer<ModifyTemporaryStatContext>) {
        val context = ModifyTemporaryStatContext(this)

        consumer.accept(context)
        validateStats()

        if (context.resetOperations.isNotEmpty()) {
            run {
                val pw = PacketWriter(8)

                pw.writeHeader(SendOpcode.TEMPORARY_STAT_RESET)
                context.resetOperations.encodeMask(pw)
                pw.write(0); // IsMovementAffectingStat

                write(pw.createPacket())
            }

            run {
                val pw = PacketWriter(8)

                pw.writeHeader(SendOpcode.USER_TEMPORARY_STAT_RESET)
                pw.writeInt(id)
                context.resetOperations.encodeMask(pw)

                field.broadcast(pw.createPacket(), this)
            }
        }

        if (context.setOperations.isNotEmpty()) {
            run {
                val pw = PacketWriter(8)

                pw.writeHeader(SendOpcode.TEMPORARY_STAT_SET)
                context.setOperations.encodeLocal(pw)
                pw.writeShort(0); // tDelay
                pw.write(0); // IsMovementAffectingStat

                write(pw.createPacket())
            }

            run {
                val pw = PacketWriter(8)

                pw.writeHeader(SendOpcode.USER_TEMPORARY_STAT_SET)
                pw.writeInt(id)
                context.setOperations.encodeRemote(pw)
                pw.writeShort(0); // tDelay

                field.broadcast(pw.createPacket(), this)
            }
        }
    }

    fun Character.modifyInventory(consumer: Consumer<ModifyInventoriesContext>, enableActions: Boolean = false) {
        val context = ModifyInventoriesContext(allInventories)

        consumer.accept(context)

        val pw = PacketWriter(32)

        pw.writeHeader(SendOpcode.INVENTORY_OPERATION)
        pw.writeBool(enableActions)
        context.encode(pw)

        pw.writeBool(false)

        write(pw.createPacket())

        // equip check
        if (context.operations.stream().anyMatch { it.slot < 0 } ||
                context.operations.stream().filter { it is MoveInventoryOperation }.anyMatch { (it as MoveInventoryOperation).toSlot < 0 }) {
            validateStats()
            modifyAvatar()
        }
    }

    fun Character.modifyAvatar() {
        with(this) {
            val pw = PacketWriter(32)

            pw.writeHeader(SendOpcode.USER_AVATAR_MODIFIED)
            pw.writeInt(id)
            pw.write(0x01) // some flag
            encodeLooks(pw, false)
            pw.writeBool(false)
            pw.writeBool(false)
            pw.writeBool(false)
            pw.writeBool(false)
            pw.writeInt(0) // set items

            this.field.broadcast(pw.createPacket(), this)
        }
    }

    fun Character.showDamage(type: Byte, dmg: Int, mobId: Int, left: Byte) {
        val pw = PacketWriter(32)

        pw.writeHeader(SendOpcode.USER_HIT)
        pw.writeInt(this.id)
        pw.write(type.toInt())
        pw.writeInt(dmg)
        if (type > -2) {
            pw.writeInt(mobId)
            pw.write(left.toInt())
            val v22: Byte = 0
            pw.write(v22.toInt()) // stance?
            if (v22 > 0) {
                pw.write(0) // bPowerGuard
                pw.writeInt(0) // ptHit.x
                pw.write(0) // nHitAction
                pw.writeShort(0) // ptHit.x
                pw.writeShort(0) // ptHit.y
            } /* else {
                pw.write(0);
                pw.writeShort(0);
                pw.writeShort(0);
            }*/
            pw.write(0) // bGuard
            pw.writeBool(false) // v36, flag of 1 or 2
        }
        pw.writeInt(dmg) // nDelta
        if (dmg < 0) {
            pw.writeInt(4120002) // thief dodge skill
        }

        this.field.broadcast(pw.createPacket(), this)
    }

    fun Character.message(message: Message) {
        val pw = PacketWriter(32)

        pw.writeHeader(SendOpcode.MESSAGE)
        message.encode(pw)

        this.write(pw.createPacket())
    }

    fun Character.message(message: BroadcastMessage) {
        val pw = PacketWriter(32)

        pw.writeHeader(SendOpcode.BROADCAST_MSG)
        message.encode(pw)

        this.write(pw.createPacket())
    }

    fun Character.localEffect(effect: EffectInterface) {
        val pw = PacketWriter(12)

        pw.writeHeader(SendOpcode.USER_EFFECT_LOCAL)
        effect.encode(pw)

        this.write(pw.createPacket())
    }

    fun Character.remoteEffect(chr: Character, effect: EffectInterface) {
        val pw = PacketWriter(12)

        pw.writeHeader(SendOpcode.USER_EFFECT_LOCAL)
        pw.writeInt(chr.id)
        effect.encode(pw)

        this.write(pw.createPacket())
    }

    fun Avatar.move(path: MovePath): Packet {
        val pw = PacketWriter(32)

        pw.writeHeader(SendOpcode.USER_MOVE)
        pw.writeInt(this.id)
        path.encode(pw)

        return pw.createPacket()
    }
}