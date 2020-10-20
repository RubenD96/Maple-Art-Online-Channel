package net.database

import client.player.Beauty
import database.jooq.Tables.HAIRS
import managers.BeautyManager
import net.database.DatabaseCore.connection

object BeautyAPI {

    fun loadHairs() {
        connection.select().from(HAIRS).fetch()
                .forEach {
                    val id = it.getValue(HAIRS.ID)
                    BeautyManager.hairs[id] = Beauty(id, it.getValue(HAIRS.GENDER).toInt(), it.getValue(HAIRS.ENABLED) == 1.toByte())
                }
    }

    fun updateHair(id: Int) {
        val hair = BeautyManager.hairs[id] ?: return
        connection.update(HAIRS)
                .set(HAIRS.ENABLED, (if (hair.isEnabled) 1 else 0).toByte())
                .where(HAIRS.ID.eq(id))
                .execute()
    }
}