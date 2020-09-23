package net.database

import database.jooq.Tables
import net.database.DatabaseCore.connection
import world.ranking.PlayerRanking

object RankingAPI {
    val nonGMCharacters: Map<String, PlayerRanking>
        get() {
            val rankings: MutableMap<String, PlayerRanking> = LinkedHashMap()
            val res = connection.select().from(Tables.CHARACTERS)
                    .leftJoin(Tables.MOBKILLS)
                    .onKey()
                    .where(Tables.CHARACTERS.GM_LEVEL.eq(0))
                    .fetch()
            res.forEach {
                val name = it.getValue(Tables.CHARACTERS.NAME)
                var ranking = rankings[name]
                if (ranking == null) {
                    ranking = PlayerRanking(
                            it.getValue(Tables.CHARACTERS.LEVEL),
                            it.getValue(Tables.CHARACTERS.JOB),
                            it.getValue(Tables.CHARACTERS.KILL_COUNT),
                            name,
                            it.getValue(Tables.CHARACTERS.HARDCORE) == 1.toByte(),
                            it.getValue(Tables.CHARACTERS.MAP) == 666)
                    rankings[name] = ranking
                }
                if (it.getValue(Tables.MOBKILLS.MID) != null)
                    ranking.mobKills[it.getValue(Tables.MOBKILLS.MID)] = it.getValue(Tables.MOBKILLS.COUNT)
            }
            return rankings
        }
}