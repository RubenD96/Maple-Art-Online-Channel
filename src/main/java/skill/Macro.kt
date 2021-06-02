package skill

import util.packet.PacketWriter

data class Macro(
    val name: String,
    val shout: Boolean,
    val skills: IntArray
) {

    fun encode(pw: PacketWriter) {
        pw.writeMapleString(name)
        pw.writeBool(shout)
        pw.writeInt(skills[0])
        pw.writeInt(skills[1])
        pw.writeInt(skills[2])
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Macro

        if (name != other.name) return false
        if (shout != other.shout) return false
        if (!skills.contentEquals(other.skills)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + shout.hashCode()
        result = 31 * result + skills.contentHashCode()
        return result
    }
}