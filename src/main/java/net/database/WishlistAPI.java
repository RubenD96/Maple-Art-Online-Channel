package net.database;

import client.Character;
import org.jooq.Record;
import org.jooq.Result;

import java.util.*;

import static database.jooq.Tables.WISHLIST;

public class WishlistAPI {

    public static void load(Character chr) {
        List<Integer> list = getWishList(chr.getId());
        for (int i = 0; i < 10; i++) {
            if (list.size() <= i) {
                chr.getWishlist()[i] = 0;
            } else {
                chr.getWishlist()[i] = list.get(i);
            }
        }
    }

    public static void save(Character chr) {
        List<Integer> old = getWishList(chr.getId());

        Arrays.stream(chr.getWishlist()).forEach(wish -> {
            if (wish != 0) {
                if (!old.contains(wish)){ // new wish
                    add(chr.getId(), wish);
                } else {
                    old.remove(Integer.valueOf(wish));
                }
            }
        });

        // old wishes that are not in current wishlist
        old.forEach(wish -> remove(chr.getId(), wish));
    }

    private static List<Integer> getWishList(int cid) {
        Result<Record> res = DatabaseCore.getConnection()
                .select().from(WISHLIST)
                .where(WISHLIST.CID.eq(cid))
                .fetch();
        List<Integer> wishlist = new ArrayList<>();
        res.forEach(rec -> wishlist.add(rec.getValue(WISHLIST.SN)));

        return wishlist;
    }

    private static void add(int cid, int sn) {
        DatabaseCore.getConnection()
                .insertInto(WISHLIST, WISHLIST.CID, WISHLIST.SN)
                .values(cid, sn)
                .execute();
    }

    private static void remove(int cid, int sn) {
        DatabaseCore.getConnection()
                .deleteFrom(WISHLIST)
                .where(WISHLIST.CID.eq(cid))
                .and(WISHLIST.SN.eq(sn))
                .execute();
    }
}