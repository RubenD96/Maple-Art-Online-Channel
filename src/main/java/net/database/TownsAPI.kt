package net.database

import client.Character
import database.jooq.Tables
import net.database.DatabaseCore.connection
import org.jooq.Record1

object TownsAPI {

    fun load(chr: Character) {
        connection
                .select(Tables.UNLOCKEDTOWNS.TOWN).from(Tables.UNLOCKEDTOWNS)
                .where(Tables.UNLOCKEDTOWNS.CID.eq(chr.id))
                .fetch()
                .forEach { rec: Record1<Int> -> chr.towns.add(rec.getValue(Tables.UNLOCKEDTOWNS.TOWN)) }
    }

    fun add(chr: Character, town: Int) {
        connection
                .insertInto(Tables.UNLOCKEDTOWNS, Tables.UNLOCKEDTOWNS.CID, Tables.UNLOCKEDTOWNS.TOWN)
                .values(chr.id, town)
                .execute()
    }
}