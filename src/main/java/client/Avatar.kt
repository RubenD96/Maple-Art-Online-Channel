package client

import client.inventory.ItemInventory
import client.inventory.ItemInventoryType
import client.pet.FieldUserPet
import client.player.Job
import field.obj.life.AbstractFieldLife
import net.maple.packets.FieldPackets.enterField
import net.maple.packets.FieldPackets.leaveField
import util.packet.Packet
import world.guild.Guild

abstract class Avatar : AbstractFieldLife() {

    abstract var gender: Int
    abstract var skinColor: Int
    abstract var face: Int
    abstract var hair: Int
    abstract var level: Int
    abstract var name: String
    abstract var job: Job

    open val pets = ArrayList<FieldUserPet>()
    protected open val inventories: Map<ItemInventoryType, ItemInventory> = mapOf(
        ItemInventoryType.EQUIP to ItemInventory(24)
    )
    var portableChair: Int? = null
    var guild: Guild? = null

    override val enterFieldPacket: Packet
        get() = enterField()
    override val leaveFieldPacket: Packet
        get() = leaveField()

    fun getEquips(): ItemInventory {
        return inventories[ItemInventoryType.EQUIP] ?: error("Equip inventory not found")
    }
}