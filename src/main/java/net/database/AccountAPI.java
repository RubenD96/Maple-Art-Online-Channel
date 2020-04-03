package net.database;

import org.jooq.Record;

import static database.jooq.Tables.ACCOUNTS;
import static database.jooq.Tables.CHARACTERS;

public class AccountAPI {

    public static Record getAccountInfo(int aid) {
        return DatabaseCore.getConnection()
                .select().from(ACCOUNTS)
                .where(ACCOUNTS.ID.eq(aid))
                .fetchOne();
    }

    public static Record getAccountInfoTemporary(int cid) {
        return getAccountInfo(DatabaseCore.getConnection()
                .select().from(CHARACTERS)
                .where(CHARACTERS.ID.eq(cid))
                .fetchOne().getValue(CHARACTERS.ACCOUNTID));
    }
}
