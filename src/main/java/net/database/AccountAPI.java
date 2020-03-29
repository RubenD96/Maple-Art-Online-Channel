package net.database;

import org.jooq.Record;

import java.sql.SQLException;

import static database.jooq.Tables.ACCOUNTS;

public class AccountAPI {

    public static Record getAccountInfo(String name) {
        Record record = null;
        try {
            record = DatabaseCore.getConnection()
                    .select().from(ACCOUNTS)
                    .where(ACCOUNTS.NAME.eq(name))
                    .fetchOne();
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
        return record;
    }
}
