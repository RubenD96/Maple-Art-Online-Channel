package managers

import client.player.Beauty

object BeautyManager {
    val hairs: MutableMap<Int, Beauty> = LinkedHashMap()
    val faces: MutableMap<Int, Beauty> = LinkedHashMap()
}