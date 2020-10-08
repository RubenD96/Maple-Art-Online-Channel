package world.ranking

import client.mastery.MasteryType
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import managers.MobManager
import net.database.RankingAPI
import util.logging.LogType
import util.logging.Logger.log
import java.util.*
import java.util.stream.Collectors
import kotlin.collections.HashSet
import kotlin.collections.LinkedHashMap
import kotlin.collections.set

object RankingKeeper {

    var isUpdating = false

    // rankings
    lateinit var characterData: Map<String, PlayerRanking>
    var regular: List<PlayerRanking> = ArrayList()
    var hardcore: List<PlayerRanking> = ArrayList()
    var killCount: List<PlayerRanking> = ArrayList()
    val mobKills: MutableMap<Int, List<PlayerRanking>> = LinkedHashMap()
    val bossKills: MutableMap<Int, List<PlayerRanking>> = LinkedHashMap()
    val masteries: MutableMap<MasteryType, List<PlayerRanking>> = LinkedHashMap()

    // todo playtime
    // todo mastery
    // todo bosses
    // todo jq's
    // selection lists
    private val mobs: MutableSet<Int> = HashSet()
    private val bosses: MutableSet<Int> = HashSet()
    val masteryTypes: MutableList<MasteryType> = ArrayList()

    /**
     * For script use
     *
     * @param players List of players to check in
     * @param name    Name to check for
     * @return PlayerRanking object including the rank, or null if it doesn't exist
     */
    fun getRankByName(players: List<PlayerRanking>, name: String): AbstractMap.SimpleEntry<Int, PlayerRanking>? {
        val player = players.stream()
                .filter { it.name == name }
                .findFirst()
                .orElse(null)
                ?: return null
        val index = players.indexOf(player)
        return AbstractMap.SimpleEntry(index, player)
    }

    /**
     * For script use
     *
     * @return A List of mob integers instead of a Set
     */
    fun getMobs(): List<Int> {
        return ArrayList(mobs)
    }

    fun getBosses(): List<Int> {
        return ArrayList(bosses)
    }

    fun updateAllRankings() {
        if (!isUpdating) {
            GlobalScope.launch {
                update()
            }
        } else log(LogType.MISC_CONSOLE, "Ranking not done updating yet", this)
    }

    private fun update() {
        isUpdating = true
        characterData = RankingAPI.nonGMCharacters
        updateRegularRanking()
        updateHardcoreRanking()
        updateKillCountRanking()
        updateMobKillsRanking()
        updateBossKillsRanking()
        updateMasteryRanking()
        isUpdating = false
        println("[RankingKeeper] All rankings have been updated!")
    }

    private fun updateRegularRanking() {
        val players = ArrayList(characterData.values)
        players.sortWith(Comparator.comparingInt { obj: PlayerRanking -> obj.level }.reversed())
        regular = players
    }

    private fun updateHardcoreRanking() {
        hardcore = characterData.values.stream()
                .filter { it.isHardcore }
                .sorted(Comparator.comparingInt { obj: PlayerRanking -> obj.level }.reversed())
                .collect(Collectors.toList())
    }

    private fun updateKillCountRanking() {
        val players = ArrayList(characterData.values)
        players.sortWith(Comparator.comparingInt { obj: PlayerRanking -> obj.killCount }.reversed())
        killCount = players
    }

    private fun updateMobKillsRanking() {
        val players = ArrayList(characterData.values)
        players.forEach { player: PlayerRanking ->
            mobs.addAll(player.mobKills
                    .keys.stream()
                    .filter { !MobManager.getMob(it).isBoss }.collect(Collectors.toList()))
        }
        mobs.forEach { updateMobKillsRanking(mobKills, it) }
    }

    private fun updateBossKillsRanking() {
        val players = ArrayList(characterData.values)
        players.forEach { player: PlayerRanking ->
            bosses.addAll(player.mobKills
                    .keys.stream()
                    .filter { MobManager.getMob(it).isBoss }.collect(Collectors.toList()))
        }
        bosses.forEach { updateMobKillsRanking(bossKills, it) }
    }

    private fun updateMobKillsRanking(list: MutableMap<Int, List<PlayerRanking>>, id: Int) {
        val players = characterData.values.stream()
                .filter { it.mobKills[id] != null }
                .sorted { p1, p2 -> p1.mobKills[id]?.let { p2.mobKills[id]?.compareTo(it) }!! }
                .collect(Collectors.toList())
        list[id] = players // override old data
    }

    private fun updateMasteryRanking() {
        masteryTypes.clear()
        MasteryType.values().forEach { type ->
            masteryTypes.add(type)

            // how the hell does one separate this...
            val players = characterData.values.stream()
                    .filter { it.masteries[type] != null }
                    .sorted { p1, p2 -> p1.masteries[type]?.let { p2.masteries[type]?.compareTo(it) }!! }
                    .collect(Collectors.toList())
            masteries[type] = players // override old data
        }
    }
}