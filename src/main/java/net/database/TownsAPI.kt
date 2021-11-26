package net.database

import client.Character
import database.jooq.Tables.UNLOCKEDTOWNS
import net.database.DatabaseCore.connection

object TownsAPI {

    fun load(chr: Character) {
        with(UNLOCKEDTOWNS) {
            connection.select(TOWN).from(this)
                .where(CID.eq(chr.id))
                .fetch()
                .forEach { chr.towns.add(it.getValue(TOWN)) }
        }
    }

    fun add(chr: Character, town: Int) {
        with(UNLOCKEDTOWNS) {
            connection.insertInto(this, CID, TOWN)
                .values(chr.id, town)
                .execute()
        }
    }
}