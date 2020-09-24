package net.database

import database.jooq.Tables.CHARACTERS
import database.jooq.Tables.MOBKILLS
import net.database.DatabaseCore.connection
import world.ranking.PlayerRanking

object RankingAPI {

    val nonGMCharacters: Map<String, PlayerRanking>
        get() {
            val rankings: MutableMap<String, PlayerRanking> = LinkedHashMap()
            val res = connection.select().from(CHARACTERS)
                    .leftJoin(MOBKILLS)
                    .onKey()
                    .where(CHARACTERS.GM_LEVEL.eq(0))
                    .fetch()

            res.forEach {
                val name = it.getValue(CHARACTERS.NAME)
                var ranking = rankings[name]
                if (ranking == null) {
                    ranking = PlayerRanking(
                            it.getValue(CHARACTERS.LEVEL),
                            it.getValue(CHARACTERS.JOB),
                            it.getValue(CHARACTERS.KILL_COUNT),
                            name,
                            it.getValue(CHARACTERS.HARDCORE) == 1.toByte(),
                            it.getValue(CHARACTERS.MAP) == 666)
                    rankings[name] = ranking
                }

                if (it.getValue(MOBKILLS.MID) != null)
                    ranking.mobKills[it.getValue(MOBKILLS.MID)] = it.getValue(MOBKILLS.COUNT)
            }
            return rankings
        }
}