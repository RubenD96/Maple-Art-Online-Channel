package net.database

import client.player.Beauty
import database.jooq.Tables
import managers.BeautyManager
import net.database.DatabaseCore.connection
import org.jooq.Record
import java.util.function.Consumer

object BeautyAPI {

    fun loadHairs() {
        connection.select().from(Tables.HAIRS)
                .fetch()
                .forEach(Consumer { hair: Record ->
                    val id = hair.getValue(Tables.HAIRS.ID)
                    BeautyManager.hairs[id] = Beauty(id, hair.getValue(Tables.HAIRS.GENDER).toInt(), hair.getValue(Tables.HAIRS.ENABLED) == 1.toByte())
                })
    }

    fun updateHair(id: Int) {
        connection.update(Tables.HAIRS)
                .set(Tables.HAIRS.ENABLED, (if (BeautyManager.hairs[id]!!.isEnabled) 1 else 0).toByte())
                .where(Tables.HAIRS.ID.eq(id))
                .execute()
    }
}