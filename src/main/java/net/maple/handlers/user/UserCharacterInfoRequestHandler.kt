package net.maple.handlers.user

import client.Character
import client.Client
import client.inventory.ItemInventoryType
import net.maple.SendOpcode
import net.maple.handlers.PacketHandler
import util.packet.Packet
import util.packet.PacketReader
import util.packet.PacketWriter
import java.util.*
import java.util.stream.Collectors

class UserCharacterInfoRequestHandler : PacketHandler {

    override fun handlePacket(reader: PacketReader, c: Client) {
        val chr = c.character

        reader.readInteger() // timestamp?

        val target = chr.field.getObject<Character>(reader.readInteger()) ?: return chr.enableActions()
        chr.write(target.getInfo())
    }

    companion object {
        private fun Character.getInfo(): Packet {
            val pw = PacketWriter(32)

            with(this) {
                pw.writeHeader(SendOpcode.CHARACTER_INFO)
                pw.writeInt(id)
                pw.write(level)
                pw.writeShort(job)
                pw.writeShort(fame)
                pw.writeBool(false) // married or not
                pw.writeMapleString(guild?.name ?: "")
                pw.writeMapleString("")
                pw.write(0) // pMedalInfo

                // pets
                var petCount = pets.size
                pw.writeBool(petCount > 0)
                pets.sortBy { it.idx }
                pets.forEach {
                    with(it.item) {
                        pw.writeInt(templateId)
                        pw.writeMapleString(petName)
                        pw.writeByte(level)
                        pw.writeShort(tameness)
                        pw.writeByte(repleteness)
                        pw.writeShort(petSkill)
                        pw.writeInt(0) // pet equips
                        pw.writeBool(--petCount > 0)
                    }
                }

                pw.write(0) // taming mob

                val writeableWishlist = Arrays.stream(wishlist).filter { it != 0 }.toArray()
                pw.write(writeableWishlist.size) // wishlist
                Arrays.stream(writeableWishlist).forEach { pw.writeInt(it) }

                // MedalAchievementInfo::Decode
                pw.writeInt(0)
                pw.writeShort(0)

                // chairs
                val chairs = getInventory(ItemInventoryType.INSTALL)
                    .items
                    .values.stream()
                    .filter { it.templateId / 10000 == 301 }
                    .collect(Collectors.toList())
                pw.writeInt(chairs.size)
                chairs.forEach { pw.writeInt(it.templateId) }
            }

            return pw.createPacket()
        }
    }
}