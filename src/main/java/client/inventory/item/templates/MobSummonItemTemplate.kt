package client.inventory.item.templates

import util.packet.PacketReader
import java.util.*

class MobSummonItemTemplate(id: Int, r: PacketReader) : ItemBundleTemplate(id, r) {

    private val mobs: MutableList<MobSummonItemEntry> = ArrayList()

    init {
        val size = r.readShort().toInt()
        for (i in 0 until size) {
            mobs.add(MobSummonItemEntry(r))
        }
    }

    class MobSummonItemEntry(r: PacketReader) {
        val templateId: Int
        val prob: Int

        init {
            templateId = r.readInteger()
            prob = r.readInteger()
        }
    }
}