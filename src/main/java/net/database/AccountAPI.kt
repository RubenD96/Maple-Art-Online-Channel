package net.database

import client.Client
import database.jooq.Tables.ACCOUNTS
import database.jooq.Tables.CHARACTERS
import net.database.DatabaseCore.connection
import org.jooq.Record

object AccountAPI {
    fun getAccountInfo(aid: Int): Record {
        return connection.select().from(ACCOUNTS)
                .where(ACCOUNTS.ID.eq(aid))
                .fetchOne()
    }

    fun getAccountInfoTemporary(cid: Int): Record {
        return getAccountInfo(connection.select().from(CHARACTERS)
                .where(CHARACTERS.ID.eq(cid))
                .fetchOne().getValue(CHARACTERS.ACCOUNTID))
    }

    fun getHighestLevelOnAccount(aid: Int): Int {
        val res = connection.select().from(CHARACTERS)
                .where(CHARACTERS.ACCOUNTID.eq(aid))
                .fetch()
        var highest = 1 // lmao

        for (rec in res) {
            val level = rec.getValue(CHARACTERS.LEVEL)

            if (highest < level) {
                highest = level
            }
        }
        return highest
    }

    fun getCharacterCount(aid: Int): Int {
        return connection.fetchCount(
                connection.select().from(CHARACTERS).where(CHARACTERS.ACCOUNTID.eq(aid))
        )
    }

    fun loadNXCash(client: Client) {
        client.cash = connection.select(ACCOUNTS.CASH).from(ACCOUNTS)
                .where(ACCOUNTS.ID.eq(client.accId))
                .fetchOne().getValue(ACCOUNTS.CASH)
    }
}