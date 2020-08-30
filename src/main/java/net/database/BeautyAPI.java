package net.database;

import client.player.Beauty;
import managers.BeautyManager;

import static database.jooq.Tables.HAIRS;

public class BeautyAPI {

    public static void loadHairs() {
        DatabaseCore.getConnection()
                .select().from(HAIRS)
                .fetch()
                .forEach(hair -> {
                    int id = hair.getValue(HAIRS.ID);
                    BeautyManager.getHairs().put(id, new Beauty(id, hair.getValue(HAIRS.GENDER), hair.getValue(HAIRS.ENABLED) == 1));
                });
    }

    public static void updateHair(int id) {
        DatabaseCore.getConnection()
                .update(HAIRS)
                .set(HAIRS.ENABLED, (byte) (BeautyManager.getHairs().get(id).isEnabled() ? 1 : 0))
                .where(HAIRS.ID.eq(id))
                .execute();
    }
}
