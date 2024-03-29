package constants

object ItemConstants {

    const val DARK_TOKEN = 4000524
    const val PERMANENT = 150841440000000000L
    const val BASE_EMOTE = 5160000

    fun isRechargeableItem(templateID: Int): Boolean {
        val type = templateID / 10000
        return type == 207 || type == 233
    }

    fun isTreatSingly(templateID: Int): Boolean {
        val type = templateID / 1000000
        if (type == 2 || type == 3 || type == 4) {
            val subType = templateID / 10000
            return subType == 207 || subType == 233
        }
        return true
    }
}