package client.pet

import client.Character
import client.inventory.item.slots.ItemSlotPet
import constants.GameConstants
import field.obj.FieldOwnedObject
import field.obj.life.AbstractFieldLife
import net.maple.SendOpcode
import net.maple.packets.CharacterPackets.modifyInventory
import util.packet.Packet
import util.packet.PacketWriter
import kotlin.math.max
import kotlin.random.Random

class FieldUserPet(override val owner: Character, val item: ItemSlotPet) : AbstractFieldLife(), FieldOwnedObject {

    var idx: Byte = -1
        set(value) {
            field = value
            item.equipSlot = value
        }
    var nameTag: Boolean = false
    var chatBalloon: Boolean = false
    var overeat: Short = 0

    init {
        field = owner.field
        position = owner.position
        foothold = owner.foothold
    }

    fun updatePetItem() {
        owner.modifyInventory({ it.update(item) })
    }

    fun onEatFood(repleteness: Int) {
        var inc = repleteness
        if (item.repleteness + repleteness > 100) {
            inc = 100 - item.repleteness
        }

        item.repleteness = (item.repleteness + inc).toByte() // ugh kotlin...
        var modified = inc > 0

        // tRemainHungriness = DateTime.Now.AddMilliseconds(1000 * (Constants.Rand.Next() % 10 + nOvereat * nOvereat + 10));

        if (10 * inc / repleteness <= Random.nextInt() % 12 || item.repleteness / 10 <= Random.nextInt() % 12) {
            if (inc == 0) {
                var rand = Random.nextInt()

                if (overeat != 10.toShort()) rand %= 10 - overeat
                if (rand > 0) {
                    overeat = (overeat + 1).toShort()
                } else {
                    item.tameness = max(item.tameness - 1, 0).toShort()
                    overeat = 10
                    modified = true
                }
            }
        } else {
            increaseTameness(1)
        }

        if (modified) updatePetItem()

        owner.write(getPetActionCommandPacket(PetActType.FEED, 1, inc > 0, true))
    }

    fun increaseTameness(amount: Int) {
        var inc = amount
        if (item.tameness + amount >= 0) {
            if (item.tameness + amount > 30000) {
                inc = 30000 - item.tameness
            }
        } else {
            inc = -item.tameness
        }

        if (inc == 0) return

        item.tameness = (inc + item.tameness).toShort()

        val level = GameConstants.getPetLevel(item.tameness)

        if (level > item.level) {
            item.level = level
            //owner.localEffect(PetEffect(0, idx))
        }
    }

    fun encodeData(pw: PacketWriter) {
        pw.writeInt(item.templateId)
        pw.writeMapleString(item.petName)
        pw.writeLong(item.cashItemSN)
        pw.writePosition(position)
        pw.writeByte(moveAction)
        pw.writeShort(foothold)
        pw.writeBool(nameTag)
        pw.writeBool(chatBalloon)
    }

    override val enterFieldPacket: Packet
        get() {
            val pw = PacketWriter(16)

            pw.writeHeader(SendOpcode.PET_ACTIVATED)
            pw.writeInt(owner.id)
            pw.writeByte(idx)

            pw.writeBool(true)
            pw.writeBool(true)

            encodeData(pw)

            return pw.createPacket()
        }

    override val leaveFieldPacket: Packet
        get() = leaveFieldPacket(0x00)

    fun leaveFieldPacket(leaveType: Byte): Packet {
        val pw = PacketWriter(9)

        pw.writeHeader(SendOpcode.PET_ACTIVATED)
        pw.writeInt(owner.id)
        pw.writeByte(idx)

        pw.writeBool(false)
        pw.writeByte(leaveType)

        return pw.createPacket()
    }

    fun getPetActionCommandPacket(type: PetActType, option: Byte, success: Boolean, chatBalloon: Boolean): Packet {
        return getPetActionCommandPacket(type.value, option, success, chatBalloon)
    }

    fun getPetActionCommandPacket(type: Byte, option: Byte, success: Boolean, chatBalloon: Boolean): Packet {
        val pw = PacketWriter(11)

        pw.writeHeader(SendOpcode.PET_ACTION_COMMAND)
        pw.writeInt(owner.id)
        pw.writeByte(idx)
        pw.writeByte(type)
        pw.writeByte(option)
        pw.writeBool(success)
        pw.writeBool(chatBalloon)

        return pw.createPacket()
    }

    fun getNameChangedPacket(): Packet {
        val pw = PacketWriter(16)

        pw.writeHeader(SendOpcode.PET_NAME_CHANGED)
        pw.writeLong(item.cashItemSN)
        pw.writeMapleString(item.petName)
        pw.writeBool(true)

        return pw.createPacket()
    }
}