package net.database

import database.jooq.Tables
import net.database.DatabaseCore.connection
import org.jooq.Record
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
            res.forEach { rec: Record ->
                val name = rec.getValue(Tables.CHARACTERS.NAME)
                var ranking = rankings[name]
                if (ranking == null) {
                    ranking = PlayerRanking(
                            rec.getValue(Tables.CHARACTERS.LEVEL),
                            rec.getValue(Tables.CHARACTERS.JOB),
                            rec.getValue(Tables.CHARACTERS.KILL_COUNT),
                            name,
                            rec.getValue(Tables.CHARACTERS.HARDCORE) == 1.toByte(),
                            rec.getValue(Tables.CHARACTERS.MAP) == 666)
                    rankings[name] = ranking
                }
                if (rec.getValue(Tables.MOBKILLS.MID) != null) ranking.mobKills[rec.getValue(Tables.MOBKILLS.MID)] = rec.getValue(Tables.MOBKILLS.COUNT)
            }
            return rankings
        }
}