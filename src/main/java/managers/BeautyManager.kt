package managers

import client.player.Beauty

object BeautyManager {

    val hairs: MutableMap<Int, MutableList<Beauty>> =
        LinkedHashMap<Int, MutableList<Beauty>>().also { map -> repeat(101) { map[it] = ArrayList() } }
    val faces: MutableMap<Int, MutableList<Beauty>> =
        LinkedHashMap<Int, MutableList<Beauty>>().also { map -> repeat(101) { map[it] = ArrayList() } }
}