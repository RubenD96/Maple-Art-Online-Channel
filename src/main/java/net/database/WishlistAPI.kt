package net.database

import client.Character
import database.jooq.Tables
import net.database.DatabaseCore.connection
import org.jooq.Record
import java.util.*

object WishlistAPI {
    fun load(chr: Character) {
        val list: List<Int> = getWishList(chr.id)
        for (i in 0..9) {
            if (list.size <= i) {
                chr.wishlist[i] = 0
            } else {
                chr.wishlist[i] = list[i]
            }
        }
    }

    fun save(chr: Character) {
        val old = getWishList(chr.id)
        Arrays.stream(chr.wishlist).forEach { wish: Int ->
            if (wish != 0) {
                if (!old.contains(wish)) { // new wish
                    add(chr.id, wish)
                } else {
                    old.remove(Integer.valueOf(wish))
                }
            }
        }

        // old wishes that are not in current wishlist
        old.forEach { wish: Int -> remove(chr.id, wish) }
    }

    private fun getWishList(cid: Int): MutableList<Int> {
        val res = connection
                .select().from(Tables.WISHLIST)
                .where(Tables.WISHLIST.CID.eq(cid))
                .fetch()
        val wishlist: MutableList<Int> = ArrayList()
        res.forEach { rec: Record -> wishlist.add(rec.getValue(Tables.WISHLIST.SN)) }
        return wishlist
    }

    private fun add(cid: Int, sn: Int) {
        connection
                .insertInto(Tables.WISHLIST, Tables.WISHLIST.CID, Tables.WISHLIST.SN)
                .values(cid, sn)
                .execute()
    }

    private fun remove(cid: Int, sn: Int) {
        connection
                .deleteFrom(Tables.WISHLIST)
                .where(Tables.WISHLIST.CID.eq(cid))
                .and(Tables.WISHLIST.SN.eq(sn))
                .execute()
    }
}