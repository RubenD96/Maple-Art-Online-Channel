package skill

data class Macro(
    val name: String,
    val shout: Boolean,
    val skills: IntArray
) {

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