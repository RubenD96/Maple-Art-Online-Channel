package net.database;

import org.jooq.Record;
import org.jooq.Result;

import java.util.ArrayList;
import java.util.List;

import static database.jooq.Tables.SHOPITEMS;
import static database.jooq.Tables.SHOPS;

public class ShopAPI {

    /**
     * @return A List of all shops
     */
    public static List<Integer> getShops() {
        List<Integer> shops = new ArrayList<>();
        DatabaseCore.getConnection()
                .select().from(SHOPS)
                .fetch()
                .forEach(shop -> shops.add(shop.getValue(SHOPS.ID)));
        return shops;
    }

    /**
     * @param shop shopid
     * @return A List of all shopitems for the shop, to be read out in NPCShop.java
     */
    public static Result<Record> getShopsItems(int shop) {
        return DatabaseCore.getConnection()
                .select().from(SHOPITEMS)
                .where(SHOPITEMS.SHOPID.eq(shop))
                .orderBy(SHOPITEMS.POSITION)
                .fetch();
    }
}
