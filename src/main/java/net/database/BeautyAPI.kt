package net.database

import client.player.Beauty
import database.jooq.Tables.HAIRS
import managers.BeautyManager
import net.database.DatabaseCore.connection

object BeautyAPI {

    fun loadHairs() {
        with(HAIRS) {
            connection.select().from(this).fetch()
                .forEach {
                    BeautyManager.hairs[it.getValue(FLOOR)]!!.add(
                        Beauty(
                            it.getValue(ID),
                            it.getValue(GENDER),
                        )
                    )
                }
        }
    }

    fun updateHair(hair: Beauty, floor: Int) {
        with(HAIRS) {
            connection.update(this)
                .set(FLOOR, floor)
                .where(ID.eq(hair.id))
                .execute()
        }
    }
}