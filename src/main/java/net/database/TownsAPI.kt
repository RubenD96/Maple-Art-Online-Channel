package net.database

import client.Character
import database.jooq.Tables.UNLOCKEDTOWNS
import net.database.DatabaseCore.connection

object TownsAPI {

    fun load(chr: Character) {
        connection.select(UNLOCKEDTOWNS.TOWN).from(UNLOCKEDTOWNS)
                .where(UNLOCKEDTOWNS.CID.eq(chr.id))
                .fetch()
                .forEach { chr.towns.add(it.getValue(UNLOCKEDTOWNS.TOWN)) }
    }

    fun add(chr: Character, town: Int) {
        connection.insertInto(UNLOCKEDTOWNS, UNLOCKEDTOWNS.CID, UNLOCKEDTOWNS.TOWN)
                .values(chr.id, town)
                .execute()
    }
}