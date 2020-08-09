package net.database;

import client.Client;
import org.jooq.Record;
import org.jooq.Result;

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

    public static int getHighestLevelOnAccount(int aid) {
        Result<Record> res = DatabaseCore.getConnection()
                .select().from(CHARACTERS)
                .where(CHARACTERS.ACCOUNTID.eq(aid))
                .fetch();

        final int[] highest = {1}; // lmao
        res.forEach(rec -> {
            int level = rec.getValue(CHARACTERS.LEVEL);
            if (highest[0] < level) {
                highest[0] = level;
            }
        });

        return highest[0];
    }

    public static int getCharacterCount(int aid) {
        return DatabaseCore.getConnection()
                .fetchCount(DatabaseCore.getConnection()
                        .select().from(CHARACTERS)
                        .where(CHARACTERS.ACCOUNTID.eq(aid))
                );
    }

    public static void loadNXCash(Client client) {
        client.setCash(DatabaseCore.getConnection()
                .select(ACCOUNTS.CASH).from(ACCOUNTS)
                .where(ACCOUNTS.ID.eq(client.getAccId()))
                .fetchOne().getValue(ACCOUNTS.CASH));
    }
}
