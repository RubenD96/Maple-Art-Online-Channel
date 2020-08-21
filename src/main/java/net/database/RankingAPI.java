package net.database;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import world.ranking.PlayerRanking;

import java.util.*;

import static database.jooq.Tables.CHARACTERS;
import static database.jooq.Tables.MOBKILLS;

public class RankingAPI {

    public static Map<String, PlayerRanking> getNonGMCharacters() {
        DSLContext con = DatabaseCore.getConnection();
        Map<String, PlayerRanking> rankings = new LinkedHashMap<>();

        Result<Record> res = con.select().from(CHARACTERS)
                .leftJoin(MOBKILLS)
                .onKey()
                .where(CHARACTERS.GM_LEVEL.eq(0))
                .fetch();

        res.forEach(rec -> {
            String name = rec.getValue(CHARACTERS.NAME);
            PlayerRanking ranking = rankings.get(name);
            if (ranking == null) {
                ranking = new PlayerRanking(
                        rec.getValue(CHARACTERS.LEVEL),
                        rec.getValue(CHARACTERS.JOB),
                        rec.getValue(CHARACTERS.KILL_COUNT),
                        name,
                        rec.getValue(CHARACTERS.HARDCORE) == 1,
                        rec.getValue(CHARACTERS.MAP) == 666);
                rankings.put(name, ranking);
            }
            if (rec.getValue(MOBKILLS.MID) != null)
                ranking.getMobKills().put(rec.getValue(MOBKILLS.MID), rec.getValue(MOBKILLS.COUNT));
        });

        return rankings;
    }
}
