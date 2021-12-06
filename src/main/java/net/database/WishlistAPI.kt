package net.database

import client.Character
import database.jooq.Tables.WISHLIST
import net.database.DatabaseCore.connection
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

        Arrays.stream(chr.wishlist).forEach {
            if (it != 0) {
                if (!old.contains(it)) { // new wish
                    add(chr.id, it)
                } else {
                    old.remove(Integer.valueOf(it))
                }
            }
        }

        // old wishes that are not in current wishlist
        old.forEach { remove(chr.id, it) }
    }

    private fun getWishList(cid: Int): MutableList<Int> {
        with(WISHLIST) {
            val res = connection.select().from(this)
                .where(CID.eq(cid))
                .fetch()

            val wishlist: MutableList<Int> = ArrayList()
            res.forEach { wishlist.add(it.getValue(SN)) }
            return wishlist
        }
    }

    private fun add(cid: Int, sn: Int) {
        with(WISHLIST) {
            connection.insertInto(this, CID, SN)
                .values(cid, sn)
                .execute()
        }
    }

    private fun remove(cid: Int, sn: Int) {
        with(WISHLIST) {
            connection.deleteFrom(this)
                .where(CID.eq(cid))
                .and(SN.eq(sn))
                .execute()
        }
    }
}