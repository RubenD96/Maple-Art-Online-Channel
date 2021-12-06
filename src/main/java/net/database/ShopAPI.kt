package net.database

import database.jooq.Tables.SHOPITEMS
import database.jooq.Tables.SHOPS
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
            connection.select().from(SHOPS)
                    .fetch()
                    .forEach { shops.add(it.getValue(SHOPS.ID)) }
            return shops
        }

    /**
     * @param shop shopid
     * @return A List of all shopitems for the shop, to be read out in NPCShop.java
     */
    fun getShopsItems(shop: Int): Result<Record> {
        with(SHOPITEMS) {
            return connection.select().from(this)
                .where(SHOPID.eq(shop))
                .orderBy(POSITION)
                .fetch()
        }
    }
}