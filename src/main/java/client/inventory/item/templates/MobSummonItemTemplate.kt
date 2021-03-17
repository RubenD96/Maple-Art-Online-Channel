package client.inventory.item.templates

import util.packet.PacketReader
import java.util.*

class MobSummonItemTemplate(id: Int) : ItemBundleTemplate(id) {

    private val mobs: MutableList<MobSummonItemEntry> = ArrayList()

    override fun decode(r: PacketReader): MobSummonItemTemplate {
        super.decode(r)

        val size = r.readShort().toInt()
        for (i in 0 until size) {
            mobs.add(MobSummonItemEntry(r))
        }

        return this
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