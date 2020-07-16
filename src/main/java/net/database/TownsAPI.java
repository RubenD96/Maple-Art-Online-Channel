package net.database;

import client.Character;

import java.util.Collections;
import java.util.stream.Collectors;

import static database.jooq.Tables.UNLOCKEDTOWNS;

public class TownsAPI {

    public static void load(Character chr) {
        DatabaseCore.getConnection()
                .select().from(UNLOCKEDTOWNS)
                .where(UNLOCKEDTOWNS.CID.eq(chr.getId()))
                .fetch()
                .forEach(rec -> chr.getTowns().add(rec.getValue(UNLOCKEDTOWNS.TOWN)));
    }

    public static void add(Character chr, int town) {
        DatabaseCore.getConnection()
                .insertInto(UNLOCKEDTOWNS, UNLOCKEDTOWNS.CID, UNLOCKEDTOWNS.TOWN)
                .values(chr.getId(), town)
                .execute();
    }
}
