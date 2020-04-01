package net.database;

import org.jooq.Record;

import static database.jooq.Tables.ACCOUNTS;

public class AccountAPI {

    public static Record getAccountInfo(String name) {
        return DatabaseCore.getConnection()
                .select().from(ACCOUNTS)
                .where(ACCOUNTS.NAME.eq(name))
                .fetchOne();
    }
}
