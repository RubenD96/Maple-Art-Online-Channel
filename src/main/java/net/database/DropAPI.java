package net.database;

import field.object.drop.DropEntry;
import org.jooq.Record;
import org.jooq.Result;

import java.util.ArrayList;
import java.util.List;

import static database.jooq.Tables.MOBDROPS;

public class DropAPI {

    public static List<DropEntry> getMobDrops(int mob) {
        List<DropEntry> drops = new ArrayList<>();
        Result<Record> res = DatabaseCore.getConnection()
                .select().from(MOBDROPS)
                .where(MOBDROPS.MID.eq(mob))
                .fetch();

        res.forEach(rec -> drops.add(new DropEntry(
                rec.get(MOBDROPS.IID),
                rec.get(MOBDROPS.CHANCE),
                rec.get(MOBDROPS.MIN),
                rec.get(MOBDROPS.MAX),
                rec.get(MOBDROPS.QUESTID)
        )));

        return drops;
    }
}
