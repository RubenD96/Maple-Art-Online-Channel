package world.ranking

import java.util.LinkedHashMap

data class PlayerRanking(val level: Int, val job: Int, val killCount: Int, val name: String, val isHardcore: Boolean, val isDead: Boolean) {

    val mobKills: MutableMap<Int, Int> = LinkedHashMap()
}