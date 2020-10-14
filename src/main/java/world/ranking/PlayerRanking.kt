package world.ranking

import client.mastery.MasteryType
import java.util.*

data class PlayerRanking(val level: Int,
                         val job: Int,
                         val killCount: Int,
                         val name: String,
                         val isHardcore: Boolean,
                         val isDead: Boolean,
                         val totalDamage: Long) {

    val mobKills: MutableMap<Int, Int> = LinkedHashMap()
    val masteries: MutableMap<MasteryType, Int> = LinkedHashMap()
}