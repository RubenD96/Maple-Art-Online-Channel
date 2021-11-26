package net.database

import client.Client
import database.jooq.Tables.ACCOUNTS
import database.jooq.Tables.CHARACTERS
import net.database.DatabaseCore.connection
import org.jooq.Record

object AccountAPI {
    fun getAccountInfo(aid: Int): Record {
        with(ACCOUNTS) {
            return connection.select().from(this)
                .where(ID.eq(aid))
                .fetchOne()
        }
    }

    fun getAccountInfoTemporary(cid: Int): Record {
        with(CHARACTERS) {
            return getAccountInfo(
                connection.select().from(this)
                    .where(ID.eq(cid))
                    .fetchOne().getValue(ACCOUNTID)
            )
        }
    }

    fun getHighestLevelOnAccount(aid: Int): Int {
        with(CHARACTERS) {
            val res = connection.select().from(this)
                .where(ACCOUNTID.eq(aid))
                .fetch()
            var highest = 1 // lmao

            for (rec in res) {
                val level = rec.getValue(LEVEL)

                if (highest < level) {
                    highest = level
                }
            }
            return highest
        }
    }

    fun getCharacterCount(aid: Int): Int {
        with(CHARACTERS) {
            return connection.fetchCount(
                connection.select().from(this).where(ACCOUNTID.eq(aid))
            )
        }
    }

    fun loadNXCash(client: Client) {
        with(ACCOUNTS) {
            client.cash = connection.select(CASH).from(this)
                .where(ID.eq(client.accId))
                .fetchOne().getValue(CASH)
        }
    }
}