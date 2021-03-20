package net.server

class MigrateInfo(
    val aid: Int,
    var port: Int,
    val ip: String
) {

    var channelId: Int = -1
    var cashShop = false

    override fun toString(): String {
        return "MigrateInfo(aid=$aid, channel=$channelId, ip='$ip', cashShop=$cashShop)"
    }
}