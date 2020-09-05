package net.database

import database.jooq.Tables
import net.database.DatabaseCore.connection
import org.jooq.Record
import org.jooq.Result
import java.util.*

object ShopAPI {
    /**
     * @return A List of all shops
     */
    val shops: List<Int>
        get() {
            val shops: MutableList<Int> = ArrayList()
            connection
                    .select().from(Tables.SHOPS)
                    .fetch()
                    .forEach { shop: Record -> shops.add(shop.getValue(Tables.SHOPS.ID)) }
            return shops
        }

    /**
     * @param shop shopid
     * @return A List of all shopitems for the shop, to be read out in NPCShop.java
     */
    fun getShopsItems(shop: Int): Result<Record> {
        return connection
                .select().from(Tables.SHOPITEMS)
                .where(Tables.SHOPITEMS.SHOPID.eq(shop))
                .orderBy(Tables.SHOPITEMS.POSITION)
                .fetch()
    }
}