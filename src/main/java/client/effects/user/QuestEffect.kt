package client.effects.user

import client.effects.AbstractEffect
import client.effects.EffectType
import org.graalvm.collections.Pair
import util.packet.PacketWriter
import java.util.*

class QuestEffect : AbstractEffect {

    private var entries: MutableList<Pair<Int, Int>> = ArrayList()

    constructor(id: Int, quantity: Int) {
        entries.add(Pair.create(id, quantity))
    }

    constructor(entries: MutableList<Pair<Int, Int>>) {
        this.entries = entries
    }

    override val type = EffectType.QUEST

    override fun encodeData(pw: PacketWriter) {
        pw.write(entries.size)
        entries.forEach {
            pw.writeInt(it.left)
            pw.writeInt(it.right)
        }
    }
}