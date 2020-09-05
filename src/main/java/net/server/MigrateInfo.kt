package net.server

class MigrateInfo(val aid: Int, var channel: Int, val ip: String) {

    val cid: Int = 0 // actually unused, not really needed either
    var cashShop = false

    override fun toString(): String {
        return "MigrateInfo(aid=$aid, channel=$channel, ip='$ip', cid=$cid, cashShop=$cashShop)"
    }
}